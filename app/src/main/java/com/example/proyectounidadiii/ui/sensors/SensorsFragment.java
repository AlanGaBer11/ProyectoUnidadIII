package com.example.proyectounidadiii.ui.sensors;

import androidx.lifecycle.ViewModelProvider;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectounidadiii.R;
import com.example.proyectounidadiii.databinding.FragmentBluetoothBinding;
import com.example.proyectounidadiii.databinding.FragmentSensorsBinding;
import com.example.proyectounidadiii.viewmodels.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SensorsFragment extends Fragment {

    private SensorsViewModel mViewModel;
    private FragmentSensorsBinding binding;
    private SharedViewModel svm;
    private BluetoothAdapter btAdapter;

    public static SensorsFragment newInstance() {
        return new SensorsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSensorsBinding.inflate(inflater, container, false);

        // Se accede a la SharedViewModel para obtener los datos compartidos
        svm = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Se obtiene el adaptador Bluetooth (aunque aquí no se utiliza directamente)
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        // Observador del estado de la conexión Bluetooth
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

        // Observador de los datos del DHT (Temp y Hum)
        svm.getSensorData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                // Sistema activo
                binding.txtPower.setText("Sistema: " + (data.isSistemaActivo() ? "Encendido" : "Apagado"));
                binding.powerIcon.setImageResource(data.isSistemaActivo() ? R.drawable.power: R.drawable.power_off);
                binding.powerIcon.setColorFilter(data.isSistemaActivo() ? Color.GREEN : Color.RED);

                // Modo ahorro
                binding.txtMode.setText("Modo: " + (data.isModoAhorro() ? "Ahorro" : "Normal"));
                binding.modeIcon.setColorFilter(data.isModoAhorro() ? Color.GREEN : Color.GRAY);


                String estadoSistema;
                if (!data.isSistemaActivo()) {
                    estadoSistema = "Estado: STANDBY";
                } else if (data.isModoAhorro()) {
                    estadoSistema = "Estado: AHORRO ENERGIA";
                } else if (data.isPresencia()) {
                    // Determinar estado según condiciones ambientales
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

                // Temperatura
                binding.txtTemp.setText("Temperatura: " + data.getTemperatura() + " °C");

                // Humedad
                binding.txtHum.setText("Humedad: " + data.getHumedad() + " %");

                // Temperatura configurada
                binding.txtConfig.setText("Temp. config: " + data.getConfigTemp() + " °C");

                // Luz
                binding.txtLdr.setText("Intensidad: " + data.getLuz());

                // SOLUCIÓN 4: Corregir la barra de progreso de luz
                int luzValue = Math.min(data.getLuz(), 1000); // Asegurar que no exceda el máximo
                binding.lightProgress.setProgress(luzValue);

                // Movimiento
                binding.txtMov.setText(data.isPresencia() ? "Movimiento detectado" : "Sin movimiento detectado");

                // Cambiar icono y color según detección de movimiento
                if (data.isPresencia()) {
                    binding.motionIcon.setImageResource(R.drawable.power);
                    binding.motionIcon.setColorFilter(Color.GREEN);
                    binding.txtUltimoMovimiento.setText("Último movimiento: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
                } else {
                    binding.motionIcon.setImageResource(R.drawable.power_off);
                    binding.motionIcon.setColorFilter(Color.GRAY);
                }

                // SOLUCIÓN 3: LEDs - Resetear todos los LEDs a gris primero
                resetAllLeds();

                // Aplicar colores según la lógica exacta del ESP32
                if (!data.isSistemaActivo()) {
                    // Solo LED azul en STANDBY
                    binding.ledAzul.setColorFilter(Color.BLUE);
                } else if (data.isModoAhorro()) {
                    // LED azul parpadeando en modo ahorro (simulamos con color azul fijo)
                    binding.ledAzul.setColorFilter(Color.BLUE);
                } else {
                    // Sistema activo - aplicar lógica de condiciones ambientales
                    boolean tempOptima = (data.getTemperatura() >= 20.0 && data.getTemperatura() <= 24.0);
                    boolean humOptima = (data.getHumedad() >= 40.0 && data.getHumedad() <= 60.0);
                    boolean luzOptima = (data.getLuz() >= 300 && data.getLuz() <= 800);

                    if (tempOptima && humOptima && luzOptima) {
                        binding.ledVerde.setColorFilter(Color.GREEN); // Condiciones óptimas
                    } else if ((tempOptima || humOptima) && luzOptima) {
                        binding.ledAmarillo.setColorFilter(Color.YELLOW); // Condiciones aceptables
                    } else {
                        binding.ledRojo.setColorFilter(Color.RED); // Necesita ajustes
                    }

                    // LED azul adicional si la luz es muy baja (< 100 según ESP32)
                    if (data.getLuz() < 100) {
                        binding.ledAzul.setColorFilter(Color.BLUE);
                    }
                }

                // Tiempo de estudio
                long tiempo = data.getTiempoEstudio(); // valor recibido desde el ESP32
                tiempo = tiempo * 1000;

                String tiempoFormateado = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(tiempo),
                        TimeUnit.MILLISECONDS.toMinutes(tiempo) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(tiempo) % 60);

                binding.txtTiempoEstudio.setText(tiempoFormateado);

                // Solo calcular hora de inicio si hay tiempo de estudio
                if (tiempo > 0) {
                    long tiempoInicioMillis = System.currentTimeMillis() - tiempo;
                    String horaInicio = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(tiempoInicioMillis));
                    binding.txtInicioEstudio.setText("Iniciado: " + horaInicio);
                } else {
                    binding.txtInicioEstudio.setText("Iniciado: --");
                }
            }
        });
        return binding.getRoot();
    }

    // Método para resetear todos los LEDs a color gris
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
        // TODO: Use the ViewModel
    }
}