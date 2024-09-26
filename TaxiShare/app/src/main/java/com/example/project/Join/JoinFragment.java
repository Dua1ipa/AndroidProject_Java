package com.example.project.Join;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.project.Data.SwipeToDeleteCallback;
import com.example.project.Data.TaxiRoom;
import com.example.project.Data.TaxiRoomsAdapter;
import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JoinFragment extends Fragment {
    private static final String TAG = "JoinFragment";

    // 파이어베이스
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private String uid;

    private RecyclerView recyclerView;
    private ArrayList<TaxiRoom> roomsList;
    private TaxiRoomsAdapter taxiRoomsAdapter;

    AppCompatButton btn_joinedRoom, btn_createdRoom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_join, container, false);

        recyclerView = viewGroup.findViewById(R.id.recyclerView);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        btn_createdRoom = viewGroup.findViewById(R.id.btn_createdRoom);
        btn_createdRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usersInfo/"+uid+"/TaxiRooms");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {  //Firebase에서 데이터 가져오기
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){  //택시 방이 개설 되어 있으면
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){  //DB 순회 하면서 읽기
                                TaxiRoom taxiRoom = dataSnapshot.getValue(TaxiRoom.class);
                                roomsList.add(taxiRoom);
                            }
                            taxiRoomsAdapter = new TaxiRoomsAdapter(roomsList, new TaxiRoomsAdapter.ItemClickListener() {
                                @Override
                                public void onItemClick(TaxiRoom item) {  //생성된 택시 방을 누르면
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                    alertDialogBuilder.setTitle("채팅 참여 안내");
                                    alertDialogBuilder.setMessage("채팅에 참여 하시겠습니까?");
                                    alertDialogBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    alertDialogBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            });  //Adapter 설정
                            recyclerView.setAdapter(taxiRoomsAdapter);
                            // ItemTouchHelper를 사용하여 스와이프 동작 추가
                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(taxiRoomsAdapter));
                            itemTouchHelper.attachToRecyclerView(recyclerView);
                            taxiRoomsAdapter.notifyDataSetChanged();
                            showToast("방이 삭제되었습니다.");
                        }else{showToast("데이터가 없습니다");}
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        });

        btn_joinedRoom = viewGroup.findViewById(R.id.btn_joinedRoom);
        btn_joinedRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return viewGroup;
    }

    private void showToast(String msg){Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();}

}