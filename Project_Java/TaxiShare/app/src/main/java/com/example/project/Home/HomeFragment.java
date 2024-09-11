package com.example.project.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.project.Data.TaxiRoom;

import com.example.project.MakeRoom.MakeRoomFragment;
import com.example.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    TextView text_count, text_1,text_2,text_3,text_4,text_5,text_6,text_7;
    Button btn_add, btn_login,btn_1,btn_all,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7;

    private RecyclerView recyclerView;
    private ArrayList<TaxiRoom> roomsList;
    MakeRoomFragment makeRoomFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        btn_all = viewGroup.findViewById(R.id.btn_all);

        text_1 = viewGroup.findViewById(R.id.text_1);
        text_1.setText(getDay(0));
        btn_1 = viewGroup.findViewById(R.id.btn_1);
        btn_1.setText(getTime(0));

        text_2 = viewGroup.findViewById(R.id.text_2);
        text_2.setText(getDay(1));
        btn_2 = viewGroup.findViewById(R.id.btn_2);
        btn_2.setText(getTime(1));

        text_3 = viewGroup.findViewById(R.id.text_3);
        text_3.setText(getDay(2));
        btn_3 = viewGroup.findViewById(R.id.btn_3);
        btn_3.setText(getTime(2));

        text_4 = viewGroup.findViewById(R.id.text_4);
        text_4.setText(getDay(3));
        btn_4 = viewGroup.findViewById(R.id.btn_4);
        btn_4.setText(getTime(3));

        text_5 = viewGroup.findViewById(R.id.text_5);
        text_5.setText(getDay(4));
        btn_5 = viewGroup.findViewById(R.id.btn_5);
        btn_5.setText(getTime(4));

        text_6 = viewGroup.findViewById(R.id.text_6);
        text_6.setText(getDay(5));
        btn_6 = viewGroup.findViewById(R.id.btn_6);
        btn_6.setText(getTime(5));

        text_7 = viewGroup.findViewById(R.id.text_7);
        text_7.setText(getDay(6));
        btn_7 = viewGroup.findViewById(R.id.btn_7);
        btn_7.setText(getTime(6));

        // 개설된 방 개수
        text_count = viewGroup.findViewById(R.id.text_count);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rooms");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                text_count.setText(String.valueOf(count)+"개");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // 방 개설 버튼
        makeRoomFragment = new MakeRoomFragment();
        btn_add = viewGroup.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, makeRoomFragment).commit();
            }
        });

        // 데이터 보여주기
        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        roomsList = new ArrayList<TaxiRoom>();

        return viewGroup;
    }

    // 시간 얻기 함수
    private String getTime(int day){
        TimeZone timeZone;
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date = calendar.getTime();
        calendar.add(Calendar.DATE, day);

        SimpleDateFormat format = new SimpleDateFormat("dd일", Locale.KOREAN);

        return format.format(calendar.getTime());
    }

    // 날짜 얻기 함수
    private String getDay(int day){
        TimeZone timeZone;
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date = calendar.getTime();
        calendar.add(Calendar.DATE, day);

        SimpleDateFormat format = new SimpleDateFormat("E", Locale.KOREAN);

        return format.format(calendar.getTime());
    }

}