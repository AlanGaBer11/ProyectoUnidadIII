package com.example.proyectounidadiii.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.proyectounidadiii.data.db.AppDB;
import com.example.proyectounidadiii.data.db.dao.HumDao;
import com.example.proyectounidadiii.data.db.entities.HumEntity;

import java.util.List;
import java.util.concurrent.Executors;

public class HumRepository {


    private final HumDao humDao;

    public HumRepository(Application application) {
        AppDB db = AppDB.getInstance(application);
        humDao = db.humDao();
    }

    public void insert(HumEntity entity) {
        Executors.newSingleThreadExecutor().execute(() -> humDao.insert(entity));
    }

    public LiveData<List<HumEntity>> getAll() {
        return humDao.getAll();
    }
}
