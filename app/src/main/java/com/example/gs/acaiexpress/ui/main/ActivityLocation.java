package com.example.gs.acaiexpress.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.gs.acaiexpress.R;

import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;

public class ActivityLocation extends AppCompatActivity {
    private TextView textLatitude;
    private TextView textLongitude;
    private TextView textCidade;
    private TextView textEstado;
    private TextView textPais;
    private Location location;
    private LocationManager locationManager;
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        textLatitude = (TextView) findViewById(R.id.textLatitude);
        textLongitude = (TextView) findViewById(R.id.textLongitude);
        textCidade = (TextView) findViewById(R.id.textCidade);
        textEstado = (TextView) findViewById(R.id.textEstado);
        textPais = (TextView) findViewById(R.id.textPais);

        double latitude = 0.0;
        double longitude = 0.0;
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
        }else{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location!= null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        textLongitude.setText("Longitude:"+longitude);
        textLatitude.setText("Latitude:"+latitude);
        try {
            address = buscarEndereco (latitude,longitude);
            textCidade.setText("Cidade:"+address.getSubAdminArea());
            textEstado.setText("Estado:"+address.getAdminArea());
            textPais.setText("País..:"+address.getCountryName());
        } catch (Exception e) {
            Log.i("GPS",e.getMessage());
        }
    }

    private Address buscarEndereco(double latitude, double longitude)throws IOException {
        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext());
        addresses = geocoder.getFromLocation(latitude,longitude,1);
        if (addresses.size()>0){
            address = addresses.get(0);
        }return address;





    }


}
