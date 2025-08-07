// TempAdapter.java
package com.example.proyectounidadiii.ui.tables;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectounidadiii.R;
import com.example.proyectounidadiii.data.db.entities.TempEntity;

import java.util.List;

public class TempAdapter extends RecyclerView.Adapter<TempAdapter.TempViewHolder> {
    private List<TempEntity> tempList;

    public TempAdapter(List<TempEntity> tempList) {
        this.tempList = tempList;
    }

    @NonNull
    @Override
    public TempViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor_data, parent, false);
        return new TempViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TempViewHolder holder, int position) {
        TempEntity temp = tempList.get(position);
        holder.bind(temp);
    }

    @Override
    public int getItemCount() {
        return tempList != null ? tempList.size() : 0;
    }

    public void updateData(List<TempEntity> newData) {
        tempList = newData;
        notifyDataSetChanged();
    }

    static class TempViewHolder extends RecyclerView.ViewHolder {
        private TextView tvValue, tvDate;

        public TempViewHolder(@NonNull View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.tv_value);
            tvDate = itemView.findViewById(R.id.tv_date);
        }

        public void bind(TempEntity temp) {
            tvValue.setText(String.format("Valor: %.1f°C", temp.value));
            tvDate.setText(String.format("Fecha: %s", temp.date));
        }
    }
}