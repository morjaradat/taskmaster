package com.example.taskmaster.DB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id ;

    @ColumnInfo(name = "task_title")
    private final String title;

    @ColumnInfo(name = "task_body")
    private final String body;

    @ColumnInfo(name = "task_state")
    private final String state;

    public Task(String title, String body, String state) {
        this.title = title;
        this.body = body;
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getState() {
        return state;
    }

    public void setId(long id) {
        this.id = id;
    }
}
