package com.example.workersapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    ActivityRegisterBinding binding;
    FirebaseFirestore db;
    FirebaseStorage storage;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private static final int REQUEST_CODE = 1;
    String fullName, nickName, birth, gender, accountType;
    int genderId;

    public static String image;


    @SuppressLint( "MissingPermission" )

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding = ActivityRegisterBinding.inflate( getLayoutInflater( ) );
        setContentView( binding.getRoot( ) );

        firebaseAuth = FirebaseAuth.getInstance( );
        firebaseUser = firebaseAuth.getCurrentUser( );
        db = FirebaseFirestore.getInstance( );
        storage = FirebaseStorage.getInstance( );

        if ( ContextCompat.checkSelfPermission( this , Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this , new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE } , REQUEST_CODE );
        }
        ActivityResultLauncher < String > al1 = registerForActivityResult( new ActivityResultContracts.GetContent( ) , result -> {
            if ( result != null ) {
                Glide.with( getBaseContext( ) ).load( result ).circleCrop( ).error( R.drawable.user ).into( binding.personImgUser );
                StorageReference reference = storage.getReference( "users/" + "images/" + firebaseUser.getPhoneNumber( ) );

                StorageTask < UploadTask.TaskSnapshot > uploadTask = reference.putFile( result );

                uploadTask.addOnSuccessListener( taskSnapshot -> reference.getDownloadUrl( ).addOnCompleteListener( task -> {
                    if ( task.isSuccessful( ) ) {
                        image = task.getResult( ).toString( );
                    }
                } ) );

            }
        } );

        binding.personBirth.setOnClickListener( view -> showDatePickerDialog( ) );

        binding.personAddImgUser.setOnClickListener( v -> al1.launch( "image/*" ) );

        binding.personBtnNext.setOnClickListener( view -> {
            fullName = Objects.requireNonNull( binding.personFullName.getText( ) ).toString( );
            nickName = Objects.requireNonNull( binding.personNickName.getText( ) ).toString( );
            birth = Objects.requireNonNull( binding.personBirth.getText( ) ).toString( );
            genderId = binding.personRadioGroup.getCheckedRadioButtonId( );
            gender = findViewById( genderId ).toString( );
            accountType = LoginActivity.sp.getString( "accountType" , "" );

            if ( binding.personMale.isChecked( ) ) {
                gender = getString( R.string.male );
            } else {
                gender = getString( R.string.female );
            }



             if ( !fullName.isEmpty( ) && !nickName.isEmpty( ) && !birth.isEmpty( ) ) {

                 Map < String, Object > data = new HashMap <>( );
                 data.put( "fullName" , fullName );
                 data.put( "nickName" , nickName );
                 data.put( "birth" , birth );
                 data.put( "gender" , gender );
                 data.put( "image" , image );


                 db.collection( "users" ).document( Objects.requireNonNull
                         ( firebaseUser.getPhoneNumber( ) ) ).set( data ).addOnSuccessListener(
                                 unused -> {}
                 );
                 Intent intent = new Intent( getBaseContext( ) , MapsActivity.class );
                 intent.putExtra( "accountType" , accountType );
                 intent.putExtra( "source" , RegisterActivity.class.getSimpleName( ) );
                 startActivity( intent );

             } else {
                 if ( fullName.isEmpty( ) ) {
                     binding.personFullName.setError( getString( R.string.tvFill ) );
                 } else if ( nickName.isEmpty( ) ) {
                     binding.personNickName.setError( getString( R.string.tvFill ) );
                 } else if ( birth.isEmpty( ) ) {
                     binding.personBirth.setError( getString( R.string.tvFill ) );



            }
        } });}


    private void showDatePickerDialog( ) {
        Calendar calendar;
        if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N ) {
            calendar = Calendar.getInstance( );
            int year = calendar.get( Calendar.YEAR );
            int month = calendar.get( Calendar.MONTH );
            int dayOfMonth = calendar.get( Calendar.DAY_OF_MONTH );

            DatePickerDialog datePickerDialog = new DatePickerDialog( this , this , year , month , dayOfMonth );
            datePickerDialog.show( );
        }

    }

    @Override
    public void onDateSet( DatePicker view , int year , int month , int dayOfMonth ) {
        String date = dayOfMonth + "/" + ( month + 1 ) + "/" + year;
        binding.personBirth.setText( date );
    }

}