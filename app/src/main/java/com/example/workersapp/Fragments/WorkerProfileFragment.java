package com.example.workersapp.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class WorkerProfileFragment extends Fragment {

    FragmentWorkerProfileBinding binding;

    public static SharedPreferences sharedPreferences;


    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    List<String> imagesList;
    ImageModelFragAdapter adapter;
    List< String > DoneList;
    List< String > inWorkList;
    private boolean jobCount;
    private boolean userData;
    String workerToken;

    private static final String TOPIC_NAME = "weather";


    public WorkerProfileFragment() {
        // Required empty public constructor
    }

    public static WorkerProfileFragment newInstance() {
        WorkerProfileFragment fragment = new WorkerProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorkerProfileBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        imagesList = new ArrayList<>();
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> tabs = new ArrayList<>();
        sharedPreferences =getContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        DoneList = new ArrayList <>(  );
        inWorkList = new ArrayList <>(  );

        getData();
        subscribeToTopic();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // تم استرداد التوكن بنجاح
                        workerToken = task.getResult();
                        // قم بتخزين التوكن في مكان مناسب (مثل قاعدة البيانات أو ملف التفضيلات)
                        // يتم استخدامه لتلقي الإشعارات
                        editor.putString("worker_token", workerToken);
                        editor.apply();

                        Log.d("WorkerToken", workerToken);
                    } else {
                        // حدث خطأ في استرداد التوكن
                        Log.d("WorkerToken", "Failed to retrieve token: " + task.getException().getMessage());
                    }
                });

        tabs.add("آراء العملاء");
        tabs.add("نماذج الأعمال");

        fragments.add(WorkerReviewsFragment.newInstance());
        fragments.add(new BusinessModelsFragment());

        adapter = new ImageModelFragAdapter(getActivity(), fragments);
        binding.FragPager.setAdapter(adapter);

        new TabLayoutMediator(binding.FragTab, binding.FragPager, ( tab , position ) -> {
            if (position < tabs.size()) {
                tab.setText(tabs.get(position));
            }
        } ).attach();

        ActivityResultLauncher<Intent> arl = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Snackbar.make(binding.pWorkerCv, "تم الحفظ بنجاح", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.fab.setOnClickListener( view -> {
            Intent intent = new Intent(getContext(), NewModelActivity.class);
            arl.launch(intent);
        } );

        binding.pWorkerImgEdit.setOnClickListener( view -> {
            Intent intent = new Intent(getContext(), EditWorkerProfileActivity.class);
            startActivity(intent);
        } );

        if ( jobCount && userData ){
            binding.ProgressBar.setVisibility( View.GONE );
            binding.ScrollView.setVisibility( View.VISIBLE );
            binding.fab.setVisibility( View.VISIBLE );
        }
        return binding.getRoot();
    }

    private void getData() {
        List decoumtId = new ArrayList(  );

        binding.ProgressBar.setVisibility(View.VISIBLE);
        binding.ScrollView.setVisibility(View.GONE);
        db.collection("users").document
                (Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                .get().addOnSuccessListener( documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        binding.ProgressBar.setVisibility( View.GONE );
                        binding.ScrollView.setVisibility(View.VISIBLE);


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

                        if (getContext() != null) {
                            Glide.with(getContext()).load(image).circleCrop().error(R.drawable.worker).into(binding.pWorkerImg);
                        }

                        long timestamp = firebaseUser.getMetadata().getCreationTimestamp();
                        // حولنا long -> date
                        Date date = new Date(timestamp);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = dateFormat.format(date);
                        binding.pWorkerJoinDate.setText(formattedDate);
                        userData = true;
                    }
                } ).addOnFailureListener( e ->
        {
            //Todo Add LLField

        });
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                decoumtId.add(documentSnapshot1);
                db.collection("posts").document(documentSnapshot1.getId())
                        .collection("userPost").get()
                        .addOnCompleteListener(task -> {
                            jobCount=true;
                            for (DocumentSnapshot document : task.getResult()) {
                                String jobState = document.getString("jobState");
                                String workerId = document.getString("workerId");
                                if (jobState != null && jobState.equals("done") && workerId != null && workerId.equals(firebaseUser.getPhoneNumber())) {
                                    Log.e("workerId", workerId.equals(firebaseUser.getPhoneNumber()) + "");
                                    Log.e("DecumentsCount", String.valueOf(task.getResult().size()));
                                    String postId = document.getString("postId");
                                    if (postId != null) {
                                        DoneList.add( postId );
                                    }
                                }
                                if (jobState != null && jobState.equals("inWork") && workerId != null && workerId.equals(firebaseUser.getPhoneNumber())) {
                                    Log.e("workerId", workerId.equals(firebaseUser.getPhoneNumber()) + "");
                                    Log.e("DecumentsCount", String.valueOf(task.getResult().size()));
                                    String postId = document.getString("postId");
                                    if (postId != null) {
                                        inWorkList.add( postId );
                                    }
                                }
                                int jobCount=inWorkList.size()+DoneList.size();
                                binding.pWorkerJobNum.setText( jobCount+"" );
                                binding.pWorkerEndNum.setText( DoneList.size()+"" );
                                binding.pWorkerCurrentNum.setText( inWorkList.size()+"" );

                            }
                        })
                        .addOnFailureListener(runnable -> { }); } });
    }
    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_NAME).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // تم الاشتراك بنجاح
                    Log.d("Subscribe", "Subscribed to topic: " + TOPIC_NAME);
                } else {
                    // حدث خطأ أثناء الاشتراك
                    Log.d("Subscribe", "Failed to subscribe to topic: " + TOPIC_NAME);
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

}