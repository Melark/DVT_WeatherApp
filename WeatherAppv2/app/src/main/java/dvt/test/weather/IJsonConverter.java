package dvt.test.weather;

import org.json.JSONObject;

/**
 * Created by marka on 2018/01/31.
 */

public interface IJsonConverter {
    String dateValue(JSONObject object);
    double maxTemperature(JSONObject object);
    double minTemperature(JSONObject object);
    int weatherIcon(JSONObject object);
    String locationValue();
}
