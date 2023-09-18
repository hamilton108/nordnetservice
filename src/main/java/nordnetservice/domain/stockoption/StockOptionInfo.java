package nordnetservice.domain.stockoption;

import nordnetservice.domain.stock.StockTicker;
import oahu.financial.StockOptionTicker;
import vega.financial.StockOptionType;

public class StockOptionInfo {

    public StatusEnum getStatus() {
        return status;
    }

    public StockTicker getStockTicker() {
        return stockTicker;
    }

    public StockOptionTicker getStockOptionTicker() {
        return stockOptionTicker;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public StockOptionType getStockOptionType() {
        return stockOptionType;
    }

    public enum StatusEnum { ERR, OK };

    private final StatusEnum status;
    private final StockTicker stockTicker;
    private final StockOptionTicker stockOptionTicker;
    private final int year;
    private final int month;
    private final StockOptionType stockOptionType;

    public StockOptionInfo(StatusEnum status) {
        this(status, null, null, 0, 0, null);
    }

    public StockOptionInfo(StatusEnum status,
                           StockTicker stockTicker,
                           StockOptionTicker stockOptionTicker,
                           int year,
                           int month,
                           StockOptionType stockOptionType) {
        this.status = status;
        this.stockTicker = stockTicker;
        this.stockOptionTicker = stockOptionTicker;
        this.year = year;
        this.month = month;
        this.stockOptionType = stockOptionType;
    }
    public static StockOptionInfo err() {
        return new StockOptionInfo(StatusEnum.ERR);
    }
}
