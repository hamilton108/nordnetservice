package nordnetservice.api.rapanui.response;

import vega.financial.StockOptionType;

public record FindOptionResponse(FindOptionItem option, int status, String msg) {
}
