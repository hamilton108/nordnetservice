package nordnetservice.adapter.nordnet;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionInfo;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.Tuple2;
import org.jsoup.nodes.Element;
import vega.financial.StockOptionType;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public abstract class NordnetAdapterBase {

    protected final Downloader<PageInfo> downloader;
    protected final Cache<Integer, Tuple2<StockPrice, List<StockOption>>> cacheStockOptions;
    protected final Cache<String, Tuple2<StockPrice,List<StockOption>>> cacheStockOption;
    private final Pattern pat = Pattern.compile("Norway\\s*(\\S*)");

    public NordnetAdapterBase(Downloader<PageInfo> downloader, int optionsExpiry, int optionExpiry) {
        this.downloader = downloader;
        cacheStockOptions = Caffeine.newBuilder().expireAfterWrite(optionsExpiry, TimeUnit.MINUTES).build();
        cacheStockOption = Caffeine.newBuilder().expireAfterWrite(optionExpiry, TimeUnit.SECONDS).build();
    }

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
}
