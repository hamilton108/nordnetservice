package nordnetservice.adapter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.dto.Tuple2;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.util.StockOptionUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import nordnetservice.domain.html.PageInfo;
import vega.financial.StockOptionType;
import vega.financial.calculator.OptionCalculator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;


@Component
public class NordnetAdapter {
    private final int SP_CLS = 4;
    private final int SP_HI = 7;
    private final int SP_LO = 8;
    private final int CALL_TICKER = 1;
    private final int CALL_BID = 3;
    private final int CALL_ASK = 4;
    private final int X = 7;
    private final int PUT_BID = 9;
    private final int PUT_ASK = 10;
    private final int PUT_TICKER = 13;
    private final Pattern pat = Pattern.compile("Norway\\s*(\\S*)");
    private final Downloader<PageInfo> downloader;
    private final RedisAdapter redisAdapter;
    private final LocalDate curDate;
    private final OptionCalculator calculator;
    private final Cache<Integer, Tuple2<StockPrice,List<StockOption>>> cache;

    public NordnetAdapter(Downloader<PageInfo> downloaderAdapter,
                          RedisAdapter redisAdapter,
                          OptionCalculator calculator,
                          @Value("${curdate}") String curDateStr) {
        this.downloader = downloaderAdapter;
        this.redisAdapter = redisAdapter;
        this.calculator = calculator;
        if (curDateStr == null || curDateStr.equals("today")) {
            curDate = LocalDate.now();
        }
        else {
            curDate = LocalDate.parse(curDateStr);
        }
        cache = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
    }

    private String elementText(Element el) {
        var result = el.children().get(0).text();
        //System.out.println(result);
        return result;
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

    private Tuple2<StockPrice,List<StockOption>> parse(StockTicker ticker) {
        var hit = cache.getIfPresent(ticker.oid());
        if ( hit == null) {
            var pages = downloader.download(ticker);
            if (pages.size() == 0) {
                return new Tuple2<>(null, Collections.emptyList());
            }
            var page = pages.get(0);
            var soup = Jsoup.parse(page.body());
            var roleTable = soup.select("[role=table]");
            var sp = parseStockPrice(ticker, roleTable.get(0));
            var options = parseOptions(sp, roleTable.get(1));
            var newCached = new Tuple2<>(sp, options);
            cache.put(ticker.oid(), newCached);
            return newCached;
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
