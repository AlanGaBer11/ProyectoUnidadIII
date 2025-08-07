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

    // ===== RANGOS ÓPTIMOS - SINCRONIZADOS CON ESP32 =====
    private static final float TEMP_MIN = 18.0f;
    private static final float TEMP_MAX = 30.0f;
    private static final float HUM_MIN = 30.0f;
    private static final float HUM_MAX = 60.0f;
    private static final int LUZ_MIN = 50;
    private static final int LUZ_MAX = 900;

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

        // Verificar condiciones óptimas con rangos del ESP32
        boolean tempOptima = (data.getTemperatura() >= TEMP_MIN && data.getTemperatura() <= TEMP_MAX);
        boolean humOptima = (data.getHumedad() >= HUM_MIN && data.getHumedad() <= HUM_MAX);
        boolean luzOptima = (data.getLuz() >= LUZ_MIN && data.getLuz() <= LUZ_MAX);

        // Contar condiciones óptimas
        int condicionesOptimas = 0;
        if (tempOptima) condicionesOptimas++;
        if (humOptima) condicionesOptimas++;
        if (luzOptima) condicionesOptimas++;

        // Estado del sistema
        String estadoSistema;
        if (!data.isSistemaActivo()) {
            estadoSistema = "Estado: STANDBY";
        } else if (data.isModoAhorro()) {
            estadoSistema = "Estado: AHORRO ENERGIA";
        } else if (!data.isPresencia()) {
            estadoSistema = "Estado: ESPERANDO PRESENCIA";
        } else {
            // Sistema activo con presencia - usar misma lógica que ESP32
            if (condicionesOptimas == 3) {
                estadoSistema = "Estado: ÓPTIMO";
            } else if (condicionesOptimas == 2) {
                estadoSistema = "Estado: BUENO";
            } else if (condicionesOptimas == 1) {
                estadoSistema = "Estado: NECESITA MEJORAS";
            } else {
                estadoSistema = "Estado: CONDICIONES INADECUADAS";
            }
        }
        binding.txtEstadoSistema.setText(estadoSistema);

        // Datos de sensores con indicadores de estado
        binding.txtTemp.setText(String.format("Temperatura: %.1f °C %s",
                data.getTemperatura(),
                tempOptima ? "✓" : "⚠"));

        binding.txtHum.setText(String.format("Humedad: %.1f %% %s",
                data.getHumedad(),
                humOptima ? "✓" : "⚠"));

        binding.txtConfig.setText("Temp. config: " + data.getConfigTemp() + " °C");

        // Luz y ProgressBar - usar rangos del ESP32
        int luzValue = Math.min(data.getLuz(), 1000);
        binding.txtLdr.setText(String.format("Intensidad: %d lux %s",
                luzValue,
                luzOptima ? "✓" : "⚠"));
        binding.lightProgress.setProgress(luzValue);

        LayerDrawable progressBarDrawable = (LayerDrawable) binding.lightProgress.getProgressDrawable();
        Drawable progressDrawable = progressBarDrawable.getDrawable(1);

        // Actualizar color del ProgressBar según rangos del ESP32
        if (luzValue < LUZ_MIN || luzValue > LUZ_MAX) {
            progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            binding.luzStatus.setVisibility(View.VISIBLE);
        } else if (luzValue >= LUZ_MIN && luzValue <= (LUZ_MIN + LUZ_MAX) / 2) {
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

        // LEDs - SINCRONIZADO CON LÓGICA DEL ESP32
        resetAllLeds();
        if (!data.isSistemaActivo()) {
            // Modo standby
            binding.ledAzul.setColorFilter(Color.BLUE);
        } else if (data.isModoAhorro()) {
            // Modo ahorro - parpadeo simulado con azul
            binding.ledAzul.setColorFilter(Color.BLUE);
        } else {
            // Sistema activo - usar misma lógica que ESP32
            if (condicionesOptimas == 3) {
                binding.ledVerde.setColorFilter(Color.GREEN);    // Todas las condiciones óptimas
            } else if (condicionesOptimas == 2) {
                binding.ledAmarillo.setColorFilter(Color.YELLOW); // Dos condiciones óptimas
            } else {
                binding.ledRojo.setColorFilter(Color.RED);        // Una o ninguna condición óptima
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

        // Debug info (opcional - puedes comentar en producción)
        System.out.println(String.format("Android LEDs - T:%s H:%s L:%s -> Condiciones:%d",
                tempOptima ? "OK" : "NO",
                humOptima ? "OK" : "NO",
                luzOptima ? "OK" : "NO",
                condicionesOptimas));
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