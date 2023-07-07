package com.example.workersapp.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Objects;
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


    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding = ActivityLoginBinding.inflate( getLayoutInflater( ) );
        setContentView( binding.getRoot( ) );

        sp = getSharedPreferences( "MyPreferences" , MODE_PRIVATE );
        editor = sp.edit( );

        auth = FirebaseAuth.getInstance( );
        firestore = FirebaseFirestore.getInstance( );
        currentUser = auth.getCurrentUser( );


        setUnderline( binding.tvRegisterNow );
        binding.tvRegisterNow.setOnClickListener( view -> showDialog( ) );

        binding.btnLogin.setOnClickListener( view -> sendCodeVerification( ) );

    }


    private void sendCodeVerification( ) {

        phoneNum = Objects.requireNonNull( binding.etPhone.getText( ) ).toString( ).trim( );
        if ( TextUtils.isEmpty( phoneNum ) ) {
            Toast.makeText( this , "Enter your phone" , Toast.LENGTH_SHORT ).show( );
            return;
        } else {
            if ( phoneNum.startsWith( "0" ) ) {
                phoneNum = phoneNum.substring( 1 );
            }
        }
        binding.progressBarLogin.setVisibility( View.VISIBLE );

        DocumentReference documentReference = firestore.collection( "users" ).document( "+970" + phoneNum );
        documentReference.get( ).addOnSuccessListener( new OnSuccessListener < DocumentSnapshot >( ) {
            @Override
            public void onSuccess( DocumentSnapshot documentSnapshot ) {
                if ( documentSnapshot.exists( ) ) {
                    binding.progressBarLogin.setVisibility( View.VISIBLE );
                    PhoneAuthProvider.getInstance( ).verifyPhoneNumber( "+970" + phoneNum , 60 , TimeUnit.SECONDS , LoginActivity.this , mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks( ) {
                        @Override
                        public void onVerificationCompleted( @NonNull PhoneAuthCredential phoneAuthCredential ) {
                        }

                        @Override
                        public void onVerificationFailed( @NonNull FirebaseException e ) {
                        }

                        @Override
                        public void onCodeSent( @NonNull String mVerificationId , @NonNull PhoneAuthProvider.ForceResendingToken token ) {
                            super.onCodeSent( mVerificationId , token );
                            binding.progressBarLogin.setVisibility( View.GONE );
                            verificationID = mVerificationId; //لكل كود ID
                            forceResendingToken = token;
                            showPhoneDialog( );
                        }
                    } );
                } else {
                    binding.progressBarLogin.setVisibility( View.GONE );
                    if ( builder == null ) {
                        builder = new AlertDialog.Builder( LoginActivity.this );
                        builder.setMessage( getString( R.string.DoNotHave ) ).setNegativeButton( getString( R.string.tvInAnotherTime ) , ( dialogInterface , i ) -> {
                            dialogInterface.dismiss( );
                            builder = null;
                        } ).setPositiveButton( getString( R.string.tvRegisterNow ) , ( dialogInterface , i ) -> {
                            showDialog( );
                            dialogInterface.dismiss( );
                            builder = null;
                        } ).setCancelable( false ).create( ).show( );
                    }
                }
            }
        } );


    }

    Dialog dialogPhoneNum = null;

    private void showPhoneDialog( ) {
        if ( dialogPhoneNum == null ) {
            dialogPhoneNum = new Dialog( this );
            dialogPhoneNum.requestWindowFeature( Window.FEATURE_NO_TITLE );
            dialogPhoneNum.setContentView( R.layout.bottom_sheet_phone );
            dialogPhoneNum.setCancelable( false );
            dialogPhoneNum.show( );
        }

        TextView tvTimer = dialogPhoneNum.findViewById( R.id.tvTimer );
        ImageView imgTimer = dialogPhoneNum.findViewById( R.id.imgTimer );
        Button btn = dialogPhoneNum.findViewById( R.id.btnLogin );
        PinView pinView = dialogPhoneNum.findViewById( R.id.firstPinView );
        tvTimer.setEnabled( false );

        tvTimer.setOnClickListener( view -> {
            if ( tvTimer.isEnabled( ) ) {
                resendVerificationCode( phoneNum , forceResendingToken );
                timer.start( );
                tvTimer.setEnabled( false );
                imgTimer.setVisibility( View.VISIBLE );
            }
        } );


        timer = new CountDownTimer( 60000 , 1000 ) {
            @SuppressLint( "SetTextI18n" )
            @Override
            public void onTick( long l ) {
                tvTimer.setText( "00:" + l / 1000 );
            }

            @Override
            public void onFinish( ) {
                tvTimer.setText( R.string.resendCode );
                imgTimer.setVisibility( View.GONE );
                tvTimer.setEnabled( true );
                timer.cancel( );
            }
        }.start( );

        pinView.addTextChangedListener( new TextWatcher( ) {
            @Override
            public void beforeTextChanged( CharSequence charSequence , int i , int i1 , int i2 ) {

            }

            @Override
            public void onTextChanged( CharSequence charSequence , int i , int i1 , int i2 ) {
                pinView.setLineColor( Color.parseColor( "#64A811" ) );
                btn.setBackgroundColor( Color.parseColor( "#0E2E3B" ) );
                btn.setTextColor( Color.WHITE );
            }

            @Override
            public void afterTextChanged( Editable editable ) {

            }
        } );

        Window window = dialogPhoneNum.getWindow( );
        window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT );
        window.setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        window.getAttributes( ).windowAnimations = R.style.DialogAnimation;
        window.setGravity( Gravity.BOTTOM );

        btn.setOnClickListener( view -> {
            if ( Objects.requireNonNull( pinView.getText( ) ).toString( ).trim( ).isEmpty( ) ) {
                Toast.makeText( this , "code empty" , Toast.LENGTH_SHORT ).show( );
                return;
            }

            String code = pinView.getText( ).toString( );
            if ( verificationID != null ) {
                binding.progressBarLogin.setVisibility( View.VISIBLE );
                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential( verificationID , code );
                FirebaseAuth.getInstance( ).signInWithCredential( phoneAuthCredential ).addOnCompleteListener( task -> {
                    binding.progressBarLogin.setVisibility( View.GONE );
                    if ( task.isSuccessful( ) ) {
                        firestore.collection( "users" ).document( "+970" + phoneNum ).get( ).addOnCompleteListener( task1 -> {
                            if ( task1.isSuccessful( ) ) {
                                User user = task1.getResult( ).toObject( User.class );
                                if ( user != null ) {
                                    if ( user.getAccountType( ).equals( "worker" ) ) {
                                        startActivity( new Intent( getBaseContext( ) , WorkerActivities.class ) );
                                        finish( );
                                    } else if ( user.getAccountType( ).equals( "work owner" ) ) {
                                        startActivity( new Intent( getBaseContext( ) , WorkOwnerProfileActivity.class ) );
                                        finish( );
                                    }
                                }
                            }
                        } );

                    } else {
                        Toast.makeText( LoginActivity.this , "Verification Code Invalid" , Toast.LENGTH_SHORT ).show( );
                    }
                } ).addOnFailureListener( e ->          {}
                        //Todo Add LLField
                );
            }
        } );
        dialogPhoneNum.show( );
    }

    private void resendVerificationCode( String phone , PhoneAuthProvider.ForceResendingToken token ) {
        PhoneAuthProvider.getInstance( ).verifyPhoneNumber( phone , 60 , TimeUnit.SECONDS , this , mCallbacks , token );
    }

    Dialog dialogAccountType = null;

    private void showDialog( ) {

        if ( dialogAccountType == null ) {
            dialogAccountType = new Dialog( this );
            dialogAccountType.requestWindowFeature( Window.FEATURE_NO_TITLE );
            dialogAccountType.setContentView( R.layout.bottom_sheet_account_type );
//            dialogAccountType.setCanceledOnTouchOutside(false);
            dialogAccountType.show( );
        }


        View workerLayout = dialogAccountType.findViewById(R.id.workerLayout);
        View workOwnerLayout = dialogAccountType.findViewById(R.id.workOwnerLayout);


        Window window = dialogAccountType.getWindow( );
        window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT );
        window.setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        window.getAttributes( ).windowAnimations = R.style.DialogAnimation;
        window.setGravity( Gravity.BOTTOM );

        dialogAccountType.show( );

        workerLayout.setOnClickListener( view -> {
            dialogAccountType.dismiss( );

            editor.putString( "accountType" , "worker" );
            editor.apply( );
            Toast.makeText( this , sp.getString( "accountTypeWorker" , "" ) , Toast.LENGTH_SHORT ).show( );
            Intent intent = new Intent( getBaseContext( ) , PhoneRegistrationActivity.class );
            startActivity( intent );
        } );
        workOwnerLayout.setOnClickListener( view -> {

            editor.putString( "accountType" , "work owner" );
            editor.apply( );
            dialogAccountType.dismiss( );
            Intent intent = new Intent( getBaseContext( ) , PhoneRegistrationActivity.class );
            startActivity( intent );
        } );

    }

    private void setUnderline( TextView tv ) {
        String text = tv.getText( ).toString( );
        SpannableString content = new SpannableString( text );
        content.setSpan( new UnderlineSpan( ) , 0 , text.length( ) , 0 );
        tv.setText( content );
    }

    @Override
    protected void onStart( ) {
        super.onStart( );
        if ( currentUser != null ) {
            String accountType = sp.getString( "accountType" , "worker" );
            if ( accountType.equals( "worker" ) ) {
                startActivity( new Intent( getBaseContext( ) , WorkerActivities.class ) );
                finish( );
            } else if ( accountType.equals( "work owner" ) ) {
                startActivity( new Intent( getBaseContext( ) , WorkOwnerProfileActivity.class ) );
                finish( );
            }

        }
    }


}