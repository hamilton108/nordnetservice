package nordnetservice.api.rapanui;

import nordnetservice.api.rapanui.response.FindOptionItem;
import nordnetservice.api.rapanui.response.FindOptionResponse;
import nordnetservice.domain.core.Core;
import nordnetservice.domain.stockoption.StockOptionTicker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vega.financial.StockOptionType;

@Controller
@RequestMapping("/rapanui")
public class RapanuiController {

    private final Core core;

    public RapanuiController(Core core) {
        this.core = core;
    }

    @ResponseBody
    @GetMapping(value = "/option/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    public FindOptionResponse findOption(@PathVariable("ticker") String ticker) {
        var result = core.findOption(new StockOptionTicker(ticker));

        var opt = result.second();
        return new FindOptionResponse(new FindOptionItem(opt.getBid(),
                opt.getAsk()),
                opt.getOpType().getIndex(),
                opt.getOpType().getValue());
    }
}
