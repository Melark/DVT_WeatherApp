package dvt.test.weather;

import java.io.IOException;
import java.net.URL;

/**
 * Created by marka on 2018/01/31.
 */

public interface IDataHandler {
    String GetWeatherData(URL url) throws IOException;
}
