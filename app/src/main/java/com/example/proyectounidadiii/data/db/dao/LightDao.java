package com.example.proyectounidadiii.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.proyectounidadiii.data.db.entities.LightEntity;

import java.util.List;

public interface LightDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(LightEntity entity);

    @Query("SELECT * FROM luz ORDER BY id DESC")
    LiveData<List<LightEntity>> getAll();
}
