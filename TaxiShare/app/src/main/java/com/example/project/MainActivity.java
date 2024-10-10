package com.example.project;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.project.Chatting.ChattingFragment;
import com.example.project.Home.HomeFragment;
import com.example.project.Join.CurrentFragment;
import com.example.project.Join.JoinedFragment;
import com.example.project.MakeRoom.MakeRoomFragment;
import com.example.project.MyPage.MyPageFragment;
import com.example.project.Search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    FrameLayout container;
    HomeFragment homeFragment;
    MakeRoomFragment makeRoomFragment;
    MyPageFragment myPageFragment;
    SearchFragment searchFragment;
    CurrentFragment currentFragment;
    ChattingFragment chattingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 프래그먼트
        homeFragment = new HomeFragment();
        makeRoomFragment = new MakeRoomFragment();
        chattingFragment = new ChattingFragment();
        myPageFragment = new MyPageFragment();
        searchFragment = new SearchFragment();
        currentFragment = new CurrentFragment();

        // 컨테이너
        container = findViewById(R.id.container);

        // 권한 설정

        // 하단바 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if(savedInstanceState == null) {  //기본 화면을 설정 (처음 실행 시 HomeFragment를 보여줌)
            getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment selectedFragment = null;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottom_home)
                    selectedFragment = homeFragment;
                else if (item.getItemId() == R.id.bottom_chat)
                    selectedFragment = chattingFragment;
                else if (item.getItemId() == R.id.bottom_search)
                    selectedFragment = searchFragment;
                else if (item.getItemId() == R.id.bottom_myPage)
                    selectedFragment = myPageFragment;
                else if (item.getItemId() == R.id.bottom_current)
                    selectedFragment = currentFragment;

                getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();

                return true;
            }
        });
    }

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}
}