package meepoohbc.mysos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        DirectionCallback{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Criteria criteria;
    private double latADouble = 13.718487, lngADouble = 100.453948;
    private LatLng userLatLng;
    private int[] mkInts = new int[]{R.mipmap.mk_user, R.mipmap.mk_friend};
    private String[] userStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Setup
        setup();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        createFragement();
    }//Main method

    @Override
    protected void onResume() {
        super.onResume();

        locationManager.removeUpdates(locationListener);

        //for Network
        Location networkLocation = myFindLocation(LocationManager.NETWORK_PROVIDER);
        if (networkLocation != null) {
            latADouble = networkLocation.getLatitude();
            lngADouble = networkLocation.getLongitude();
        }

        //For GPS
        Location gpsLocation = myFindLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            latADouble = gpsLocation.getLatitude();
            lngADouble = gpsLocation.getLongitude();
        }
        Log.d("SiamTwo", "Lat = >>>" + latADouble);
        Log.d("SiamTwo", "Lng = >>>" + lngADouble);



    }

    private void checkAndEditLocation() {

        MyConstant myConstant = new MyConstant();
        String tag = "SiamTree";
        boolean b = true;
        String urlPHP = null;

        try {

            //check
            GetDataToServer getDataToServer = new GetDataToServer(MapsActivity.this);
            getDataToServer.execute(myConstant.getUrlGetAllLocation());
            String strJSON = getDataToServer.get();
            Log.d(tag,"JSON >>> " + strJSON);

            JSONArray jsonArray = new JSONArray(strJSON);
            for(int i =0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (userStrings[1].equals(jsonObject.getString("Name"))) {
                    b = false;

                }// if

                else {
                    myCreateMarker(jsonObject.getString("Name"),
                            new LatLng(Double.parseDouble(jsonObject.getString("Lat")),
                                    (Double.parseDouble(jsonObject.getString("Lng")))), mkInts[1]);

                }

            }// for
            if (b) {

                //No name
                Log.d(tag, "No Name");
                urlPHP = myConstant.getUrlAddLocation();


            } else {

                //Have name
                Log.d(tag, "Have Name");
                urlPHP = myConstant.getUrlEditLocation();

            }

            AddAndEditLocation addAndEditLocation = new AddAndEditLocation(MapsActivity.this);
            addAndEditLocation.execute(userStrings[1], Double.toString(latADouble),
                    Double.toString(lngADouble), urlPHP);
            Log.d(tag, "Result >>>" + addAndEditLocation.get());

        } catch (Exception e) {
            Log.d(tag, "e Check >>>" + e.toString());
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    public Location myFindLocation(String strProvider) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);

        }

        return location;
    }


    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latADouble = location.getLatitude();
            lngADouble = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void setup() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        userStrings = getIntent().getStringArrayExtra("Login");
    }

    private void createFragement() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //setup Center Map
        userLatLng = new LatLng(latADouble,lngADouble);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng,15));

        myCreateMarker(userStrings[1],userLatLng,mkInts[0]);
        checkAndEditLocation();

        //ClickMarker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("SiamV4", "Marker >>>> " + marker.getPosition().latitude);
                Log.d("SiamV4", "Marker >>>> " + marker.getPosition().longitude);

                GoogleDirection.withServerKey("AIzaSyCCvjznonDrveo7ign2W1OlmYxTl6uW-PM")
                        .from(new LatLng(latADouble, lngADouble))
                        .to(marker.getPosition()).transportMode(TransportMode.DRIVING)
                        .execute(MapsActivity.this);

                return true;
            }

        });

    }// onMapReady

    private  void  myCreateMarker(String StrName , LatLng latLng,int inTImage) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(StrName)
        .icon(BitmapDescriptorFactory.fromResource(inTImage)));
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if(direction.isOK()) {
            ArrayList<LatLng> arrayList = direction.getRouteList().get(0).getLegList().get(0).getSectionPoint();
            mMap.addPolyline(DirectionConverter.createPolyline(MapsActivity.this, arrayList, 5, Color.RED));

        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }
}// Main class
