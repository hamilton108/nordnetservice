package nordnetservice.api.nordnet;

import nordnetservice.api.nordnet.response.CallsResponse;
import nordnetservice.domain.core.Core;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOption;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


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
        List<StockOption> options = core.getCalls(ticker);
        StockPrice price = core.getStockPrice(ticker);
        return null;
    }

}
