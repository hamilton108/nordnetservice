package nordnetservice.domain.core;

import nordnetservice.adapter.CritterAdapter;
import nordnetservice.adapter.NordnetAdapter;
import nordnetservice.adapter.RedisAdapter;
import nordnetservice.critter.stockoption.StockOptionPurchase;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.PurchaseType;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.Tuple2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Core {
    private final NordnetAdapter nordnetAdapter;
    private final CritterAdapter critterAdapter;
    private final RedisAdapter redisAdapter;

    public Core(NordnetAdapter nordnetAdapter,
                CritterAdapter critterAdapter,
                RedisAdapter redisAdapter) {
        this.nordnetAdapter = nordnetAdapter;
        this.critterAdapter = critterAdapter;
        this.redisAdapter = redisAdapter;
    }

    public Tuple2<StockPrice, StockOption> findOption(StockOptionTicker ticker) {
        return nordnetAdapter.findOption(ticker);
    }

    public List<StockOptionPurchase> fetchCritters(PurchaseType purchaseType) {
        return critterAdapter.fetchCritters(purchaseType);
    }

    public List<StockOption> getCalls(StockTicker ticker) {
        return null;

    }

    public StockPrice getStockPrice(StockTicker ticker) {
        return null;
    }
}
