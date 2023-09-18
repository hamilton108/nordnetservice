package nordnetservice.adapter;


import oahu.financial.StockTicker;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisAdapter {

    private final RedisTemplate<String,Object> redisTemplate;

    public RedisAdapter(RedisTemplate<String,Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public double openingPrice(StockTicker ticker) {
        var result = (String)redisTemplate.opsForHash().get("openingprices", ticker.getValue());
        return Double.parseDouble(result);
    }

    public String fetchValue(String key) {
        return (String)redisTemplate.opsForValue().get(key);
    }
}

