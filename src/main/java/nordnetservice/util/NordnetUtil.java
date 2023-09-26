package nordnetservice.util;

import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOptionTicker;

import java.net.MalformedURLException;
import java.net.URL;

public class NordnetUtil {

    public static URL urlFor(StockOptionTicker ticker ) {
        try {
            var info = StockOptionUtil.stockOptionInfoFromTicker(ticker);
            return new URL ("https","www.nordnet.no", pathQueryFor(info.getStockTicker(), info.getNordnetMillis()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL urlFor(StockTicker ticker, long nordnetMillis) {
        try {
            return new URL ("https","www.nordnet.no", pathQueryFor(ticker, nordnetMillis));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String pathQueryFor(StockTicker ticker, long nordnetMillis) {
        return String.format("/market/options?currency=NOK&underlyingSymbol=%s&expireDate=%d",  ticker.ticker(), nordnetMillis);
    }
    /*
   scheme:[//authority]path[?query][#fragment]
     */
}
