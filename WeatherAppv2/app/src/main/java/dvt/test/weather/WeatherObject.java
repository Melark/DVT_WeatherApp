package dvt.test.weather;

/**
 * Created by marka on 2018/01/31.
 */

public class WeatherObject {
    private double maxTemperature;
    private double minTemperature;
    private int icon;
    private String dateValue;

    public WeatherObject(double maxTemp, double minTemp, int icon, String date) {
        this.maxTemperature = maxTemp;
        this.minTemperature = minTemp;
        this.icon = icon;
        this.dateValue = date;
    }

    public double getMaxTemp() {
        return maxTemperature;
    }

    public double getMinTemp() {
        return minTemperature;
    }

    public int getIcon() {
        return icon;
    }

    public String getDate() {
        return dateValue;
    }

    public WeatherObject() {
    }
}
