package nordnetservice.adapter.nordnet;

import nordnetservice.domain.repository.NordnetRepository;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.Tuple2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("v2")
public class NordnetAdapterV2 implements NordnetRepository  {

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
}
