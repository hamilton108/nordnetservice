package nordnetservice.adapter.downloader;

import com.gargoylesoftware.htmlunit.WebClient;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.util.NordnetUtil;
import nordnetservice.util.StockOptionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@Profile({"demo","test", "integration"})
public class DemoDownloaderAdapter implements Downloader<PageInfo> {
    private final WebClient client;
    private final String testUrl;
    private final String testUrl2;

    public DemoDownloaderAdapter(@Value("${url.test}") String testUrl,
                                 @Value("${url.test.2}") String testUrl2) {
        System.out.println(testUrl);
        System.out.println(testUrl2);
        this.testUrl = testUrl;
        this.testUrl2 = testUrl2;

        this.client = new WebClient();
        this.client.getOptions().setJavaScriptEnabled(false);
    }

    @Override
    public List<PageInfo> download(StockTicker ticker) {
        try {
            var page = client.getPage(testUrl);
            var content = page.getWebResponse().getContentAsString();
            var info = new PageInfo(content);
            return Collections.singletonList(info);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*
        try {
            var uri = new URI(testUrl); //Path.of(testUrl).toUri();
            var req =
                    HttpRequest.newBuilder(uri).GET().build();
            HttpResponse<String> response = getClient().send(req, HttpResponse.BodyHandlers.ofString());
            var info = new PageInfo(response.body());
            return Collections.singletonList(info);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
         */
    }

    @Override
    public PageInfo download(StockOptionTicker ticker) {
        try {
            //var url = NordnetUtil.urlFor(ticker);

            var page = client.getPage(testUrl);
            var content = page.getWebResponse().getContentAsString();
            return new PageInfo(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
