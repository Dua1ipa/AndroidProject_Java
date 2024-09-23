package com.example.project.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.project.Login.LoginActivity;
import com.example.project.Login.SignUpActivity;

import com.example.project.R;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";

    FragmentPagerAdapter fragmentPagerAdapter;
    ImageButton btn_login;
    TextView btn_signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        FirstFragment firstFragment = new FirstFragment();
        adapter.addItem(firstFragment);
        SecondFragment secondFragment = new SecondFragment();
        adapter.addItem(secondFragment);
        ThirdFragment thirdFragment = new ThirdFragment();
        adapter.addItem(thirdFragment);

        viewPager.setAdapter(adapter);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        // 로그인 버튼
        btn_login = findViewById(R.id.btn_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                StartActivity.this.finish();

            }
        });

        // 회원가입 버튼
        btn_signUp = findViewById(R.id.btn_signUp);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                StartActivity.this.finish();
            }
        });
    }

    public static class MyPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();
        public MyPagerAdapter(FragmentManager fragmentManager){super(fragmentManager);}

        public void addItem(Fragment item){items.add(item);}

        @NonNull
        @Override
        public Fragment getItem(int position) {return items.get(position);}

        @Override
        public int getCount() {return items.size();}
    }
}