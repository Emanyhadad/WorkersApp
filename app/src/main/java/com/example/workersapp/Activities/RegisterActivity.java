package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.User;
import com.example.workersapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    ActivityRegisterBinding binding;
    FirebaseFirestore db;
    FirebaseStorage storage;
    private static final int REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST = 1;
    DatePickerDialog dpd;
    String age, fullName, nickName, birth, gender;
    int genderId;
    private Uri imageUri;
    String profileImageUrlNow;
    private FusedLocationProviderClient mFusedLocationClient;

    @SuppressLint("MissingPermission")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot() ) ;


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // التحقق مما إذا كان التطبيق يملك إذن الوصول إلى معرض الصور أم لا
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // إذا لم يكن لديك إذن ، فقم بطلبه من المستخدم.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            // إذا كان لديك إذن ، قم بتنفيذ الإجراءات اللازمة للوصول إلى معرض الصور هنا.
        }
        // Check if the image exists in internal storage
        File file = new File(getFilesDir(), "my_image.jpg");
        if (file.exists()) {
            // If the image exists, load it into the ImageView
            Glide.with(this).load(file).centerCrop()
                    .error(R.drawable.user).into(binding.imageView);
        } else {
            // If the image does not exist, download it from Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("images/my_image.jpg");

            // Download the image into internal storage
            File localFile = new File(getFilesDir(), "my_image.jpg");
            imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // If the image is downloaded successfully, load it into the ImageView
                    Glide.with(getBaseContext()).load(localFile).centerCrop()
                            .error(R.drawable.user).into(binding.imageView);
                }
            });
        }

        // Initialize FusedLocationProviderClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // User's location retrieved successfully, do something with it
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Pass the user's location to the location screen
                            Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            startActivity(intent);
                        } else {
                            // Location is null, handle the error
                        }
                    }
                });
        binding.personBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        binding.personAddImgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //     Handle the result of the image picker intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of the selected image
            imageUri = data.getData();

            // Save the image to local storage
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                FileOutputStream fos = openFileOutput("my_image.jpg", Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Upload the image to Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("images/my_image.jpg");
            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(Task<Uri> task) {
                            profileImageUrlNow = task.getResult().toString();
                            Glide.with(getBaseContext()).load(imageUri).centerCrop()
                                    .error(R.drawable.user).into(binding.imageView);

                            binding.personBtnNext.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    fullName = binding.personFullName.getText().toString();
                                    nickName = binding.personNickName.getText().toString();
                                    birth = binding.personBirth.getText().toString();
                                    genderId = binding.personRadioGroup.getCheckedRadioButtonId();
                                    gender = findViewById(genderId).toString();

                                    if (binding.personMale.isChecked()) {
                                        gender = "ذكر";
                                    } else {
                                        gender = "أنثى";
                                    }

                                    User user = new User(fullName, nickName, birth, gender, profileImageUrlNow);

                                    if (!fullName.isEmpty() && !nickName.isEmpty() && !birth.isEmpty()) {
                                        db.collection("person")
                                                .add(user)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Log.d("PersonalSuccess", "DocumentSnapshot written with ID: " + documentReference.getId());
                                                        Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("PersonalField", e.getMessage());
                                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        startActivity(new Intent(getBaseContext(), MapsActivity.class));
                                    } else {
                                        if (fullName.isEmpty()) {
                                            binding.personFullName.setError("يرجى تعبئة هذا الحقل");
                                        } else if (nickName.isEmpty()) {
                                            binding.personNickName.setError("يرجى تعبئة هذا الحقل");
                                        } else if (birth.isEmpty()) {
                                            binding.personBirth.setError("يرجى تعبئة هذا الحقل");
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
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
//                                db.collection("users").document("user1")
//                                        .set(locationMap)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                // location saved successfully
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                // error occurred while saving the location
//                                            }
//                                        });
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

    private void showDatePickerDialog() {
        Calendar calendar = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    this,
                    year,
                    month,
                    dayOfMonth
            );
            datePickerDialog.show();
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
        binding.personBirth.setText(date);
    }
}