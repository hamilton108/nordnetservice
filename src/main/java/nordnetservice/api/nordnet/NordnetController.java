package nordnetservice.api.nordnet;

import nordnetservice.api.nordnet.response.CallsResponse;
import nordnetservice.api.nordnet.response.StockOptionDTO;
import nordnetservice.api.nordnet.response.StockPriceDTO;
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
import java.util.function.Function;


@Controller
@RequestMapping("/nordnet")
public class NordnetController {

    private final Core core;

    public NordnetController(Core core) {
        this.core = core;
    }

    @ResponseBody
    @GetMapping(value = "/spot/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StockPriceDTO spot(@PathVariable("oid") int oid) {
        var ticker = new StockTicker(oid);
        StockPrice price = core.getStockPrice(ticker);
        return new StockPriceDTO(price);
    }

    @ResponseBody
    @GetMapping(value = "/calls/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CallsResponse calls(@PathVariable("oid") int oid) {
        return getOptions(oid, core::getCalls);
    }

    @ResponseBody
    @GetMapping(value = "/puts/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CallsResponse puts(@PathVariable("oid") int oid) {
        return getOptions(oid, core::getPuts);
    }

    private CallsResponse getOptions(int oid, Function<StockTicker,List<StockOption>> fn) {
        var ticker = new StockTicker(oid);
        var options = fn.apply(ticker);
        StockPrice price = core.getStockPrice(ticker);
        return new CallsResponse(new StockPriceDTO(price), map(options));
    }

    private List<StockOptionDTO> map(List<StockOption> options) {
        return options.stream().map(StockOptionDTO::new).toList();
    }
}
