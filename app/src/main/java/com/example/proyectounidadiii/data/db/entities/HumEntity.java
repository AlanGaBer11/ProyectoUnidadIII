package com.example.proyectounidadiii.data.db.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "humedad")
public class HumEntity {

    @PrimaryKey(autoGenerate = true)

    public int id;

    public float value;

    @NonNull
    public String date;
    @NonNull
    public String time;

    public HumEntity(float value, @NonNull String date, @NonNull String time) {
        this.value = value;
        this.date = date;
        this.time = time;
    }
}
