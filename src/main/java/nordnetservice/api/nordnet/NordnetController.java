package nordnetservice.api.nordnet;

import nordnetservice.api.nordnet.response.CallsResponse;
import nordnetservice.domain.core.Core;
import nordnetservice.domain.stock.StockTicker;
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
    @GetMapping(value = "/calls/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CallsResponse calls(@PathVariable("oid") int oid) {
        var ticker = new StockTicker(oid);
        return null;
    }

}
