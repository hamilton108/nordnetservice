package nordnetservice.critter.repos;

import nordnetservice.critter.stock.Stock;
import nordnetservice.critter.stockoption.StockOption;
import nordnetservice.critter.stockoption.StockOptionPurchase;
import vega.financial.StockOptionType;

import java.util.List;
import java.util.Optional;

public interface StockMarketRepository<T,T2> {
    Stock findStock(T stockInfo);
    Optional<StockOption> findStockOption(T2 stockOptInfo);
    List<StockOptionPurchase> activePurchasesWithCritters(int purchaseType);
    List<StockOptionPurchase> purchasesWithSalesAll(int purchaseType, int status, StockOptionType ot);
}
