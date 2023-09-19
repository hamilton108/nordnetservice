package nordnetservice.adapter.downloader;

import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOptionTicker;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;
import java.util.Collections;
import java.util.List;

@Component
@Profile("prod")
public class DefaultDownloaderAdapter  {
}
