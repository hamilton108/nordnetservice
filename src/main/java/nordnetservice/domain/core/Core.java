package nordnetservice.domain.core;

import nordnetservice.adapter.CritterAdapter;
import nordnetservice.adapter.RedisAdapter;
import nordnetservice.critter.stockoption.StockOptionPurchase;
import nordnetservice.domain.repository.NordnetRepository;
import nordnetservice.domain.stock.OpeningPrice;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.PurchaseType;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.Tuple2;
import nordnetservice.dto.YearMonthDTO;
import nordnetservice.util.NordnetUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Core {
    private final NordnetRepository nordnetRepositoryV1;
    private final NordnetRepository nordnetRepositoryV2;
    private final CritterAdapter critterAdapter;
    private final RedisAdapter redisAdapter;

    public Core(@Qualifier("v1") NordnetRepository nordnetRepositoryV1,
                @Qualifier("v2") NordnetRepository nordnetRepositoryV2,
                CritterAdapter critterAdapter,
                RedisAdapter redisAdapter) {
        this.nordnetRepositoryV1 = nordnetRepositoryV1;
        this.nordnetRepositoryV2 = nordnetRepositoryV2;
        this.critterAdapter = critterAdapter;
        this.redisAdapter = redisAdapter;
    }

    public Tuple2<StockPrice, StockOption> findOption(StockOptionTicker ticker) {
        return nordnetRepositoryV1.findOption(ticker);
    }

    public List<StockOptionPurchase> fetchCritters(PurchaseType purchaseType) {
        return critterAdapter.fetchCritters(purchaseType);
    }

    public List<StockOption> getCalls(StockTicker ticker) {
        return nordnetRepositoryV1.getCalls(ticker);
    }

    public List<StockOption> getPuts(StockTicker ticker) {
        return nordnetRepositoryV1.getPuts(ticker);
    }
    public StockPrice getStockPrice(StockTicker ticker) {
        return nordnetRepositoryV1.getStockPrice(ticker);
    }
    public OpeningPrice openingPrice(StockTicker ticker) {
        return nordnetRepositoryV1.openingPrice(ticker);
    }

    public void thirdFridayMillis(List<YearMonthDTO> items) {
        var millis = items.stream().map(NordnetUtil::calcUnixTimeForThirdFriday).toList();
        redisAdapter.updateNordnetMillis(millis);
    }
}
