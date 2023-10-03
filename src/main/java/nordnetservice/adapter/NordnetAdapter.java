package nordnetservice.adapter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.dto.Tuple2;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionInfo;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.util.ListUtil;
import nordnetservice.util.StockOptionUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import nordnetservice.domain.html.PageInfo;
import vega.financial.StockOptionType;
import vega.financial.calculator.OptionCalculator;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;


@Component
public class NordnetAdapter {
    private static final int SP_CLS = 4;
    private static final int SP_HI = 7;
    private static final int SP_LO = 8;
    private static final int CALL_TICKER = 1;
    private static final int CALL_BID = 3;
    private static final int CALL_ASK = 4;
    private static final int X = 7;
    private static final int PUT_BID = 9;
    private static final int PUT_ASK = 10;
    private static final int PUT_TICKER = 13;
    private final Pattern pat = Pattern.compile("Norway\\s*(\\S*)");
    private final Downloader<PageInfo> downloader;
    private final RedisAdapter redisAdapter;
    private final LocalDate curDate;
    private final OptionCalculator calculator;
    private final Cache<Integer, Tuple2<StockPrice,List<StockOption>>> cacheStockOptions;
    private final Cache<String, Tuple2<StockPrice,List<StockOption>>> cacheStockOption;

    public NordnetAdapter(Downloader<PageInfo> downloaderAdapter,
                          RedisAdapter redisAdapter,
                          OptionCalculator calculator,
                          @Value("${curdate}") String curDateStr,
                          @Value("${cache.options.expiry}") int optionsExpiry,
                          @Value("${cache.option.expiry}") int optionExpiry) {
        this.downloader = downloaderAdapter;
        this.redisAdapter = redisAdapter;
        this.calculator = calculator;
        if (curDateStr == null || curDateStr.equals("today")) {
            curDate = LocalDate.now();
        }
        else {
            curDate = LocalDate.parse(curDateStr);
        }
        cacheStockOptions = Caffeine.newBuilder().expireAfterWrite(optionsExpiry, TimeUnit.MINUTES).build();
        cacheStockOption = Caffeine.newBuilder().expireAfterWrite(optionExpiry, TimeUnit.SECONDS).build();
    }

    private String elementText(Element el) {
        return el.children().get(0).text();
    }
    private StockOptionTicker el2ticker(Element el) {
        var m = pat.matcher(elementText(el));
        if (m.matches()) {
            return new StockOptionTicker(m.group(1));
        }
        else {
            return null;
        }
    }

    private double el2double(Element el) {
        var tx = elementText(el);
        var digits = tx.split(",");
        if (digits.length == 2) {
            return Double.parseDouble(String.format("%s.%s", digits[0], digits[1]));
        }
        else {
            return -1.0;
        }
    }

    private StockOption parseOption(int indexTicker,
                                    int indexBid,
                                    int indexAsk,
                                    StockOptionType ot,
                                    StockPrice stockPrice,
                                    Element el) {
        var ch = el.children();
        var ticker = el2ticker(ch.get(indexTicker));
        if (ticker == null)  {
            return null;
        }
        var x = el2double(ch.get(X));
        var bid = el2double(ch.get(indexBid));
        var ask = el2double(ch.get(indexAsk));
        var isoAndDays = StockOptionUtil.iso8601andDays(ticker, curDate);
        return  new StockOption(ticker, ot, x, bid, ask, isoAndDays.second(), isoAndDays.first(), stockPrice, calculator);
    }

    private StockOption parseCall(StockPrice stockPrice, Element el) {
        return parseOption(CALL_TICKER, CALL_BID, CALL_ASK, StockOptionType.CALL, stockPrice, el);
    }
    private StockOption parsePut(StockPrice stockPrice, Element el) {
        return parseOption(PUT_TICKER, PUT_BID, PUT_ASK, StockOptionType.PUT, stockPrice, el);
    }
    private List<StockOption> parseOptions(StockPrice sp, Element el) {
        var callFn = new StockOptionCreator(StockOptionType.CALL, sp, this);
        var putFn = new StockOptionCreator(StockOptionType.PUT, sp, this);
        var rows = el.children();
        var calls = rows.stream().skip(1).map(callFn).filter(Objects::nonNull).toList();
        var puts = rows.stream().skip(1).map(putFn).filter(Objects::nonNull).toList();
        return Stream.concat(calls.stream(), puts.stream()).toList();
    }

    private StockPrice parseStockPrice(StockTicker ticker, Element el) {
        var rows = el.children();
        var stockPriceRow = rows.get(1);
        var rc = stockPriceRow.children();
        var opn = redisAdapter.openingPrice(ticker);
        var hi = el2double(rc.get(SP_HI));
        var lo = el2double(rc.get(SP_LO));
        var cls = el2double(rc.get(SP_CLS));
        return new StockPrice(opn, hi, lo, cls);
    }

    /*
    private List<StockOption> parse(StockPrice stockPrice, PageInfo page) {
        var soup = Jsoup.parse(page.body());
        var roleTable = soup.select("[role=table]");
        return parseOptions(stockPrice, roleTable.get(1));
    }
     */

    private Tuple2<StockPrice,List<StockOption>> parse(StockTicker ticker, PageInfo page) {
        var soup = Jsoup.parse(page.body());
        var roleTable = soup.select("[role=table]");
        var sp = parseStockPrice(ticker, roleTable.get(0));
        var options = parseOptions(sp, roleTable.get(1));
        return new Tuple2<>(sp, options);
    }

    private List<StockOption> parsePage(StockPrice stockPrice, PageInfo page) {
        var soup = Jsoup.parse(page.body());
        var roleTable = soup.select("[role=table]");
        return parseOptions(stockPrice, roleTable.get(1));
    }

    private Tuple2<StockPrice,List<StockOption>> parse(StockTicker ticker) {

        var hit = cacheStockOptions.getIfPresent(ticker.oid());

        if (hit == null) {
            var pages = downloader.download(ticker);
            if (pages.size() == 0) {
                return new Tuple2<>(null, Collections.emptyList());
            }
            var page = pages.get(0);

            var result0 = parse(ticker, page);

            if (pages.size() > 1) {

                var parsePageClosure = new ParsePageClosure(result0.first(), this);

                var restList = pages.stream().skip(1).map(parsePageClosure).toList();

                var result = ListUtil.joinLists(result0.second(), restList);

                var newCached = new Tuple2<>(result0.first(), result);

                cacheStockOptions.put(ticker.oid(), newCached);

                return newCached;
            }
            else {
                cacheStockOptions.put(ticker.oid(), result0);

                return result0;
            }
        }
        else {
            return hit;
        }
    }

    private List<StockOption> getOptions(StockTicker ticker, StockOptionType ot) {
        var result = parse(ticker);
        return result.second().stream().filter(x -> x.getOpType() == ot).toList();
    }

    public List<StockOption> getCalls(StockTicker ticker) {
        return getOptions(ticker, StockOptionType.CALL);
    }

    public List<StockOption> getPuts(StockTicker ticker) {
        return getOptions(ticker, StockOptionType.PUT);
    }

    public StockPrice getStockPrice(StockTicker ticker) {
        var result = parse(ticker);
        return result.first();
    }

    private String keyFor(StockOptionInfo info) {
        return String.format("%d:%d", info.getStockTicker().oid(), info.getNordnetMillis());
    }

    public Tuple2<StockPrice,StockOption> findOption(StockOptionTicker ticker) {

        var info = StockOptionUtil.stockOptionInfoFromTicker(ticker);

        var key = keyFor(info);

        var hit = cacheStockOption.getIfPresent(key);

        if (hit == null) {

            var page = downloader.download(ticker);

            Tuple2<StockPrice, List<StockOption>> options = parse(info.getStockTicker(), page);

            cacheStockOption.put(key, options);

            hit = options;
        }

        var tickerS = ticker.value();

        var opt = hit.second().stream().filter(s -> s.getTicker().value().equals(tickerS)).findFirst();

        if (opt.isPresent()) {
            return new Tuple2<>(hit.first(), opt.get());
        }
        else {
            return null;
        }

        /*
            var tickerS = ticker.value();

            var opt = options.second().stream().filter(s -> s.getTicker().value().equals(tickerS)).findFirst();

            if (opt.isPresent()) {
                var newCached = new Tuple2<StockPrice,StockOption>(options.first(), opt.get());
                cacheStockOption.put(info.getStockTicker().oid(), newCached);
                return newCached;
            }
            else {
                return Optional.empty();
            }

        }
        return Optional.of(hit);

         */
    }

    private record ParsePageClosure(StockPrice stockPrice,
                                    NordnetAdapter adapter) implements Function<PageInfo, List<StockOption>> {
        @Override
        public List<StockOption> apply(PageInfo page) {
            return adapter.parsePage(stockPrice, page);
        }
    }

    private record StockOptionCreator(StockOptionType ot,
                                      StockPrice stockPrice,
                                      NordnetAdapter adapter) implements Function<Element, StockOption> {
        @Override
            public StockOption apply(Element element) {
                if (ot == StockOptionType.CALL) {
                    return adapter.parseCall(stockPrice, element);
                }
                else {
                    return adapter.parsePut(stockPrice, element);
                }
            }
        }
}
