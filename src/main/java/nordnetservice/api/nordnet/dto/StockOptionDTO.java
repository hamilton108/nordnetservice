package nordnetservice.api.nordnet.dto;

import nordnetservice.domain.stockoption.StockOption;

public class StockOptionDTO {

    private final StockOption stockOption;

    public StockOptionDTO(StockOption stockOption) {
        this.stockOption = stockOption;
    }
}
