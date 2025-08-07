package com.example.proyectounidadiii.viewmodels;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class SharedViewModel extends ViewModel {

    // Bluetooth
    private BluetoothSocket socket;
    private InputStream in;
    private OutputStream out;
    private Thread rxThread;
    private BluetoothDevice dispositivoGuardado;

    // LiveData para estado y datos
    private final MutableLiveData<String> estado = new MutableLiveData<>("Desconectado");
    private final MutableLiveData<SensorData> sensorData = new MutableLiveData<>(new SensorData());

    public SharedViewModel() {
    }

    // Método para conectar (igual)
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT})
    public void conectar(BluetoothDevice device) {
        if (rxThread != null) rxThread.interrupt();
        estado.postValue("Conectando...");
        dispositivoGuardado = device;

        rxThread = new Thread(() -> {
            try {
                if (socket != null && socket.isConnected()) socket.close();

                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                socket.connect();

                if (socket.isConnected()) {
                    in = socket.getInputStream();
                    out = socket.getOutputStream();
                    estado.postValue("Conectado a " + device.getName());
                    leerDatos();
                } else {
                    estado.postValue("Error de conexión");
                }
            } catch (IOException e) {
                e.printStackTrace();
                estado.postValue("Error de conexión");
            }
        });
        rxThread.start();
    }

    private void leerDatos() {
        byte[] buffer = new byte[1024];
        StringBuilder sb = new StringBuilder();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                int n = in.read(buffer);
                if (n > 0) {
                    sb.append(new String(buffer, 0, n));
                    int idx;
                    while ((idx = sb.indexOf("#")) != -1) {
                        String trama = sb.substring(0, idx);
                        parseSensorData(trama);
                        sb.delete(0, idx + 1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                estado.postValue("Desconectado");
                break;
            }
        }
    }

    private void parseSensorData(String rawData) {
        if (rawData.startsWith("OK:") || rawData.startsWith("ERROR:")) {
            Log.d("SharedViewModel", "Respuesta del ESP32: " + rawData);
            return;
        }

        String[] dataPairs = rawData.split("\\|");
        SensorData data = new SensorData();

        for (String pair : dataPairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];

                try {
                    switch (key) {
                        case "T":
                            data.setTemperatura(Float.parseFloat(value));
                            break;
                        case "H":
                            data.setHumedad(Float.parseFloat(value));
                            break;
                        case "L":
                            data.setLuz(Integer.parseInt(value));
                            break;
                        case "P":
                            data.setPresencia(Integer.parseInt(value) == 1);
                            break;
                        case "S":
                            data.setSistemaActivo(Integer.parseInt(value) == 1);
                            break;
                        case "M":
                            data.setModoAhorro(Integer.parseInt(value) == 1);
                            break;
                        case "C":
                            data.setConfigTemp(Integer.parseInt(value));
                            break;
                        case "E":
                            data.setTiempoEstudio(Long.parseLong(value));
                            break;
                        default:
                            Log.w("SharedViewModel", "Clave desconocida: " + key);
                    }
                } catch (NumberFormatException e) {
                    Log.e("SharedViewModel", "Error parseando valor: " + key + "=" + value, e);
                }
            }
        }
        sensorData.postValue(data);
    }

    public LiveData<SensorData> getSensorData() {
        return sensorData;
    }

    public LiveData<String> getEstado() {
        return estado;
    }

    public void desconectar() {
        estado.postValue("Desconectado");
        if (rxThread != null) rxThread.interrupt();
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        }
    }

    // Getter/setter Bluetooth si los necesitas
    public BluetoothSocket getSocket() {
        return socket;
    }

    public OutputStream getOutputStream() {
        return out;
    }

    public BluetoothDevice getDispositivoGuardado() {
        return dispositivoGuardado;
    }

    public void setDispositivoGuardado(BluetoothDevice dispositivo) {
        this.dispositivoGuardado = dispositivo;
    }

    // Clase interna para contener datos
    public static class SensorData {
        private float temperatura;
        private float humedad;
        private int luz;
        private boolean presencia;
        private boolean sistemaActivo;
        private boolean modoAhorro;
        private int configTemp;
        private long tiempoEstudio;

        // Getters y setters
        public float getTemperatura() {
            return temperatura;
        }

        public void setTemperatura(float temperatura) {
            this.temperatura = temperatura;
        }

        public float getHumedad() {
            return humedad;
        }

        public void setHumedad(float humedad) {
            this.humedad = humedad;
        }

        public int getLuz() {
            return luz;
        }

        public void setLuz(int luz) {
            this.luz = luz;
        }

        public boolean isPresencia() {
            return presencia;
        }

        public void setPresencia(boolean presencia) {
            this.presencia = presencia;
        }

        public boolean isSistemaActivo() {
            return sistemaActivo;
        }

        public void setSistemaActivo(boolean sistemaActivo) {
            this.sistemaActivo = sistemaActivo;
        }

        public boolean isModoAhorro() {
            return modoAhorro;
        }

        public void setModoAhorro(boolean modoAhorro) {
            this.modoAhorro = modoAhorro;
        }

        public int getConfigTemp() {
            return configTemp;
        }

        public void setConfigTemp(int configTemp) {
            this.configTemp = configTemp;
        }

        public long getTiempoEstudio() {
            return tiempoEstudio;
        }

        public void setTiempoEstudio(long tiempoEstudio) {
            this.tiempoEstudio = tiempoEstudio;
        }
    }
}
