package com.example.workersapp.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.workersapp.Activities.EditWorkerProfileActivity;
import com.example.workersapp.Activities.LoginActivity;
import com.example.workersapp.Activities.NewModelActivity;
import com.example.workersapp.Adapters.ImageModelFragAdapter;
import com.example.workersapp.R;
import com.example.workersapp.databinding.FragmentWorkerProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
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

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;

    public static SharedPreferences sp1;
    public static SharedPreferences.Editor editor1;

    FirebaseFirestore db;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    List<String> imagesList;
    ImageModelFragAdapter adapter;
    List<String> DoneList;
    List<String> inWorkList;
    private boolean jobCount;
    private boolean userData;
    String workerToken;
    private static final String TOPIC_NAME = "weather";

    double rate = 0;
    int count = 0;


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

        sp = getContext().getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = sp.edit();

        sp1 = getContext().getSharedPreferences("MyPreferencesBoarding", MODE_PRIVATE);
        editor1 = sp1.edit();

        DoneList = new ArrayList<>();
        inWorkList = new ArrayList<>();

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

        tabs.add(getString(R.string.TvReviews));
        tabs.add(getString(R.string.businessToolBar));

        fragments.add(WorkerReviewsFragment.newInstance());
        fragments.add(new BusinessModelsFragment());

        adapter = new ImageModelFragAdapter(getActivity(), fragments);
        binding.FragPager.setAdapter(adapter);

        new TabLayoutMediator(binding.FragTab, binding.FragPager, (tab, position) -> {
            if (position < tabs.size()) {
                tab.setText(tabs.get(position));
            }
        }).attach();

        ActivityResultLauncher<Intent> arl = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Snackbar.make(binding.pWorkerCv, "تم الحفظ بنجاح", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), NewModelActivity.class);
            arl.launch(intent);
        });

        if (jobCount && userData) {
            binding.ProgressBar.setVisibility(View.GONE);
            binding.ScrollView.setVisibility(View.VISIBLE);
            binding.fab.setVisibility(View.VISIBLE);
        }

        binding.pWorkerImgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
        /////التقيم
        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> num = new ArrayList<>();

                    List<DocumentSnapshot> documents = task.getResult().getDocuments();

                    for (DocumentSnapshot document : documents) {
                         num.add(document.getId());
                        Log.d("tag", document.getId());
                    }
                    Log.d("tag", String.valueOf(num.size()));

                    for (int i = 0; i < num.size(); i++) {
                        db.collection("posts").document(num.get(i)).collection("userPost")
                                .whereEqualTo("jobState", "done").whereEqualTo("workerId", firebaseUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<Integer> rating = task.getResult().toObjects(Integer.class);
                                        count = count + rating.size();
                                        for (int j = 0; j < rating.size(); j++) {
                                            rate = rate + rating.get(j);
                                        }
                                    }
                                });
                    }
                }
                Log.d("tag", String.valueOf(rate));
                Log.d("tag", String.valueOf(count));

                double pWorkerRate = rate / count;
                binding.pWorkerRate.setText(String.valueOf(pWorkerRate));
            }
        });

        return binding.getRoot();
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.edit_logout);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_edit) {
                    Intent intent = new Intent(getContext(), EditWorkerProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    FirebaseAuth.getInstance().signOut();
                    editor.clear();
                    editor.apply();

                    editor1.clear();
                    editor1.apply();

                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
//        super.onCreate(savedInstanceState);
//    }

    private void getData() {
        List decoumtId = new ArrayList();

        binding.ProgressBar.setVisibility(View.VISIBLE);
        binding.ScrollView.setVisibility(View.GONE);
        db.collection("users").document
                        (Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        binding.ProgressBar.setVisibility(View.GONE);
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
                }).addOnFailureListener(e -> {
                    //Todo Add LLField

                });
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                decoumtId.add(documentSnapshot1);
                db.collection("posts").document(documentSnapshot1.getId())
                        .collection("userPost").get()
                        .addOnCompleteListener(task -> {
                            jobCount = true;
                            for (DocumentSnapshot document : task.getResult()) {
                                String jobState = document.getString("jobState");
                                String workerId = document.getString("workerId");
                                if (jobState != null && jobState.equals("done") && workerId != null && workerId.equals(firebaseUser.getPhoneNumber())) {
                                    Log.e("workerId", workerId.equals(firebaseUser.getPhoneNumber()) + "");
                                    Log.e("DecumentsCount", String.valueOf(task.getResult().size()));
                                    String postId = document.getString("postId");
                                    if (postId != null) {
                                        DoneList.add(postId);
                                    }
                                }
                                if (jobState != null && jobState.equals("inWork") && workerId != null && workerId.equals(firebaseUser.getPhoneNumber())) {
                                    Log.e("workerId", workerId.equals(firebaseUser.getPhoneNumber()) + "");
                                    Log.e("DecumentsCount", String.valueOf(task.getResult().size()));
                                    String postId = document.getString("postId");
                                    if (postId != null) {
                                        inWorkList.add(postId);
                                    }
                                }
                                int jobCount = inWorkList.size() + DoneList.size();
                                binding.pWorkerJobNum.setText(jobCount + "");
                                binding.pWorkerEndNum.setText(DoneList.size() + "");
                                binding.pWorkerCurrentNum.setText(inWorkList.size() + "");

                            }
                        })
                        .addOnFailureListener(runnable -> {
                        });
            }
        });
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