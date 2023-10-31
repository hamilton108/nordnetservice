package nordnetservice.domain.stock;

import nordnetservice.domain.stockoption.StockOptionTicker;

public record OpeningPrice(StockTicker ticker, double price) {
}
