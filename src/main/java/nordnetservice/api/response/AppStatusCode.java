package nordnetservice.api.response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AppStatusCode {
    Ok(1),
    GENERAL_ERROR(2);

    private final int statusCode;

    AppStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @JsonValue
    public int getStatusCode() {
        return statusCode;
    }
}
