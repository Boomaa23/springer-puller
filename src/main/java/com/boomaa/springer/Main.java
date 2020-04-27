package com.boomaa.springer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String dlBaseUrl = "https://link.springer.com";
    private static final String listBaseUrl = "https://link.springer.com/search/page/";
    private static final String query = "?facet-content-type=%22Book%22&package=mat-covid19_textbooks&%23038;facet-language=%22En%22&%23038;sortOrder=newestFirst&%23038;showAll=true";
    private static final String folder = "spr_dl/";
    public static final String LOG_FILE = "sprdl.log";
    private static final List<String> dlList = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        new File(folder).mkdir();
        System.setOut(new PrintStream(new DualOutputStream()));
        int max = 24;
        for (int i = 1;i <= max;i++) {
            Elements liList = Jsoup.connect(listBaseUrl + i + query).followRedirects(false).get()
                    .getElementById("results-list")
                    .getAllElements();
            System.out.println();
            System.out.println("Page " + i + " of " + max);
            int pageCtr = 1;
            for (Element li : liList) {
                Element liInner = li.getElementsByTag("a").attr("class", "title").first();
                if (liInner != null && !liInner.attr("href").contains("/search")
                        && !liInner.attr("href").contains("bookseries") && !dlList.contains(liInner.attr("href"))) {
                    String dlUrl = liInner.attr("href") + ".pdf";
                    dlUrl = dlBaseUrl + dlUrl.replace("/book/", "/content/pdf/");
                    download(dlUrl, folder + "/" + liInner.text() + ".pdf");
                    dlList.add(liInner.attr("href"));
                    String pdfLen = humanReadable(new URL(dlUrl).openConnection().getContentLength());
                    System.out.println("Downloading (" + pageCtr + " / 10) " + liInner.text() + " from " + dlUrl + " (" + pdfLen + ")");
                    pageCtr++;
                }
            }
            Thread.sleep(1000);
        }
    }

    private static void download(String url, String outputPath) {
        if (new File(outputPath).exists()) {
            return;
        }
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(outputPath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String humanReadable(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}
