package com.example.realtugas4;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mymap;
    private EditText etLatitude;
    private EditText etLongitude;
    private Button btnSimpan;
    private DatabaseReference database;
    private boolean mapTap = false; // Menandakan apakah peta sudah ditap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        btnSimpan = findViewById(R.id.btnSimpan);

        // Menggunakan path yang sesuai dengan struktur Firebase Database Anda
        database = FirebaseDatabase.getInstance().getReference("locations/koordinat");

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TambahRandomColoredMarkerFromEditText();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mymap = googleMap;

        mymap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!mapTap) {
                    // Menambah marker dengan warna biru pada lokasi yang di-tap pertama kali
                    addMarker(latLng, BitmapDescriptorFactory.HUE_BLUE);

                    // Menonaktifkan interaksi pengguna dengan peta
                    mymap.getUiSettings().setAllGesturesEnabled(false);

                    // Mengisi nilai ke EditText
                    etLatitude.setText(String.valueOf(latLng.latitude));
                    etLongitude.setText(String.valueOf(latLng.longitude));

                    mapTap = true;

                    // Menonaktifkan listener setelah tap pertama
                    mymap.setOnMapClickListener(null);
                }
            }
        });
    }

    private void addMarker(LatLng latLng, float markerColor) {
        mymap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Location")
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

        // Menggerakkan kamera ke lokasi marker
        mymap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void addRandomColoredMarker(LatLng latLng) {
        // Menghasilkan warna acak
        float hue = new Random().nextFloat() * 360;

        // Menambah marker dengan warna acak pada lokasi yang di-tap
        addMarker(latLng, hue);
    }

    private void TambahRandomColoredMarkerFromEditText() {
        try {
            double latitude = Double.parseDouble(etLatitude.getText().toString());
            double longitude = Double.parseDouble(etLongitude.getText().toString());

            // Menambah marker dengan warna acak pada koordinat dari EditText
            addRandomColoredMarker(new LatLng(latitude, longitude));

            // Memasukkan atau mengupdate data ke Firebase
            database.child("latitude").setValue(latitude);
            database.child("longitude").setValue(longitude);

            Toast.makeText(this, "Data berhasil disimpan di Firebase", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Masukkan koordinat yang valid", Toast.LENGTH_SHORT).show();
        }
    }
}
