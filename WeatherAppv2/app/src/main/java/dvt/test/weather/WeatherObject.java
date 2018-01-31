package dvt.test.weather;

/**
 * Created by marka on 2018/01/31.
 */

public class WeatherObject {
    private double maxTemperature;
    private double minTemperature;
    private String iconURL;
    private String dateValue;

    public WeatherObject(double maxTemp, double minTemp, String iconURL, String date) {
        this.maxTemperature = maxTemp;
        this.minTemperature = minTemp;
        this.iconURL = iconURL;
        this.dateValue = date;
    }

    public double getMaxTemp() {
        return maxTemperature;
    }

    public double getMinTemp() {
        return minTemperature;
    }

    public String getIconURL() {
        return iconURL;
    }

    public String getDate() {
        return dateValue;
    }

    public WeatherObject() {
    }
}
