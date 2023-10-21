package nordnetservice.adapter.downloader;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOptionTicker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Profile({"demo","docker","test", "integration"})
public class DemoDownloaderAdapter implements Downloader<PageInfo> {
    private final WebClient client;
    private final String testUrl;
    private final String testUrl2;
    private final String testUrl3;
    private boolean isV1 = true;

    public DemoDownloaderAdapter(@Value("${url.test}") String testUrl,
                                 @Value("${url.test.2:#{null}}") String testUrl2,
                                 @Value("${url.test.3:#{null}}") String testUrl3) {
        System.out.println(testUrl);
        System.out.println(testUrl2);
        this.testUrl = testUrl;
        this.testUrl2 = testUrl2;
        this.testUrl3 = testUrl3;

        this.client = new WebClient();
        this.client.getOptions().setJavaScriptEnabled(false);
    }

    private List<PageInfo> result = null;
    @Override
    public List<PageInfo> download(StockTicker ticker) {
        if (result == null) {

            if (isV1) {
                try {
                    var page = client.getPage(testUrl);
                    var content = page.getWebResponse().getContentAsString();
                    var info = new PageInfo(content);

                    if (testUrl2 == null) {
                        result = Collections.singletonList(info);
                    } else {
                        var page2 = client.getPage(testUrl2);
                        var content2 = page2.getWebResponse().getContentAsString();
                        var info2 = new PageInfo(content2);
                        result = Arrays.asList(info, info2);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                Page page = null;
                try {
                    page = client.getPage(testUrl3);
                    var content = page.getWebResponse().getContentAsString();
                    var info = new PageInfo(content);
                    result = Collections.singletonList(info);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return result;
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

    public boolean isV1() {
        return isV1;
    }

    public void setV1(boolean value) {
        isV1 = value;
    }
}
