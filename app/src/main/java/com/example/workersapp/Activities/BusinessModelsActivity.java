package com.example.workersapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.workersapp.Adapters.SliderImgAdapter;
import com.example.workersapp.Adapters.categoryAdapter;
import com.example.workersapp.Listeneres.clickListener;
import com.example.workersapp.R;
import com.example.workersapp.databinding.ActivityBusinessModelsBinding;

import java.util.ArrayList;

public class BusinessModelsActivity extends AppCompatActivity implements clickListener {
    ActivityBusinessModelsBinding binding;

    ArrayList<String> categoryArrayList;

    categoryAdapter categoryAdapter;

    ArrayList<String> imageArrayList;
    SliderImgAdapter sliderImgAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusinessModelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryArrayList = new ArrayList<>();
        imageArrayList = new ArrayList<>();

        //TODO : تخزين مصفوفة الفئات
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add(" خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث ");
        categoryArrayList.add(" خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث خشبي");
        categoryArrayList.add("أثاث ");
        categoryArrayList.add("أثاث خشبي");

        categoryAdapter = new categoryAdapter(categoryArrayList);
        binding.businessRv.setAdapter(categoryAdapter);
        binding.businessRv.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                RecyclerView.HORIZONTAL, false));


//        imageArrayList.add(String.valueOf(R.drawable.profile));
//        imageArrayList.add(String.valueOf(R.drawable.user));
//        imageArrayList.add(String.valueOf(R.drawable.pic));
//        imageArrayList.add(String.valueOf(R.drawable.pic2));

        //TODO: اجيب الصور من الفايرستور من شاشة اضافة نموذج
        imageArrayList.add(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.user).toString());
        imageArrayList.add(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.user).toString());
        imageArrayList.add(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.user).toString());
        imageArrayList.add(Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.user).toString());

//        imageArrayList.add("android.resource://" + getPackageName() + "/drawable/" + R.drawable.user);
//        imageArrayList.add("android.resource://" + getPackageName() + "/drawable/" + R.drawable.user);
//        imageArrayList.add("android.resource://" + getPackageName() + "/drawable/" + R.drawable.user);
//        imageArrayList.add("android.resource://" + getPackageName() + "/drawable/" + R.drawable.user);
//        imageArrayList.add("android.resource://" + getPackageName() + "/drawable/" + R.drawable.user);
//        imageArrayList.add("android.resource://" + getPackageName() + "/drawable/" + R.drawable.user);
//        imageArrayList.add("android.resource://" + getPackageName() + "/drawable/" + R.drawable.user);


        sliderImgAdapter = new SliderImgAdapter(imageArrayList, getBaseContext(), this);
        binding.businessRvImg.setAdapter(sliderImgAdapter);
        binding.businessRvImg.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                RecyclerView.HORIZONTAL, false));
        binding.businessRvImg.scrollToPosition(0);
        // منع التمرير
        binding.businessRvImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }
    @Override
    public void next(int currentPos) {
        currentPos = ((LinearLayoutManager) binding.businessRvImg.getLayoutManager()).findFirstVisibleItemPosition();
        binding.businessRvImg.smoothScrollToPosition(currentPos + 1);
    }

    @Override
    public void back(int currentPos) {
        currentPos = ((LinearLayoutManager) binding.businessRvImg.getLayoutManager()).findFirstVisibleItemPosition();
        binding.businessRvImg.smoothScrollToPosition(currentPos - 1);
    }

}