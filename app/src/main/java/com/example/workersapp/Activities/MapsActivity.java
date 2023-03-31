package com.example.workersapp.Activities;

import static android.content.ContentValues.TAG;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FirebaseFirestore db;
    //FusedLocationProviderClient: توفر الوصول إلى آخر موقع معروف للجهاز ويمكنها باستمرار تحديث الموقع في الوقت الفعلي.
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //    Spinner mySpinner;
//    Button next_bottom_sheet;
//    EditText sheetTitle;
    ArrayAdapter<CharSequence> adapter;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        View parentLayout = getLayoutInflater().inflate(R.layout.bottom_sheet, null);


        db = FirebaseFirestore.getInstance();
        // Initialize FusedLocationProviderClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
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


//        db.collection("person")
//                .add(new Location(latitude,longitude))
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d("PersonalSuccess", "DocumentSnapshot written with ID: " + documentReference.getId());
//                        Toast.makeText(MapsActivity.this, "Success Maps", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception e) {
//                Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });


//        DocumentReference userLocationRef = db.collection("person").document("location");
//        userLocationRef.update("latitude", latitude, "longitude", longitude)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
////                        Log.d(TAG, "Location updated successfully");
//                        Toast.makeText(MapsActivity.this, "Location updated successfully", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
////                        Log.w(TAG, "Error updating location", e);
//                        Toast.makeText(MapsActivity.this, "Error updating location", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//    } else {
//        // Location is null, handle the error
//    }

//        mFusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<android.location.Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) {
        ////////////////////////////////////////////////
//                            // User's location retrieved successfully, do something with it
//                            double latitude = location.getLatitude();
//                            double longitude = location.getLongitude();
//
//                            // Pass the user's location to the location screen
//                            Intent intent = new Intent(getBaseContext(), MapsActivity.class);
//                            intent.putExtra("latitude", latitude);
//                            intent.putExtra("longitude", longitude);
//                            startActivity(intent);

        //////////////////////////////////////


//                            CollectionReference userLocationRef = db.collection("person");
//                            userLocationRef.add(new com.example.workersapp.Location(latitude,longitude))
//                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                        @Override
//                                        public void onSuccess(DocumentReference documentReference) {
//                                            Toast.makeText(MapsActivity.this, "success Maps", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        } else {
//                            // Location is null, handle the error
//                        }
//                    }
//                });


        /////////////////////////////////////////


        // Save the location to Firebase Realtime Database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("users").child("location");
//        myRef.setValue(new com.example.workersapp.Location(latitude, longitude));
//
//// Retrieve the location from Firebase Realtime Database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
////                Location location = dataSnapshot.getValue(Location.class);
//                com.example.workersapp.Location location = dataSnapshot.getValue(com.example.workersapp.Location.class);
//                if (location != null) {
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//                    // Do something with the location
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value, handle the error
//            }
//        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_city);

        EditText sheet = dialog.findViewById(R.id.sheetTitle);
        Button next = dialog.findViewById(R.id.sheetBtnNext);
        AutoCompleteTextView mySpinner = dialog.findViewById(R.id.sheetCity);
//        next_bottom_sheet = parentLayout.findViewById(R.id.sheetBtnNext);
//        sheetTitle = parentLayout.findViewById(R.id.sheetTitle);

        adapter = ArrayAdapter.createFromResource(
                this,
                R.array.cities,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = sheet.getText().toString();
                String city = mySpinner.getText().toString();

                if (!title.isEmpty() && !city.isEmpty()) {

                    Map<String, Object> data = new HashMap<>();
                    data.put("city", city);
                    data.put("title", title);

                    db.collection("person")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(MapsActivity.this, "success add city and title", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                    startActivity(new Intent(getBaseContext(), CvActivity.class));
                } else if (title.isEmpty()) {
                    sheet.setError("يرجى تعبئة هذا الحقل");
                }
            }
        });
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
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> userLocation = new HashMap<>();
                                userLocation.put("latitude", location.getLatitude());
                                userLocation.put("longitude", location.getLongitude());
                                db.collection("users").document("user1")
                                        .set(userLocation)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
//                                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                            }
                        }
                    });
        } else {

        }
        showDialog();
    }
    @SuppressLint({"MissingSuperCall", "MissingPermission"})
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, proceed to get the user's location
                    FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
                    client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // save the location on Firestore
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//                                if (currentUser != null) {
//                                    String userId = currentUser.getUid();
                                Map<String, Object> locationMap = new HashMap<>();
                                locationMap.put("latitude", location.getLatitude());
                                locationMap.put("longitude", location.getLongitude());

                                db.collection("users").document("user1")
                                        .set(locationMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // location saved successfully
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // error occurred while saving the location
                                            }
                                        });
                            }
                        }
                    });
                } else {
                    // permission denied, inform the user and handle the case
                }
                return;
            }
        }
    }

//    @SuppressLint("MissingPermission")
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
////        // Add a marker in Sydney and move the camera
////        LatLng sydney = new LatLng(-34, 151);
////        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
////        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        mMap = googleMap;
//
//        // Enable location button on map
//        mMap.setMyLocationEnabled(true);
//
//        // Set default location to user's current location
//        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
//            if (location != null) {
//                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//            }
//        });
//
//        // Add marker on map and save location to Firebase on long click
//        mMap.setOnMapLongClickListener(latLng -> {
//            mMap.clear(); // remove previous marker if any
//            mMap.addMarker(new MarkerOptions().position(latLng));
//            mDatabase.child("user_location").setValue(latLng);
//        });
//    }
}