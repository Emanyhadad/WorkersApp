package com.example.workersapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.workersapp.Adapters.OnboardingPageAdapter;
import com.example.workersapp.R;
import com.example.workersapp.Utilities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class OnboardingActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private Button btnSkip;
    private LinearLayout dotsLayout;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    FirebaseFirestore firestore;

    private int[] imageResources = {R.drawable.bording1, R.drawable.bording2, R.drawable.bording3};
    private String[] texts = {"شغيل، المنصة الفلسطينية الأولى لدمج السوق المهني في قطاعات التكنولوجيا ",
            "مرحبًا بك في تطبيق شغيل! نحن هنا لمساعدتك على توسيع دائرة عملك وتحقيق المزيد من العمل. ",
            "استعد لتجربة رائعة وفرص مثيرة. دعنا نبدأ!"};
    private OnboardingPageAdapter pageAdapter;

    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance( );

        sp = getSharedPreferences("MyPreferencesBoarding", MODE_PRIVATE);
        editor = sp.edit();

        viewPager = findViewById(R.id.viewPager);
        btnSkip = findViewById(R.id.btnSkip);
        dotsLayout = findViewById(R.id.dotsLayout);

        pageAdapter = new OnboardingPageAdapter(this, imageResources, texts);
        viewPager.setAdapter(pageAdapter);

        addDotsIndicator(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);
                if (position == imageResources.length - 1) {
                    btnSkip.setText("البدء!");
                } else {
                    btnSkip.setText("التالي");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        btnSkip.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem == imageResources.length - 1) {
                if (currentUser != null){
                    firestore.collection( "users" ).document(currentUser.getPhoneNumber() ).get( ).addOnCompleteListener( task1 -> {
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
                    startActivity(new Intent(OnboardingActivity.this, GuestActivity.class));
                    finish();
                }
                editor.putBoolean("appUp-lode", true);
                editor.apply();
            } else {
                viewPager.setCurrentItem(currentItem + 1);
            }
        });
    }

    private void addDotsIndicator(int position) {
        TextView[] dots = new TextView[imageResources.length];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("\u2022");
            dots[i].setTextSize(45);
            dots[i].setTextColor(getResources().getColor(R.color.gray));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.green));
        }
    }
}

