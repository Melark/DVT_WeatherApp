package dvt.test.weather;

import android.*;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private TextView textViewDate,
            textViewCurrentMaxTemp,
            textViewCurrentMinTemp,
            textViewLocation,
            textViewDay1,
            textViewDay2,
            textViewDay3,
            textViewDay4,
            textViewDay5,
            textViewDay1MinTemp,
            textViewDay1MaxTemp,
            textViewDay2MinTemp,
            textViewDay2MaxTemp,
            textViewDay3MinTemp,
            textViewDay3MaxTemp,
            textViewDay4MinTemp,
            textViewDay4MaxTemp,
            textViewDay5MinTemp,
            textViewDay5MaxTemp;

    private ImageView imageViewCurrent,
            imageViewDay1,
            imageViewDay2,
            imageViewDay3,
            imageViewDay4,
            imageViewDay5;

    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    private static final int LOCATION_REFRESH_PERIOD = (1000 * 60) * 5;
    private static final int FINE_LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssignTextViews();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(LOCATION_REFRESH_PERIOD);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
        }else
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case FINE_LOCATION_REQUEST_CODE:
                if((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        &&ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                            locationRequest, this);
                    onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
                }
                else{
                    Log.e("RequestPermission", "Location permission not granted");
                    showErrorPage();
                }
        }
    }

    private class AsyncDataLoader extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {

            final ProgressDialog progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Connecting");
            progress.setMessage("Please wait, establishing a connection to the server");
            progress.show();

            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    progress.cancel();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 3000);

        }

        @Override
        protected String doInBackground(URL... url) {
            String json = null;
            try{
                json = DataHandler.GetWeatherData(url[0]);
            }catch (IOException e){
                e.printStackTrace();
            }
            return json;
        }


        @Override
        protected void onPostExecute(String json) {
            updateDisplayBasedOnJSON(json);
        }


    }

    private void showErrorPage() {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Connection","Connection has been suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(!connectionResult.hasResolution()){
            Log.e("Connection","Connection Failed");
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Log.e("Location", "location null");
            showErrorPage();
        }else
        {
            URL url = DataHandler.buildUrl(Double.toString(location.getLatitude()),
                    Double.toString(location.getLongitude()));
            new AsyncDataLoader().execute(url);
            Log.i("API URL", url.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!googleApiClient.isConnected())
            googleApiClient.connect();

    }

    private void updateDisplayBasedOnJSON(String Json){
        if(Json == null || Json.equals("")){
            Log.e("Show Error", "displayWeatherDataFromJson Json string is null");
            showErrorPage();
            return;
        }

        JsonConverter jsonConverter = new JsonConverter(Json,MainActivity.this);
        if(jsonConverter.isWeatherListEmpty()){
            Log.e("JSON UTILS", "json string error");
            showErrorPage();
            return;
        }
        WeatherObject currentWeather = jsonConverter.GetCurrentWeatherObject();
        textViewCurrentMaxTemp.setText(String.format("Max: %s",String.valueOf( (int) Math.round(currentWeather.getMaxTemp()))));
        textViewCurrentMinTemp.setText(String.format("Min: %s",String.valueOf( (int) Math.round(currentWeather.getMinTemp()))));

        textViewDate.setText(BuildDate());
        textViewLocation.setText(jsonConverter.locationValue());

        DataHandler.loadImageFromURL(currentWeather.getIconURL(),imageViewCurrent);
        WeatherObject[] weatherItems = jsonConverter.getWeatherObjects();

        /*
        //update table layout display

        for (int rows = 0; rows < 4 ; rows++) {
            for (int columns = 0; columns < weatherItems.length; columns++) {
                TextView temp = null;
                ImageView imageTemp = null;
                if (rows <3)
                {
                    temp = (TextView) findViewById(tableRowFields[rows][columns]);
                    temp.setTypeface(robotoRegular);

                }else{
                    imageTemp = (ImageView) findViewById(tableRowFields[rows][columns]);
                }
                if (rows == 0)
                {
                    temp.setText(weatherItems[columns].getDate().substring(8,10));
                    temp.setTypeface(montserratBold);
                }
                else if (rows ==1)
                    temp.setText(String.valueOf( (int) Math.round(weatherItems[columns].getMinTemp())));

                else if (rows ==2)
                    temp.setText(String.valueOf( (int) Math.round(weatherItems[columns].getMaxTemp())));

                else if(rows == 3)
                    imageTemp.setImageResource(weatherItems[columns].getIcon());
            }
        }
        */
    }

    private String BuildDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        Date date = new Date();
        return   dateFormat.format(date).toUpperCase();
    }

    private void AssignTextViews() {
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewCurrentMaxTemp = (TextView) findViewById(R.id.textViewCurrentMaxTemp);
        textViewCurrentMinTemp = (TextView) findViewById(R.id.textViewCurrentMinTemp);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        textViewDay1 = (TextView) findViewById(R.id.textViewDay1);
        textViewDay2 = (TextView) findViewById(R.id.textViewDay2);
        textViewDay3 = (TextView) findViewById(R.id.textViewDay3);
        textViewDay4 = (TextView) findViewById(R.id.textViewDay4);
        textViewDay5 = (TextView) findViewById(R.id.textViewDay5);
        textViewDay1MinTemp = (TextView) findViewById(R.id.textViewDay1Min);
        textViewDay1MaxTemp = (TextView) findViewById(R.id.textViewDay1Max);
        textViewDay2MinTemp = (TextView) findViewById(R.id.textViewDay2Min);
        textViewDay2MaxTemp = (TextView) findViewById(R.id.textViewDay2Max);
        textViewDay3MinTemp = (TextView) findViewById(R.id.textViewDay3Min);
        textViewDay3MaxTemp = (TextView) findViewById(R.id.textViewDay3Max);
        textViewDay4MinTemp = (TextView) findViewById(R.id.textViewDay4Min);
        textViewDay4MaxTemp = (TextView) findViewById(R.id.textViewDay4Max);
        textViewDay5MinTemp = (TextView) findViewById(R.id.textViewDay5Min);
        textViewDay5MaxTemp = (TextView) findViewById(R.id.textViewDay5Max);

        imageViewCurrent = (ImageView) findViewById(R.id.imageViewMain);
        imageViewDay1 = (ImageView) findViewById(R.id.imageViewDay1);
        imageViewDay2 = (ImageView) findViewById(R.id.imageViewDay2);
        imageViewDay3 = (ImageView) findViewById(R.id.imageViewDay3);
        imageViewDay4 = (ImageView) findViewById(R.id.imageViewDay4);
        imageViewDay5 = (ImageView) findViewById(R.id.imageViewDay5);
    }
}
