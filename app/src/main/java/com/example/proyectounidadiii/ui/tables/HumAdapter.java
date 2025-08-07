package com.example.proyectounidadiii.ui.tables;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectounidadiii.R;
import com.example.proyectounidadiii.data.db.entities.HumEntity;

import java.util.List;

public class HumAdapter extends RecyclerView.Adapter<HumAdapter.HumViewHolder> {
    private List<HumEntity> humList;

    public HumAdapter(List<HumEntity> humList) {
        this.humList = humList;
    }

    @NonNull
    @Override
    public HumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor_data, parent, false);
        return new HumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HumViewHolder holder, int position) {
        HumEntity hum = humList.get(position);
        holder.bind(hum);
    }

    @Override
    public int getItemCount() {
        return humList != null ? humList.size() : 0;
    }

    public void updateData(List<HumEntity> newData) {
        humList = newData;
        notifyDataSetChanged();
    }

    static class HumViewHolder extends RecyclerView.ViewHolder {
        private TextView tvValue, tvDate;

        public HumViewHolder(@NonNull View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.tv_value);
            tvDate = itemView.findViewById(R.id.tv_date);
        }

        public void bind(HumEntity hum) {
            tvValue.setText(String.format("Valor: %.1f%%", hum.value));
            tvDate.setText(String.format("Fecha: %s", hum.date));
        }
    }
}
