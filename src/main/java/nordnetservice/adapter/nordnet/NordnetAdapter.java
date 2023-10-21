package nordnetservice.adapter.nordnet;

import nordnetservice.adapter.RedisAdapter;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.repository.NordnetRepository;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.Tuple2;
import nordnetservice.util.ListUtil;
import nordnetservice.util.StockOptionUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vega.financial.StockOptionType;
import vega.financial.calculator.OptionCalculator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;


@Component("v1")
public class NordnetAdapter extends NordnetAdapterBase {
    private static final int SP_CLS = 4;
    private static final int SP_HI = 7;
    private static final int SP_LO = 8;
    private static final int X = 7;

    private static final int CALL_TICKER = 1;
    private static final int CALL_BID = 3;
    private static final int CALL_ASK = 4;
    private static final int PUT_BID = 9;
    private static final int PUT_ASK = 10;
    private static final int PUT_TICKER = 13;
    private final RedisAdapter redisAdapter;
    private final LocalDate curDate;
    private final OptionCalculator blackScholes;
    private final OptionCalculator binomialTree;

    public NordnetAdapter(Downloader<PageInfo> downloaderAdapter,
                          RedisAdapter redisAdapter,
                          @Qualifier("blackScholes") OptionCalculator blackScholes,
                          @Qualifier("binomialTree") OptionCalculator binomialTree,
                          @Value("${curdate}") String curDateStr,
                          @Value("${cache.options.expiry}") int optionsExpiry,
                          @Value("${cache.option.expiry}") int optionExpiry) {
        super(downloaderAdapter, optionsExpiry, optionExpiry);
        this.redisAdapter = redisAdapter;
        this.blackScholes = blackScholes;
        this.binomialTree = binomialTree;
        if (curDateStr == null || curDateStr.equals("today")) {
            curDate = LocalDate.now();
        }
        else {
            curDate = LocalDate.parse(curDateStr);
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
        return  new StockOption(ticker, ot, x, bid, ask, isoAndDays.second(), isoAndDays.first(), stockPrice, blackScholes);
    }

    @Override
    protected StockOption parsePut(StockPrice stockPrice, Element el) {
        return parseOption(PUT_TICKER, PUT_BID, PUT_ASK, StockOptionType.PUT, stockPrice, el);
    }

    @Override
    protected StockOption parseCall(StockPrice stockPrice, Element el) {
        return parseOption(CALL_TICKER, CALL_BID, CALL_ASK, StockOptionType.CALL, stockPrice, el);
    }

    private List<StockOption> parseOptions(StockPrice sp, Element el) {
        var callFn = new StockOptionCreator(StockOptionType.CALL, sp, this);
        var putFn = new StockOptionCreator(StockOptionType.PUT, sp, this);
        var rows = el.children();
        //var rows2 = rows.get(0).children();
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

    @Override
    protected Tuple2<StockPrice,List<StockOption>> parse(StockTicker ticker, PageInfo page) {
        var soup = Jsoup.parse(page.body());
        var roleTable = soup.select("[role=table]");
        var sp = parseStockPrice(ticker, roleTable.get(0));
        var options = parseOptions(sp, roleTable.get(1));
        return new Tuple2<>(sp, options);
    }

    @Override
    protected List<StockOption> parsePage(StockPrice stockPrice, PageInfo page) {
        var soup = Jsoup.parse(page.body());
        var roleTable = soup.select("[role=table]");
        return parseOptions(stockPrice, roleTable.get(1));
    }

    private List<StockOption> getOptions(StockTicker ticker, StockOptionType ot) {
        var result = parse(ticker);
        return result.second().stream().filter(x -> x.getOpType() == ot).toList();
    }

    //--------------------------- NordnetRepository BEGIN ---------------------------
    @Override
    public List<StockOption> getCalls(StockTicker ticker) {
        return getOptions(ticker, StockOptionType.CALL);
    }

    @Override
    public List<StockOption> getPuts(StockTicker ticker) {
        return getOptions(ticker, StockOptionType.PUT);
    }

    @Override
    public StockPrice getStockPrice(StockTicker ticker) {
        var result = parse(ticker);
        return result.first();
    }

    //--------------------------- NordnetRepository END ---------------------------

}
