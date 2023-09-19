package nordnetservice.adapter;

import nordnetservice.domain.stock.StockTicker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

//@ActiveProfiles("integration")

@SpringBootTest
class NordnetAdapterTest {

    private final StockTicker stockTicker = new StockTicker("YAR");

    @Autowired
    NordnetAdapter nordnetAdapter;


    @Test
    void test_parse() {
        var calls = nordnetAdapter.getCalls(stockTicker);
        assertEquals(27, calls.size());
    }

    /*
    @Test
    void test_demo() {
        var info = nordnetAdapter.demo();
        System.out.println(info.body());
    }

     */
}