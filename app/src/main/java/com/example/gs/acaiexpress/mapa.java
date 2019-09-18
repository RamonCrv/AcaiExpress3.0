package com.example.gs.acaiexpress;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
public class mapa extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private GoogleMap mMap ;
    View mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(0.025921, -51.068596);
        final Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("aqui").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_music_background2)));
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.8f));
        //mMap.animateCamera(CameraUpdateFactory.zoomIn());
       // mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        mMap.setTrafficEnabled(false);
        mMap.setMinZoomPreference(10.0f);
        mMap.setMaxZoomPreference(17.5f);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setMyLocationEnabled(true);
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Vocé esta aqui", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
}