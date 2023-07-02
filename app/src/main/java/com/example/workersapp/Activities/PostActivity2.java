package com.example.workersapp.Activities;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.workersapp.Adapters.ShowCategoryAdapter;
import com.example.workersapp.Adapters.ShowImagesAdapter;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityPost2Binding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostActivity2 extends AppCompatActivity {
    ShowCategoryAdapter showCategoryAdapter;

    ShowImagesAdapter showImagesAdapter;

    private Dialog evaluationDialog;
    private AlertDialog deleteDialog;
    AlertDialog.Builder deleteDialog_builder;
    View deleteDialogView;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String postId, path;
    float rating;
    String comment;
    String workerId;

    public static SharedPreferences sharedPreferences;


    String jobState, title, description, expectedWorkDuration, projectedBudget, jobLocation, projectState;
    ActivityPost2Binding binding;

    @SuppressLint({"UseCompatLoadingForDrawables", "InflateParams"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPost2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // تم استرداد التوكن بنجاح
//                        String token = task.getResult();
//                        // قم بتخزين التوكن في مكان مناسب (مثل قاعدة البيانات أو ملف التفضيلات)
//                        // يتم استخدامه لإرسال الإشعارات للعمال
//                        editor.putString("token",token);
//                        editor.apply();
//
//                        Log.d("tokenOwner", token);
//                    } else {
//                        // حدث خطأ في استرداد التوكن
//                        Log.d("Token", "Failed to retrieve token: " + task.getException().getMessage());
//                    }
//                });

        binding.progressBar.setVisibility(View.VISIBLE);

        //For Rating
        evaluationDialog = new Dialog(this);
        evaluationDialog.setContentView(R.layout.dialog_worker_evaluation);

        //Delete Dialog
        deleteDialog_builder = new AlertDialog.Builder(this);
        deleteDialogView = getLayoutInflater().inflate(R.layout.deluge_delete_work, null);
        deleteDialog_builder.setView(deleteDialogView);
        deleteDialog = deleteDialog_builder.create();

        binding.inculd.editIcon.setVisibility(View.GONE);
        binding.inculd.tvPageTitle.setText("بيانات الوظيفة");


        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

//        userId = "+970594461722";//TODO GET UserCurrent
        postId = getIntent().getStringExtra("PostId").trim(); //TODO GET FROM INTENT

        path = "posts/" + user + "/userPost/" + Objects.requireNonNull(postId);
        documentReference = firestore.collection("posts").document(Objects.requireNonNull(user.getPhoneNumber())).
                collection("userPost").document(postId);

//        Toast.makeText(this, Objects.requireNonNull(postId), Toast.LENGTH_SHORT).show();

        getData();


    }

    void getData(){

        firestore.collection("posts").document(user.getPhoneNumber()).
                collection("userPost").document(Objects.requireNonNull(postId)).get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        jobState = documentSnapshot.getString("jobState");
                        projectState = jobState;

                        switch (Objects.requireNonNull(projectState)) {
                            case "open":
                                binding.APbtnCloseProject.setVisibility(View.VISIBLE);
                                binding.APbtnComments.setVisibility(View.VISIBLE);
                                binding.APTvJobData.setVisibility(View.GONE);
                                binding.APCLInWork.setVisibility(View.GONE);
                                binding.ApBtnFinishJob.setVisibility(View.GONE);
                                DeleteJob();
                                binding.APbtnComments.setOnClickListener(view -> {
                                    Intent intent = new Intent(PostActivity2.this, OffersActivity.class);
                                    intent.putExtra("postID", postId);
                                    startActivity(intent);
                                });
                                binding.APbtnCloseProject.setOnClickListener(view -> deleteDialog.show());
                                break;

                            case "close":
                                binding.PAtvProjectstate.setText("مغلق");
                                binding.PAtvProjectstate.setBackground(getResources().getDrawable(R.drawable.close_bg));
                                binding.APbtnCloseProject.setVisibility(View.GONE);
                                binding.APbtnComments.setVisibility(View.GONE);
                                binding.APTvJobData.setVisibility(View.GONE);
                                binding.APCLInWork.setVisibility(View.GONE);

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("jobState", "close");
                                documentReference.update(updates).addOnSuccessListener(aVoid -> {
                                    // Handle the case when the update is successful
                                }).addOnFailureListener(e -> {
                                    // Handle the case when the update fails
                                });
                                break;

                            case "inWork":
                                binding.PAtvProjectstate.setText("قيد العمل");
                                binding.PAtvProjectstate.setBackground(getResources().getDrawable(R.drawable.inwork_bg));
                                binding.APbtnCloseProject.setVisibility(View.GONE);
                                binding.APbtnComments.setVisibility(View.GONE);
                                binding.APTvJobData.setVisibility(View.VISIBLE);
                                binding.APCLInWork.setVisibility(View.VISIBLE);
                                binding.ApBtnFinishJob.setVisibility(View.VISIBLE);
                                binding.APTvIWJStartDate.setText(documentSnapshot.getString("jobStartDate"));
                                binding.linearLayoutRateWorker.setVisibility(View.GONE);
//                                binding.linearLayoutRateClint.setVisibility(View.GONE);

                                CollectionReference workerOffersRef = firestore.collection("offers").document(postId).collection("workerOffers");
                                workerOffersRef.document(Objects.requireNonNull(documentSnapshot.getString("workerId"))).get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            //Todo get data from offer (price)
                                            String workerId = document.getString("workerID");
                                            String postId = document.getString("postID");
                                            String offerDuration = document.getString("offerDuration");
                                            String offerDescription = document.getString("offerDescription");
                                            String offerBudget = document.getString("offerBudget");
                                            binding.APtvIWJOfferPrice.setText(offerBudget + "");
                                            String clientId = document.getString("clintID");
                                        } else Log.d(TAG, "No such document");
                                    } else
                                        Log.d(TAG, "Error getting document: ", task.getException());
                                });


                                Rating();

                                firestore.collection("users")
                                        .document(Objects.requireNonNull(documentSnapshot.getString("workerId"))).get()
                                        .addOnSuccessListener(documentSnapshot1 -> {
                                            if (documentSnapshot1.exists()) {
                                                String fullName = documentSnapshot1.getString("fullName");
                                                binding.tvIWJWorkerName.setText(fullName);
                                                String image = documentSnapshot1.getString("image");
                                                Glide.with(PostActivity2.this)
                                                        .load(image)
                                                        .circleCrop()
                                                        .error(R.drawable.worker)
                                                        .into(binding.imgIWJWorker);
                                            }
                                        }).addOnFailureListener(e -> {
                                        });

                                binding.ApBtnFinishJob.setOnClickListener(view -> {
                                            new AlertDialog.Builder(PostActivity2.this)
                                                    .setMessage("هل أنت متأكد أنك تريد اكمال الوظيفة؟")
                                                    .setNegativeButton("لا, الغاء", (dialogInterface, i) -> {
                                                    })
                                                    .setPositiveButton("نعم ، اكمال", (dialogInterface, i) -> {
                                                                evaluationDialog.show();
                                                                //Todo: After Note
//                                binding.tvClintComment.setText( documentSnapshot.getString( "Comment-clint" ) );
//                                double ratingClint =  documentSnapshot.getDouble("Rating-clint");
//                                binding.ratingBarClint.setProgress((int) ratingClint*2);
                                                            }
                                                    ).create().show();
                                            binding.PAtvProjectstate.setText("مكتمل");
                                            binding.PAtvProjectstate.setBackground(getResources().getDrawable(R.drawable.bg_done));
                                            binding.APbtnCloseProject.setVisibility(View.GONE);
                                            binding.APbtnComments.setVisibility(View.GONE);
                                            binding.APTvJobData.setVisibility(View.VISIBLE);
                                            binding.APCLInWork.setVisibility(View.VISIBLE);
                                            binding.ApBtnFinishJob.setVisibility(View.GONE);
//                                            binding.linearLayoutRateClint.setVisibility(View.VISIBLE);
                                            binding.linearLayoutRateWorker.setVisibility(View.VISIBLE);
                                            //Todo: How get in same time
//                                                    binding.tvWorkerComment.setText( documentSnapshot.getString( "Comment-worker" ) );
//                                                    double ratingWorker =  documentSnapshot.getDouble("Rating-worker");
//                                                    binding.ratingBarWorker.setProgress((int) ratingWorker*2);
                                        }


                                );
                                break;

                            case "done":
                                binding.PAtvProjectstate.setText("مكتمل");
                                binding.PAtvProjectstate.setBackground(getResources().getDrawable(R.drawable.bg_done));
                                binding.APbtnCloseProject.setVisibility(View.GONE);
                                binding.APbtnComments.setVisibility(View.GONE);
                                binding.APTvJobData.setVisibility(View.VISIBLE);
                                binding.APCLInWork.setVisibility(View.VISIBLE);
                                binding.ApBtnFinishJob.setVisibility(View.GONE);
//                                binding.linearLayoutRateClint.setVisibility(View.VISIBLE);
                                binding.linearLayoutRateWorker.setVisibility(View.VISIBLE);
                                binding.tvWorkerComment.setText(documentSnapshot.getString("Comment-worker"));
                                double ratingWorker = documentSnapshot.getDouble("Rating-worker");
                                binding.ratingBarWorker.setProgress((int) ratingWorker * 2);
                                binding.APTvIWJStartDate.setText(documentSnapshot.getString("jobStartDate"));
                                binding.APTvIWJFinishedDate.setText("-" + documentSnapshot.getString("jobFinishDate"));
                                //Todo: After Note
//                                binding.tvClintComment.setText( documentSnapshot.getString( "Comment-clint" ) );
//                                double ratingClint =  documentSnapshot.getDouble("Rating-clint");
//                                binding.ratingBarClint.setProgress((int) ratingClint*2);
                                firestore.collection("users")
                                        .document(Objects.requireNonNull(documentSnapshot.getString("workerId"))).get()
                                        .addOnSuccessListener(documentSnapshot1 -> {
                                            if (documentSnapshot1.exists()) {
                                                String fullName = documentSnapshot1.getString("fullName");
                                                binding.tvIWJWorkerName.setText(fullName);
                                                String image = documentSnapshot1.getString("image");
                                                Glide.with(PostActivity2.this)
                                                        .load(image)
                                                        .circleCrop()
                                                        .error(R.drawable.worker)
                                                        .into(binding.imgIWJWorker);
                                            }
                                        }).addOnFailureListener(e -> {
                                        });
                                CollectionReference workerOffersRef1 = firestore.collection("offers").document(postId).collection("workerOffers");
                                workerOffersRef1.document(Objects.requireNonNull(documentSnapshot.getString("workerId"))).get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            //Todo get data from offer (price)
                                            String workerId = document.getString("workerID");
                                            String postId = document.getString("postID");
                                            String offerDuration = document.getString("offerDuration");
                                            String offerDescription = document.getString("offerDescription");
                                            String offerBudget = document.getString("offerBudget");
                                            binding.APtvIWJOfferPrice.setText(offerBudget + "");
                                            String clientId = document.getString("clintID");
                                        } else Log.d(TAG, "No such document");
                                    } else
                                        Log.d(TAG, "Error getting document: ", task.getException());
                                });
                                //Todo: get Rating
                                break;

                        }

                        title = documentSnapshot.getString("title");
                        binding.APTvJobTitle.setText(title);

                        description = documentSnapshot.getString("description");
                        binding.APTvJobDec.setText(description);

                        firestore.collection("offers").document(Objects.requireNonNull(postId))
                                .collection("workerOffers")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        int count = task.getResult().size();
                                        binding.APbtnComments.setText(count == 0 ? "0" : String.valueOf(count));
                                    }
                                });

                        List<String> images = (List<String>) documentSnapshot.get("images");
                        if (images == null || images.isEmpty()) {
                            binding.APImageRV.setVisibility(View.GONE);
                        } else {
                            showImagesAdapter = new ShowImagesAdapter(images, this);
                            binding.APImageRV.setAdapter(showImagesAdapter);
                            binding.APImageRV.setLayoutManager(new LinearLayoutManager(this,
                                    LinearLayoutManager.HORIZONTAL, false));
                        }

                        List<String> categoriesList = (List<String>) documentSnapshot.get("categoriesList");
                        showCategoryAdapter = new ShowCategoryAdapter((ArrayList<String>) categoriesList);
                        binding.APCategoryRV.setAdapter(showCategoryAdapter);
                        binding.APCategoryRV.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                                LinearLayoutManager.HORIZONTAL, false));


                        expectedWorkDuration = documentSnapshot.getString("expectedWorkDuration");
                        binding.APTvJobTime.setText(expectedWorkDuration);

                        projectedBudget = documentSnapshot.getString("projectedBudget");
                        binding.APtvJopPrice.setText(projectedBudget);

                        jobLocation = documentSnapshot.getString("jobLocation");
                        binding.APTvJobLoc.setText(jobLocation);
                        //TODO GET Timestamp
                        binding.progressBar.setVisibility(View.GONE);
                        binding.SV.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(this, "Prop", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("GettingPost", e.getMessage()));

    }

    void Rating() {
        firestore.collection("posts").document(user.getPhoneNumber())
                .collection("userPost").document(Objects.requireNonNull(postId)).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            workerId = documentSnapshot.getString("workerId");
                            Log.d("userActivity", user.getPhoneNumber());
                            Log.d("workerActivity", workerId);
                        }
                    }
                });
        RatingBar ratingBar = evaluationDialog.findViewById(R.id.ratingBar);
        EditText commentEditText = evaluationDialog.findViewById(R.id.et_comment);
        Button sendButton = evaluationDialog.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            rating = ratingBar.getRating();
            comment = commentEditText.getText().toString();


            Map<String, Object> updates1 = new HashMap<>();
            updates1.put("Comment-worker", comment);
            updates1.put("Rating-worker", (int) rating);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("ar"));
                String currentDate = dateFormat.format(new Date());
                updates1.put("jobFinishDate", currentDate);
            } else {
                // For older devices
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DateFormatSymbols arabicDFS = new DateFormatSymbols(new Locale("ar"));
                String[] arabicMonthNames = arabicDFS.getMonths();
                String monthInArabic = arabicMonthNames[month - 1];
                String date = String.format(Locale.getDefault(), "%d %s %d", day, monthInArabic, year);
                updates1.put("jobFinishDate", date);
            }
            updates1.put("jobState", "done");

            documentReference.update(updates1).addOnSuccessListener(aVoid -> {
                // Handle the case when the update is successful
                sendNotification(workerId, comment);
            }).addOnFailureListener(e -> {
                // Handle the case when the update fails
            });

            evaluationDialog.dismiss();
        });

    }

    void DeleteJob() {

        // Set the click listeners for the delete and cancel buttons
        Button deleteButton = deleteDialogView.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(v -> {
            // TODO: delete the work and change its state to closed & delete job

            closeProject();

            deleteDialog.dismiss();
        });

        Button cancelButton = deleteDialogView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> deleteDialog.dismiss());
    }

    void openProject() {
        binding.APbtnCloseProject.setVisibility(View.VISIBLE);
        binding.APbtnComments.setVisibility(View.VISIBLE);

        DeleteJob();

        binding.APbtnComments.setOnClickListener(view ->
                startActivity(new Intent(PostActivity2.this, OffersActivity.class)));

        binding.APbtnCloseProject.setOnClickListener(view -> {
            // Show the delete deluge
            deleteDialog.show();
        });


    }

    void closeProject() {
        binding.PAtvProjectstate.setText("مغلق");
        binding.PAtvProjectstate.setBackgroundColor(getResources().getColor(R.color.red));
        binding.APbtnCloseProject.setVisibility(View.GONE);
        Map<String, Object> updates = new HashMap<>();
        updates.put("jobState", "close");
        documentReference.update(updates).addOnSuccessListener(aVoid -> {
            // Handle the case when the update is successful
        }).addOnFailureListener(e -> {
            // Handle the case when the update fails
        });
    }
    private void sendNotification(String workerId, String comment) {
        // استعداد بناء الطلب لإرسال الإشعار عبر FCM
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");

        // بناء هيكل البيانات للإشعار

        JSONObject jsonNotification = new JSONObject();
        try {
            jsonNotification.put("comment", comment);
            String d = jsonNotification.getString("comment");
            Toast.makeText(this, "jsonNotification "+d, Toast.LENGTH_SHORT).show();
            Log.d("jsonNotification",d);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // بناء هيكل البيانات للطلب
        JSONObject jsonRequest = new JSONObject();
        try {
//            jsonRequest.put("to", "/topics/" + workerId);

//            jsonRequest.put("to", token);
            jsonRequest.put("to", workerId);
            Log.d("Topic", workerId);
            jsonRequest.put("data", jsonNotification);
            Log.d("jsonRequest",jsonRequest.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // إنشاء طلب POST لإرسال الإشعار عبر FCM
        RequestBody requestBody = RequestBody.create(mediaType, jsonRequest.toString());
        String service_key = "AAAAMN-4IYw:APA91bEuml1NnrGH04Jm4fNA3x7TSVs8yDYdj_TWecffStxyyBFeG0cld069_Fj55o80eM3mCygYonNe5vAwyxVIjFh4P2DhL0ZJ9fwM7-EUxQzd5LaPWtfB1ch01_4MHx1m0utma6_z";
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer "+service_key)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PostActivity2.this, "فشل إرسال الإشعار", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                // استجابة ناجحة من خادم FCM
                String responseData = response.body().string();
                Log.d("responseActivity", responseData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PostActivity2.this, "تم إرسال الإشعار بنجاح", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


//    private void sendNotification(String workerId, String comment) {
//        // استعادة التوكن المخزن لصاحب العمل
//        String ownerToken = sharedPreferences.getString("worker_token", "");
//
//        Toast.makeText(this, "ownerToken: " + ownerToken, Toast.LENGTH_SHORT).show();
//
//        JSONObject notificationData = new JSONObject();
//        try {
//            notificationData.put("comment", comment);
//            String c = notificationData.getString("comment");
//            Log.d("notificationData", c);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JSONObject jsonRequest = new JSONObject();
//        try {
//            jsonRequest.put("to", workerId);
//            jsonRequest.put("data", notificationData);
//            Log.d("jsonRequest", jsonRequest.toString());
//            String j = jsonRequest.getString("data");
//            Log.d("jsonRequestData", j);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        String url = "https://fcm.googleapis.com/fcm/send";
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonRequest,
//                response -> {
//                    // تم إرسال الإشعار بنجاح
//                    Log.d("Notification", "تم إرسال الإشعار بنجاح");
//                },
//                error -> {
//                    // حدث خطأ أثناء إرسال الإشعار
//                    Log.d("Notification", "حدث خطأ أثناء إرسال الإشعار: " + error.getMessage());
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer AAAAMN-4IYw:APA91bEuml1NnrGH04Jm4fNA3x7TSVs8yDYdj_TWecffStxyyBFeG0cld069_Fj55o80eM3mCygYonNe5vAwyxVIjFh4P2DhL0ZJ9fwM7-EUxQzd5LaPWtfB1ch01_4MHx1m0utma6_z"); // استبدل YOUR_SERVER_KEY بمفتاح الخادم الخاص بك
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//
//        requestQueue.add(request);
//
//    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}