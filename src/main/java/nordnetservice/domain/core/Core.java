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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Core {
    private final NordnetRepository nordnetRepository;
    private final CritterAdapter critterAdapter;
    private final RedisAdapter redisAdapter;

    public Core(NordnetRepository nordnetRepository,
                CritterAdapter critterAdapter,
                RedisAdapter redisAdapter) {
        this.nordnetRepository = nordnetRepository;
        this.critterAdapter = critterAdapter;
        this.redisAdapter = redisAdapter;
    }

    public Tuple2<StockPrice, StockOption> findOption(StockOptionTicker ticker) {
        return nordnetRepository.findOption(ticker);
    }

    public List<StockOptionPurchase> fetchCritters(PurchaseType purchaseType) {
        return critterAdapter.fetchCritters(purchaseType);
    }

    public List<StockOption> getCalls(StockTicker ticker) {
        return nordnetRepository.getCalls(ticker);
    }

    public List<StockOption> getPuts(StockTicker ticker) {
        return nordnetRepository.getPuts(ticker);
    }
    public StockPrice getStockPrice(StockTicker ticker) {
        return nordnetRepository.getStockPrice(ticker);
    }
    public OpeningPrice openingPrice(StockTicker ticker) {
        return nordnetRepository.openingPrice(ticker);
    }

    public List<Long> thirdFridayMillis(List<YearMonthDTO> items) {
        return items.stream().map(NordnetUtil::calcUnixTimeForThirdFriday).toList();
    }
}
