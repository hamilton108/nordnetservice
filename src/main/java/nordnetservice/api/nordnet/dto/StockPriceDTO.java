package nordnetservice.api.nordnet.dto;

import nordnetservice.domain.stock.StockPrice;

public class StockPriceDTO {

    private final StockPrice stockPrice;

    public StockPriceDTO(StockPrice stockPrice) {
        this.stockPrice = stockPrice;
    }
}
