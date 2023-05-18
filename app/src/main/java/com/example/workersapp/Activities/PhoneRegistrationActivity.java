package com.example.workersapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityPhoneRegistrationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneRegistrationActivity extends AppCompatActivity {
    private ActivityPhoneRegistrationBinding binding;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth auth;
    private CountDownTimer timer;
    private String verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        binding.btnSendVerificationCode.setOnClickListener(view -> sendCodeVerification());

        binding.imgBack.setOnClickListener(view -> onBackPressed());
    }

    private void sendCodeVerification() {
        //todo Show loader
        String phone = binding.etPhoneReg.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter your phone", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.startsWith("0")) {
            phone = phone.substring(1);
        }
        binding.progressBarRegister.setVisibility(View.VISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+970" + phone,
                60,
                TimeUnit.SECONDS,
                PhoneRegistrationActivity.this,
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
                        //todo dismiss loader
                        binding.progressBarRegister.setVisibility(View.GONE);
                        verificationID = mVerificationId; //لكل كود ID
                        forceResendingToken = token;
                        showPhoneDialog();
                    }
                }
        );
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

        String phone = binding.etPhoneReg.getText().toString().trim();

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

        //كل متى ينفذ الكود الي في ال interval : onTick
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                //الوقت المتبقي للانتهاء : l
                tvTimer.setText("00:" + l / 1000);
            }

            @Override
            public void onFinish() {
                //كود اعادة الارسال
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

        btn.setOnClickListener(View -> {

            if (pinView.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "code empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String code = pinView.getText().toString();
            if (verificationID != null) {
                //todo loader
                binding.progressBarRegister.setVisibility(android.view.View.VISIBLE);
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationID, code);
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(task -> {
                            // Todo loader_dialog.dismiss();
                            binding.progressBarRegister.setVisibility(android.view.View.GONE);
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(PhoneRegistrationActivity.this, RegisterActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(PhoneRegistrationActivity.this, "Verification Code Invalid", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PhoneRegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

}