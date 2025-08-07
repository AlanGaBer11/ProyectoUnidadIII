package com.example.proyectounidadiii.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.proyectounidadiii.data.db.entities.HumEntity;
import com.example.proyectounidadiii.data.db.entities.LightEntity;
import com.example.proyectounidadiii.data.db.entities.MovEntity;
import com.example.proyectounidadiii.data.db.entities.TempEntity;
import com.example.proyectounidadiii.data.repository.HumRepository;
import com.example.proyectounidadiii.data.repository.LightRepository;
import com.example.proyectounidadiii.data.repository.MovRepository;
import com.example.proyectounidadiii.data.repository.TempRepository;

import java.util.List;

public class SensorDBViewModel extends AndroidViewModel {

    // Repositorios por cada tipo
    private final TempRepository tempRepository;
    private final HumRepository humRepository;

    private final LightRepository lightRepository;

    private final MovRepository movRepository;

    public SensorDBViewModel(@NonNull Application application) {
        super(application);
        tempRepository = new TempRepository(application);
        humRepository = new HumRepository(application);
        lightRepository = new LightRepository(application);
        movRepository = new MovRepository(application);
    }

    // Temperatura
    public void addTemp(TempEntity data) {
        tempRepository.insert(data);
    }

    public LiveData<List<TempEntity>> getAllTemp() {
        return tempRepository.getAll();
    }

    // Humedad
    public void addHum(HumEntity data) {
        humRepository.insert(data);
    }

    public LiveData<List<HumEntity>> getAllHum() {
        return humRepository.getAll();
    }

    // Luz
    public void addLight(LightEntity data) {
        lightRepository.insert(data);
    }

    public LiveData<List<LightEntity>> getAllLight() {
        return lightRepository.getAll();
    }

    //Movimiento
    public void addMov(MovEntity data) {
        movRepository.insert(data);
    }

    public LiveData<List<MovEntity>> getAllMov() {
        return movRepository.getAll();
    }
}
