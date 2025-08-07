package com.example.proyectounidadiii.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.proyectounidadiii.data.db.entities.MovEntity;

import java.util.List;

public interface MovDao {

    @Insert( onConflict = OnConflictStrategy.IGNORE)

    void insert(MovEntity entity);

    @Query("SELECT * FROM movimiento ORDER BY id DESC")
    LiveData<List<MovEntity>> getAll();
}
