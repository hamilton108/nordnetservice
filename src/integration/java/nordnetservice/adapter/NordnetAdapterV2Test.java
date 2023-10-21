package nordnetservice.adapter;

import nordnetservice.adapter.downloader.DemoDownloaderAdapter;
import nordnetservice.adapter.nordnet.NordnetAdapterV2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NordnetAdapterV2Test {

    @Autowired
    DemoDownloaderAdapter downloaderAdapter;
    @Autowired
    NordnetAdapterV2 nordnetAdapter;

    @Test
    void test_parse() {
        downloaderAdapter.setV1(false);
    }

}
