package net.myconbook.android;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * URL file fetcher.
 */
public class UrlFileFetcher {
    public static void fetch(OutputStream outStream, String urlToDownload) throws IOException {
        URL url = new URL(urlToDownload);
        URLConnection connection = url.openConnection();
        connection.connect();

        InputStream input = new BufferedInputStream(url.openStream());

        byte data[] = new byte[1024];
        int count;
        while ((count = input.read(data)) != -1) {
            outStream.write(data, 0, count);
        }

        outStream.flush();
        outStream.close();
        input.close();
    }
}
