package nordnetservice.critter.mybatis;

import nordnetservice.critter.stock.Stock;
import nordnetservice.critter.stock.StockPrice;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;
import java.util.Map;


public interface StockMapper {
    void insertStockPrice(StockPrice bean);

    List<Map<Integer, Date>> selectMaxDate();

    List<Stock> selectStocks();

    List<StockPrice> selectStockPrices(@Param("tickerId") int tickerId,
                                  @Param("fromDx") Date fromDx);

}
