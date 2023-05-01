package com.example.workersapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityPostForWorkerBinding;

import java.util.Objects;

@SuppressLint( "Registered" )
public class PostActivity_forWorker extends AppCompatActivity {
    String offerDes,offerPrice,offerDuration;



ActivityPostForWorkerBinding binding;
    @Override
    protected void onCreate( @NonNull Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding = ActivityPostForWorkerBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );


        binding.btnSendOffer.setOnClickListener( view -> {

            offerDes=  Objects.requireNonNull( binding.etOfferDes.getText( ) ).toString();
            offerPrice=binding.spOfferPrice.getText().toString();
            offerDuration=binding.spOfferDuration.getText().toString();

            //TODO: GET USERID

            binding.tvWriteOffer.setVisibility( View.GONE );
            binding.LLWriteOffer.setVisibility( View.GONE );
                } );

    }

}