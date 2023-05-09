package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.example.workersapp.Adapters.FragmentAdapter;
import com.example.workersapp.Fragments.NewJobFragment;
import com.example.workersapp.Fragments.PostFragment_inWorker;
import com.example.workersapp.Fragments.PostsFragment;
import com.example.workersapp.databinding.ActivityWorkOwnerProfileBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class WorkOwnerProfileActivity extends AppCompatActivity {
    ActivityWorkOwnerProfileBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkOwnerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        Toast.makeText( this , user.getPhoneNumber() , Toast.LENGTH_SHORT ).show( );
//        getSupportFragmentManager().beginTransaction().add(R.id.container, new NewJobFragment()).commit();
//        getSupportFragmentManager().beginTransaction().add( R.id.container,new PostsFragment() ).commit();

        ArrayList <String> tab1 = new ArrayList <>(  );
        tab1.add( "F1" );
        tab1.add( "F2" );
        tab1.add( "F3" );
        tab1.add( "F4" );

        ArrayList< Fragment > fragments=new ArrayList <>(  );
        fragments.add( NewJobFragment.newInstance( "","" ) );
        fragments.add( PostsFragment.newInstance( "","" ) );
        fragments.add( PostFragment_inWorker.newInstance( ) );
        fragments.add( PostsFragment.newInstance( "","" ) );

        FragmentAdapter adapter=new FragmentAdapter(this,fragments );
        binding.viewPager2.setAdapter( adapter );
        new TabLayoutMediator( binding.tabLayout , binding.viewPager2 , ( tab , position ) -> {

        } ).attach();

    }
}