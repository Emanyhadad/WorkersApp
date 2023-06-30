package com.example.workersapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    ActivityRegisterBinding binding;
    FirebaseFirestore db;
    FirebaseStorage storage;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private static final int REQUEST_CODE = 1;
    String fullName, nickName, birth, gender, accountType;
    int genderId;

    public static String image;


    @SuppressLint("MissingPermission")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
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
        ActivityResultLauncher al1 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            Glide.with(getBaseContext()).load(result).circleCrop().error(R.drawable.user).into(binding.personImgUser);
                            StorageReference reference = storage.getReference("users/" + "images/" + firebaseUser.getPhoneNumber());

                            StorageTask<UploadTask.TaskSnapshot> uploadTask =
                                    reference.putFile(result);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                image = task.getResult().toString();
                                            }
                                        }
                                    });
                                }
                            });

                        }
                    }
                }
        );

        binding.personBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        binding.personAddImgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al1.launch("image/*");
            }
        });

        binding.personBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullName = binding.personFullName.getText().toString();
                nickName = binding.personNickName.getText().toString();
                birth = binding.personBirth.getText().toString();
                genderId = binding.personRadioGroup.getCheckedRadioButtonId();
                gender = findViewById(genderId).toString();
                accountType = LoginActivity.sp.getString("accountType", "");

                if (binding.personMale.isChecked()) {
                    gender = "ذكر";
                } else {
                    gender = "أنثى";
                }


                Map<String, Object> data = new HashMap<>();
                data.put("fullName", fullName);
                data.put("nickName", nickName);
                data.put("birth", birth);
                data.put("gender", gender);
                data.put("image", image);

                if (!fullName.isEmpty() && !nickName.isEmpty() && !birth.isEmpty()) {

                    db.collection("users").document(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(RegisterActivity.this, "success phone", Toast.LENGTH_SHORT).show();
                                }
                            });
                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                    intent.putExtra("accountType", accountType);
                    intent.putExtra("source", RegisterActivity.class.getSimpleName());
                    startActivity(intent);

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