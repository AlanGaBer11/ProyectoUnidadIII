package com.example.proyectounidadiii.ui.database;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectounidadiii.R;
import com.example.proyectounidadiii.data.db.entities.HumEntity;
import com.example.proyectounidadiii.data.db.entities.LightEntity;
import com.example.proyectounidadiii.data.db.entities.MovEntity;
import com.example.proyectounidadiii.data.db.entities.TempEntity;
import com.example.proyectounidadiii.databinding.FragmentDataBaseBinding;
import com.example.proyectounidadiii.viewmodels.SensorDBViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class DataBaseFragment extends Fragment {

    private DataBaseViewModel mViewModel;
    private FragmentDataBaseBinding binding;
    private SensorDBViewModel sensorDB;

    public static DataBaseFragment newInstance() {
        return new DataBaseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDataBaseBinding.inflate(inflater, container, false);
        sensorDB = new ViewModelProvider(requireActivity()).get(SensorDBViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Gráficos
        setupLineChart(binding.tempChart, "Temperatura");
        setupLineChart(binding.humChart, "Humedad");
        setupLineChart(binding.lightChart, "Luz");
        setupPieChart(binding.movChart, "Movimiento");

        loadHistoricalData();
    }

    private void loadHistoricalData() {
        // Temperatura
        sensorDB.getAllTemp().observe(getViewLifecycleOwner(), temps -> {
            if (temps != null && !temps.isEmpty()) {
                Log.d("DB_TEMP", "Datos recibidos: " + temps.size());
                LineData data = createLineDataFromTemp(temps);
                binding.tempChart.setData(data);
                binding.tempChart.invalidate();
            } else {
                Log.d("DB_TEMP", "Sin datos de temperatura");
            }
        });

        // Humedad
        sensorDB.getAllHum().observe(getViewLifecycleOwner(), hums -> {
            if (hums != null && !hums.isEmpty()) {
                Log.d("DB_HUM", "Datos recibidos: " + hums.size());
                LineData data = createLineDataFromHum(hums);
                binding.humChart.setData(data);
                binding.humChart.invalidate();
            } else {
                Log.d("DB_HUM", "Sin datos de humedad");
            }
        });

        // Luz
        sensorDB.getAllLight().observe(getViewLifecycleOwner(), lights -> {
            if (lights != null && !lights.isEmpty()) {
                Log.d("DB_LIGHT", "Datos recibidos: " + lights.size());
                LineData data = createLineDataFromLights(lights);
                binding.lightChart.setData(data);
                binding.lightChart.invalidate();
            } else {
                Log.d("DB_LIGHT", "Sin datos de temperatura");
            }
        });

        // Movimiento (ahora PieChart)
        sensorDB.getAllMov().observe(getViewLifecycleOwner(), movs -> {
            if (movs != null && !movs.isEmpty()) {
                Log.d("DB_MOV", "Datos recibidos: " + movs.size());
                PieData data = createPieDataFromMov(movs);
                binding.movChart.setData(data);
                binding.movChart.invalidate();
            } else {
                Log.d("DB_MOV", "Sin datos de temperatura");
            }
        });
    }

    // Configuraciones básicas para los gráficos
    private void setupLineChart(LineChart chart, String label) {
        chart.getDescription().setText(label);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        chart.setBackgroundColor(Color.WHITE);
        chart.getLegend().setEnabled(true);
    }

    private void setupPieChart(PieChart chart, String label) {
        chart.getDescription().setText(label);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(30f);
        chart.setTransparentCircleRadius(35f);
        chart.getLegend().setEnabled(true);
    }

    // Crear LineData para Temperatura
    private LineData createLineDataFromTemp(List<TempEntity> temps) {
        ArrayList<Entry> entries = new ArrayList<>();
        int index = 0;
        for (TempEntity temp : temps) {
            entries.add(new Entry(index++, temp.value));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Temperatura (°C)");
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);
        return new LineData(dataSet);
    }

    // Crear LineData para Humedad
    private LineData createLineDataFromHum(List<HumEntity> hums) {
        ArrayList<Entry> entries = new ArrayList<>();
        int index = 0;
        for (HumEntity hum : hums) {
            entries.add(new Entry(index++, hum.value));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Humedad (%)");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);
        return new LineData(dataSet);
    }


    // Crear LineData para Luz (valores flotantes)
    private LineData createLineDataFromLights(List<LightEntity> lights) {
        ArrayList<Entry> entries = new ArrayList<>();
        int index = 0;
        for (LightEntity light : lights) {
            try {
                int val = Integer.parseInt(String.valueOf(light.value));
                entries.add(new Entry(index++, val));
            } catch (NumberFormatException e) {
                // Ignorar valores no numéricos
            }
        }
        LineDataSet dataSet = new LineDataSet(entries, "Luz");
        dataSet.setColor(Color.YELLOW);
        dataSet.setCircleColor(Color.YELLOW);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);
        return new LineData(dataSet);
    }

    // Crear PieData para Movimiento (sí/no)
    private PieData createPieDataFromMov(List<MovEntity> movs) {
        int yesCount = 0, noCount = 0;
        for (MovEntity mov : movs) {
            if ("SI".equalsIgnoreCase(mov.value)) yesCount++;
            else noCount++;
        }
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (yesCount > 0) entries.add(new PieEntry(yesCount, "Detectado"));
        if (noCount > 0) entries.add(new PieEntry(noCount, "No detectado"));

        PieDataSet dataSet = new PieDataSet(entries, "Movimiento");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setSliceSpace(3f);
        return new PieData(dataSet);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DataBaseViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}