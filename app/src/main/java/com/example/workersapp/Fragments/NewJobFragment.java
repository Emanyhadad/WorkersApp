package com.example.workersapp.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.workersapp.Adapters.ImageAdapter;
import com.example.workersapp.Adapters.JobCategoryAdapter;
import com.example.workersapp.Listeneres.DeleteListener;
//import com.example.workersapp.R;
//import com.example.workersapp.databinding.FragmentNewJobBinding;
import com.example.workersapp.R;
import com.example.workersapp.databinding.FragmentNewJobBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.List;


public class NewJobFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
     FirebaseStorage firebaseStorage;
    List<Uri> uriList = new ArrayList<>();
    String budget;
    String duration;
    ImageAdapter imageAdapter;
    JobCategoryAdapter jobCategoryAdapter;
    List<String> jobCategory = new ArrayList<>();
    List<String> categoriesListF = new ArrayList<>();

    private String mParam1;
    private String mParam2;

    public NewJobFragment() {
        // Required empty public constructor
    }

    public static NewJobFragment newInstance(String param1, String param2) {
        NewJobFragment fragment = new NewJobFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentNewJobBinding binding = FragmentNewJobBinding.inflate(inflater, container, false);

        String[] durationList = {"يوم", "يومين", "3 ايام", "4 ايام", "5 ايام", "اسبوع", "اسبوعين", "3 اسابيع", "شهر", "شهرين"};
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(requireContext(), R.layout.list_item, durationList);
        AutoCompleteTextView autoCompleteDuration = (AutoCompleteTextView) binding.splExpectedWorkDuration.getEditText();
        autoCompleteDuration.setAdapter(durationAdapter);
        autoCompleteDuration.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                duration = adapterView.getItemAtPosition(i).toString();
            }
        });

        String[] budgetList = {"50", "100", "150", "200", "250", "300"};
        ArrayAdapter<String> budgetAdapter = new ArrayAdapter<>(requireContext(), R.layout.list_item, budgetList);
        AutoCompleteTextView autoCompleteBudget = (AutoCompleteTextView) binding.splProjectedBudget.getEditText();
        autoCompleteBudget.setAdapter(budgetAdapter);
        autoCompleteBudget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                budget = adapterView.getItemAtPosition(i).toString();
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
                            binding.rcImg.setVisibility(View.VISIBLE);
                            binding.imgAdd.setVisibility(View.VISIBLE);
                            binding.imgAddImage.setVisibility(View.GONE);
                        } else if (uriList.size() == 0) {
                            binding.rcImg.setVisibility(View.GONE);
                            binding.imgAdd.setVisibility(View.GONE);
                            binding.imgAddImage.setVisibility(View.VISIBLE);
                        }

                        imageAdapter = new ImageAdapter(uriList, getContext(), new DeleteListener() {
                            @Override
                            public void onDelete(int pos) {
                                uriList.remove(uriList.get(pos));
                                imageAdapter.notifyItemRemoved(pos);
                                imageAdapter.notifyItemRangeChanged(pos, uriList.size());
                                if (uriList.size() != 0) {
                                    binding.rcImg.setVisibility(View.VISIBLE);
                                    binding.imgAdd.setVisibility(View.VISIBLE);
                                    binding.imgAddImage.setVisibility(View.GONE);
                                } else if (uriList.size() == 0) {
                                    binding.rcImg.setVisibility(View.GONE);
                                    binding.imgAdd.setVisibility(View.GONE);
                                    binding.imgAddImage.setVisibility(View.VISIBLE);
                                }

                            }
                        });
                        binding.rcImg.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                        binding.rcImg.setAdapter(imageAdapter);
                    }
                });
        binding.imgAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al1.launch("image/*");
            }
        });
        binding.imgAdd.setOnClickListener(new View.OnClickListener() {
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
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categoriesListF);
                            binding.etoJobType.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        binding.etoJobType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (categoriesListF.size() != 0) {
                    jobCategory.add(categoriesListF.get(i));
                }

                binding.etoJobType.setText("");
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

                FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
                binding.recyclerView.setLayoutManager(layoutManager);
                binding.recyclerView.setAdapter(jobCategoryAdapter);

            }
        });

        //التحقق و التخزين
//        binding.btnAddPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String workTitle = binding.etWorkTitle.getText().toString();
//                String description = binding.etDescription.getText().toString();
//                if (!TextUtils.isEmpty(workTitle) && !TextUtils.isEmpty(description)
//                        && !TextUtils.isEmpty(budget) && !TextUtils.isEmpty(duration)) {
//                    if (uriList.size() != 0) {
//                        for (int i = 0; i <uriList.size() ; i++) {
//                            StorageReference reference = firebaseStorage.getReference("posts/" + "+970595560706"+ "/" +""+ uriList.get(i).getLastPathSegment());
//                            UploadTask uploadTask = reference.putFile(uri);
//                            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                                @Override
//                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                    if (!task.isSuccessful()) {
//                                        throw task.getException();
//                                    }
//                                    return reference.getDownloadUrl();
//                                }
//                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Uri> task) {
//                                    if (task.isSuccessful()) {
//
//                                        String uriString = task.getResult().toString();
//
//                                        if (uriString.isEmpty()) {
//                                            updateUserInfo(user);
//                                        }
//
//                                    } else {
//                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//                        //نرفع الصور ونخزنهم
//                    }
//                    if (jobCategory.size() != 0) {
//                        //تخزين الكل
//                    } else {
//                        Toast.makeText(getContext(), "قم باختيار فئة عمل واحدة على الاقل", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(getContext(), "قم بملء جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        return binding.getRoot();
    }


}