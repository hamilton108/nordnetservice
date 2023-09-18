package nordnetservice.critter.mybatis;


import nordnetservice.critter.stockoption.StockOptionPurchase;
import nordnetservice.critter.stockoption.StockOptionSale;
import nordnetservice.critter.critterrule.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CritterMapper {
    void toggleAcceptRule(@Param("oid") int oid, @Param("isActive") String isActive);
    void toggleDenyRule(@Param("oid") int oid, @Param("isActive") String isActive);

    List<StockOptionPurchase> activePurchasesWithCritters(@Param("purchaseType") int purchaseType);

    List<StockOptionPurchase> purchasesWithSales(@Param("stockId") int stockId,
                                                 @Param("purchaseType") int purchaseType,
                                                 @Param("status") int status,
                                                 @Param("optype") String optype);

    List<StockOptionPurchase> purchasesWithSalesAll(
                                                      @Param("purchaseType") int purchaseType,
                                                      @Param("status") int status,
                                                      @Param("optype") String optype);

    StockOptionPurchase findPurchase(@Param("purchaseId") int purchaseId);

    StockOptionPurchase findPurchaseForCritId(@Param("critterId") int critterId);

    StockOptionPurchase findPurchaseForAccId(@Param("accId") int accId);

    void insertPurchase(StockOptionPurchase purchase);

    void insertCritter(Critter critter);

    void insertGradientRule(GradientRule rule);

    void insertAcceptRule(AcceptRule rule);

    void insertDenyRule(DenyRule rule);

    List<RuleType> ruleTypes();

    void registerCritterClosedWithSale(Critter critter);

    void registerPurchaseFullySold(StockOptionPurchase purchase);

    void insertCritterSale(StockOptionSale sale);
}
