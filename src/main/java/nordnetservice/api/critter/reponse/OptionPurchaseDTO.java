package nordnetservice.api.critter.reponse;

import nordnetservice.critter.critterrule.Critter;
import nordnetservice.critter.stockoption.StockOptionPurchase;

import java.util.ArrayList;
import java.util.List;

public class OptionPurchaseDTO {
    private final StockOptionPurchase purchase;

    public OptionPurchaseDTO(StockOptionPurchase purchase) {
        this.purchase = purchase;
    }

    public int getOid() {
        return purchase.getOid();
    }

    public String getTicker() {
        return purchase.getOptionName();
    }

    public double getPrice() {
        return purchase.getPrice();
    }

    public List<CritterDTO> getCritters()  {
        List<CritterDTO> result = new ArrayList<>();
        for (Critter critter : purchase.getCritters()) {
            result.add(new CritterDTO(getOid(), critter));
        }
        return result;
    }
}
