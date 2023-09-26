package nordnetservice.util;

import nordnetservice.domain.stockoption.StockOptionTicker;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class NordnetUtilTest {


    @Test
    void test_url_for_stock_ticker() throws MalformedURLException {
        var ticker = new StockOptionTicker("YAR3X380");
        var info = StockOptionUtil.stockOptionInfoFromTicker(ticker);
        var actual = NordnetUtil.urlFor(info.getStockTicker(), info.getNordnetMillis());
        var expected = new URL("https", "www.nordnet.no", "/market/options?currency=NOK&underlyingSymbol=YAR&expireDate=1702594800000");
        assertEquals(expected, actual);
    }
    @Test
    void test_url_for_stockoption_ticker() throws MalformedURLException {
        var ticker = new StockOptionTicker("YAR3X380");
        var actual = NordnetUtil.urlFor(ticker);
        var expected = new URL("https", "www.nordnet.no", "/market/options?currency=NOK&underlyingSymbol=YAR&expireDate=1702594800000");
        assertEquals(expected, actual);
    }
}