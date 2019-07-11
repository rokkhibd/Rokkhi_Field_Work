package com.rokkhi.rokkhifieldwork;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SeeLocationActivity extends FragmentActivity implements OnMapReadyCallback,

        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mMap;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Location lastLocation;
    Marker currentLocationMarker;
    public static final int REQUEST_PERMISSION_CODE=99;
    int PROXIMITY_RADIUS=10000;
    double latitude,longitude;
    Buildings buildings;
    FirebaseFirestore db;
    DocumentReference documentReference;
    FusedLocationProviderClient fusedLocationProviderClient;
    HouseLocation houseLocation;
    private final String TAG="tag";
    String houseLat,houseLang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_location);

        db=FirebaseFirestore.getInstance();
        houseLocation=new HouseLocation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            getLastKnownLocation();

        }

    }

    protected synchronized void buildGoogleApiClient() {

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //LocationRequest objects are used to request a quality of service for location updates from the
        //FusedLocationProviderApi
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

            //TODO:Get the current location update of the user
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);


        }
        lastLocation=LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(lastLocation!=null){
            Toast.makeText(SeeLocationActivity.this,"Latitude:"+String.valueOf(lastLocation.getLatitude())+
                    "Longitude"+String.valueOf(lastLocation.getLongitude()),Toast.LENGTH_LONG).show();

            saveUserLocation();

           // getLastKnownLocation();

        }

    }

    //TODO: In this method we are going to show location permission
    public boolean checkLocationPermission(){

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSION_CODE);
            }else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSION_CODE);
            }
            return false;
        }else {
            return true;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation=location;

        //TODO: marker zodi onno kono location e set kora thake tahole prothome ta remove korte hobe.
        if (currentLocationMarker!=null){
            currentLocationMarker.remove();
        }

        //TODO:Set a marker to new location
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);

        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentLocationMarker=mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        //TODO: Stop the location update
        if (googleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient ,this);

        }
    }

    //TODO:Save current location lat lang in Firestore DB
   private void saveUserLocation(){

        houseLat=String.valueOf(lastLocation.getLatitude());
        houseLang=String.valueOf(lastLocation.getLongitude());

       Map<String,Object> latlang=new HashMap<>();
       latlang.put("Latitude",houseLat);
       latlang.put("Longitude",houseLang);

       db.collection("AreaLocation").add(latlang).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
           @Override
           public void onComplete(@NonNull Task<DocumentReference> task) {
               if (task.isSuccessful()){
                   Toast.makeText(SeeLocationActivity.this,"Location Saved!!",Toast.LENGTH_SHORT).show();
               }
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(SeeLocationActivity.this,"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
           }
       });
/*
       if (houseLocation!=null){
           DocumentReference doc=db.collection("HouseLocation").document();
           doc.set(houseLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {

                   if (task.isSuccessful()){
                       //Log.d(TAG,"saveuserlocation: \ninserted user location"+
                              // "\n latitude"+houseLocation.getGeo_Point().getLatitude()+
                               //"\n longtitude"+houseLocation.getGeo_Point().getLongitude());
                       houseLocation.getGeo_Point().getLatitude();
                   }
               }
           });
       }*/
   }

   private void getLastKnownLocation(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                if (task.isSuccessful()){
                    lastLocation=task.getResult();
                    GeoPoint geoPoint=new GeoPoint(lastLocation.getLatitude(),lastLocation.getLongitude());

                    Log.d(TAG,"onComplete: latitude:"+geoPoint.getLatitude());
                    Log.d(TAG,"onComplete: longitude:"+geoPoint.getLongitude());

                   // houseLocation.setGeo_Point(geoPoint);
                    //saveUserLocation();
                    //houseLocation.setGeoPoint(geoPoint);
                    //saveUserLocation();
                    //buildings.setGeo_point(geoPoint);
                }
            }
        });

    }



    //When the app will run, a dialogue box will PopUp and this method is being called to handle
    //request permission from user either it is selected as granted or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //permission is granted
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if (googleApiClient==null){
                            buildGoogleApiClient();
                            getLastKnownLocation();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }else //if permission is denied
                {
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
        }
    }


}
