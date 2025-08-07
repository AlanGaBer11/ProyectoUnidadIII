package com.example.proyectounidadiii.ui.tables;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectounidadiii.R;
import com.example.proyectounidadiii.viewmodels.SensorDBViewModel;

import java.util.ArrayList;

public class TablesFragment extends Fragment {

    private SensorDBViewModel sensorDBViewModel;
    private TempAdapter tempAdapter;
    private HumAdapter humAdapter;
    private LightAdapter lightAdapter;
    private MovAdapter movAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tables, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sensorDBViewModel = new ViewModelProvider(requireActivity()).get(SensorDBViewModel.class);

        setupRecyclerViews(view);
        observeData();
    }

    private void setupRecyclerViews(View view) {
        // Configuración común para todos los RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        // Temperatura
        RecyclerView rvTemp = view.findViewById(R.id.rv_temp);
        rvTemp.setLayoutManager(layoutManager);
        tempAdapter = new TempAdapter(new ArrayList<>());
        rvTemp.setAdapter(tempAdapter);

        // Humedad
        RecyclerView rvHum = view.findViewById(R.id.rv_hum);
        rvHum.setLayoutManager(new LinearLayoutManager(getContext()));
        humAdapter = new HumAdapter(new ArrayList<>());
        rvHum.setAdapter(humAdapter);

        // Luz
        RecyclerView rvLight = view.findViewById(R.id.rv_light);
        rvLight.setLayoutManager(new LinearLayoutManager(getContext()));
        lightAdapter = new LightAdapter(new ArrayList<>());
        rvLight.setAdapter(lightAdapter);

        // Movimiento
        RecyclerView rvMov = view.findViewById(R.id.rv_mov);
        rvMov.setLayoutManager(new LinearLayoutManager(getContext()));
        movAdapter = new MovAdapter(new ArrayList<>());
        rvMov.setAdapter(movAdapter);
    }

    private void observeData() {
        sensorDBViewModel.getAllTemp().observe(getViewLifecycleOwner(), tempEntities -> {
            if (tempEntities != null && !tempEntities.isEmpty()) {
                tempAdapter.updateData(tempEntities);
            }
        });

        sensorDBViewModel.getAllHum().observe(getViewLifecycleOwner(), humEntities -> {
            if (humEntities != null && !humEntities.isEmpty()) {
                humAdapter.updateData(humEntities);
            }
        });

        sensorDBViewModel.getAllLight().observe(getViewLifecycleOwner(), lightEntities -> {
            if (lightEntities != null && !lightEntities.isEmpty()) {
                lightAdapter.updateData(lightEntities);
            }
        });

        sensorDBViewModel.getAllMov().observe(getViewLifecycleOwner(), movEntities -> {
            if (movEntities != null && !movEntities.isEmpty()) {
                movAdapter.updateData(movEntities);
            }
        });
    }
}
