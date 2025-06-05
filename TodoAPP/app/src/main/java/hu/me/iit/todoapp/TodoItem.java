package hu.me.iit.todoapp;

public class TodoItem {
    public int id;
    public String task;
    public boolean completed;
    public String username; // To associate tasks with users

    public TodoItem(String task, boolean completed, String username) {
        this.task = task;
        this.completed = completed;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }
}