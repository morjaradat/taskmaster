package com.example.taskmaster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amplifyframework.core.Amplify;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.net.URL;

public class TaskDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient mFusedLocationClient;

    int PERMISSION_ID = 44;

    private double latitude;
    private double longitude;

    GoogleMap googleMap;


    private static final String TAG = "TaskDetailsActivity";
    private URL url = null;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

        Intent intent = getIntent();
        Log.i(TAG, "onCreate: file name " + intent.getExtras().get(MainActivity.TASK_FILE));

        String fileName = intent.getExtras().get(MainActivity.TASK_FILE).toString();

        getFileFromS3Storage(fileName);


        ImageView imageView = findViewById(R.id.testImg);

        handler = new Handler(Looper.getMainLooper(),
                message -> {
                    Glide.with(getBaseContext())
                            .load(url.toString())
                            .placeholder(R.drawable.ic_pictures)
                            .error(R.drawable.ic_pictures)
                            .centerCrop()
                            .into(imageView);
                    String linkedText = String.format("<a href=\"%s\">download File</a> ", url);

                    TextView test = findViewById(R.id.link);
                    test.setText(Html.fromHtml(linkedText));
                    test.setMovementMethod(LinkMovementMethod.getInstance());
                    return false;
                });


        TextView taskTitle = findViewById(R.id.singleTaskTitle);
        taskTitle.setText(intent.getExtras().get(MainActivity.TASK_TITLE).toString());

        TextView taskBody = findViewById(R.id.task_body);
        taskBody.setText(intent.getExtras().get(MainActivity.TASK_BODY).toString());

        TextView taskStatus = findViewById(R.id.task_status);
        taskStatus.setText(intent.getExtras().get(MainActivity.TASK_STATUS).toString());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private void getFileFromS3Storage(String key) {
        Amplify.Storage.downloadFile(
                key,
                new File(getApplicationContext().getFilesDir() + key),
                result -> Log.i(TAG, "Successfully downloaded: " + result.getFile().getAbsoluteFile()),
                error -> Log.e(TAG, "Download Failure", error)
        );

        Amplify.Storage.getUrl(
                key,
                result -> {
                    Log.i(TAG, "Successfully generated: " + result.getUrl());
                    url = result.getUrl();
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "URL generation failure", error)
        );
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng
                                        (Double.parseDouble(getIntent().getExtras().get(MainActivity.TASK_LATITUDE).toString())
                                                , Double.parseDouble(getIntent().getExtras().get(MainActivity.TASK_LONGITUDE).toString())))
                                .title("Marker"));
                        Log.i(TAG, "onCreate: latitude => " + latitude);
                        Log.i(TAG, "onCreate: longitude => " + longitude);
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat
                        .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }
}