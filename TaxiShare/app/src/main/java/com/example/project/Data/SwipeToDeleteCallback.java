package com.example.project.Data;

import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private static final String TAG = "SwipeToDeleteCallback";

    private TaxiRoomsAdapter taxiRoomsAdapter;

    public SwipeToDeleteCallback(TaxiRoomsAdapter adapter) {
        super(0, ItemTouchHelper.LEFT);  // 왼쪽 스와이프 허용
        taxiRoomsAdapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;  //드래그 앤 드롭 기능 사용 안함
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();  //스와이프된 항목의 위치를 가져옴

        taxiRoomsAdapter.deleteItem(position);  //Adapter에서 항목을 삭제하고 Firebase에서도 삭제
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder,
                dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(Color.RED)
                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                .addSwipeLeftLabel("삭제")
                .setSwipeLeftLabelColor(Color.WHITE)
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

}
