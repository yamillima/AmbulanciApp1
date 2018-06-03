package com.ambulanciapp.ambulanciapp1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AlertDialogLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.security.Provider;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static public final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static final int REQUEST_IMAGE_CAPTURE =1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            Toast.makeText(this, "Toma la foto y toca el signo de verificación para continuar", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Enviando foto", Toast.LENGTH_LONG).show();
            LatLng NeivaHospital = new LatLng(2.932305, -75.280828);
            mMap.addMarker(new MarkerOptions().position(NeivaHospital).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()), (float)13.5));

            View btnRecomendaciones = findViewById(R.id.recomendaciones);
            recomendaciones(btnRecomendaciones);
            btnRecomendaciones.setVisibility(View.VISIBLE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    public void recomendaciones(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View recomendaciones = inflater.inflate(R.layout.recomendaciones, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recomendaciones");
        builder.setView(recomendaciones);
        builder.setPositiveButton("Ocultar", null);
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final LatLng Bogota = new LatLng(4.595306, -74.077655);
        LatLng Bangalore = new LatLng(12.978954, 77.589565);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Bangalore, 15));
        mMap.setMyLocationEnabled(true);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (enabled) {
            Toast.makeText(this, "Detectando ubicación", Toast.LENGTH_LONG).show();
            try {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                    Task lastLocation = mFusedLocationProviderClient.getLastLocation();
                    lastLocation.addOnCompleteListener(this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Location mLastKnownLocation;
                                mLastKnownLocation = (Location)task.getResult();
                                LatLng mLastPosition = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastPosition, 15));
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Activa el GPS", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            } catch(SecurityException e)  {
                Log.e("Exception: %s", e.getMessage());
            }
            } else {
            Toast.makeText(this, "Activa el GPS", Toast.LENGTH_LONG).show();
        }
    }

    public void solicitarOnClick(View view) {
        View solicitar = findViewById(R.id.solicitar);
        solicitar.setVisibility(View.INVISIBLE);

        View cancelar = findViewById(R.id.cancelar);
        cancelar.setVisibility(View.VISIBLE);

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_layout, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(dialogLayout);
        alertBuilder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dispatchTakePictureIntent();
            }
        });
        alertBuilder.setCancelable(false);
        alertBuilder.show();
    }
}
