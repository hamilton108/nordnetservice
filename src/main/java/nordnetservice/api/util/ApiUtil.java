package nordnetservice.api.util;

import nordnetservice.api.response.AppStatusCode;
import nordnetservice.api.response.DefaultResponse;
import nordnetservice.api.response.PayloadResponse;
import nordnetservice.domain.error.ApplicationError;
import nordnetservice.domain.error.GeneralError;
import nordnetservice.domain.error.SqlError;
import nordnetservice.domain.functional.Either;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.function.Function;

public class ApiUtil {

    public static ResponseEntity<DefaultResponse> mapWithErrFn(@NonNull Optional<ApplicationError> err,
                                                               HttpStatus errorStatus,
                                                               String okMsg) {
        return err.map(r ->
                ResponseEntity
                        .status(errorStatus)
                        .body(mapError(r))
        ).orElse(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new DefaultResponse(AppStatusCode.OK, okMsg))
        );
    }

    public static <Q,T> ResponseEntity<PayloadResponse<T>> mapWithErrFn(@NonNull Either<ApplicationError,Q> result,
                                                                        Function<Q,T> fn,
                                                                        T inCaseOfError) {
        if (result.isRight()) {
            var result1 = fn.apply(result.getRight());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new PayloadResponse<T>(result1, AppStatusCode.OK, null));
        }
        else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new PayloadResponse<T>(inCaseOfError, AppStatusCode.GENERAL_ERROR, mapErr2str(result.getLeft())));
        }
    }

    public static <Q,T> ResponseEntity<PayloadResponse<T>> mapWithFn(@NonNull Either<ApplicationError,Q> result, Function<Q,T> fn) {
        return mapWithErrFn(result,fn,null);
    }

    public static <T> ResponseEntity<PayloadResponse<T>> mapWithDefault(@NonNull Either<ApplicationError,T> result, T inCaseOfError) {
        if (result.isRight()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new PayloadResponse<T>(result.getRight(), AppStatusCode.OK, null));
        }
        else {
            return mapAppError(result.getLeft(), inCaseOfError);
        }
    }

    public static <T> ResponseEntity<PayloadResponse<T>> map(@NonNull Either<ApplicationError,T> result) {
        return mapWithDefault(result, null);
    }

    public static <T> ResponseEntity<PayloadResponse<T>> mapAppError(@NonNull ApplicationError appError, T inCaseOfError) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new PayloadResponse<T>(inCaseOfError, AppStatusCode.GENERAL_ERROR, mapErr2str(appError)));
    }

    public static DefaultResponse mapError(ApplicationError err) {
        return switch (err) {
            case SqlError.DuplicateKeyError e -> new DefaultResponse(AppStatusCode.GENERAL_ERROR, mapErr2str(err));
            case SqlError.GeneralSqlError e -> new DefaultResponse(AppStatusCode.GENERAL_ERROR, mapErr2str(err));
            case SqlError.MybatisSqlError e -> new DefaultResponse(AppStatusCode.GENERAL_ERROR, mapErr2str(err));
            case GeneralError.GeneralApplicationError e -> new DefaultResponse(AppStatusCode.GENERAL_ERROR, mapErr2str(err));
        };
    }

    public static String mapErr2str(ApplicationError err) {
        return switch (err) {
            case SqlError.DuplicateKeyError e -> "DuplicateKeyError: " + e.msg();
            case SqlError.GeneralSqlError e -> "GeneralSqlError: " + e.msg();
            case SqlError.MybatisSqlError e -> "MybatisSqlError: " + e.msg();
            case GeneralError.GeneralApplicationError e -> "GeneralApplicationError: " + e.msg();
        };
    }
}
