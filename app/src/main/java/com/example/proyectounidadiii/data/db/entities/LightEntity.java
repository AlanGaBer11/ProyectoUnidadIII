package com.example.proyectounidadiii.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "luz")
public class LightEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int value;

    @NonNull
    public String date;

    @NonNull
    public String time;

    public LightEntity(int value, @NonNull String date, @NonNull String time) {
        this.value = value;
        this.date = date;
        this.time = time;
    }

}
