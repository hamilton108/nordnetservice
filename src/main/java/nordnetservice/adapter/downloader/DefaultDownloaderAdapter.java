package nordnetservice.adapter.downloader;

import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.util.NordnetUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

@Component
@Profile("prod")
public class DefaultDownloaderAdapter  implements  Downloader<PageInfo> {

    private HttpClient client;

    @Override
    public List<PageInfo> download(StockTicker ticker) {
        try {
            var url = NordnetUtil.urlFor(ticker, 1000);
            var req =
                    HttpRequest.newBuilder(url.toURI()).GET().build();
            //assert getClient() != null;
            HttpResponse<String> response = getClient().send(req, HttpResponse.BodyHandlers.ofString());
            var info = new PageInfo(response.body());
            return Collections.singletonList(info);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageInfo download(StockOptionTicker ticker) {
        return null;
    }

    private HttpClient getClient() {
        if (client == null) {
            client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        }
        return client;
    }
}
