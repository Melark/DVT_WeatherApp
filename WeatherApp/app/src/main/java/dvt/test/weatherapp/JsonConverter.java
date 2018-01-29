package dvt.test.weatherapp;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by marka on 2018/01/29.
 */

public class JsonConverter implements IJsonConverter{
    private JSONObject jsonWeatherObject;
    private List<WeatherObject> weatherObjectList;

    private final static String MAIN_IDENTIFIER = "main";

    private final static String DATE_IDENTIFIER = "dt_txt";
    private final static String WEATHER_ARRAY_IDENTIFIER = "weather";
    private final static String WEATHER_ICON_IDENTIFIER = "icon";
    private final static String MIN_TEMPERATURE_IDENTIFIER = "temp_min";
    private final static String MAX_TEMPERATURE_IDENTIFIER = "temp_max";

    private final static String COUNTRY_IDENTIFIER = "country";
    private final static String CITY_IDENTIFIER = "city";
    private final static String AREA_IDENTIFIER = "name";

    public JsonConverter() {
    }

    @Override
    public String dateValue(JSONObject object) {
        return null;
    }

    @Override
    public double maxTemperature(JSONObject object) {
        return 0;
    }

    @Override
    public double minTemperature(JSONObject object) {
        return 0;
    }

    @Override
    public int weatherIcon(JSONObject object) {
        return 0;
    }

    @Override
    public String locationValue() {
        return null;
    }
}
