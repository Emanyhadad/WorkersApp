package com.example.workersapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    //FusedLocationProviderClient: توفر الوصول إلى آخر موقع معروف للجهاز ويمكنها باستمرار تحديث الموقع في الوقت الفعلي.
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    ArrayList<String> list;

    List<String> citiesListF = new ArrayList<>();

    AutoCompleteTextView mySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        list = new ArrayList<>();

        fetchData();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        // اخر موقع
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // إظهار الموقع على الخريطة
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                        }
                    }
                });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_city);
        dialog.setCancelable(false);

        EditText sheet = dialog.findViewById(R.id.sheetTitle);
        Button next = dialog.findViewById(R.id.sheetBtnNext);
        mySpinner = dialog.findViewById(R.id.sheetCity);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = sheet.getText().toString();
                String city = mySpinner.getText().toString();
                String accountType = getIntent().getStringExtra("accountType");
                Intent intent = getIntent();
                String intentSource = intent.getStringExtra("source");

                if (intentSource.equals("RegisterActivity")) {
                    if (!title.isEmpty() && !city.isEmpty()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("city", city);
                        data.put("title", title);
                        data.put("accountType", accountType);

                        db.collection("users").document(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                                .update(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                });

                        if (accountType.equals("worker")) {
                            startActivity(new Intent(getBaseContext(), CvActivity.class));
                            finish();
                        } else if (accountType.equals("work owner")) {
                            startActivity(new Intent(getBaseContext(), WorkOwnerProfileActivity.class));
                            finish();
                        }
                    } else if (title.isEmpty()) {
                        sheet.setError("يرجى تعبئة هذا الحقل");
                    }
                }
                else if (intentSource.equals("NewJobFragment")) {
                    if (!title.isEmpty() && !city.isEmpty()) {
                        Intent intent1 = new Intent();
                        intent1.putExtra("city", city);
                        setResult(RESULT_OK, intent1);
                        finish();
                    }
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker at the user's current location and move the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                                // Save current location to Firebase Firestore
                                Map<String, Object> userLocation = new HashMap<>();
                                userLocation.put("latitude", location.getLatitude());
                                userLocation.put("longitude", location.getLongitude());

//                                db.collection("users").document(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
//                                        .update(userLocation)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void unused) {
//                                                Toast.makeText(MapsActivity.this, "success lat and long", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });

                            }
                        }
                    });
        } else {

        }
        showDialog();
    }

    public void fetchData() {
        db.collection("city").document("city")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            citiesListF = (List<String>) task.getResult().get("cities");
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, citiesListF);
                            mySpinner.setAdapter(adapter);
                        } else {
                            //Todo Add LLField
                        }
                    }
                });
    }

}