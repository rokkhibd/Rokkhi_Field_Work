package com.rokkhi.rokkhifieldwork;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AreaCoveredMapActivity extends FragmentActivity implements OnMapReadyCallback,

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
    FirebaseFirestore db;
    //ArrayList<LatLng>latLngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_coverd_map);

        db=FirebaseFirestore.getInstance();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.area_map);

        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            //getLastKnownLocation();
        }

        db.collection("AreaLocation").document().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String area_latitude=documentSnapshot.getString("Latitude");
                    String area_longitude=documentSnapshot.getString("Longitude");

                    latitude=Double.parseDouble(area_latitude);
                    longitude=Double.parseDouble(area_longitude);
                }
            }
        });

        MarkerOptions markerOptions=new MarkerOptions();
        ArrayList<LatLng>latLngs=new ArrayList<>();
        latLngs.add(new LatLng(latitude,longitude));


        for (LatLng point : latLngs) {
            markerOptions.position(point);
            markerOptions.title("someTitle");
            markerOptions.snippet("someDesc");
            googleMap.addMarker(markerOptions);
        }

       /* Marker m1 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude,longitude))
                .anchor(0.5f, 0.5f)
                .title("Title1")
                .snippet("Snippet1")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


        Marker m2 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(23.7931355,90.405023))
                .anchor(0.5f, 0.5f)
                .title("Title2")
                .snippet("Snippet2")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
*/
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


    }

    private void setMarker(){
        db.collection("AreaLocation").document().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String area_latitude=documentSnapshot.getString("Latitude");
                    String area_longitude=documentSnapshot.getString("Longitude");

                    latitude=Double.parseDouble(area_latitude);
                    longitude=Double.parseDouble(area_longitude);
                }
            }
        });

        LatLng latLng=new LatLng(latitude,longitude);
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
    }
}
