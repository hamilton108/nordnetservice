package nordnetservice.adapter.downloader;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import nordnetservice.domain.downloader.Downloader;
import nordnetservice.domain.html.PageInfo;
import nordnetservice.domain.stock.StockTicker;
import nordnetservice.domain.stockoption.StockOptionTicker;
import nordnetservice.util.NordnetUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@Profile("prod")
public class HtmlUnitDownloaderAdapter implements Downloader<PageInfo> {

    private final WebClient client;

    public HtmlUnitDownloaderAdapter() {
        this.client = new WebClient();
        this.client.getOptions().setJavaScriptEnabled(false);
    }

    @Override
    public List<PageInfo> download(StockTicker ticker) {
        try {
            var url = NordnetUtil.urlFor(ticker, 1000);
            var page = client.getPage(url);
            var content = page.getWebResponse().getContentAsString();
            var info = new PageInfo(content);
            return Collections.singletonList(info);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageInfo download(StockOptionTicker ticker) {
        return null;
    }
}
