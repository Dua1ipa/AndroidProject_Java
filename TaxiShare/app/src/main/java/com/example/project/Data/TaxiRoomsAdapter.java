package com.example.project.Data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TaxiRoomsAdapter extends RecyclerView.Adapter<TaxiRoomsAdapter.ViewHolder> {
    private static final String TAG = "TaxiRoomsAdapter";

    private ArrayList<TaxiRoom> arrayList;
    private ItemClickListener itemClickListener;

    public TaxiRoomsAdapter(ArrayList<TaxiRoom> arrayList, ItemClickListener itemClickListener){
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public TaxiRoomsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taxi_room_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaxiRoomsAdapter.ViewHolder holder, int position) {
        TaxiRoom taxiRoom = arrayList.get(position);


    }

    @Override
    public int getItemCount() {return arrayList.size();}

    public interface ItemClickListener{ void onItemClick(TaxiRoom item);}

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text_roomName, text_currentPerson, text_departure, text_arrival, text_departureTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // 애니메이션 함수 //
    public void animation(){}

}
