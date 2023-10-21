package nordnetservice.adapter;

import nordnetservice.adapter.downloader.DemoDownloaderAdapter;
import nordnetservice.adapter.nordnet.NordnetAdapterV2;
import nordnetservice.domain.stock.StockTicker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class NordnetAdapterV2Test {

    private final StockTicker stockTicker = new StockTicker("YAR");
    @Autowired
    DemoDownloaderAdapter downloaderAdapter;
    @Autowired
    NordnetAdapterV2 nordnetAdapter;

    @Test
    void test_parse() {
        downloaderAdapter.setV1(false);
        var calls = nordnetAdapter.getCalls(stockTicker);
        assertEquals(27, calls.size());
    }

}
