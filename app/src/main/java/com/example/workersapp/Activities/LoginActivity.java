package com.example.workersapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private CountDownTimer timer;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUser = auth.getCurrentUser();
        auth = FirebaseAuth.getInstance();

        setUnderline(binding.textView);
        binding.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = binding.etPhone.getText().toString().trim();
         /*       PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(phone)      // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // مدة الانتظار قبل إعادة إرسال رمز التحقق (بالثواني) Timeout and unit
                                .setActivity(LoginActivity.this)       // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);*/

                showPhoneDialog();
            }
        });


//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            @Override
//            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                // تم التحقق من رمز التحقق بنجاح، وتم تسجيل الدخول إلى Firebase Authentication
//            }
//
//            @Override
//            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//
//                super.onCodeSent(s, forceResendingToken);
//                // تم إرسال رمز التحقق بنجاح، ويمكنك تخزين الـ verification ID للاستخدام في الخطوة التالية
//
//            }
//
//            @Override
//            public void onVerificationFailed(@NonNull FirebaseException e) {
//                // This callback is invoked in an invalid request for verification is made,
//                // for instance if the the phone number format is not valid.
//                //  Log.w(TAG, "onVerificationFailed", e);
//
//                // فشل التحقق من رمز التحقق، يمكنك تحديد الإجراء المناسب هنا
//
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                }
//                // Show a message and update the UI
//            }
//        };


    }

    private void showPhoneDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_phone);

        TextView tvTimer = dialog.findViewById(R.id.tvTimer);
        Button btn = dialog.findViewById(R.id.btnLogin);

        //String verificationCode = editTextVerificationCode.getText().toString().trim();

//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential("verificationId", "verificationCode");
//
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // تم تسجيل الدخول بنجاح
//                    } else {
//                        // فشل تسجيل الدخول، يمكنك تحديد الإجراء المناسب هنا
//                    }
//                });


        //كل متى ينفذ الكود الي في ال interval : onTick
        timer = new CountDownTimer(40000, 1000) {
            @Override
            public void onTick(long l) {
                //الوقت المتبقي للانتهاء : l
                tvTimer.setText("00:" + l / 1000);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("أعد ارسال الرمز");
                timer.cancel();
                btn.setBackgroundColor(Color.parseColor("#0E2E3B"));
                btn.setTextColor(Color.WHITE);
            }
        }.start();

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setGravity(Gravity.BOTTOM);
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_account_type);

        View workerLayout = dialog.findViewById(R.id.workerLayout);
        View workOwnerLayout = dialog.findViewById(R.id.workOwnerLayout);


        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        window.setGravity(Gravity.BOTTOM);

        workerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String accountType = "worker";
                Intent intent = new Intent(getBaseContext(), PhoneRegistrationActivity.class);
                startActivity(intent);
            }
        });
        workOwnerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String accountType = "work owner";
                Intent intent = new Intent(getBaseContext(), PhoneRegistrationActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setUnderline(TextView tv) {
        String text = tv.getText().toString();
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        tv.setText(content);
    }

}