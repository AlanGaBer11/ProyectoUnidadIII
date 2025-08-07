package com.example.proyectounidadiii.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.proyectounidadiii.data.db.dao.HumDao;
import com.example.proyectounidadiii.data.db.dao.LightDao;
import com.example.proyectounidadiii.data.db.dao.MovDao;
import com.example.proyectounidadiii.data.db.dao.TempDao;
import com.example.proyectounidadiii.data.db.entities.HumEntity;
import com.example.proyectounidadiii.data.db.entities.LightEntity;
import com.example.proyectounidadiii.data.db.entities.MovEntity;
import com.example.proyectounidadiii.data.db.entities.TempEntity;

@Database(entities = {
        TempEntity.class, HumEntity.class, MovEntity.class,
        LightEntity.class
}, version = 1)

public abstract class AppDB extends RoomDatabase {

    private static volatile AppDB INSTANCE;

    public abstract TempDao tempDao();

    public abstract HumDao humDao();

    public abstract LightDao lightDao();

    public abstract MovDao movDao();


    public static AppDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDB.class, "sensor_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

