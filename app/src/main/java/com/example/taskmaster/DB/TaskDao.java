package com.example.taskmaster.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insertOne(Task task);

    @Query("SELECT * FROM task WHERE id like :id")
    Task findById(long id);

    @Query("SELECT * FROM Task")
    List<Task> findAll();

    @Delete
    void delete(Task task);
}
