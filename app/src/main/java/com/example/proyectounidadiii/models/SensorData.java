package com.example.proyectounidadiii.models;

/**
 * Modelo de datos para almacenar información de todos los sensores del ESP32
 *
 * Formato esperado:
 * T:23.5|H:45.2|L:450|P:1|S:1|M:0|C:22|E:3600
 */
public class SensorData {

    // ===== DATOS DE SENSORES =====

    private float temperatura;
    private float humedad;
    private int luz;
    private boolean presencia;

    // ===== ESTADOS DEL SISTEMA =====

    private boolean sistemaActivo;
    private boolean modoAhorro;

    // ===== CONFIGURACIONES =====

    private int configTemp;
    private long tiempoEstudio;

    // ===== OTROS =====

    private long timestamp;

    // ===== RANGOS ÓPTIMOS =====

    public static final float TEMP_MIN_OPTIMA = 20.0f;
    public static final float TEMP_MAX_OPTIMA = 24.0f;
    public static final float HUM_MIN_OPTIMA = 40.0f;
    public static final float HUM_MAX_OPTIMA = 60.0f;
    public static final int LUZ_MIN_OPTIMA = 300;
    public static final int LUZ_MAX_OPTIMA = 800;

    // ===== CONSTRUCTORES =====

    public SensorData() {
        this.timestamp = System.currentTimeMillis();
    }

    public SensorData(float temperatura, float humedad, int luz, boolean presencia) {
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.luz = luz;
        this.presencia = presencia;
        this.timestamp = System.currentTimeMillis();
    }

    public SensorData(SensorData other) {
        this.temperatura = other.temperatura;
        this.humedad = other.humedad;
        this.luz = other.luz;
        this.presencia = other.presencia;
        this.sistemaActivo = other.sistemaActivo;
        this.modoAhorro = other.modoAhorro;
        this.configTemp = other.configTemp;
        this.tiempoEstudio = other.tiempoEstudio;
        this.timestamp = other.timestamp;
    }

    // ===== GETTERS Y SETTERS =====

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // ===== MÉTODOS DE UTILIDAD =====

    public String getEstadoAmbiental() {
        if (isCondicionesOptimas()) {
            return "ÓPTIMO";
        } else {
            return "NO ÓPTIMO";
        }
    }

    /**
     * Evalúa si las condiciones son óptimas según temperatura, humedad y luz
     */
    public boolean isCondicionesOptimas() {
        return temperatura >= TEMP_MIN_OPTIMA && temperatura <= TEMP_MAX_OPTIMA &&
                humedad >= HUM_MIN_OPTIMA && humedad <= HUM_MAX_OPTIMA &&
                luz >= LUZ_MIN_OPTIMA && luz <= LUZ_MAX_OPTIMA;
    }
}
