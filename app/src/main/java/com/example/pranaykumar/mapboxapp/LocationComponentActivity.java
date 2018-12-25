package com.example.pranaykumar.mapboxapp;


import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

import static com.example.pranaykumar.mapboxapp.R.string.user_location_permission_explanation;

/**
 * Use the Location component to easily add a device location "puck" to a Mapbox map.
 */
public class LocationComponentActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {

    private static final String TAG ="O_MY";
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private Location origin;
    String id,token;
    DatabaseReference databaseUsers;
    FirebaseDatabase database;
    ArrayList<User> all_users;
    Icon icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_location_component);

        all_users=new ArrayList<>();
        mapView = findViewById(R.id.mapUserView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        database = FirebaseDatabase.getInstance();
        databaseUsers = database.getReference("active_users");
        token=FirebaseInstanceId.getInstance().getId();

    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseUsers = database.getReference("active_users");
        LocationComponentActivity.this.mapboxMap = mapboxMap;
        enableLocationComponent();

        icon = IconFactory.getInstance(LocationComponentActivity.this).fromResource(R.drawable.mapbox_marker_icon_default);


        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(18.79097, 78.914476))
                .title(getString(R.string.draw_custom_marker_options_title))
                .snippet("A7jBjallY")
                .icon(icon));

        mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Log.d("O_MY",token+" "+marker.getSnippet());
                Intent intent =new Intent(LocationComponentActivity.this,ChatActivity.class);
                intent.putExtra("userId",token);
                intent.putExtra("chatWith",marker.getSnippet());

                startActivity(intent);
                return false;
            };
            }
        );

        id = token;
        User current_user=new User("name",origin.getLatitude(),origin.getLongitude(),"email","password",id,token);
        databaseUsers.child(id).setValue(current_user);
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot uniqueSnapshot:dataSnapshot.getChildren()){
                    String userKey=uniqueSnapshot.getKey();
                    LatLng ltlg=new LatLng((double)uniqueSnapshot.child("latitude").getValue(),(double)uniqueSnapshot.child("longitude").getValue());

                    User u=new User((String)uniqueSnapshot.child("name").getValue(),
                            (Double) uniqueSnapshot.child("latitude").getValue(),
                            (Double) uniqueSnapshot.child("longitude").getValue(),
                            (String)uniqueSnapshot.child("email").getValue(),
                            (String)uniqueSnapshot.child("password").getValue(),
                            (String)uniqueSnapshot.child("id").getValue(),
                            (String)uniqueSnapshot.child("token").getValue());
                    all_users.add(u);

                    mapboxMap.addMarker(new MarkerOptions()
                            .position(ltlg)
                            .title(getString(R.string.draw_custom_marker_options_title))
                            .snippet(u.getToken())
                            .icon(icon));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponentOptions options = LocationComponentOptions.builder(this)
                    .trackingGesturesManagement(true)
                    .accuracyColor(ContextCompat.getColor(this, R.color.mapBoxGreen))
                    .build();

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(this, options);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

            origin=locationComponent.getLastKnownLocation();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent();
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        databaseUsers.child(id).removeValue();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}