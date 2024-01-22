package nordnetservice.util;

import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.YearMonthDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class NordnetUtilTest {
    /*
        2024-02-16: 1708038000000 but got: 1708041600000
        2024-03-15: 1710457200000
        2024-04-19: 1713477600000
        2024-06-21: 1718920800000L
        2024-09-20: 1726783200000L
        2024-12-20: 1734649200000L
     */
    private static Stream<Arguments> yearsAndMonthsSource() {
        return Stream.of(
                Arguments.of(new YearMonthDTO(2024,2), 1708038000000L),
                Arguments.of(new YearMonthDTO(2024,3), 1710457200000L),
                Arguments.of(new YearMonthDTO(2024,4), 1713477600000L),
                Arguments.of(new YearMonthDTO(2024,6), 1718920800000L),
                Arguments.of(new YearMonthDTO(2024,9), 1726783200000L),
                Arguments.of(new YearMonthDTO(2024,12), 1734649200000L)
        );
    }

    @ParameterizedTest
    @MethodSource("yearsAndMonthsSource")
    void test_nordnet_millis_collection_from_years_and_months(YearMonthDTO yearMonth, long expected) {
        var actual = NordnetUtil.calcUnixTimeForThirdFriday(yearMonth);
        assertEquals(expected, actual);
    }

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