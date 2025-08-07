package com.example.proyectounidadiii.ui.sensors;

import androidx.lifecycle.ViewModelProvider;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectounidadiii.R;
import com.example.proyectounidadiii.data.db.entities.HumEntity;
import com.example.proyectounidadiii.data.db.entities.LightEntity;
import com.example.proyectounidadiii.data.db.entities.MovEntity;
import com.example.proyectounidadiii.data.db.entities.TempEntity;
import com.example.proyectounidadiii.databinding.FragmentSensorsBinding;
import com.example.proyectounidadiii.viewmodels.SensorDBViewModel;
import com.example.proyectounidadiii.viewmodels.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SensorsFragment extends Fragment {

    private SensorsViewModel mViewModel;
    private FragmentSensorsBinding binding;
    private SharedViewModel svm;
    private SensorDBViewModel sensorDB;
    private BluetoothAdapter btAdapter;
    private String lastTemp = "";
    private String lastHum = "";
    private String lastLight = "";
    private String lastMov = "";

    public static SensorsFragment newInstance() {
        return new SensorsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSensorsBinding.inflate(inflater, container, false);

        // Inicializar ViewModels
        svm = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sensorDB = new ViewModelProvider(this).get(SensorDBViewModel.class);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        // Observador del estado Bluetooth
        svm.getEstado().observe(getViewLifecycleOwner(), estado -> {
            binding.txtEstado.setText("Estado: " + estado);
            if (estado != null && estado.contains("Conectado")) {
                binding.statusIcon.setImageResource(R.drawable.bluetooth_connected);
                binding.statusIcon.setColorFilter(Color.GREEN);
            } else {
                binding.statusIcon.setImageResource(R.drawable.bluetooth_disabled);
                binding.statusIcon.setColorFilter(Color.RED);
            }
        });

        // Observador de datos de sensores
        svm.getSensorData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                updateUI(data);
                saveSensorData(data);
            }
        });

        return binding.getRoot();
    }

    private void updateUI(SharedViewModel.SensorData data) {
        // Sistema activo
        binding.txtPower.setText("Sistema: " + (data.isSistemaActivo() ? "Encendido" : "Apagado"));
        binding.powerIcon.setImageResource(data.isSistemaActivo() ? R.drawable.power : R.drawable.power_off);
        binding.powerIcon.setColorFilter(data.isSistemaActivo() ? Color.GREEN : Color.RED);

        // Modo ahorro
        binding.txtMode.setText("Modo: " + (data.isModoAhorro() ? "Ahorro" : "Normal"));
        binding.modeIcon.setColorFilter(data.isModoAhorro() ? Color.GREEN : Color.GRAY);

        // Estado del sistema
        String estadoSistema;
        if (!data.isSistemaActivo()) {
            estadoSistema = "Estado: STANDBY";
        } else if (data.isModoAhorro()) {
            estadoSistema = "Estado: AHORRO ENERGIA";
        } else if (data.isPresencia()) {
            boolean tempOptima = (data.getTemperatura() >= 20.0 && data.getTemperatura() <= 24.0);
            boolean humOptima = (data.getHumedad() >= 40.0 && data.getHumedad() <= 60.0);
            boolean luzOptima = (data.getLuz() >= 300 && data.getLuz() <= 800);

            if (tempOptima && humOptima && luzOptima) {
                estadoSistema = "Estado: ÓPTIMO";
            } else if ((tempOptima || humOptima) && luzOptima) {
                estadoSistema = "Estado: ACEPTABLE";
            } else {
                estadoSistema = "Estado: AJUSTANDO...";
            }
        } else {
            estadoSistema = "Estado: ESPERANDO PRESENCIA";
        }
        binding.txtEstadoSistema.setText(estadoSistema);

        // Datos de sensores
        binding.txtTemp.setText("Temperatura: " + data.getTemperatura() + " °C");
        binding.txtHum.setText("Humedad: " + data.getHumedad() + " %");
        binding.txtConfig.setText("Temp. config: " + data.getConfigTemp() + " °C");

        // Luz y ProgressBar
        int luzValue = Math.min(data.getLuz(), 1000);
        binding.txtLdr.setText("Intensidad: " + luzValue + " lux");
        binding.lightProgress.setProgress(luzValue);

        LayerDrawable progressBarDrawable = (LayerDrawable) binding.lightProgress.getProgressDrawable();
        Drawable progressDrawable = progressBarDrawable.getDrawable(1);

        if (luzValue < 300 || luzValue > 800) {
            progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            binding.luzStatus.setVisibility(View.VISIBLE);
        } else if (luzValue >= 300 && luzValue <= 500) {
            progressDrawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
            binding.luzStatus.setVisibility(View.GONE);
        } else {
            progressDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            binding.luzStatus.setVisibility(View.GONE);
        }
        binding.lightProgress.invalidate();

        // Movimiento
        binding.txtMov.setText(data.isPresencia() ? "Movimiento detectado" : "Sin movimiento detectado");
        if (data.isPresencia()) {
            binding.motionIcon.setImageResource(R.drawable.power);
            binding.motionIcon.setColorFilter(Color.GREEN);
            binding.txtUltimoMovimiento.setText("Último movimiento: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
        } else {
            binding.motionIcon.setImageResource(R.drawable.power_off);
            binding.motionIcon.setColorFilter(Color.GRAY);
        }

        // LEDs
        resetAllLeds();
        if (!data.isSistemaActivo()) {
            binding.ledAzul.setColorFilter(Color.BLUE);
        } else if (data.isModoAhorro()) {
            binding.ledAzul.setColorFilter(Color.BLUE);
        } else {
            boolean tempOptima = (data.getTemperatura() >= 20.0 && data.getTemperatura() <= 24.0);
            boolean humOptima = (data.getHumedad() >= 40.0 && data.getHumedad() <= 60.0);
            boolean luzOptima = (data.getLuz() >= 300 && data.getLuz() <= 800);

            if (tempOptima && humOptima && luzOptima) {
                binding.ledVerde.setColorFilter(Color.GREEN);
            } else if ((tempOptima || humOptima) && luzOptima) {
                binding.ledAmarillo.setColorFilter(Color.YELLOW);
            } else {
                binding.ledRojo.setColorFilter(Color.RED);
            }

            if (data.getLuz() < 100) {
                binding.ledAzul.setColorFilter(Color.BLUE);
            }
        }

        // Tiempo de estudio
        long tiempo = data.getTiempoEstudio() * 1000;
        String tiempoFormateado = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(tiempo),
                TimeUnit.MILLISECONDS.toMinutes(tiempo) % 60,
                TimeUnit.MILLISECONDS.toSeconds(tiempo) % 60);
        binding.txtTiempoEstudio.setText(tiempoFormateado);

        if (tiempo > 0) {
            long tiempoInicioMillis = System.currentTimeMillis() - tiempo;
            String horaInicio = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(tiempoInicioMillis));
            binding.txtInicioEstudio.setText("Iniciado: " + horaInicio);
        } else {
            binding.txtInicioEstudio.setText("Iniciado: --");
        }
    }

    private void saveSensorData(SharedViewModel.SensorData data) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Guardar temperatura si ha cambiado
        String currentTemp = String.valueOf(data.getTemperatura());
        if (!lastTemp.equals(currentTemp)) {
            lastTemp = currentTemp;
            sensorDB.addTemp(new TempEntity(data.getTemperatura(), date, time));
        }

        // Guardar humedad si ha cambiado
        String currentHum = String.valueOf(data.getHumedad());
        if (!lastHum.equals(currentHum)) {
            lastHum = currentHum;
            sensorDB.addHum(new HumEntity(data.getHumedad(), date, time));
        }

        // Guardar luz si ha cambiado
        String currentLight = String.valueOf(data.getLuz());
        if (!lastLight.equals(currentLight)) {
            lastLight = currentLight;
            sensorDB.addLight(new LightEntity(data.getLuz(), date, time));
        }

        // Guardar movimiento si ha cambiado
        String currentMov = data.isPresencia() ? "1" : "0";
        if (!lastMov.equals(currentMov)) {
            lastMov = currentMov;
            sensorDB.addMov(new MovEntity(currentMov, date, time));
        }
    }

    private void resetAllLeds() {
        binding.ledRojo.setColorFilter(Color.GRAY);
        binding.ledAmarillo.setColorFilter(Color.GRAY);
        binding.ledVerde.setColorFilter(Color.GRAY);
        binding.ledAzul.setColorFilter(Color.GRAY);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SensorsViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}