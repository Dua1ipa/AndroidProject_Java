package com.example.project.Data;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TaxiRoomsAdapter extends RecyclerView.Adapter<TaxiRoomsAdapter.ViewHolder> {
    private static final String TAG = "TaxiRoomsAdapter";

    FirebaseUser user;

    private ArrayList<TaxiRoom> roomsList;
    private ArrayList<ChatData> chatList;
    private ItemClickListener itemClickListener;

    // 생성자
    public TaxiRoomsAdapter(ArrayList<TaxiRoom> roomsList){
        this.roomsList = roomsList;
    }

    // 생성자
    public TaxiRoomsAdapter(ArrayList<TaxiRoom> roomsList,ItemClickListener itemClickListener){
        this.roomsList = roomsList;
        this.itemClickListener = itemClickListener;
    }

    public TaxiRoomsAdapter(ArrayList<TaxiRoom> roomsList, ArrayList<ChatData> chatList, ItemClickListener itemClickListener){
        this.roomsList = roomsList;
        this.chatList = chatList;
        this.itemClickListener = itemClickListener;
    }

    // 새로운 ViewHolder 객체를 생성
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taxi_room_item, parent, false);

        return new ViewHolder(view);
    }

    // ViewHolder와 데이터를 바인딩
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaxiRoom taxiRoom = roomsList.get(position);

        holder.roomName.setText(taxiRoom.getRoomName());
        holder.countPerson.setText(taxiRoom.getCountPerson());
        holder.departure.setText(taxiRoom.getDeparture());
        holder.arrival.setText(taxiRoom.getDestination());
        holder.departureDate.setText(taxiRoom.getDateOfDeparture());
        holder.departureTime.setText(taxiRoom.getTimeOfDeparture());
    }

    // RecyclerView의 항목 수 반환 //
    @Override
    public int getItemCount() {return roomsList.size();}

    //
    public interface ItemClickListener{ void onItemClick(TaxiRoom item);}

    //
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView roomName, countPerson, departure, arrival, departureDate ,departureTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.text_roomName);
            countPerson = itemView.findViewById(R.id.text_countPerson);
            departure = itemView.findViewById(R.id.text_departure);
            arrival = itemView.findViewById(R.id.text_arrival);
            departureDate = itemView.findViewById(R.id.text_departureDate);
            departureTime = itemView.findViewById(R.id.text_departureTime);
            itemView.setOnClickListener(view -> itemClickListener.onItemClick(roomsList.get(getAdapterPosition())));
        }
    }

    // 삭제 함수 //
    public void deleteItem(int position){
        TaxiRoom taxiRoom = roomsList.get(position);
        String roomKey = taxiRoom.getRoomKey();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        // Firebase에서 해당 항목 삭제
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usersInfo/"+uid+"/TaxiRooms").child(roomKey);
        databaseReference.removeValue();

        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chatsInfo/"+uid).child(roomKey);
        chatsRef.removeValue();

        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("roomsInfo/"+uid).child(roomKey);
        roomsRef.removeValue();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collectionPath = "chatsInfo";
        String documentPath = roomKey;

        db.collection(collectionPath)
                .document(documentPath)
                .collection(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                            document.getReference().delete();  // 각 메시지 문서 삭제
                        }
                        Log.d(TAG, "모든 메시지 삭제 완료");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "메시지 삭제 실패", e);
                    }
                });

        roomsList.remove(position);

        notifyItemRemoved(position);
    }

    // 애니메이션 함수 //
    public void animation(){}
}
