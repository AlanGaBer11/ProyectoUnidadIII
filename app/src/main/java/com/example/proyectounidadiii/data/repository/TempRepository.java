package com.example.proyectounidadiii.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.proyectounidadiii.data.db.AppDB;
import com.example.proyectounidadiii.data.db.dao.TempDao;
import com.example.proyectounidadiii.data.db.entities.TempEntity;

import java.util.List;
import java.util.concurrent.Executors;

public class TempRepository {

    private final TempDao tempDao;

    public TempRepository (Application application){
        AppDB db = AppDB.getInstance(application);
        tempDao = db.tempDao();
    }

    public void insert(TempEntity entity) {
        Executors.newSingleThreadExecutor().execute(() -> tempDao.insert(entity));
    }

    public LiveData<List<TempEntity>> getAll() {
        return tempDao.getAll();
    }
}
