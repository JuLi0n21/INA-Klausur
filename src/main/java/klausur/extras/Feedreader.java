package klausur.extras;

import com.apptasticsoftware.rssreader.Item;
import com.apptasticsoftware.rssreader.RssReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Feedreader {

    public ArrayList<String> extractRssLink(String urlString) throws IOException {

        //ArrayList für den fall das mehrere Links pro seite existieren.
        ArrayList rssurl = new ArrayList<URL>();
        URL url = null;

        //Füge https:// hinzu wenn nicht vorhanden

        if (urlString.contains("https://")) {
            url = new URL(urlString);
        } else {
            url = new URL("https://" + urlString);
        }

        //Verbindung zum Server aufbauen
        HttpURLConnection huc = (HttpURLConnection) url.openConnection();
        huc.setRequestMethod("GET");
        if (huc.getResponseCode() != HttpURLConnection.HTTP_OK) {
           // System.out.println(huc.getResponseMessage());
        } else {

            //Wenn verbindungs aufbau erfolgreich wird die website geladen und in einen String gepackt
           // System.out.println(url + " : " + huc.getResponseMessage()); // OK

            InputStream is = huc.getInputStream();
            StringBuilder sb = new StringBuilder();

            Scanner in = new Scanner(is);
            while (in.hasNextLine()) {
                sb.append(in.nextLine());
            }

            //mit Jsoup werden die einzelnen Elemente aus der Website gefiltert
            Document doc = Jsoup.parse(sb.toString());

            //nach rrs elementen suchen
            Elements rrsxml = doc.select("link[rel=alternate]");

            for (Element rrs : rrsxml) {

                //überprüfen ob sie wirklich rss elemente sind und gegebenenfalls den Link passend zusammen bauen
                if (rrs.attr("type").equals("application/rss+xml")) {
                    if (rrs.attr("href").contains("rss")) {

                        //www.rss.example.de -> www.rss.example.de
                        if (rrs.attr("href").contains(url.getHost().substring(4))) {
                           // System.out.println(url.getHost().substring(4));
                            rssurl.add(rrs.attr("href"));

                            //www.example.de/rss.xml -> www.example.de/rss.xml
                        } else if (rrs.attr("href").contains(url.getHost())) {
                           // System.out.println(url.getHost());
                            rssurl.add(rrs.attr("href"));

                            // "/rss.xml -> https://www.example.com/rss.xml
                        } else {
                           // System.out.println(url + rrs.attr("href"));
                            rssurl.add(url + rrs.attr("href"));
                        }
                    }
                }
            }
        }
        return rssurl;
    }

    //gibt ein html a element mit title und link aus den rsslinks zurück
    ArrayList<String> readRssLink(String rsslink) throws IOException {

        ArrayList<String> feeds = new ArrayList<>();
        //mit hilfe von rssReader library elemente aus der website entnehemen und aus geben
        RssReader rssReader = new RssReader();
        List<Item> items = rssReader.read(rsslink).toList();

        for (Item item : items) {
            String title = item.getTitle().orElse("");
            String link = item.getLink().orElse("");

            feeds.add("<a href='" + link + "'>" + title + "</a><br>");

        }
        return feeds;
    }

    public ArrayList<String> getWebsitefeeds(String website) throws IOException {
        ArrayList<String> rssFeeds = new ArrayList<>();

        try {
            //Hole rrslinks aus der website soweit vorhanden
            ArrayList<String> rssLinks = extractRssLink(website);
            for (String rssLink : rssLinks) {
                //Hole rssfeeds aus den rsslinks
                ArrayList<String> feeds = readRssLink(rssLink);
                rssFeeds.addAll(feeds);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rssFeeds;
    }
}