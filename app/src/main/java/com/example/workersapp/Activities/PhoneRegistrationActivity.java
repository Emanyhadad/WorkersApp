package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityPhoneRegistrationBinding;

public class PhoneRegistrationActivity extends AppCompatActivity {
    ActivityPhoneRegistrationBinding binding;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.btnSendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhoneDialog();
            }
        });
    }
    private void showPhoneDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_phone);

        TextView tvTimer = dialog.findViewById(R.id.tvTimer);
        Button btn= dialog.findViewById(R.id.btnLogin);

        //كل متى ينفذ الكود الي في ال interval : onTick
        timer = new CountDownTimer(40000, 1000) {
            @Override
            public void onTick(long l) {
                //الوقت المتبقي للانتهاء : l
                tvTimer.setText("00:"+l / 1000);
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

}