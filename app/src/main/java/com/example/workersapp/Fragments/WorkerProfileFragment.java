package com.example.workersapp.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.workersapp.Activities.EditWorkerProfileActivity;
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

    FragmentWorkerProfileBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkerProfileBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        imagesList = new ArrayList<>();
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> tabs = new ArrayList<>();
        sp = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        editor = sp.edit();


        getData();

        String token = sp.getString("token", "");

        Log.d("tokenWorker",token);
//        Toast.makeText(getContext(), "token: "+token, Toast.LENGTH_SHORT).show();

        tabs.add("آراء العملاء");
        tabs.add("نماذج الأعمال");

        fragments.add(WorkerReviewsFragment.newInstance());
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

        ActivityResultLauncher<Intent> arl = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
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

        binding.pWorkerImgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditWorkerProfileActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    private void getData() {
        db.collection("users").document(Objects.requireNonNull(firebaseUser.getPhoneNumber())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                    Glide.with(getContext()).load(image).circleCrop().error(R.drawable.worker).into(binding.pWorkerImg);

                    long timestamp = firebaseUser.getMetadata().getCreationTimestamp();
                    // حولنا long -> date
                    Date date = new Date(timestamp);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String formattedDate = dateFormat.format(date);
                    binding.pWorkerJoinDate.setText(formattedDate);


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }


//    private void updateToken(String refreshToken) {
//        // قم بإنشاء كائن Token باستخدام رمز الاشتراك المحدث
//        Token token = new Token(refreshToken);
//
//        // استعراض العملاء المسجلين في التطبيق وتحديث رمز الاشتراك الخاص بهم
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference documentReference = db.collection("tokens").document(firebaseUser.getUid());
//        documentReference.set(token)
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "Token updated successfully"))
//                .addOnFailureListener(e -> Log.d(TAG, "Failed to update token: " + e.getMessage()));
//
//        // حفظ رمز الاشتراك في SharedPreferences (اختياري)
//        editor.putString("FCM_TOKEN", token.getToken());
//        editor.apply();
//    }


}