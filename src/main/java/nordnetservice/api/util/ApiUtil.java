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

    public static ResponseEntity<DefaultResponse> map(@NonNull Optional<ApplicationError> err, HttpStatus errorStatus, String okMsg, boolean isOk) {
        return err.map(r ->
                ResponseEntity
                        .status(errorStatus)
                        .body(mapError(r))
        ).orElse(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new DefaultResponse(AppStatusCode.GENERAL_ERROR, okMsg))
        );
    }

    public static <Q,T> ResponseEntity<PayloadResponse<T>> map(Either<ApplicationError,Q> result, Function<Q,T> fn) {
        if (result.isRight()) {
            var result1 = fn.apply(result.getRight());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new PayloadResponse<T>(result1, AppStatusCode.Ok, null));
        }
        else {
            return mapAppError(result.getLeft());
        }
    }

    public static <T> ResponseEntity<PayloadResponse<T>> map(Either<ApplicationError,T> result) {
        if (result.isRight()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new PayloadResponse<T>(result.getRight(), AppStatusCode.Ok, null));
        }
        else {
            return mapAppError(result.getLeft());
        }
    }

    public static <T> ResponseEntity<PayloadResponse<T>> mapAppError(ApplicationError appError) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new PayloadResponse<T>(null, AppStatusCode.GENERAL_ERROR, mapErr2str(appError)));
    }

    public static DefaultResponse mapError(ApplicationError err) {
        return switch (err) {
            case SqlError.DuplicateKeyError e -> new DefaultResponse(AppStatusCode.GENERAL_ERROR, mapErr2str(err));
            case SqlError.GeneralSqlError e -> new DefaultResponse(AppStatusCode.GENERAL_ERROR, mapErr2str(err));
            case GeneralError.GeneralApplicationError e -> new DefaultResponse(AppStatusCode.GENERAL_ERROR, mapErr2str(err));
        };
    }

    public static String mapErr2str(ApplicationError err) {
        return switch (err) {
            case SqlError.DuplicateKeyError e -> "DuplicateKeyError: " + e.msg();
            case SqlError.GeneralSqlError e -> "GeneralSqlError : " + e.msg();
            case GeneralError.GeneralApplicationError e -> "GeneralApplicationError : " + e.msg();
        };
    }
}
