package hu.me.iit.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import hu.me.iit.todoapp.TodoAdapter;
import hu.me.iit.todoapp.TodoItem;
import hu.me.iit.todoappe.DatabaseHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private EditText taskEditText;
    private DatabaseHelper databaseHelper;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        taskEditText = findViewById(R.id.taskEditText);

        databaseHelper = new DatabaseHelper(this);

        currentUserEmail = getIntent().getStringExtra("userEmail");
        if (currentUserEmail == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new TodoAdapter(this, databaseHelper, currentUserEmail); // Initialize the adapter
        recyclerView.setAdapter(todoAdapter);

        loadTasks(); // Load tasks initially
    }

    private void loadTasks() {
        List<TodoItem> tasks = databaseHelper.getTasks(currentUserEmail);
        todoAdapter.setTasks(tasks); // Use the adapter's setTasks method
    }


    public void addTask(View view) {
        String task = taskEditText.getText().toString().trim();
        if (!task.isEmpty()) {
            TodoItem newItem = new TodoItem(task, false, currentUserEmail);
            long insertedId = databaseHelper.addTask(newItem); // Get the ID
            if (insertedId != -1) {
                newItem.id = (int) insertedId; // Set the ID on the new item
                taskEditText.setText("");
                loadTasks(); // Refresh the list
            } else {
                Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void logout(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}