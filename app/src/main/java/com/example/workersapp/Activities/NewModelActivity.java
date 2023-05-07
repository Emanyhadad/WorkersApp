package com.example.workersapp.Activities;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workersapp.Adapters.ImageAdapter;
import com.example.workersapp.Adapters.JobCategoryAdapter;
import com.example.workersapp.Listeneres.DeleteListener;
import com.example.workersapp.Utilities.Model;
import com.example.workersapp.databinding.ActivityNewModelBinding;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class NewModelActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    ActivityNewModelBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    List<Uri> uriList;
    ImageAdapter imageAdapter;
    JobCategoryAdapter jobCategoryAdapter;
    List<String> jobCategory;
    List<String> categoriesListF;

    List<String> uriFromStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewModelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        uriList = new ArrayList<>();
        jobCategory = new ArrayList<>();
        categoriesListF = new ArrayList<>();
        uriFromStorage = new ArrayList<>();


        binding.formLinearCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        //اختيار صورة
        ActivityResultLauncher<String> al1 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null)
                            uriList.add(result);

                        if (uriList.size() != 0) {
                            binding.formRcImg.setVisibility(View.VISIBLE);
                            binding.formImgAdd.setVisibility(View.VISIBLE);
                            binding.formImgAddImage.setVisibility(View.GONE);
                        } else if (uriList.size() == 0) {
                            binding.formRcImg.setVisibility(View.GONE);
                            binding.formImgAdd.setVisibility(View.GONE);
                            binding.formImgAddImage.setVisibility(View.VISIBLE);
                        }

                        imageAdapter = new ImageAdapter(uriList, getBaseContext(), new DeleteListener() {
                            @Override
                            public void onDelete(int pos) {
                                uriList.remove(uriList.get(pos));
                                imageAdapter.notifyItemRemoved(pos);
                                imageAdapter.notifyItemRangeChanged(pos, uriList.size());
                                if (uriList.size() != 0) {
                                    binding.formRcImg.setVisibility(View.VISIBLE);
                                    binding.formImgAdd.setVisibility(View.VISIBLE);
                                    binding.formImgAddImage.setVisibility(View.GONE);
                                } else if (uriList.size() == 0) {
                                    binding.formRcImg.setVisibility(View.GONE);
                                    binding.formImgAdd.setVisibility(View.GONE);
                                    binding.formImgAddImage.setVisibility(View.VISIBLE);
                                }

                            }
                        });
                        binding.formRcImg.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL, false));
                        binding.formRcImg.setAdapter(imageAdapter);
                    }
                });
        binding.formImgAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al1.launch("image/*");
            }
        });
        binding.formImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al1.launch("image/*");
            }
        });
        firebaseFirestore.collection("workCategoryAuto").document("category").get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            categoriesListF = (List<String>) task.getResult().get("categories");
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, categoriesListF);
                            binding.formEtoJobType.setAdapter(adapter);
                        } else {
                            Toast.makeText(getBaseContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        binding.formEtoJobType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCategory = ((TextView) view).getText().toString();
                jobCategory.add(selectedCategory);
                binding.formEtoJobType.setText("");

                if (jobCategory.size() != 0) {
                    jobCategoryAdapter = new JobCategoryAdapter(jobCategory, new DeleteListener() {
                        @Override
                        public void onDelete(int pos) {
                            jobCategory.remove(jobCategory.get(pos));
                            jobCategoryAdapter.notifyItemRemoved(pos);
                            jobCategoryAdapter.notifyItemRangeChanged(pos, jobCategory.size());
                        }
                    });
                }

                FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getBaseContext());
                binding.formRecyclerView.setLayoutManager(layoutManager);
                binding.formRecyclerView.setAdapter(jobCategoryAdapter);
            }
        });

        //التحقق و التخزين
        binding.formBtnAddForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = binding.formEtDescription.getText().toString();
                String date = binding.formTvCalender.getText().toString();
                if (TextUtils.isEmpty(description)) {
                    binding.formEtDescription.setError("يجب ملء هذا الحقل");
                } else if (TextUtils.isEmpty(date) || date.equals("تاريخ الإنجاز")) {
                    binding.formTvCalender.setError("يجب ملء هذا الحقل");
                } else if (jobCategory.size() == 0) {
                    Toast.makeText(getBaseContext(), "قم باختيار فئة عمل واحدة على الاقل", Toast.LENGTH_SHORT).show();
                } else {
                    String uid = firebaseUser.getUid();
                    long time = System.currentTimeMillis();
                    String userPhoneNumber = firebaseUser.getPhoneNumber();
                    if (uriList.size() != 0) {
                        //نرفع الصور ونخزنهم
                        uriFromStorage = new ArrayList<>();
                        for (int i = 0; i < uriList.size(); i++) {
                            StorageReference reference = firebaseStorage.getReference("forms/" + userPhoneNumber + "/" + uid + time + "/" + "/" + uriList.get(i).getLastPathSegment());
                            UploadTask uploadTask = reference.putFile(uriList.get(i));
                            int finalI = i;
                            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return reference.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        String uriString = task.getResult().toString();
                                        if (!uriString.isEmpty()) {
                                            Toast.makeText(getBaseContext(), "im not null", Toast.LENGTH_SHORT).show();
                                            uriFromStorage.add(uriString);
                                            if (finalI == uriList.size() - 1) {
                                                createModel(description, uid, time, date);
                                            }
                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        createModel(description, uid, time, date);
                    }
                    //تخزين الكل
                }
            }
        });

    }

    private void createModel(String description, String uid, long time, String date) {
        Model model = new Model();
        model.setImages(uriFromStorage);
        model.setDescription(description);
        model.setCategoriesList(jobCategory);
        model.setDate(date);
        model.setDocumentId(uid+time);
        uploadForm(model, uid + time);
    }

    private void uploadForm(Model model, String documentName) {
        firebaseFirestore.collection("forms").document(firebaseUser.getPhoneNumber())
                .collection("userForm").document(documentName)
                .set(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String documentId = documentName;
                        if (!task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            finish();
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
        binding.formTvCalender.setText(date);
    }
}
