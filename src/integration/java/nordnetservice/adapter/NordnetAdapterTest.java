package nordnetservice.adapter;

import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@ActiveProfiles("integration")

@SpringBootTest
class NordnetAdapterTest {

    private final StockTicker stockTicker = new StockTicker("YAR");

    @Autowired
    NordnetAdapter nordnetAdapter;


    @Test
    void test_parse() {
        var stockPrice = nordnetAdapter.getStockPrice(stockTicker);
        assertEquals(403.0, stockPrice.opn(), 0.1);
        assertEquals(409.9, stockPrice.hi(), 0.1);
        assertEquals(402.3, stockPrice.lo(), 0.1);
        assertEquals(409.9, stockPrice.cls(), 0.1);
        var calls = nordnetAdapter.getCalls(stockTicker);
        assertEquals(27, calls.size());
        var puts = nordnetAdapter.getPuts(stockTicker);
        assertEquals(27, puts.size());
        checkStockOption("YAR4C540", 0.55, 1.6, 540, calls);
        checkStockOption("YAR4C500", 2.2, 4.6, 500, calls);
        checkStockOption("YAR4C340", 77.75, 83.25, 340, calls);

        checkStockOption("YAR4O780", 367.25, 373.25, 780, puts);
        checkStockOption("YAR4O520", 107.5, 113.5, 520, puts);
        checkStockOption("YAR4O320", 1.25, 2.05, 320, puts);
    }

    private void checkStockOption(String tickerS,
                                     double bid,
                                     double ask,
                                     double x,
                                     List<StockOption> options) {

        var opt = options.stream().filter(s -> s.getTicker().value().equals(tickerS)).findFirst();
        assertFalse(opt.isEmpty());
        var o = opt.get();
        assertEquals(bid, o.getBid(), 0.01);
        assertEquals(ask, o.getAsk(), 0.01);
        assertEquals(x, o.getX(), 0.001);
    }
}