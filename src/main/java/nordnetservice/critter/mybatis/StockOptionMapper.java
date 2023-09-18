package nordnetservice.critter.mybatis;

import nordnetservice.critter.stock.Stock;
import nordnetservice.critter.stockoption.StockOption;
import nordnetservice.critter.stockoption.StockOptionPrice;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;


public interface StockOptionMapper {

    //void insertSpot(StockPrice s);

    void insertStockOptionPrice(StockOptionPrice d);

    void insertStockOption(StockOption d);

    int countStockOption(String ticker);

    //int countIvForSpot(StockPrice s);

    //int countOpxPricesForSpot(StockPrice s);

    //Integer findSpotId(StockPrice s);

    //Integer findStockOptionId(String ticker);

    StockOption findStockOption(String ticker);

    /*
    List<SpotOptionPrice> spotsOprices();
    List<SpotOptionPrice> spotsOpricesOpxId(@Param("opxId") int opxId);
    List<SpotOptionPrice> spotsOpricesStockId(@Param("stockId") int stockId,
                                                  @Param("fromDx") Date fromDx,
                                                  @Param("toDx") Date toDx);
    List<SpotOptionPrice> spotsOpricesStockIds(@Param("stockIds") List<Integer> stockIds,
                                              @Param("fromDx") Date fromDx,
                                              @Param("toDx") Date toDx);
    List<SpotOptionPrice> spotsOpricesStockTix(@Param("stockTix") List<String> stockTix,
                                               @Param("fromDx") Date fromDx,
                                               @Param("toDx") Date toDx);

    List<SpotOptionPriceSummaryBean> spotOptionPriceSummary();

    void insertBlackScholes(@Param("oid") int oid,
                            @Param("ivBuy") double ivBuy,
                            @Param("ivSell") double ivSell);
     */

    Stock calls(@Param("tickerId") int tickerId,
                @Param("fromDx") Date fromDx);

    Stock puts(@Param("tickerId") int tickerId,
               @Param("fromDx") Date fromDx);

}
