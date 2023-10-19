package nordnetservice.adapter.downloader;

import org.apache.http.HttpStatus;

public class DownloadException extends RuntimeException {
    private final int httpStatus;
    public DownloadException(int httpStatus) {
        super();
        this.httpStatus = httpStatus;
    }
}
