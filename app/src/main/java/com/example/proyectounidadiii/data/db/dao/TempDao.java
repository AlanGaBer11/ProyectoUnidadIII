package com.example.proyectounidadiii.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.proyectounidadiii.data.db.entities.TempEntity;

import java.util.List;

public interface TempDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TempEntity entity);

    @Query("SELECT * FROM temperatura ORDER BY id DESC")
    LiveData<List<TempEntity>> getAll();
}
