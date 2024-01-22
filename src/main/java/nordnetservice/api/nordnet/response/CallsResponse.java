package nordnetservice.api.nordnet.response;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.List;

public record CallsResponse(@JsonGetter("stockprice") StockPriceDTO price,
                            @JsonGetter("opx") List<StockOptionDTO> options) {
}
