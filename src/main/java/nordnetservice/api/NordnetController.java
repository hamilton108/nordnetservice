package nordnetservice.api;

import nordnetservice.domain.core.Core;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/api")
public class NordnetController {

    private final Core core;

    public NordnetController(Core core) {
        this.core = core;
    }

    @ResponseBody
    @GetMapping(value = "/stockoption/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String findOption(@PathVariable("ticker") String ticker) {
        return "";
    }

}
