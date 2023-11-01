package nordnetservice.api.nordnet.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import nordnetservice.domain.stock.OpeningPrice;

public class OpeningPriceDTO {
    private final OpeningPrice price;

    public OpeningPriceDTO(OpeningPrice price) {
        this.price = price;
    }

    /*
    @JsonGetter("stockticker")
    String getTicker() {
        return price.ticker().ticker();
    }
     */

    @JsonGetter("oid")
    int getOid() {
        return price.ticker().oid();
    }

    @JsonGetter("price")
    double getPrice() {
        return price.price();
    }
}
