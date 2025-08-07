package com.example.proyectounidadiii.ui.tables;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectounidadiii.R;
import com.example.proyectounidadiii.data.db.entities.MovEntity;

import java.util.List;

public class MovAdapter extends RecyclerView.Adapter<MovAdapter.MovViewHolder> {
    private List<MovEntity> movList;

    public MovAdapter(List<MovEntity> movList) {
        this.movList = movList;
    }

    @NonNull
    @Override
    public MovViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor_data, parent, false);
        return new MovViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovViewHolder holder, int position) {
        MovEntity mov = movList.get(position);
        holder.bind(mov);
    }

    @Override
    public int getItemCount() {
        return movList != null ? movList.size() : 0;
    }

    public void updateData(List<MovEntity> newData) {
        movList = newData;
        notifyDataSetChanged();
    }

    static class MovViewHolder extends RecyclerView.ViewHolder {
        private TextView tvValue, tvDate;

        public MovViewHolder(@NonNull View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.tv_value);
            tvDate = itemView.findViewById(R.id.tv_date);
        }

        public void bind(MovEntity mov) {
            String estado = "1".equals(mov.value) ? "Detección" : "Sin detección";
            tvValue.setText(String.format("Estado: %s", estado));
            tvDate.setText(String.format("Fecha: %s - Hora: %s", mov.date, mov.time));
        }
    }
}