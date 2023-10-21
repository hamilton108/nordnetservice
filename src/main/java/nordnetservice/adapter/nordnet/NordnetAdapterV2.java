package nordnetservice.adapter.nordnet;

import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.repository.NordnetRepository;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.Tuple2;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import vega.financial.StockOptionType;

import java.util.List;

@Component("v2")
public class NordnetAdapterV2 extends NordnetAdapterBase implements NordnetRepository  {

    private final Downloader<PageInfo> downloaderAdapter;

    public NordnetAdapterV2(Downloader<PageInfo> downloaderAdapter) {
        super(10, 10);
        this.downloaderAdapter = downloaderAdapter;
    }

    @Override
    public List<StockOption> getCalls(StockTicker ticker) {
        return null;
    }

    @Override
    public List<StockOption> getPuts(StockTicker ticker) {
        return null;
    }

    @Override
    public StockPrice getStockPrice(StockTicker ticker) {
        return null;
    }

    @Override
    public Tuple2<StockPrice, StockOption> findOption(StockOptionTicker ticker) {
        return null;
    }

    private List<StockOption> getOptions(StockTicker ticker, StockOptionType ot) {
        var result = parse(ticker);
        return result.second().stream().filter(x -> x.getOpType() == ot).toList();
    }
    private Tuple2<StockPrice,List<StockOption>> parse(StockTicker ticker) {
        return null;
    }

    @Override
    protected StockOption parseOption(int indexTicker, int indexBid, int indexAsk, StockOptionType ot, StockPrice stockPrice, Element el) {
        return null;
    }
}
