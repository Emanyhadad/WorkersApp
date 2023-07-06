package com.example.workersapp.Activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityEditWorkerProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditWorkerProfileActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {
    ActivityEditWorkerProfileBinding binding ;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    public static String image;
    String gender;
    List<String> citiesListF = new ArrayList<>();
    List<String> categoriesListF = new ArrayList<>();
    StorageTask<UploadTask.TaskSnapshot> uploadTask;

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditWorkerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = sp.edit();
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();

        fetchDataCity();
        fetchDataCategory();

        binding.inculd.tvPageTitle.setText( "البيانات الشخصية" );
        binding.inculd.editIcon.setVisibility( View.GONE );
        ActivityResultLauncher al1 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            Glide.with(getBaseContext()).load(result).circleCrop().error(R.drawable.user).into(binding.editImgUser);
                            StorageReference reference = firebaseStorage.getReference("users/"+"images/" + firebaseUser.getPhoneNumber());

                            uploadTask = reference.putFile(result);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            image = task.getResult().toString();
                                        }
                                    });
                                }
                            });

                        }
                    }
                }
        );

        binding.editBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        binding.editAddImgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al1.launch("image/*");
            }
        });

      getData();

      binding.editBtnNext.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              String fullName = binding.editFullName.getText().toString();
              String nickName = binding.editNickName.getText().toString();
              String birth = binding.editBirth.getText().toString();
              String city = binding.editSheetCity.getText().toString();
              int genderId = binding.editRadioGroup.getCheckedRadioButtonId();
              gender = findViewById(genderId).toString();
              String account=  sp.getString("account","null");

              if (binding.editPersonMale.isChecked()) {
                  gender = "ذكر";
              } else {
                  gender = "أنثى";
              }

              Map<String, Object> data = new HashMap<>();

              if (account.equals("worker")){
                  String cv = binding.editCv.getText().toString();
                  String work = binding.editCvWork.getText().toString(); 
                  data.put("cv",cv);
                  data.put("work",work);
              } else if (account.equals("work owner")) {
              }
              data.put("fullName",fullName);
              data.put("nickName",nickName);
              data.put("birth",birth);
              data.put("city",city);
              data.put("gender",gender);
              data.put("image",image);

              firebaseFirestore.collection("users").document(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                      .update(data)
                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {
                              finish();
                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              
                          }
                      });
          }
      });

    }
    private void getData(){
        firebaseFirestore.collection("users").document(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String fullName = documentSnapshot.getString("fullName");
                        String nickName = documentSnapshot.getString("nickName");
                        String birth = documentSnapshot.getString("birth");
                        String city = documentSnapshot.getString("city");
                        String gender = documentSnapshot.getString("gender");
                        String image = documentSnapshot.getString("image");
                        String accountType = documentSnapshot.getString("accountType");

                        editor.putString("account",accountType);
                        editor.apply();

                        if (accountType != null) {
                            if (accountType.equals("worker")) {
                                String cv = documentSnapshot.getString("cv");
                                String work = documentSnapshot.getString("work");
                                binding.editCvGone.setVisibility(View.VISIBLE);
                                binding.editCvWorkGone.setVisibility(View.VISIBLE);
                                binding.editCv.setText(cv);
                                binding.editCvWork.setText(work);
                            } else if (accountType.equals("work owner")) {
                                binding.editCvGone.setVisibility(View.GONE);
                                binding.editCvWorkGone.setVisibility(View.GONE);
                            }
                        }
                        binding.editFullName.setText(fullName);
                        binding.editNickName.setText(nickName);
                        binding.editBirth.setText(birth);
                        binding.editSheetCity.setText(city);
                        Glide.with(getBaseContext())
                                .load(image)
                                .circleCrop()
                                .error(R.drawable.worker)
                                .into(binding.editImgUser);

                        if(gender != null) {
                            if (gender.equals("أنثى")) {
                                binding.editPersonFemale.setChecked(true);
                            } else {
                                binding.editPersonMale.setChecked(true);
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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
        binding.editBirth.setText(date);
    }

    public void fetchDataCity() {
        firebaseFirestore.collection("city").document("city")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            citiesListF = (List<String>) task.getResult().get("cities");
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, citiesListF);
                            binding.editSheetCity.setAdapter(adapter);
                        } else {
                            //Todo Add LLField
                        }
                    }
                });
    }
    public void fetchDataCategory() {
        firebaseFirestore.collection("category").document("category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            categoriesListF = (List<String>) task.getResult().get("categories");
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, categoriesListF);
                            binding.editCvWork.setAdapter(adapter);
                        } else {
                            //Todo Add LLField
                        }
                    }
                });
    }
}