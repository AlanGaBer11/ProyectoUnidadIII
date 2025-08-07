package com.example.proyectounidadiii.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.proyectounidadiii.data.db.entities.HumEntity;

public interface HumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HumEntity entity);

    @Query("SELECT * FROM humedad ORDER BY id DESC")
    LiveData<List<HumEntity>> getAll();
}
