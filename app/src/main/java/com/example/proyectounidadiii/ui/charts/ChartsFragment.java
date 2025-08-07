package com.example.proyectounidadiii.ui.charts;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyectounidadiii.R;
import com.example.proyectounidadiii.databinding.FragmentChartsBinding;
import com.example.proyectounidadiii.viewmodels.SharedViewModel;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class ChartsFragment extends Fragment {

    private ChartsViewModel mViewModel;
    private FragmentChartsBinding binding;
    private SharedViewModel svm;

    private int entryIndex = 0;

    // Gráficas
    private LineData tempData;
    private LineData humData;
    private LineData lightData;
    private int movDetected = 0, movNone = 0;
    private boolean chartsInitialized = false;

    public static ChartsFragment newInstance() {
        return new ChartsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChartsBinding.inflate(inflater, container, false);

        // Inicializar ViewModel compartido
        svm = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observar estado de conexión
        svm.getEstado().observe(getViewLifecycleOwner(), estado -> {
            if (estado == null || estado.startsWith("Error") || estado.equals("Desconectado")) {
                Toast.makeText(requireContext(), "Debes estar conectado a un dispositivo", Toast.LENGTH_LONG).show();
                if (getView() != null) {
                    Navigation.findNavController(getView()).navigate(R.id.nav_home);
                }
            }
        });

        // Observar datos del sensor
        svm.getSensorData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;

            if (!chartsInitialized) {
                setupCharts();
                chartsInitialized = true;
            }

            parseAndUpdateCharts(data);
        });

        return binding.getRoot();
    }

    private void setupCharts() {
        // Temperatura
        LineDataSet tempSet = new LineDataSet(new ArrayList<>(), "Temperatura");
        tempSet.setColor(Color.RED);
        tempSet.setValueTextColor(Color.BLACK);
        tempSet.setDrawCircles(false);
        tempSet.setLineWidth(2f);
        tempData = new LineData(tempSet);
        binding.tempChart.setData(tempData);
        binding.tempChart.getDescription().setText("Temperatura");

        // Humedad
        LineDataSet humSet = new LineDataSet(new ArrayList<>(), "Humedad");
        humSet.setColor(Color.BLUE);
        humSet.setValueTextColor(Color.BLACK);
        humSet.setDrawCircles(false);
        humSet.setLineWidth(2f);
        humData = new LineData(humSet);
        binding.humChart.setData(humData);
        binding.humChart.getDescription().setText("Humedad");

        // Luz
        LineDataSet lightSet = new LineDataSet(new ArrayList<>(), "Luz");
        lightSet.setColor(Color.YELLOW);
        lightSet.setValueTextColor(Color.BLACK);
        lightSet.setDrawCircles(false);
        lightSet.setLineWidth(2f);
        lightData = new LineData(lightSet);
        binding.lightChart.setData(lightData);
        binding.lightChart.getDescription().setText("Luz");

        // Movimiento (Pie Chart)
        actualizarPieChart(); // Inicial
    }

    private void parseAndUpdateCharts(SharedViewModel.SensorData data) {
        entryIndex++;

        // Temperatura
        tempData.addEntry(new Entry(entryIndex, data.getTemperatura()), 0);
        tempData.notifyDataChanged();
        binding.tempChart.notifyDataSetChanged();
        binding.tempChart.setVisibleXRangeMaximum(50);
        binding.tempChart.moveViewToX(entryIndex);

        // Humedad
        humData.addEntry(new Entry(entryIndex, data.getHumedad()), 0);
        humData.notifyDataChanged();
        binding.humChart.notifyDataSetChanged();
        binding.humChart.setVisibleXRangeMaximum(50);
        binding.humChart.moveViewToX(entryIndex);

        // Luz
        lightData.addEntry(new Entry(entryIndex, data.getLuz()), 0);
        lightData.notifyDataChanged();
        binding.lightChart.notifyDataSetChanged();
        binding.lightChart.setVisibleXRangeMaximum(50);
        binding.lightChart.moveViewToX(entryIndex);

        // Movimiento
        if (data.isPresencia()) {
            movDetected++;
        } else {
            movNone++;
        }
        actualizarPieChart();
    }

    private void actualizarPieChart() {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(movDetected, "Presencia"));
        entries.add(new PieEntry(movNone, "Sin Presencia"));

        PieDataSet dataSet = new PieDataSet(entries, "Movimiento");
        dataSet.setColors(Color.GREEN, Color.GRAY);
        dataSet.setValueTextColor(Color.BLACK);
        PieData pieData = new PieData(dataSet);

        binding.movChart.setData(pieData);
        binding.movChart.invalidate(); // Refrescar
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChartsViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
