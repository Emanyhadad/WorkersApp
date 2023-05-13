package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.workersapp.Adapters.FragmentAdapter;
import com.example.workersapp.Fragments.BlankFragment;
import com.example.workersapp.Fragments.NewJobFragment;
import com.example.workersapp.Fragments.PostFragment_inWorker;
import com.example.workersapp.Fragments.PostsFragment;
import com.example.workersapp.databinding.ActivityHomeForWorkerBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class HomeActivity_forWorker extends AppCompatActivity {
ActivityHomeForWorkerBinding binding;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        binding=ActivityHomeForWorkerBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );

        ArrayList <String> tab1 = new ArrayList <>(  );
        tab1.add( "عمل جديد" );
        tab1.add( "وظائف" );
        tab1.add( "قيد العمل" );
        tab1.add( "حسابي" );


        ArrayList< Fragment > fragments=new ArrayList <>(  );
        fragments.add( PostFragment_inWorker.newInstance( ) );
        fragments.add( PostsFragment.newInstance(  ) );
        fragments.add( BlankFragment.newInstance() );


        FragmentAdapter adapter=new FragmentAdapter(this,fragments );
        binding.viewPager2.setAdapter( adapter );
        new TabLayoutMediator( binding.tabLayout , binding.viewPager2 , ( tab , position ) -> {

        } ).attach();

    }
}