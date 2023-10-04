package nordnetservice.api.critter;

import nordnetservice.api.critter.reponse.OptionPurchaseDTO;
import nordnetservice.domain.core.Core;
import nordnetservice.domain.stockoption.PurchaseType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/critter")
public class CritterController {

    private final Core core;

    public CritterController (Core core) {
        this.core = core;
    }

    @ResponseBody
    @GetMapping(value = "/purchase/{purchasetype}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OptionPurchaseDTO> fetchCritters(@PathVariable("purchasetype") int purchaseType) {
        var purchases = core.fetchCritters(PurchaseType.fromInt(purchaseType));
        if (purchases == null) {
            //logger.warn(String.format("Empty list for critters, purchaseType=%d", purchaseType));
            return Collections.emptyList();
        }
        return purchases.stream().map(OptionPurchaseDTO::new).toList();
    }

}
