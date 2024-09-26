package com.example.project;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project.Home.HomeFragment;
import com.example.project.Join.JoinFragment;
import com.example.project.MakeRoom.MakeRoomFragment;
import com.example.project.MyPage.MyPageFragment;
import com.example.project.Search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.naver.maps.map.MapFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    FrameLayout container;
    HomeFragment homeFragment;
    MakeRoomFragment makeRoomFragment;
    MyPageFragment myPageFragment;
    SearchFragment searchFragment;
    JoinFragment joinFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 프래그먼트
        homeFragment = new HomeFragment();
        makeRoomFragment = new MakeRoomFragment();
        myPageFragment = new MyPageFragment();
        searchFragment = new SearchFragment();
        joinFragment = new JoinFragment();

        // 컨테이너
        container = findViewById(R.id.container);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        // 권한 설정

        // 하단바 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottom_home)
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                else if (item.getItemId() == R.id.bottom_makeRoom)
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, makeRoomFragment).commit();
                else if (item.getItemId() == R.id.bottom_search)
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, searchFragment).commit();
                else if (item.getItemId() == R.id.bottom_myPage)
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, myPageFragment).commit();
                else if (item.getItemId() == R.id.bottom_join)
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, joinFragment).commit();
                return false;
            }
        });

    }

    private void showToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}
}