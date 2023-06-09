package nordnetservice.adapter;

import oahu.financial.StockTicker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class RedisAdapterTest {

    @Autowired
    private RedisAdapter redisAdapter;

    @Test
    public void test_opening_price() {
        var result = redisAdapter.openingPrice(new StockTicker("YAR"));
        assertEquals(452.0, result, 0.01);
    }
}
