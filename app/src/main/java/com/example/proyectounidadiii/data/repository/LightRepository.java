package com.example.proyectounidadiii.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.proyectounidadiii.data.db.AppDB;
import com.example.proyectounidadiii.data.db.dao.LightDao;
import com.example.proyectounidadiii.data.db.entities.LightEntity;

import java.util.List;
import java.util.concurrent.Executors;

public class LightRepository {

    private final LightDao lightDao;

    public LightRepository(Application application) {
        AppDB db = AppDB.getInstance(application);
        lightDao = db.lightDao();
    }

    public void insert(LightEntity entity) {
        Executors.newSingleThreadExecutor().execute(() -> lightDao.insert(entity));
    }

    public LiveData<List<LightEntity>> getAll() {
        return lightDao.getAll();
    }
}
