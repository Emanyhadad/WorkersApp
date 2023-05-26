package com.example.workersapp.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.workersapp.databinding.ActivityEditModelBinding;
import com.google.android.flexbox.FlexboxLayoutManager;
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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditModelActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    ActivityEditModelBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;

    ArrayList<String> categoryArrayList;
    List<Uri> uriList;
    ImageAdapter imageAdapter;
    JobCategoryAdapter jobCategoryAdapter;
    List<String> categoriesListF;
    List<String> jobCategory;
    ActivityResultLauncher<String> al1;

    List<String> uriFromStorage;

    String document, date, description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditModelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        categoryArrayList = new ArrayList<>();
        uriList = new ArrayList<>();
        jobCategory = new ArrayList<>();
        Intent intent = getIntent();
        document = intent.getStringExtra("document");

        binding.editLinearCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        choosePicture();

        binding.editImgAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al1.launch("image/*");
            }
        });
        binding.editImgAdd.setOnClickListener(new View.OnClickListener() {
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
                            binding.editEtoJobType.setAdapter(adapter);
                        } else {
                            Toast.makeText(getBaseContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        categoryJobType();
        getData();

        binding.editBtnEditModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 description = binding.editEtDescription.getText().toString();
                 date = binding.editTvCalender.getText().toString();
                String userPhoneNumber = firebaseUser.getPhoneNumber();
                if (uriList.size() != 0) {
                    //نرفع الصور ونخزنهم
                    uriFromStorage = new ArrayList<>();
                    binding.editBtnEditModel.setVisibility(View.GONE);
                    binding.progressBarEdit.setVisibility(View.VISIBLE);
                    for (int i = 0; i < uriList.size(); i++) {
                        StorageReference reference = firebaseStorage.getReference("forms/" + userPhoneNumber + "/" + document + "/" + "/" + uriList.get(i).getLastPathSegment());
                        UploadTask uploadTask = reference.putFile(uriList.get(i));
                        int finalI = i;
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String uriString = uri.toString();
                                        uriFromStorage.add(uriString);
                                        if (finalI == uriList.size() - 1) {
                                            updateModel();
                                        }
                                    }
                                });
                            }
                        });
                    }
                } else {
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
        binding.editTvCalender.setText(date);
    }
    private void choosePicture(){
        //اختيار صورة
        al1 = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null)
                            uriList.add(result);

                        if (uriList.size() != 0) {
                            binding.editRcImg.setVisibility(View.VISIBLE);
                            binding.editImgAdd.setVisibility(View.VISIBLE);
                            binding.editImgAddImage.setVisibility(View.GONE);
                        } else if (uriList.size() == 0) {
                            binding.editRcImg.setVisibility(View.GONE);
                            binding.editImgAdd.setVisibility(View.GONE);
                            binding.editImgAddImage.setVisibility(View.VISIBLE);
                        }

                        imageAdapter = new ImageAdapter(uriList, getBaseContext(), new DeleteListener() {
                            @Override
                            public void onDelete(int pos) {
                                uriList.remove(uriList.get(pos));
                                imageAdapter.notifyItemRemoved(pos);
                                imageAdapter.notifyItemRangeChanged(pos, uriList.size());
                                if (uriList.size() != 0) {
                                    binding.editRcImg.setVisibility(View.VISIBLE);
                                    binding.editImgAdd.setVisibility(View.VISIBLE);
                                    binding.editImgAddImage.setVisibility(View.GONE);
                                } else if (uriList.size() == 0) {
                                    binding.editRcImg.setVisibility(View.GONE);
                                    binding.editImgAdd.setVisibility(View.GONE);
                                    binding.editImgAddImage.setVisibility(View.VISIBLE);
                                }

                            }
                        });
                        binding.editRcImg.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL, false));
                        binding.editRcImg.setAdapter(imageAdapter);
                    }
                });
    }
    private void categoryJobType(){
        binding.editEtoJobType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCategory = ((TextView) view).getText().toString();
                jobCategory.add(selectedCategory);
                binding.editEtoJobType.setText("");

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
                binding.editRecyclerView.setLayoutManager(layoutManager);
                binding.editRecyclerView.setAdapter(jobCategoryAdapter);
            }
        });
    }
    private void getData(){
        firebaseFirestore.collection("forms")
                .document(firebaseUser.getPhoneNumber())
                .collection("userForm")
                .document(document)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> images = (List<String>) documentSnapshot.get("images");
                        for (String imageUrl : images) {
                            if (uriList.size() != 0) {
                                binding.editRcImg.setVisibility(View.VISIBLE);
                                binding.editImgAdd.setVisibility(View.VISIBLE);
                                binding.editImgAddImage.setVisibility(View.GONE);
                            } else if (uriList.size() == 0) {
                                binding.editRcImg.setVisibility(View.GONE);
                                binding.editImgAdd.setVisibility(View.GONE);
                                binding.editImgAddImage.setVisibility(View.VISIBLE);
                            }
                            uriList.add(Uri.parse(imageUrl));
                            imageAdapter = new ImageAdapter(uriList, getBaseContext(), new DeleteListener() {
                                @Override
                                public void onDelete(int pos) {
                                    uriList.remove(uriList.get(pos));
                                    imageAdapter.notifyItemRemoved(pos);
                                    imageAdapter.notifyItemRangeChanged(pos, uriList.size());
                                    if (uriList.size() != 0) {
                                        binding.editRcImg.setVisibility(View.VISIBLE);
                                        binding.editImgAdd.setVisibility(View.VISIBLE);
                                        binding.editImgAddImage.setVisibility(View.GONE);
                                    } else if (uriList.size() == 0) {
                                        binding.editRcImg.setVisibility(View.GONE);
                                        binding.editImgAdd.setVisibility(View.GONE);
                                        binding.editImgAddImage.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            binding.editRcImg.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.HORIZONTAL, false));
                            binding.editRcImg.setAdapter(imageAdapter);
                        }

                        List<String> categories = (List<String>) documentSnapshot.get("categoriesList");

                        for (String categoryList : categories) {
                            jobCategory.add(categoryList);
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
                            binding.editRecyclerView.setLayoutManager(layoutManager);
                            binding.editRecyclerView.setAdapter(jobCategoryAdapter);
                        }

                        String description = (String) documentSnapshot.get("description");
                        binding.editEtDescription.setText(description);

                        String date = (String) documentSnapshot.get("date");
                        binding.editTvCalender.setText(date);
                    }
                });

    }

    private void updateModel(){
        firebaseFirestore.collection("forms")
                .document(firebaseUser.getPhoneNumber())
                .collection("userForm")
                .document(document)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("description", description);
                        map.put("date", date);
                        List<String> images = (List<String>) documentSnapshot.get("images");
                        if (images != null) {
                            uriFromStorage.addAll(images);
                        }
                        if (!uriFromStorage.isEmpty()) {
                            map.put("images", uriFromStorage);
                        }
                        if (!jobCategory.isEmpty()) {
                            map.put("categoriesList", jobCategory);
                        }
                        firebaseFirestore.collection("forms")
                                .document(firebaseUser.getPhoneNumber())
                                .collection("userForm")
                                .document(document)
                                .update(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        binding.progressBarEdit.setVisibility(View.GONE);
                                        binding.editBtnEditModel.setVisibility(View.VISIBLE);
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditModelActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

    }
}
