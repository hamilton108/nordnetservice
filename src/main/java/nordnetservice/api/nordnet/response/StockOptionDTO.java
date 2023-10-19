package nordnetservice.api.nordnet.response;

import nordnetservice.domain.stockoption.StockOption;

public class StockOptionDTO {
    private final StockOption stockOption;
    public StockOptionDTO(StockOption stockOption) {
        this.stockOption = stockOption;
    }

    public String getTicker() {
        return stockOption.getTicker().value();
    }
    public double getX() {
        return stockOption.getX();
    }
    public double getBuy() {
        return stockOption.getBid();
    }
    public double getSell() {
        return stockOption.getAsk();
    }
    public long getDays() {
        return stockOption.getDays();
    }
    public double getIvBuy() {
        return stockOption.getIvBid();
    }
    public double getIvSell() {
        return stockOption.getIvAsk();
    }
    public double getBrEven() {
        return stockOption.getBreakEven();
    }
    public String getExpiry() {
        return stockOption.getExpiry();
    }
}
