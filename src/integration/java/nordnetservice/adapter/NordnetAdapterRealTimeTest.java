package nordnetservice.adapter;

import nordnetservice.adapter.downloader.DefaultDownloaderAdapter;
import nordnetservice.adapter.nordnet.NordnetAdapter;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vega.financial.calculator.BlackScholes;
import vega.financial.calculator.OptionCalculator;
import vega.financial.calculator.binomialtree.BinomialTreeCalculator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NordnetAdapterRealTimeTest {

    private final StockTicker stockTicker = new StockTicker("YAR");
    //private final StockOptionTicker stockOptionTicker = new StockOptionTicker("YAR");

    @Autowired
    RedisAdapter redisAdapter;

    Downloader<PageInfo> downloader;

    NordnetAdapter nordnetAdapter;

    @BeforeEach
    void init() {
        downloader = new DefaultDownloaderAdapter(redisAdapter);
        OptionCalculator blackScholes = new BlackScholes();
        OptionCalculator binomialTree = new BinomialTreeCalculator();

        nordnetAdapter =
                new NordnetAdapter(downloader,
                        redisAdapter,
                        blackScholes,
                        binomialTree,
                        "today",
                        30,
                        600);

    }

    @Test
    void test_parse_real_time() {
        var calls = nordnetAdapter.getCalls(stockTicker);
        checkCalls(calls);
        var stockPrice = nordnetAdapter.getStockPrice(stockTicker);
        checkStockPrice(stockPrice);
    }

    private void checkStockPrice(StockPrice stockPrice) {
        assertNotNull(stockPrice);
        assertTrue(stockPrice.opn() > 0);
        assertTrue(stockPrice.hi() > 0);
        assertTrue(stockPrice.lo() > 0);
        assertTrue(stockPrice.cls() > 0);
    }
    private void checkCalls(List<StockOption> calls) {
        assertNotNull(calls);
        assertFalse(calls.isEmpty());
    }
}
