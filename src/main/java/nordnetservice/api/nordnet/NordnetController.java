package nordnetservice.api.nordnet;

import nordnetservice.api.nordnet.response.FindOptionResponse;
import nordnetservice.domain.core.Core;
import nordnetservice.domain.stockoption.StockOptionTicker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/nordnet")
public class NordnetController {

    private final Core core;

    public NordnetController(Core core) {
        this.core = core;
    }

    @ResponseBody
    @GetMapping(value = "/option/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    public FindOptionResponse findOption(@PathVariable("ticker") String ticker) {
        var result = core.findOption(new StockOptionTicker(ticker));
        return null;
    }

}
