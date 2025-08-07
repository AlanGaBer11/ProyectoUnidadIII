package com.example.proyectounidadiii.data.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movimiento")
public class MovEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String value;

    @NonNull
    public String date;

    @NonNull
    public String time;

    public MovEntity(String value, @NonNull String date, @NonNull String time) {
        this.value = value;
        this.date = date;
        this.time = time;
    }
}
