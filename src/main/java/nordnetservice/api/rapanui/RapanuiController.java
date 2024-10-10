package nordnetservice.api.rapanui;

import nordnetservice.api.rapanui.response.FindOptionItem;
import nordnetservice.api.rapanui.response.FindOptionResponse;
import nordnetservice.api.response.PayloadResponse;
import nordnetservice.api.util.ApiUtil;
import nordnetservice.domain.core.Core;
import nordnetservice.domain.stockoption.StockOptionTicker;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rapanui")
public class RapanuiController {

    private final Core core;

    public RapanuiController(Core core) {
        this.core = core;
    }

    @ResponseBody
    @GetMapping(value = "/option/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PayloadResponse<FindOptionResponse>> findOption(@PathVariable("ticker") String ticker) {

        var result = core.findOption(new StockOptionTicker(ticker));

        return ApiUtil.mapWithFn(result, r -> {
            var opt = r.second();
            return new FindOptionResponse(new FindOptionItem(opt.getBid(),
                    opt.getAsk()),
                    opt.getOpType().getIndex(),
                    opt.getOpType().getValue());
        });

    }
}
