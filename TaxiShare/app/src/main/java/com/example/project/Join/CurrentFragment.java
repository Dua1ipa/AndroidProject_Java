package com.example.project.Join;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.project.R;

public class CurrentFragment extends Fragment {

    Button btn_created, btn_joined;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_current, container, false);

        // 생성한 방
        btn_created = viewGroup.findViewById(R.id.btn_created);
        btn_created.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.current_container, new CreatedFragment()).commit();
                textColorChange(0);
            }
        });

        // 참여한 방
        btn_joined = viewGroup.findViewById(R.id.btn_joined);
        btn_joined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.current_container, new JoinedFragment()).commit();
                textColorChange(1);
            }
        });

        textColorChange(0);
        getFragmentManager().beginTransaction().replace(R.id.current_container, new CreatedFragment()).commit();

        return viewGroup;
    }

    // 색 변경 함수 //
    private void textColorChange(int num){
        if(num == 0){
            btn_created.setTextColor(getResources().getColor(R.color.black));
            btn_joined.setTextColor(getResources().getColor(R.color.white));
        }else if (num == 1){
            btn_created.setTextColor(getResources().getColor(R.color.white));
            btn_joined.setTextColor(getResources().getColor(R.color.black));
        }
    }

}