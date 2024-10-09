package nordnetservice.domain.core;

import nordnetservice.adapter.CritterAdapter;
import nordnetservice.adapter.RedisAdapter;
import nordnetservice.critter.stockoption.StockOptionPurchase;
import nordnetservice.domain.error.ApplicationError;
import nordnetservice.domain.error.GeneralError;
import nordnetservice.domain.error.SqlError;
import nordnetservice.domain.functional.Either;
import nordnetservice.domain.repository.NordnetRepository;
import nordnetservice.domain.stock.OpeningPrice;
import nordnetservice.domain.stock.StockPrice;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.PurchaseType;
import nordnetservice.domain.stockoption.StockOption;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.dto.Tuple2;
import nordnetservice.dto.YearMonthDTO;
import nordnetservice.util.NordnetUtil;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class Core {
    private final NordnetRepository nordnetRepository;
    private final CritterAdapter critterAdapter;
    private final RedisAdapter redisAdapter;

    public Core(@Qualifier("v2") NordnetRepository nordnetRepository,
                CritterAdapter critterAdapter,
                RedisAdapter redisAdapter) {
        this.nordnetRepository = nordnetRepository;
        this.critterAdapter = critterAdapter;
        this.redisAdapter = redisAdapter;
    }

    public Tuple2<StockPrice, StockOption> findOption(StockOptionTicker ticker) {
        return nordnetRepository.findOption(ticker);
    }

    @FunctionalInterface
    private interface HandleEitherCommand<T> {
        T handle() throws Exception;
    };
    /*
    @FunctionalInterface
    private interface HandleOptionalCommand<T> {
        T handle() throws Exception;
    };
     */

    private <T> Either<ApplicationError,T> handle(HandleEitherCommand<T> fn) {
        try {
            return Either.right(fn.handle());
        }
        catch (MyBatisSystemException ex) {
            if (ex.getMessage() == null) {
                return Either.left(new SqlError.MybatisSqlError(ex.getCause().getMessage()));
            }
            else {
                return Either.left(new SqlError.MybatisSqlError(ex.getMessage()));
            }
        }
        catch (Exception ex) {
            return Either.left(new GeneralError.GeneralApplicationError(ex.getMessage()));
        }
    }

    public Either<ApplicationError,List<StockOptionPurchase>> fetchCritters(PurchaseType purchaseType) {
        return handle(() -> critterAdapter.fetchCritters(purchaseType));
        /*
        try {
            return Either.right(critterAdapter.fetchCritters(purchaseType));
        }
        catch (MyBatisSystemException ex) {
            if (ex.getMessage() == null) {
                return Either.left(new SqlError.MybatisSqlError(ex.getCause().getMessage()));
            }
            else {
                return Either.left(new SqlError.MybatisSqlError(ex.getMessage()));
            }
        }
        catch (Exception ex) {
            return Either.left(new GeneralError.GeneralApplicationError(ex.getMessage()));
        }

         */
    }
    public Either<ApplicationError,List<StockOption>> getCalls(StockTicker ticker) {
        return handle(() -> nordnetRepository.getCalls(ticker));
        /*
        try {
            return Either.right(nordnetRepository.getCalls(ticker));
        }
        catch (Exception ex) {
            return Either.left(new GeneralError.GeneralApplicationError(ex.getMessage()));
        }

         */
    }

    public Either<ApplicationError,List<StockOption>> getPuts(StockTicker ticker) {
        try {
            return Either.right(nordnetRepository.getPuts(ticker));
        }
        catch (Exception ex) {
            return Either.left(new GeneralError.GeneralApplicationError(ex.getMessage()));
        }
    }
    public Either<ApplicationError,StockPrice> getStockPrice(StockTicker ticker) {
        try {
            return Either.right(nordnetRepository.getStockPrice(ticker));
        }
        catch (Exception ex) {
            return Either.left(new GeneralError.GeneralApplicationError(ex.getMessage()));
        }
    }

    public Either<ApplicationError,OpeningPrice> openingPrice(StockTicker ticker) {
        try {
            return Either.right(nordnetRepository.openingPrice(ticker));
        }
        catch (Exception ex) {
            return Either.left(new GeneralError.GeneralApplicationError(ex.getMessage()));
        }
    }

    public Optional<ApplicationError> thirdFridayMillis(List<YearMonthDTO> items) {
        try {
            var millis = items.stream().map(NordnetUtil::calcUnixTimeForThirdFriday).toList();
            redisAdapter.updateNordnetMillis(millis);
            return Optional.empty();
        }
        catch (Exception ex) {
            return Optional.of(new GeneralError.GeneralApplicationError(ex.getMessage()));
        }
    }

    public void resetCaffeine() {
        nordnetRepository.resetCaffeine();
    }
}
