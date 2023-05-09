package com.example.workersapp.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.workersapp.Activities.NewModelActivity;
import com.example.workersapp.Adapters.ImageModelFragAdapter;
import com.example.workersapp.R;
import com.example.workersapp.databinding.FragmentWorkerProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class WorkerProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    List<String> imagesList;
    ImageModelFragAdapter adapter;

    public WorkerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkerProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkerProfileFragment newInstance(String param1, String param2) {
        WorkerProfileFragment fragment = new WorkerProfileFragment();
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
        // Inflate the layout for this fragment
        FragmentWorkerProfileBinding binding = FragmentWorkerProfileBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        db.collection("users").document(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            String nickName = documentSnapshot.getString("nickName");
                            String work = documentSnapshot.getString("work");
                            String cv = documentSnapshot.getString("cv");
                            String city = documentSnapshot.getString("city");
                            String image = documentSnapshot.getString("image");
                            binding.pWorkerUserName.setText(fullName);
                            binding.pWorkerNickName.setText("( " + nickName + " )");
                            binding.pWorkerJobName.setText(work);
                            binding.pWorkerCv.setText(cv);
                            binding.pWorkerLocation.setText(city);
                            binding.pWorkerPhone.setText(firebaseUser.getPhoneNumber());
                            Glide.with(getContext())
                                    .load(image)
                                    .circleCrop()
                                    .error(R.drawable.worker)
                                    .into(binding.pWorkerImg);

                            long timestamp = firebaseUser.getMetadata().getCreationTimestamp();
                            // حولنا long -> date
                            Date date = new Date(timestamp);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            String formattedDate = dateFormat.format(date);

                            binding.pWorkerJoinDate.setText(formattedDate);


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
                    }
                });
        imagesList = new ArrayList<>();
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> tabs = new ArrayList<>();
        tabs.add("آراء العملاء");
        tabs.add("نماذج الأعمال");

        fragments.add(BlankFragment.newInstance("Audi"));
        fragments.add(new BusinessModelsFragment());

        adapter = new ImageModelFragAdapter(getActivity(), fragments);
        binding.FragPager.setAdapter(adapter);

        new TabLayoutMediator(binding.FragTab, binding.FragPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position < tabs.size()) {
                    tab.setText(tabs.get(position));
                }
            }
        }).attach();

        ActivityResultLauncher<Intent> arl = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Snackbar.make(binding.pWorkerCv, "تم الحفظ بنجاح", Snackbar.LENGTH_SHORT).show();
                    }
                });


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewModelActivity.class);
                arl.launch(intent);
            }
        });

        return binding.getRoot();
    }
}