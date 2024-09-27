package com.example.project.MakeRoom;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.project.Data.TaxiRoom;

import com.example.project.Home.HomeFragment;
import com.example.project.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MakeRoomFragment extends Fragment {
    private static final String TAG = "MakeRoomFragment";

    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    TaxiRoom taxiRoom;

    ImageView btn_back;

    TextInputEditText edit_roomName;  //방 이름
    Spinner count_person, depart, arrive;  //인원, 출발지, 도착지
    TextView text_selectDate;  //출발 날짜 표시
    ImageView btn_selectDate;   //출발 날짜
    TimePicker timePicker;      //출발 시간
    EditText edit_description;; //상세 설명
    AppCompatButton btn_makeRoom;   //개설 버튼

    TextView text_date, text_time;

    int selectedHour, selectedMinute;;
    int currentYear, currentMonth, currentDayOfMonth;
    int selectedYear, selectedMonth, selectedDayOfMonth;

    private AlertDialog.Builder alertDialogBuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_make_room, container, false);

        timePicker = viewGroup.findViewById(R.id.timePicker);
        text_selectDate = viewGroup.findViewById(R.id.text_selectDate);

        text_date  = viewGroup.findViewById(R.id.text_date);
        text_time = viewGroup.findViewById(R.id.text_time);

        edit_roomName = viewGroup.findViewById(R.id.edit_roomName); //방 이름
        count_person = viewGroup.findViewById(R.id.count_person); //인원
        depart = viewGroup.findViewById(R.id.depart); //출발지
        arrive = viewGroup.findViewById(R.id.arrive); //도착지
        edit_description = viewGroup.findViewById(R.id.edit_description); //상세 설명

        database = FirebaseDatabase.getInstance();    //파이어베이스 참조
        databaseReference = database.getReference();  //파이어베이스 -> 데이터베이스 참조

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));

        // 달력 버튼 (출발 날짜)
        btn_selectDate = viewGroup.findViewById(R.id.btn_selectDate);
        btn_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentYear = calendar.get(Calendar.YEAR);
                currentMonth = calendar.get(Calendar.MONTH);
                currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                if ((year < currentYear) ||
                                        (year == currentYear && month < currentMonth) ||
                                        (year == currentYear && month == currentMonth && day < currentDayOfMonth)) {
                                    showToast("지나간 날짜를 선택할 수 없습니다");
                                }else {
                                    selectedYear = year;
                                    selectedMonth = month;
                                    selectedDayOfMonth = day;

                                    String selectedDate = year+"년"+" "+(month+1)+"월"+" "+day+"일";
                                    text_selectDate.setText(selectedDate);
                                }
                            }
                        }, currentYear, currentMonth, currentDayOfMonth);
                datePickerDialog.show();
            }
        });

        // 시간 선택
        int hour = calendar.get(Calendar.HOUR_OF_DAY);  //서울 현재 시간
        int minute = calendar.get(Calendar.MINUTE);     //서울 현재 분
        timePicker.setHour(hour);
        timePicker.setMinute(minute+1);
        selectedHour = hour;
        selectedMinute = minute;

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                if (selectedYear == currentYear && selectedMonth == currentMonth && selectedDayOfMonth == currentDayOfMonth){
                    if((i < hour) || (i == hour && i1 <= minute)){
                        showToast("지나간 시간은 선택할 수 없습니다");
                        timePicker.setHour(hour);
                        timePicker.setMinute(minute+1);
                    } else{
                        selectedHour = i;
                        selectedMinute = i1;
                    }
                }else{
                    selectedHour = i;
                    selectedMinute = i1;
                }
            }
        });

        // 뒤로가기 버튼
        btn_back = viewGroup.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
            }
        });

        // 방 개설 버튼
        btn_makeRoom = viewGroup.findViewById(R.id.btn_makeRoom);
        btn_makeRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeRoomAlter();
            }
        });
        return viewGroup;
    }

    // 방 생성 알림 //
    private void makeRoomAlter(){
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("방을 개설 하시겠습니까?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String roomName = edit_roomName.getText().toString();       //방 이름
                String person = count_person.getSelectedItem().toString();  //인원
                String departure = depart.getSelectedItem().toString();     //출발지
                String arrival = arrive.getSelectedItem().toString();       //도착지
                String selectedDate = String.format(Locale.getDefault(), "%d년 %d월 %d일", selectedYear, selectedMonth+1, selectedDayOfMonth);
                String selectedTime = String.format(Locale.getDefault(), "%02d시 : %02d분", selectedHour, selectedMinute);
                String desc = edit_description.getText().toString();    //설명

                if(roomName.equals("")){  //방 이름이 공백이면
                    edit_roomName.setError("방 이름을 입력해주세요");
                } else if(selectedDate.equals("")){  //출발 날짜가 공백이면
                    text_selectDate.setError("날짜를 선택해주세요");
                }else if (selectedTime.equals("")){  //출발 시간이 공백이면
                    text_time.setError("시간을 선택해주세요");
                }else{  //아무 문제 없으면
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    String roomKey = databaseReference.push().getKey();
                    if (desc.equals("")){  //설명이 공백이면
                        taxiRoom = new TaxiRoom(roomKey, uid, roomName, person, departure, arrival, selectedDate, selectedTime, "없음");
                    }else{
                        taxiRoom = new TaxiRoom(roomKey, uid, roomName, person, departure, arrival, selectedDate, selectedTime, desc);
                    }
                    DatabaseReference taxiRef = databaseReference.child("roomsInfo");
                    DatabaseReference roomRef = databaseReference.child("usersInfo").child(uid);
                    roomRef.child("TaxiRooms").child(roomKey).setValue(taxiRoom)
                            .addOnSuccessListener(aVoid -> {
                                taxiRef.child(roomKey).setValue(taxiRoom);
                                showToast("방이 생성 되었습니다");
                                getFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit(); 
                            })
                            .addOnFailureListener(e -> {
                                showToast("방 생성 실패");
                            });
                }
            }
        });
        alertDialogBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {dialogInterface.cancel();}
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showToast(String msg){Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();}
}