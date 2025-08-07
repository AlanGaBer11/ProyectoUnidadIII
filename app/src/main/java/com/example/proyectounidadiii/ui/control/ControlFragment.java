package com.example.proyectounidadiii.ui.control;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyectounidadiii.databinding.FragmentControlBinding;
import com.example.proyectounidadiii.viewmodels.SharedViewModel;

public class ControlFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private FragmentControlBinding binding;
    private boolean esperandoEstadoActual = false;


    public static ControlFragment newInstance() {
        return new ControlFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentControlBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        setupObservers();
        setupListeners();
    }

    private void setupObservers() {
        sharedViewModel.getEstado().observe(getViewLifecycleOwner(), estado -> {
            if (estado != null) {
                binding.txtEstadoConexion.setText("Estado: " + estado);
                boolean conectado = estado.contains("Conectado");
                binding.switchSistema.setEnabled(conectado);
                binding.seekBarTemperatura.setEnabled(conectado);
                binding.btnAplicarTemperatura.setEnabled(conectado);
                binding.btnEstadoActual.setEnabled(conectado);
            }
        });

        sharedViewModel.getSensorData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                binding.switchSistema.setChecked(data.isSistemaActivo());

                if (esperandoEstadoActual) {
                    String mensaje = "Temperatura: " + data.getTemperatura() + " °C\n" +
                            "Humedad: " + data.getHumedad() + " %\n" +
                            "Luz: " + data.getLuz();

                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                    esperandoEstadoActual = false;  // desactiva la espera para futuros cambios
                }
            }
        });

    }

    private void setupListeners() {
        binding.switchSistema.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String comando = isChecked ? "SISTEMA_ON" : "SISTEMA_OFF";
            sharedViewModel.enviarComando(comando);
        });

        binding.seekBarTemperatura.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                int temperatura = 18 + progress;
                binding.txtTemperaturaConfig.setText(temperatura + " °C");
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}
        });

        binding.btnAplicarTemperatura.setOnClickListener(v -> {
            int temperatura = 18 + binding.seekBarTemperatura.getProgress();
            sharedViewModel.enviarComando("TEMP:" + temperatura);
            Toast.makeText(getContext(), "Temperatura configurada a " + temperatura + "°C", Toast.LENGTH_SHORT).show();
        });

        binding.btnEstadoActual.setOnClickListener(v -> {
            esperandoEstadoActual = true; // activas la espera
            sharedViewModel.enviarComando("STATUS");
            Toast.makeText(getContext(), "Solicitando estado actual...", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // evitar memory leaks
    }
}
