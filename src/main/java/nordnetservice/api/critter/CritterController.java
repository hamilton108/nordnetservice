package nordnetservice.api.critter;

import nordnetservice.api.critter.reponse.OptionPurchaseDTO;
import nordnetservice.api.response.PayloadResponse;
import nordnetservice.api.util.ApiUtil;
import nordnetservice.domain.core.Core;
import nordnetservice.domain.stockoption.PurchaseType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@Controller
@RequestMapping("/critter")
public class CritterController {

    private final Core core;

    public CritterController (Core core) {
        this.core = core;
    }


    @ResponseBody
    @GetMapping(value = "/purchase/{purchasetype}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PayloadResponse<List<OptionPurchaseDTO>>> fetchCritters(@PathVariable("purchasetype") int purchaseType) {
        var purchases = core.fetchCritters(PurchaseType.fromInt(purchaseType));
        return ApiUtil.mapWithErrFn(purchases, p -> purchases.getRight().stream().map(OptionPurchaseDTO::new).toList(), Collections.emptyList());
    }

}
