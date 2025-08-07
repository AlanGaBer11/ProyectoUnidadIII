package com.example.proyectounidadiii.ui.tables;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectounidadiii.R;
import com.example.proyectounidadiii.data.db.entities.LightEntity;

import java.util.List;

public class LightAdapter extends RecyclerView.Adapter<LightAdapter.LightViewHolder> {
    private List<LightEntity> lightList;

    public LightAdapter(List<LightEntity> lightList) {
        this.lightList = lightList;
    }

    @NonNull
    @Override
    public LightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor_data, parent, false);
        return new LightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LightViewHolder holder, int position) {
        LightEntity light = lightList.get(position);
        holder.bind(light);
    }

    @Override
    public int getItemCount() {
        return lightList != null ? lightList.size() : 0;
    }

    public void updateData(List<LightEntity> newData) {
        lightList = newData;
        notifyDataSetChanged();
    }

    static class LightViewHolder extends RecyclerView.ViewHolder {
        private TextView tvValue, tvDate;

        public LightViewHolder(@NonNull View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.tv_value);
            tvDate = itemView.findViewById(R.id.tv_date);
        }

        public void bind(LightEntity light) {
            tvValue.setText(String.format("Valor: %d lux", light.value));
            tvDate.setText(String.format("Fecha: %s", light.date));
        }
    }
}