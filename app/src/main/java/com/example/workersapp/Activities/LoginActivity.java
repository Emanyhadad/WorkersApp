package com.example.workersapp.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.User;
import com.example.workersapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private CountDownTimer timer;
    String phoneNum;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    FirebaseFirestore firestore;

    AlertDialog.Builder builder;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken forceResendingToken;

    String verificationID;

    //me
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = getSharedPreferences("Login", MODE_PRIVATE);
        editor = sp.edit();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();


        setUnderline(binding.tvRegisterNow);
        binding.tvRegisterNow.setOnClickListener(view -> showDialog());

        binding.btnLogin.setOnClickListener(view -> sendCodeVerification());

    }

    //Todo:
    // 1-تحقق هل الرقم موجود في الفير بيز ام لا
    //في حال كان موجود يتم ارسال كود التحقق
    //غير موجود يظهر ايرور ارشادي لتسجيل مستخدم جديد
    private void sendCodeVerification() {

        phoneNum = binding.etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(this, "Enter your phone", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (phoneNum.startsWith("0")) {
                phoneNum = phoneNum.substring(1);
            }
        }
        binding.progressBarLogin.setVisibility(View.VISIBLE);

        firestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d("TAG", document.getId() + " => " + document.getData());
                        if (document.getId().equals("+970" + phoneNum)) {
                            binding.progressBarLogin.setVisibility(View.VISIBLE);
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    "+970" + phoneNum,
                                    60,
                                    TimeUnit.SECONDS,
                                    LoginActivity.this,
                                    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            Log.e("FirebaseException", "is send");
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            Log.e("FirebaseException", e.toString());
                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String mVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                            super.onCodeSent(mVerificationId, token);
                                            binding.progressBarLogin.setVisibility(View.GONE);
                                            verificationID = mVerificationId; //لكل كود ID
                                            forceResendingToken = token;
                                            showPhoneDialog();
                                        }
                                    });
                        } else {
                            binding.progressBarLogin.setVisibility(View.GONE);
                            if (builder == null) {
                                builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("انت لست مسجل مسبق, قم بالتجسيل الان!")
                                        .setNegativeButton("في وقت لاحق", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                builder = null;
                                            }
                                        })
                                        .setPositiveButton("سجل الان", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                showDialog();
                                                dialogInterface.dismiss();
                                                builder = null;
//                                            Intent intent = new Intent(getBaseContext(), PhoneRegistrationActivity.class);
//                                            intent.putExtra("phoneNum", phoneNum);
//                                            startActivity(intent);
                                            }
                                        })
                                        .setCancelable(false)
                                        .create().show();
                            }
                        }
                    }
                }
            }
        });


    }

    Dialog dialogPhoneNum = null;

    private void showPhoneDialog() {
        if (dialogPhoneNum == null) {
            dialogPhoneNum = new Dialog(this);
            dialogPhoneNum.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogPhoneNum.setContentView(R.layout.bottom_sheet_phone);
            dialogPhoneNum.setCancelable(false);//ما يطفي الديلوج لما نضغط عالباك جراوند
            dialogPhoneNum.show();
        }

        TextView tvTimer = dialogPhoneNum.findViewById(R.id.tvTimer);
        ImageView imgTimer = dialogPhoneNum.findViewById(R.id.imgTimer);
        Button btn = dialogPhoneNum.findViewById(R.id.btnLogin);
        PinView pinView = dialogPhoneNum.findViewById(R.id.firstPinView);
        tvTimer.setEnabled(false);

        String phone = binding.etPhone.getText().toString().trim();
        tvTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvTimer.isEnabled()) {
                    resendVerificationCode(phone, forceResendingToken);
                    timer.start();
                    tvTimer.setEnabled(false);
                    imgTimer.setVisibility(View.VISIBLE);
                }
            }
        });

//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder( auth )
//                        .setPhoneNumber( "+970" + phone )      // Phone number to verify
//                        .setTimeout( 60L , TimeUnit.SECONDS ) // مدة الانتظار قبل إعادة إرسال رمز التحقق (بالثواني) Timeout and unit
//                        .setActivity( LoginActivity.this )       // Activity (for callback binding)
//                        .setCallbacks( mCallbacks )          // OnVerificationStateChangedCallbacks
//                        .build( );
//        PhoneAuthProvider.verifyPhoneNumber( options );
//        auth.useAppLanguage( );
//
//        String code = verificationCode1 + verificationCode2 + verificationCode3 +
//                verificationCode4 + verificationCode5;
//        button.setOnClickListener( view -> {
//            PhoneAuthCredential credential = PhoneAuthProvider.getCredential( verificationID , code );
//
//            auth.signInWithCredential( credential )
//                    .addOnCompleteListener( task -> {
//                        if ( task.isSuccessful( ) ) {
//                            // تم تسجيل الدخول بنجاح
//                            startActivity( new Intent( getBaseContext( ) , MainActivity.class ) );
//
//                        } else {
//                            // فشل تسجيل الدخول، يمكنك تحديد الإجراء المناسب هنا
//                        }
//                    } );
//        } );

        //كل متى ينفذ الكود الي في ال interval : onTick
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                //الوقت المتبقي للانتهاء : l
                tvTimer.setText("00:" + l / 1000);
            }

            @Override
            public void onFinish() {
                tvTimer.setText(R.string.resendCode);
                imgTimer.setVisibility(View.GONE);
                tvTimer.setEnabled(true);
                timer.cancel();
            }
        }.start();

        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pinView.setLineColor(Color.parseColor("#64A811"));
                btn.setBackgroundColor(Color.parseColor("#0E2E3B"));
                btn.setTextColor(Color.WHITE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Window window = dialogPhoneNum.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setGravity(Gravity.BOTTOM);

        btn.setOnClickListener(view -> {
            if (pinView.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "code empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String code = pinView.getText().toString();
            if (verificationID != null) {
                binding.progressBarLogin.setVisibility(View.VISIBLE);
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationID, code);
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(task -> {
                            binding.progressBarLogin.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                firestore.collection("users").document("+970" + phoneNum).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            User user = task.getResult().toObject(User.class);
                                            if (user != null) {
                                                if (user.getAccountType().equals("worker")) {
                                                    startActivity(new Intent(getBaseContext(), WorkerActivities.class));
                                                    finish();
                                                } else if (user.getAccountType().equals("work owner")) {
                                                    startActivity(new Intent(getBaseContext(), WorkOwnerProfileActivity.class));
                                                    finish();
                                                }
                                            }
                                        }
                                    }
                                });
//                                ////////////////////////////////////////////////////////////////////////
//                                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                                startActivity(intent);
//                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "Verification Code Invalid", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        dialogPhoneNum.show();
    }

    // إعادة إرسال رمز التحقق
    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone, // رقم الهاتف المسجل
                60, // فترة صلاحية الرمز بالثواني
                TimeUnit.SECONDS, // وحدة فترة صلاحية الرمز
                this, // النشاط الحالي
                mCallbacks, // مجموعة مستمعي الاتصال
                token // رمز إعادة الإرسال الذي تم توليده من قبل
        );
    }

    Dialog dialogAccountType = null;

    private void showDialog() {

        if (dialogAccountType == null) {
            dialogAccountType = new Dialog(this);
            dialogAccountType.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogAccountType.setContentView(R.layout.bottom_sheet_account_type);
//            dialogAccountType.setCanceledOnTouchOutside(false);
            dialogAccountType.show();
        }

        View workerLayout = dialogAccountType.findViewById(R.id.workerLayout);
        View workOwnerLayout = dialogAccountType.findViewById(R.id.workOwnerLayout);

        Window window = dialogAccountType.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setGravity(Gravity.BOTTOM);

        dialogAccountType.show();

        workerLayout.setOnClickListener(view -> {
            String accountType = "worker";
            dialogAccountType.dismiss();

            editor.putString("accountType", "worker");
            editor.apply();
            Toast.makeText(this, sp.getString("accountTypeWorker", ""), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), PhoneRegistrationActivity.class);
            startActivity(intent);
        });
        workOwnerLayout.setOnClickListener(view -> {
            String accountType = "work owner";

            editor.putString("accountType", "work owner");
            editor.apply();
            dialogAccountType.dismiss();
            Intent intent = new Intent(getBaseContext(), PhoneRegistrationActivity.class);
            startActivity(intent);
        });

    }

    private void setUnderline(TextView tv) {
        String text = tv.getText().toString();
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        tv.setText(content);
    }


    //    @Override
//    protected void onStart() {
//        super.onStart();
//
//
//    }
    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            String accountType = sp.getString("accountType", "worker");
            if (accountType.equals("worker")) {
                startActivity(new Intent(getBaseContext(), WorkerActivities.class));
            } else if (accountType.equals("work owner")) {
                startActivity(new Intent(getBaseContext(), WorkOwnerProfileActivity.class));
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        dialogAccountType=null;
//    }
}