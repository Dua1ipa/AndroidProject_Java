package com.example.project.MakeRoom;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.project.Data.TaxiRoom;

import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class MakeRoomFragment extends Fragment {
    private static final String TAG = "MakeRoomFragment";

    private final  String taxiRoomPath ="Rooms/TaxiRooms";

    TaxiRoom taxiRoom;

    TextView selectedDateTextView;
    ImageView btn_selectDate;
    TimePicker timePicker;

    int hour, minute;

    private AlertDialog.Builder alertDialogBuilder;

    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_make_room, container, false);

        btn_selectDate = viewGroup.findViewById(R.id.selectDateImageButton);
        selectedDateTextView = viewGroup.findViewById(R.id.selectDateTextView);
        timePicker = viewGroup.findViewById(R.id.departure_time_picker);

        setDate();
        makeRoomAlter();

        return viewGroup;
    }

    public void setDate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }
        String selectedTime = String.format(Locale.getDefault(), "%02d시 : %02d분", hour, minute);

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
                                    showToast("지나간 날짜를 선택할 수 없습니다!!!");
                                } else {
                                    String selectedDate = selectedYear+"년"+" "+(selectedMonth+1)+"월"+" "+selectedDayOfMonth+"일";
                                    selectedDateTextView.setText(selectedDate);
                                }
                            }
                        }, currentYear, currentMonth, currentDayOfMonth);
                datePickerDialog.show();
            }
        });
    }

    private void makeRoomAlter(){
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("방을 개설 하시겠습니까?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                user = FirebaseAuth.getInstance().getCurrentUser();
                String userid = user.getUid();

                databaseReference = databaseReference.child(taxiRoomPath).push();


            }
        });
        alertDialogBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }

    private void showToast(String msg){Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();}
}