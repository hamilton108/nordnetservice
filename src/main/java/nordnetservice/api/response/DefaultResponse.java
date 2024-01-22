package nordnetservice.api.response;

import com.fasterxml.jackson.annotation.JsonGetter;

public record DefaultResponse(@JsonGetter("statuscode") AppStatusCode statusCode,
                              @JsonGetter("msg") String msg) {
}
