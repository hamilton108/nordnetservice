package nordnetservice.adapter.downloader;

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
    private final String testUrlV2;

    public enum NordnetAdapterVersion { NOT_SET, VER_1, VER_2 };

    private NordnetAdapterVersion nordnetAdapterVersion = NordnetAdapterVersion.NOT_SET;

    public NordnetAdapterVersion getNordnetAdapterVersion() {
        return nordnetAdapterVersion;
    }

    public void setNordnetAdapterVersion(NordnetAdapterVersion nordnetAdapterVersion) {
        this.nordnetAdapterVersion = nordnetAdapterVersion;
    }

    public DemoDownloaderAdapter(@Value("${url.test}") String testUrl,
                                 @Value("${url.test.2:#{null}}") String testUrl2,
                                 @Value("${url.test.v2:#{null}}") String testUrlV2) {
        System.out.println(testUrl);
        System.out.println(testUrl2);
        System.out.println(testUrlV2);
        this.testUrl = testUrl;
        this.testUrl2 = testUrl2;
        this.testUrlV2 = testUrlV2;

        this.client = new WebClient();
        this.client.getOptions().setJavaScriptEnabled(false);
    }

    private List<PageInfo> result = null;

    @Override
    public List<PageInfo> download(StockTicker ticker) {
        if (nordnetAdapterVersion == NordnetAdapterVersion.NOT_SET) {
            throw new RuntimeException("Adapter version not set");
        }
        if (nordnetAdapterVersion == NordnetAdapterVersion.VER_1) {
            return downloadV1(ticker);
        }
        else if (nordnetAdapterVersion == NordnetAdapterVersion.VER_2) {
            return downloadV2(ticker);
        }
        else {
            return Collections.emptyList();
        }
    }

    private List<PageInfo> downloadV2(StockTicker ticker) {
        if (result == null) {
            try {
                var page = client.getPage(testUrlV2);
                var content = page.getWebResponse().getContentAsString();
                var info = new PageInfo(content);
                result = Collections.singletonList(info);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private List<PageInfo> downloadV1(StockTicker ticker) {
        if (result == null) {

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
        return downloadOne();
    }

    private PageInfo downloadOne() {
        try {
            var page = client.getPage(testUrl);
            var content = page.getWebResponse().getContentAsString();
            return new PageInfo(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageInfo downloadOne(StockTicker ticker) {
        return downloadOne();
    }

}
