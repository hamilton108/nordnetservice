package nordnetservice.adapter;

import nordnetservice.critter.mybatis.CritterMapper;
import nordnetservice.critter.stockoption.StockOptionPurchase;
import nordnetservice.domain.stockoption.PurchaseType;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CritterAdapter {

    private final SqlSession session;

    public CritterAdapter(SqlSession session) {
        this.session = session;
    }

    public List<StockOptionPurchase> fetchCritters(PurchaseType purchaseType) {
        var mapper = session.getMapper(CritterMapper.class);
        var result = mapper.activePurchasesWithCritters(purchaseType.getValue());
        return result;
    }

}
