package com.example.workersapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityPhoneRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneRegistrationActivity extends AppCompatActivity {
    ActivityPhoneRegistrationBinding binding;
    FirebaseAuth auth;
    private CountDownTimer timer;
    String verificationID;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding = ActivityPhoneRegistrationBinding.inflate( getLayoutInflater( ) );
        setContentView( binding.getRoot( ) );

        auth = FirebaseAuth.getInstance( );
        binding.btnSendVerificationCode.setOnClickListener( view -> sendCodeVerification( ) );

        binding.imgBack.setOnClickListener( view -> onBackPressed( ) );
    }

    private void sendCodeVerification( ) {
        //todo Show loader
        String phone = binding.etPhoneReg.getText( ).toString( ).trim( );

        PhoneAuthProvider.getInstance( ).verifyPhoneNumber(
                "+970" + phone ,
                60 ,
                TimeUnit.SECONDS ,
                PhoneRegistrationActivity.this ,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks( ) {
                    @Override
                    public void onVerificationCompleted( @NonNull PhoneAuthCredential phoneAuthCredential ) {
                        Log.e( "FirebaseException" , "is send" );

                    }

                    @Override
                    public void onVerificationFailed( @NonNull FirebaseException e ) {
                        Log.e( "FirebaseException" , e.toString( ) );
                    }

                    @Override
                    public void onCodeSent( @NonNull String mVerificationId , @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken ) {
                        super.onCodeSent( mVerificationId , forceResendingToken );
                        //todo dismiss loader
                        verificationID = mVerificationId; //لكل كود ID

                        showPhoneDialog( );
                    }
                }
        );
    }

    private void showPhoneDialog( ) {

        final Dialog dialog = new Dialog( this );
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
        dialog.setContentView( R.layout.bottom_sheet_phone );
        dialog.setCancelable( false );//ما يطفي الديلوج لما نضغط عالباك جراوند

        TextView tvTimer = dialog.findViewById( R.id.tvTimer );
        Button btn = dialog.findViewById( R.id.btnLogin );

        //كل متى ينفذ الكود الي في ال interval : onTick
        timer = new CountDownTimer( 60000 , 1000 ) {
            @Override
            public void onTick( long l ) {
                //الوقت المتبقي للانتهاء : l
                tvTimer.setText( "00:" + l / 1000 );
            }

            @Override
            public void onFinish( ) {
                tvTimer.setText( "أعد ارسال الرمز" );
                //كود اعادة الارسال
                timer.cancel( );
                btn.setBackgroundColor( Color.parseColor( "#0E2E3B" ) );
                btn.setTextColor( Color.WHITE );
            }
        }.start( );


        Window window = dialog.getWindow( );
        window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT );
        window.setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        window.getAttributes( ).windowAnimations = R.style.DialogAnimation;
        window.setGravity( Gravity.BOTTOM );

        PinView pinView = dialog.findViewById( R.id.firstPinView );
        btn.setOnClickListener( View -> {

            if ( pinView.getText( ).toString( ).trim( ).isEmpty( ) ) {
                Toast.makeText( this , "code empty" , Toast.LENGTH_SHORT ).show( );
                return;
            }

            String code = pinView.getText( ).toString( );
            if ( verificationID != null ) {
                //todo loader
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationID ,
                        code
                );
                FirebaseAuth.getInstance( ).signInWithCredential( phoneAuthCredential )
                        .addOnCompleteListener( task -> {
                            // Todo loader_dialog.dismiss();
                            if ( task.isSuccessful( ) ) {
                                Intent intent = new Intent( PhoneRegistrationActivity.this , RegisterActivity.class );
                                startActivity( intent );
                                finish( );

                            } else {
                                Toast.makeText( PhoneRegistrationActivity.this , "VerificationCodeInvalid" , Toast.LENGTH_SHORT ).show( );
                            }
                        } );
            }
        } );
        dialog.show( );
    }



}