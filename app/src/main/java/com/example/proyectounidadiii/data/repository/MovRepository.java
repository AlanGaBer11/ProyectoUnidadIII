package com.example.proyectounidadiii.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.proyectounidadiii.data.db.AppDB;
import com.example.proyectounidadiii.data.db.dao.MovDao;
import com.example.proyectounidadiii.data.db.entities.MovEntity;

import java.util.List;
import java.util.concurrent.Executors;

public class MovRepository {

    private final MovDao movDao;

    public MovRepository(Application application) {
        AppDB db = AppDB.getInstance(application);
        movDao = db.movDao();
    }

    public void insert(MovEntity entity) {
        Executors.newSingleThreadExecutor().execute(() -> movDao.insert(entity));
    }

    public LiveData<List<MovEntity>> getAll() {
        return movDao.getAll();
    }
}
