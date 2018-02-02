package dvt.test.weather;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

/**
 * Created by marka on 2018/01/31.
 */

public class JsonConverter implements IJsonConverter {
    private JSONObject jsonWeatherObject;
    private List<WeatherObject> weatherObjList;

    private final static String MAIN_IDENTIFIER = "main";
    private final static String ICON_BASE_URL = "http://openweathermap.org/img/w/";

    private final static String DATE_IDENTIFIER = "dt_txt";
    private final static String WEATHER_ARRAY_IDENTIFIER = "weather";
    private final static String WEATHER_ICON_IDENTIFIER = "icon";
    private final static String MIN_TEMPERATURE_IDENTIFIER = "temp_min";
    private final static String MAX_TEMPERATURE_IDENTIFIER = "temp_max";

    private final static String COUNTRY_IDENTIFIER = "country";
    private final static String CITY_IDENTIFIER = "city";
    private final static String AREA_IDENTIFIER = "name";

    public JsonConverter(String rawJson, Context context) {
        try {
            if (rawJson == null || rawJson.equals("")) {
                throw new ErrorException("Connection Error", "An error occurred when reading from the server", context);
            } else {
                jsonWeatherObject = new JSONObject(rawJson);
                weatherObjList = new ArrayList<>();
                populateWeatherObjectList();
            }
        } catch (ErrorException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", "Object creation failed");
        }
    }

    private void populateWeatherObjectList() {

        try {
            weatherObjList = new ArrayList<>();

            JSONArray jsonArray = jsonWeatherObject.getJSONArray("list");
            String currentDate = dateValue(jsonArray.getJSONObject(0));
            String icon = weatherIconURL(jsonArray.getJSONObject(0));
            double minTemp = Double.MAX_VALUE;
            double maxTemp = Double.MIN_VALUE;


            for (int i = 0; i < jsonArray.length() - 1; i++) {
                String nextDate = dateValue(jsonArray.getJSONObject(i + 1));
                double minForTimePeriod = minTemperature(jsonArray.getJSONObject(i));


                if (minTemp > minForTimePeriod)
                    minTemp = minForTimePeriod;


                double maxForTimePeriod = maxTemperature(jsonArray.getJSONObject(i));
                if (maxTemp < maxForTimePeriod)
                    maxTemp = maxForTimePeriod;


                if (!currentDate.equals(nextDate)) {
                    weatherObjList.add(new WeatherObject(maxTemp, minTemp, icon, currentDate));
                    minTemp = Double.MAX_VALUE;
                    maxTemp = Double.MIN_VALUE;
                    icon = weatherIconURL(jsonArray.getJSONObject(i + 1));
                    currentDate = nextDate;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String dateValue(JSONObject object) {
        try {
            String dateValue = object.getString(DATE_IDENTIFIER);
            if (!(dateValue == null || dateValue.equals(""))) {
                return dateValue.substring(0, 10);
            }

            return "";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public double maxTemperature(JSONObject object) {
        double temperature = 0;
        try {
            temperature = object.getJSONObject(MAIN_IDENTIFIER).getDouble(MAX_TEMPERATURE_IDENTIFIER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temperature;
    }

    @Override
    public double minTemperature(JSONObject object) {
        double temperature = 0;
        try {
            temperature = object.getJSONObject(MAIN_IDENTIFIER).getDouble(MIN_TEMPERATURE_IDENTIFIER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temperature;
    }

    @Override
    public String weatherIconURL(JSONObject object) {
        String iconURL = "";

        try {
            String iconDesc = object.getJSONArray(WEATHER_ARRAY_IDENTIFIER).getJSONObject(0).getString(WEATHER_ICON_IDENTIFIER);
            iconURL = String.format("%s%s", ICON_BASE_URL, iconDesc);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return iconURL;
    }

    boolean isWeatherListEmpty() {
        return (weatherObjList.size() > 0) ? false : true;
    }

    @Override
    public String locationValue() {
        if (isWeatherListEmpty())
            return "";

        String country;
        String area;
        country = getCountryValue(jsonWeatherObject);
        area = getAreaValue(jsonWeatherObject);
        Locale locale = new Locale("", country);
        country = locale.getDisplayCountry();
        return String.format("%s, \n%s", area, country);
    }

    private String getAreaValue(JSONObject object) {
        String area = "";
        try {
            area = object.getJSONObject(CITY_IDENTIFIER).getString(AREA_IDENTIFIER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return area;
    }

    private String getCountryValue(JSONObject object) {
        String country = "";
        try {
            country = object.getJSONObject(CITY_IDENTIFIER).getString(COUNTRY_IDENTIFIER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return country;
    }

    WeatherObject[] getWeatherObjects() {
        if (isWeatherListEmpty())
            return null;
        return weatherObjList.toArray(new WeatherObject[weatherObjList.size()]);
    }

    WeatherObject GetCurrentWeatherObject() {
        if (isWeatherListEmpty())
            return null;
        return weatherObjList.get(0);
    }

}
