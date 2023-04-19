package com.example.workersapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.workersapp.databinding.ActivityMaps2Binding;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    //FusedLocationProviderClient: توفر الوصول إلى آخر موقع معروف للجهاز ويمكنها باستمرار تحديث الموقع في الوقت الفعلي.
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    ArrayAdapter<String> adapter;
    ArrayList<String> list;

    AutoCompleteTextView mySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Toast.makeText(this,"account: "+ LoginActivity.sp.getString("accountType", ""), Toast.LENGTH_SHORT).show();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        list = new ArrayList<>();

        fetchData();

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
        mySpinner = dialog.findViewById(R.id.sheetCity);

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

                    if (LoginActivity.sp.getString("accountType", "").equals("worker")) {
                        String workerId = getIntent().getStringExtra("workerId");

                        db.collection("user").document("worker").collection(firebaseUser.getUid())
                                .document(workerId)
                                .update(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MapsActivity2.this, "success add city and title", Toast.LENGTH_SHORT).show();

                                    }
                                });
                        startActivity(new Intent(getBaseContext(), CvActivity.class));

                    } else if (LoginActivity.sp.getString("accountType", "").equals("work owner")) {

                        String ownerId = getIntent().getStringExtra("ownerId");
                        db.collection("user").document("work owner").collection(firebaseUser.getUid())
                                .document(ownerId)
                                .update(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MapsActivity2.this, "success add city and title", Toast.LENGTH_SHORT).show();

                                    }
                                });
                        startActivity(new Intent(getBaseContext(), CvActivity.class));
                    }
                } else if (title.isEmpty()) {
                    sheet.setError("يرجى تعبئة هذا الحقل");
                }
            }
        });
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
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> userLocation = new HashMap<>();
                                userLocation.put("latitude", location.getLatitude());
                                userLocation.put("longitude", location.getLongitude());

                                if (LoginActivity.sp.getString("accountType", "").equals("worker")) {
                                    String workerId = getIntent().getStringExtra("workerId");
                                    db.collection("user").document("worker").collection(firebaseUser.getUid())
                                            .document(workerId)
                                            .update(userLocation)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(MapsActivity2.this, "success add city and title", Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                } else if (LoginActivity.sp.getString("accountType", "").equals("work owner")) {
                                    String ownerId = getIntent().getStringExtra("ownerId");
                                    db.collection("user").document("work owner").collection(firebaseUser.getUid())
                                            .document(ownerId)
                                            .update(userLocation)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(MapsActivity2.this, "success add city and title", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
//
//
//                                if (LoginActivity.sp.getString("accountType", "").equals("worker")) {
//                                    db.collection("user").document("worker").collection(firebaseUser.getUid())
//                                            .add(userLocation)
//                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                @Override
//                                                public void onSuccess(DocumentReference documentReference) {
//                                                    Toast.makeText(MapsActivity2.this, "success add city and title", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(Exception e) {
//
//                                                }
//                                            });
//                                } else if (LoginActivity.sp.getString("accountType", "").equals("work owner")) {
//                                    db.collection("user").document("work owner").collection(firebaseUser.getUid())
//                                            .add(userLocation)
//                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                @Override
//                                                public void onSuccess(DocumentReference documentReference) {
//                                                    Toast.makeText(MapsActivity2.this, "success add city and title", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(Exception e) {
//
//                                                }
//                                            });
//                                }
                            }
                        }
                    });
        } else {

        }
        showDialog();
    }

    public void fetchData(){
        db.collection("city")
                .document("QguJvTTXU6qm1LYco10m")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // تحميل البيانات وإضافتها إلى ArrayList
                            ArrayList<String> cityNames = new ArrayList<>();
                            cityNames.add(documentSnapshot.getString("city1"));
                            cityNames.add(documentSnapshot.getString("city2"));
                            cityNames.add(documentSnapshot.getString("city3"));
                            cityNames.add(documentSnapshot.getString("city4"));
                            cityNames.add(documentSnapshot.getString("city5"));
                            cityNames.add(documentSnapshot.getString("city6"));
                            cityNames.add(documentSnapshot.getString("city7"));
                            cityNames.add(documentSnapshot.getString("city8"));

                            // إنشاء ArrayAdapter وتعيينه كبيانات مصدر لـ Spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_item, cityNames);
//                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mySpinner.setAdapter(adapter);
                        } else {
                            Toast.makeText(MapsActivity2.this, "No such document", Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "No such document");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapsActivity2.this, "get failed with", Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, "get failed with ", e);
                    }
                });

    }






//    @SuppressLint({"MissingSuperCall", "MissingPermission"})
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_LOCATION: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, proceed to get the user's location
//                    FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
//                    client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            if (location != null) {
//                                // save the location on Firestore
//                                FirebaseFirestore db = FirebaseFirestore.getInstance();
////                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
////                                if (currentUser != null) {
////                                    String userId = currentUser.getUid();
//                                Map<String, Object> locationMap = new HashMap<>();
//                                locationMap.put("latitude", location.getLatitude());
//                                locationMap.put("longitude", location.getLongitude());
//
//                                if (LoginActivity.sp.getString("accountType", "").equals("worker")) {
//                                    db.collection("user").document("worker").collection(firebaseUser.getUid())
//                                            .add(locationMap)
//                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                @Override
//                                                public void onSuccess(DocumentReference documentReference) {
//                                                    Toast.makeText(MapsActivity2.this, "success add city and title", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(Exception e) {
//
//                                                }
//                                            });
//                                } else if (LoginActivity.sp.getString("accountType", "").equals("work owner")) {
//                                    db.collection("user").document("work owner").collection(firebaseUser.getUid())
//                                            .add(locationMap)
//                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                @Override
//                                                public void onSuccess(DocumentReference documentReference) {
//                                                    Toast.makeText(MapsActivity2.this, "success add city and title", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(Exception e) {
//
//                                                }
//                                            });
//                                }
////                                db.collection("users").document("user1")
////                                        .set(locationMap)
////                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
////                                            @Override
////                                            public void onSuccess(Void aVoid) {
////                                                // location saved successfully
////                                            }
////                                        })
////                                        .addOnFailureListener(new OnFailureListener() {
////                                            @Override
////                                            public void onFailure(@NonNull Exception e) {
////                                                // error occurred while saving the location
////                                            }
////                                        });
//                            }
//                        }
//                    });
//                } else {
//                    // permission denied, inform the user and handle the case
//                }
//                return;
//            }
//        }
//    }

}