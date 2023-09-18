package nordnetservice.util;

import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOptionInfo;
import oahu.financial.StockOptionTicker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static nordnetservice.domain.stockoption.StockOptionInfo.StatusEnum.OK;
import static vega.financial.StockOptionType.CALL;
import static vega.financial.StockOptionType.PUT;

public class StockOptionUtilTest {

    @Test
    void test_nordnet_millis() {
        var testDate = LocalDate.of(2023, 3, 30);
        assertEquals(1680127200000L, StockOptionUtil.nordnetMillis(testDate));
    }

    @ParameterizedTest
    @MethodSource("stockOptionInfoSource")
    void test_stockoption_info(StockOptionTicker ticker, StockOptionInfo info) {
        var actual = StockOptionUtil.stockOptionInfoFromTicker(ticker);
        assertEquals(info.getStatus(), actual.getStatus());
        assertEquals(info.getStockOptionTicker().getValue(), actual.getStockOptionTicker().getValue());
        assertEquals(info.getStockTicker().ticker(), actual.getStockTicker().ticker());
        assertEquals(info.getYear(), actual.getYear());
        assertEquals(info.getMonth(), actual.getMonth());
        assertEquals(info.getStockOptionType(), actual.getStockOptionType());
    }

    private static Stream<Arguments> stockOptionInfoSource() {
        return Stream.of(
                Arguments.of("EQNR3C428.99Z",
                        new StockOptionInfo(OK, new StockTicker("EQNR"), new StockOptionTicker("EQNR3C428.99Z"),2023, 3, CALL))
        );
    }

}

//     {:t o :r {:ticker "EQNR", :option o :oid 2, :year 2023, :month 3, :ot :call}})
