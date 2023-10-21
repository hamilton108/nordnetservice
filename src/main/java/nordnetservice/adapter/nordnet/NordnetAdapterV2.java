package nordnetservice.adapter.nordnet;

import nordnetservice.adapter.RedisAdapter;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.dto.Tuple2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vega.financial.StockOptionType;
import vega.financial.calculator.OptionCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//public class NordnetAdapterV2 extends NordnetAdapterBase implements NordnetRepository  {

@Component("v2")
public class NordnetAdapterV2 extends NordnetAdapterBase {

    public NordnetAdapterV2(Downloader<PageInfo> downloaderAdapter,
                          RedisAdapter redisAdapter,
                          @Qualifier("blackScholes") OptionCalculator blackScholes,
                          @Qualifier("binomialTree") OptionCalculator binomialTree,
                          @Value("${curdate}") String curDateStr,
                          @Value("${cache.options.expiry}") int optionsExpiry,
                          @Value("${cache.option.expiry}") int optionExpiry) {
        super(downloaderAdapter, optionsExpiry, optionExpiry);
    }

    @Override
    protected Tuple2<StockPrice, List<StockOption>> parse(StockTicker ticker, PageInfo page) {
        var soup = Jsoup.parse(page.body());
        var roleTable = soup.select("[role=table]");
        var options = parseOptions(null, roleTable.get(1));
        return new Tuple2<>(null, options);
    }

    private List<StockOption> parseOptions(StockPrice sp, Element el) {
        var result = new ArrayList<StockOption>();
        var rows = el.children();
        for (var row : rows) {
            var opt = parseOption(StockOptionType.CALL, sp, row);
            if (opt != null) {
                result.add(opt);
            }
        }
        return result;
    }

    @Override
    protected List<StockOption> parsePage(StockPrice stockPrice, PageInfo page) {
        return null;
    }

    private StockOption parseOption(StockOptionType ot,
                                    StockPrice stockPrice,
                                    Element el) {
        var ch = el.children();
        var ticker = el2ticker(ch.get(1));
        if (ticker == null)  {
            return null;
        }
        return  new StockOption(ticker, ot, 0, 0, 0, 0L, "", stockPrice, null);
    }

    @Override
    protected StockOption parseCall(StockPrice stockPrice, Element el) {
        return parseOption(StockOptionType.CALL, stockPrice, el);
    }

    @Override
    protected StockOption parsePut(StockPrice stockPrice, Element el) {
        return null;
    }

    //--------------------------- NordnetRepository BEGIN ---------------------------
    @Override
    public List<StockOption> getCalls(StockTicker ticker) {
        var result = parse(ticker);
        return result.second().stream().filter(x -> x.getOpType() == StockOptionType.CALL).toList();
    }

    @Override
    public List<StockOption> getPuts(StockTicker ticker) {
        return null;
    }

    @Override
    public StockPrice getStockPrice(StockTicker ticker) {
        return null;
    }
    //--------------------------- NordnetRepository END ---------------------------
}
