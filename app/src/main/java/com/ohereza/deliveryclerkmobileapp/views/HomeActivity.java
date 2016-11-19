package com.ohereza.deliveryclerkmobileapp.views;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ohereza.deliveryclerkmobileapp.R;
import com.ohereza.deliveryclerkmobileapp.helper.Configs;
import com.ohereza.deliveryclerkmobileapp.helper.DeliveryRequestUpdater;
import com.ohereza.deliveryclerkmobileapp.helper.DeliveryRequestUpdaterResponse;
import com.ohereza.deliveryclerkmobileapp.helper.LoginResponse;
import com.ohereza.deliveryclerkmobileapp.interfaces.PdsAPI;
import com.ohereza.deliveryclerkmobileapp.other.CircleTransform;
import com.ohereza.deliveryclerkmobileapp.other.MyApplication;
import com.ohereza.deliveryclerkmobileapp.services.LocatorService;
import com.ohereza.deliveryclerkmobileapp.services.MyPubnubListenerService;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;
import static com.ohereza.deliveryclerkmobileapp.helper.Configs.PREFS_NAME;
import static com.ohereza.deliveryclerkmobileapp.helper.Configs.pubnub;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG_PUBNUB = "pubnub";
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker marker;

    private LocationManager manager;

    private PolylineOptions mPolylineOptions;

    private LatLng clientLocation;
    private LatLng myLocation;

    // urls to load navigation header background image
    // and profile image
    private static final String urlProfileImg = "https://media.licdn.com/mpr/mpr/shrinknp_200_200/p/4/000/148/278/26f545a.jpg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";

    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private ClearableCookieJar cookieJar;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private PdsAPI pdsAPI;
    private Location mCurrentLocation;
    private int gpsStatus = 0;

    //by default zoom to the clerk location
    private boolean zoomToClient = true;

    private SharedPreferences sharedPreferences;
    private String username;

    public HomeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NOTICE");
            builder.setMessage("Please enable GPS to allow tracking of your location");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Prompt to enable location.
                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(onGPS);
                }
            });
            builder.create();
            builder.show();

        }

//        try {
//            gpsStatus = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        if (gpsStatus == 0) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("NOTICE");
//            builder.setMessage("Please enable GPS to allow tracking of your location");
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    // Prompt to enable location.
//                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivity(onGPS);
//                }
//            });
//            builder.create();
//            builder.show();
//        }

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header with image, name, and address
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.address);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        // Find logged in user
        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        username = sharedPreferences.getString("usr",null);

        // Pubnub subscribe to a channel
        pubnub.subscribe().channels(Arrays.asList(username)).execute();

        // Listen for incoming messages
        //pubnub.addListener(new MyPubnubListenerService());

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                // Handle new message stored in message.message
                Log.v(TAG_PUBNUB, "message(" + message.getMessage() + ")");
                // {"order_id":"f88d553b6b","type":"Delivery Request"}
                JSONObject jsonRequest = null;

                try {
                    jsonRequest = new JSONObject(String.valueOf(message.getMessage()));
                    Log.v(TAG_PUBNUB, "json object: "+jsonRequest);

                    if (jsonRequest != null && jsonRequest.getString("type").equalsIgnoreCase("Delivery Request")) {
                        // Handle new delivery request received
                        // launch notification activity
                        Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);

                        // update sharedPreferences with the order_id
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("order_id",jsonRequest.getString("order_id"));
                        editor.commit();
                        //intent.putExtra("order_id", jsonRequest.getString("order_id"));
                        startActivity(intent);


                    } else if(message.getMessage().toString().toLowerCase().contains("latlng")) {
                            // Message format
                            // {"wc_order_58304ca8159ae":{"latlng":[-1.94442,30.089541]}}
                            // json object: {"wc_order_58304ca8159ae":{"latlng":[-1.94442,30.089541]}}
                            zoomToClient = false;
                            String latLon = message.getMessage().toString().split("(\\{)|(:)|(\\[)|(\\])")[3];
                            double lat = Double.parseDouble(latLon.split(",")[0]);
                            double lon = Double.parseDouble(latLon.split(",")[1]);

                            clientLocation = new LatLng(lat, lon);
                            mPolylineOptions = new PolylineOptions();
                            mPolylineOptions.color(Color.BLUE).width(10);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updatePolyline();
                                    updateCamera();
                                    updateMarker();
                                }

                            });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private synchronized void updatePolyline() {
                mMap.clear();
                mMap.addPolyline(mPolylineOptions.add(clientLocation));

            }

            private synchronized void updateMarker() {
                mMap.addMarker(new MarkerOptions().position(clientLocation));
            }

            private synchronized void updateCamera() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(clientLocation, 14));
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            }
        });

    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website
     */
    private void loadNavHeader() {
        txtName.setText("ClerkId: Kabagamba");
        txtWebsite.setText("addr: ibereshi");

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
    }

//
//    private void selectNavMenu() {
//        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
//    }


    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_history:
                        startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
                        drawer.closeDrawers();
                        return true;
/*                    case R.id.nav_notifications:
                        startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
                        drawer.closeDrawers();
                        return true;*/
                    case R.id.nav_picked:
                        updateDeliveryRequestStatus("Delivering");
                        //startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_delivered:
                        updateDeliveryRequestStatus("Delivered");
                        //startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
                        drawer.closeDrawers();
                        return true;
/*                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(HomeActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;*/
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                //loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                //loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 2) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Switch Online and Offline
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();
            // clear saved credentials
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            // stop running locator service
            Intent locationServiceIntent = new Intent(this,
                    LocatorService.class);
            stopService(locationServiceIntent);
            // Go back to log in screen
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            finish();

            return true;
        }




//        //switch btn off and ON
//        if (id == R.id.action_status) {
//            Toast.makeText(getApplicationContext(), "User Offline", Toast.LENGTH_LONG).show();
//            return true;
//        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateDeliveryRequestStatus(final String newStatus){

        cookieJar = new PersistentCookieJar(new SetCookieCache(),
                new SharedPrefsCookiePersistor(getApplicationContext()));
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Configs.serverAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        pdsAPI = retrofit.create(PdsAPI.class);

        pdsAPI.login("Administrator", "pds").enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response){

                pdsAPI.updateDeliveryRequest( sharedPreferences.getString("order_id",null),
                        new DeliveryRequestUpdater(newStatus)).enqueue(
                        new Callback<DeliveryRequestUpdaterResponse>() {
                            @Override
                            public void onResponse(Call<DeliveryRequestUpdaterResponse> call,
                                                   Response<DeliveryRequestUpdaterResponse> response){
                                Toast.makeText(getApplicationContext(),
                                        "Request successfully accepted", Toast.LENGTH_LONG).show();
                                finish();
                            }

                            @Override
                            public void onFailure(Call<DeliveryRequestUpdaterResponse> call, Throwable t){
                            }
                        }
                );
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.mapActivityPaused();

        // start pubnub service
        Intent service = new Intent(getApplicationContext(), MyPubnubListenerService.class);
        startWakefulService(getApplicationContext(), service);

    }


    @Override
    public void onResume() {
        super.onResume();
        MyApplication.mapActivityResumed();

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NOTICE");
            builder.setMessage("Please enable GPS to allow tracking of your location");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Prompt to enable location.
                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(onGPS);
                }
            });
            builder.create();
            builder.show();
        }
        //}

//        try {
//            gpsStatus = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
//            Toast.makeText(this,"While trying: "+gpsStatus, Toast.LENGTH_LONG);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }
//        Toast.makeText(this,"After try: "+gpsStatus, Toast.LENGTH_LONG);
//        if (gpsStatus == 0) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("NOTICE");
//            builder.setMessage("Please enable GPS to allow tracking of your location");
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    // Prompt to enable location.
//                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivity(onGPS);
//                }
//            });
//            builder.create();
//            builder.show();
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getUiSettings().setMapToolbarEnabled(true);

        //check if location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        Location loc = getMyLocation();

        if (loc != null) {
            Toast toast = Toast.makeText(this, "Loc is null 1st", Toast.LENGTH_LONG);
            myLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(myLoc).title("My auto loc"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16.0f));
        }

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //mLocationRequest.setSmallestDisplacement(0.1F);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Double lat = Double.valueOf(mLastLocation.getLatitude());
            Double lon = Double.valueOf(mLastLocation.getLongitude());
            myLocation = new LatLng(lat, lon);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16.0f));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        //remove previous current location Marker
        if (marker != null) {
            marker.remove();
        }

        double lat = mLastLocation.getLatitude();
        double lon = mLastLocation.getLongitude();

        myLocation = new LatLng(lat, lon);

        marker = mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        if (zoomToClient) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }

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

    private Location getMyLocation() {
        // Get location from GPS if it's available
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast toast= Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG);
            toast.show();
            return null;
        }
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            myLocation = lm.getLastKnownLocation(provider);
        }

        return myLocation;
    }
}