package nordnetservice.adapter;

import critter.mybatis.CritterMapper;
import critter.stockoption.StockOptionPurchase;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CritterAdapter {

    private final SqlSession session;

    public CritterAdapter(SqlSession session) {
        this.session = session;
    }

    public List<StockOptionPurchase> fetchCritters(int purchaseType) {
        var mapper = session.getMapper(CritterMapper.class);
        var result = mapper.activePurchasesWithCritters(purchaseType);
        return result;
    }

}
