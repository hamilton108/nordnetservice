package nordnetservice.api.nordnet.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import nordnetservice.api.nordnet.dto.StockOptionDTO;
import nordnetservice.api.nordnet.dto.StockPriceDTO;

public record FindOptionResponse(@JsonGetter("option") StockOptionDTO stockOption,
                                 @JsonGetter("stockprice") StockPriceDTO stockPrice) {
}
