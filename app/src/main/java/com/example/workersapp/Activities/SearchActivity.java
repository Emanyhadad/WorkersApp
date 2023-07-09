package com.example.workersapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workersapp.Adapters.Post_forWorkerAdapter;
import com.example.workersapp.Utilities.Post;
import com.example.workersapp.databinding.ActivitySearchBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    List<Post> postList;
    Post_forWorkerAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        postList = new ArrayList<>();

        postAdapter = new Post_forWorkerAdapter(postList, getBaseContext(), pos -> {
            Intent intent = new Intent(getBaseContext(), PostActivity_forWorker.class);
            intent.putExtra("PostId", postList.get(pos).getPostId());
            intent.putExtra("OwnerId", postList.get(pos).getOwnerId());
            startActivity(intent);
        });

        binding.RVSearch.setAdapter(postAdapter);


        binding.searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String searchTerm = binding.searchEt.getText().toString();
                    SearchTitle(searchTerm);

                    return true;
                }
                return false;
            }
        });
    }

    void SearchTitle(String searchTerm) {
        AtomicBoolean hasMatch = new AtomicBoolean(false); // تعيين قيمة افتراضية للتحقق من وجود تطابقات

        binding.ProgressBarSearch.setVisibility(View.VISIBLE);
        postList.clear();

        firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Task<?>> tasks = new ArrayList<>(); // قائمة لتخزين المهام (عمليات الاستعلام) للانتظار عند الحاجة
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Task<QuerySnapshot> task = firebaseFirestore
                        .collection("posts")
                        .document(documentSnapshot.getId())
                        .collection("userPost")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots1 -> {
                            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots1.getDocuments()) {
                                String title = documentSnapshot1.getString("title");
                                String jobState = documentSnapshot1.getString("jobState");
                                Log.d("title", title);

                                if (title.contains(searchTerm) && jobState.contains("open")) {
                                    Post post = documentSnapshot1.toObject(Post.class);
                                    postList.add(post);
                                    hasMatch.set(true); // يتم تحديث القيمة إلى true في حالة وجود تطابق
                                    Log.d("documentActivity", documentSnapshot1.getData().toString());
                                    Log.d("postList", postList.size() + "");
                                }
                            }
                        });
                tasks.add(task);
            }

            Tasks.whenAllComplete(tasks).addOnCompleteListener(taskSnapshots -> {
                // التحقق من وجود تطابقات أو عدمها وتحديث واجهة المستخدم
                if (hasMatch.get() && postList != null) {
                    // تحديث واجهة المستخدم لعرض العناصر
                    updateRecyclerView();
                } else {
                    SearchDescription(searchTerm);
                }
            });
        });
    }

    void SearchDescription(String searchTerm) {
        AtomicBoolean hasMatch = new AtomicBoolean(false); // تعيين قيمة افتراضية للتحقق من وجود تطابقات
        postList.clear();


        firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Task<?>> tasks = new ArrayList<>(); // قائمة لتخزين المهام (عمليات الاستعلام) للانتظار عند الحاجة
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Task<QuerySnapshot> task = firebaseFirestore
                        .collection("posts")
                        .document(documentSnapshot.getId())
                        .collection("userPost")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots1 -> {
                            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots1.getDocuments()) {
                                String description = documentSnapshot1.getString("description");
                                String jobState = documentSnapshot1.getString("jobState");
                                Log.d("description", description);

                                if (description.contains(searchTerm) && jobState != null && jobState.equals("open")) {
                                    Post post = documentSnapshot1.toObject(Post.class);
                                    postList.add(post);
                                    hasMatch.set(true); // يتم تحديث القيمة إلى true في حالة وجود تطابق
                                    Log.d("documentActivity", documentSnapshot1.getData().toString());
                                    Log.d("postList", postList.size() + "");
                                }
                            }
                        });
                tasks.add(task);
            }

            Tasks.whenAllComplete(tasks).addOnCompleteListener(taskSnapshots -> {
                // التحقق من وجود تطابقات أو عدمها وتحديث واجهة المستخدم
                if (hasMatch.get() && postList != null) {
                    // تحديث واجهة المستخدم لعرض العناصر المطابقة
                    updateRecyclerView();
                } else {
                    SearchCategory(searchTerm);
                }
            });
        });
    }


    void SearchCategory(String searchTerm) {
        AtomicBoolean hasMatch = new AtomicBoolean(false); // تعيين قيمة افتراضية للتحقق من وجود تطابقات
        postList.clear();

        firebaseFirestore.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Task<?>> tasks = new ArrayList<>(); // قائمة لتخزين المهام (عمليات الاستعلام) للانتظار عند الحاجة
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Task<QuerySnapshot> task = firebaseFirestore
                        .collection("posts")
                        .document(documentSnapshot.getId())
                        .collection("userPost")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots1 -> {
                            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots1.getDocuments()) {
                                List<String> categoriesList = (List<String>) documentSnapshot1.get("categoriesList");
                                String jobState = documentSnapshot1.getString("jobState");
                                Log.d("categoriesList", categoriesList + "");

                                if (categoriesList != null && categoriesList.contains(searchTerm) && jobState != null && jobState.equals("open")) {
                                    Post post = documentSnapshot1.toObject(Post.class);
                                    postList.add(post);
                                    hasMatch.set(true); // يتم تحديث القيمة إلى true في حالة وجود تطابق
                                    Log.d("documentActivity", documentSnapshot1.getData().toString());
                                    Log.d("postList", postList.size() + "");
                                }
                            }

                        });
                tasks.add(task);

            }

            Tasks.whenAllComplete(tasks).addOnCompleteListener(taskSnapshots -> {
                // التحقق من وجود تطابقات أو عدمها وتحديث واجهة المستخدم
                if (hasMatch.get() && postList != null) {
                    // تحديث واجهة المستخدم لعرض العناصر المطابقة
                    updateRecyclerView();
                } else {
                    binding.imageView7.setVisibility(View.VISIBLE);
                    binding.RVSearch.setVisibility(View.GONE);
                    binding.ProgressBarSearch.setVisibility(View.GONE);
                }
            });
        });
    }

    private void updateRecyclerView() {
        binding.RVSearch.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        postAdapter.notifyDataSetChanged();
        binding.RVSearch.setVisibility(View.VISIBLE);
        binding.imageView7.setVisibility(View.GONE);
        binding.ProgressBarSearch.setVisibility(View.GONE);

    }

}
