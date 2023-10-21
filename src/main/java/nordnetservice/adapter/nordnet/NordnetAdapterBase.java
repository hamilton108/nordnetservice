package nordnetservice.adapter.nordnet;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.repository.NordnetRepository;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionInfo;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.Tuple2;
import nordnetservice.util.ListUtil;
import nordnetservice.util.StockOptionUtil;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

public abstract class NordnetAdapterBase implements NordnetRepository {

    protected final Downloader<PageInfo> downloader;
    protected final Cache<Integer, Tuple2<StockPrice, List<StockOption>>> cacheStockOptions;
    protected final Cache<String, Tuple2<StockPrice,List<StockOption>>> cacheStockOption;
    private final Pattern pat = Pattern.compile("Norway\\s*(\\S*)");

    public NordnetAdapterBase(Downloader<PageInfo> downloader, int optionsExpiry, int optionExpiry) {
        this.downloader = downloader;
        cacheStockOptions = Caffeine.newBuilder().expireAfterWrite(optionsExpiry, TimeUnit.MINUTES).build();
        cacheStockOption = Caffeine.newBuilder().expireAfterWrite(optionExpiry, TimeUnit.SECONDS).build();
    }

    //--------------------------- NordnetRepository BEGIN ---------------------------
    @Override
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

    }
    //--------------------------- NordnetRepository END ---------------------------

    protected String keyFor(StockOptionInfo info) {
        return String.format("%d:%d", info.getStockTicker().oid(), info.getNordnetMillis());
    }

    protected String elementText(Element el) {
        return el.children().get(0).text();
    }
    protected StockOptionTicker el2ticker(Element el) {
        var m = pat.matcher(elementText(el));
        if (m.matches()) {
            return new StockOptionTicker(m.group(1));
        }
        else {
            return null;
        }
    }
    protected double el2double(Element el) {
        var tx = elementText(el);
        var digits = tx.split(",");
        if (digits.length == 2) {
            return Double.parseDouble(String.format("%s.%s", digits[0], digits[1]));
        } else {
            return -1.0;
        }
    }


    protected Tuple2<StockPrice,List<StockOption>> parse(StockTicker ticker) {

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


    protected abstract Tuple2<StockPrice,List<StockOption>> parse(StockTicker ticker, PageInfo page);
    protected abstract List<StockOption> parsePage(StockPrice stockPrice, PageInfo page);

    protected record ParsePageClosure(StockPrice stockPrice,
                            NordnetAdapterBase adapter) implements Function<PageInfo, List<StockOption>> {
        @Override
        public List<StockOption> apply(PageInfo page) {
            return adapter.parsePage(stockPrice, page);
        }
    }
}
