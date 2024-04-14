package nordnetservice.adapter.nordnet;

import nordnetservice.domain.downloader.Downloader;

import nordnetservice.domain.html.PageInfo;

public class NordnetAdapterBase {

    private final Downloader<PageInfo> downloader;

    public NordnetAdapterBase(Downloader<PageInfo> downloader) {
        this.downloader = downloader;
    }

    public Downloader<PageInfo> getDownloader() {
        return downloader;
    }

}
