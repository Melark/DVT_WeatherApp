package dvt.test.weather;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by marka on 2018/01/31.
 */

public class DataHandler {

    private final static String URL_BASE = "http://api.openweathermap.org/data/2.5/forecast";
    private final static String IMAGE_URL_BASE = "http://openweathermap.org/img/w/";
    private final static String LATITUDE_IDENTIFIER = "lat";
    private final static String LONGITUDE_IDENTIFIER = "lon";
    private final static String API_REQUEST_IDENTIFIER = "appid";
    private final static String API_KEY = "ad6e84f02f79a8dfd63339d7b5dd5460";
    private final static String UNITS_IDENTIFIER = "units";
    private final static String UNIT_TYPE_IDENTIFIER = "metric";

    static URL buildUrl(String latitude, String longitude) {
        Uri uri = Uri.parse(URL_BASE).buildUpon()
                .appendQueryParameter(LATITUDE_IDENTIFIER, latitude)
                .appendQueryParameter(LONGITUDE_IDENTIFIER, longitude)
                .appendQueryParameter(API_REQUEST_IDENTIFIER, API_KEY)
                .appendQueryParameter(UNITS_IDENTIFIER, UNIT_TYPE_IDENTIFIER)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    static String GetWeatherData(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            InputStream stream = conn.getInputStream();
            Scanner scanner = new Scanner(stream).useDelimiter("\\A");
            if (scanner.hasNext()) {
                return scanner.next();
            }
            return null;
        } finally {
            conn.disconnect();
        }
    }

    public static boolean loadImageFromURL(String imageDesc, ImageView image) {
        try {
            String fullUrl = String.format("%s%s", IMAGE_URL_BASE, imageDesc);
            URL imageURL = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) imageURL.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            image.setImageBitmap(BitmapFactory.decodeStream(is));

            return true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
