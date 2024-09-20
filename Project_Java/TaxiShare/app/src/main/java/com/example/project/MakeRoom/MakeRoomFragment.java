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

import com.example.project.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;

import java.util.Calendar;
import java.util.Locale;

public class MakeRoomFragment extends Fragment {
    private static final String TAG = "MakeRoomFragment";

    private final  String taxiRoomPath ="Rooms/TaxiRooms";

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

    int hour, minute;

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

        timePicker.setHour(Calendar.HOUR_OF_DAY);
        timePicker.setMinute(Calendar.MINUTE);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                hour = i;
                minute = i1;
            }
        });

        // 달력 버튼 (출발 날짜)
        btn_selectDate = viewGroup.findViewById(R.id.btn_selectDate);
        btn_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                                if (selectedYear < currentYear ||
                                        (selectedYear == currentYear && selectedMonth < currentMonth) ||
                                        (selectedYear == currentYear && selectedMonth == currentMonth && selectedDayOfMonth < currentDayOfMonth)) {
                                    showToast("지나간 날짜를 선택할 수 없습니다");
                                } else {
                                    String selectedDate = selectedYear+"년"+" "+(selectedMonth+1)+"월"+" "+selectedDayOfMonth+"일";
                                    text_selectDate.setText(selectedDate);
                                }
                            }
                        }, currentYear, currentMonth, currentDayOfMonth);
                datePickerDialog.show();
            }
        });

        // 뒤로가기 버튼
        btn_back = viewGroup.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                String selectedDate = text_selectDate.getText().toString(); //출발 날짜
                String selectedTime = String.format(Locale.getDefault(), "%02d시 : %02d분", hour, minute);
                String desc = edit_description.getText().toString();    //설명

                if(roomName.equals("")){  //방 이름이 공백이면
                    edit_roomName.setError("방 이름을 입력해주세요");
                } else if(selectedDate.equals("")){  //출발 날짜가 공백이면
                    text_date.setTextColor(getResources().getColor(R.color.navigation_color_mood));
                    text_selectDate.setError("날짜를 선택해주세요");
                }else if (selectedTime.equals("")){  //출발 시간이 공백이면
                    text_time.setTextColor(getResources().getColor(R.color.navigation_color_mood));
                }else{
                    if (desc.equals("")){  //설명이 공백이면
                        edit_description.setText("없음");
                    }

                    user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    String roomKey = databaseReference.child(taxiRoomPath).push().getKey();  //고유 키 생성

                    taxiRoom = new TaxiRoom(roomKey, uid, roomName, person, departure, arrival, selectedDate, selectedTime, desc);

                    DatabaseReference roomRef = databaseReference.child("usersInfo").child(uid);
                    roomRef.child("TaxiRooms").child(roomKey).setValue(taxiRoom)
                            .addOnSuccessListener(aVoid -> {
                                showToast("방이 생성 되었습니다");
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