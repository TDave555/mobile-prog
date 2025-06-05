package hu.me.iit.todoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hu.me.iit.todoapp.TodoItem;
import hu.me.iit.todoapp.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private Context context;
    private List<TodoItem> tasks;
    private DatabaseHelper databaseHelper;
    private String username;

    public TodoAdapter(Context context, DatabaseHelper databaseHelper, String username) {
        this.context = context;
        this.databaseHelper = databaseHelper;
        this.username = username;
        this.tasks = new ArrayList<>();
    }

    public void setTasks(List<TodoItem> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TodoAdapter.TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false); // Inflate your item layout
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoAdapter.TodoViewHolder holder, int position) {
        TodoItem task = tasks.get(position);
        holder.taskTextView.setText(task.task);
        holder.completedCheckBox.setChecked(task.completed);

        holder.completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.completed = isChecked;
            databaseHelper.updateTask(task);
        });

        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                int taskId = tasks.get(adapterPosition).id;
                databaseHelper.deleteTask(taskId);
                tasks.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
            }
        });

        if (holder instanceof TodoViewHolder) {
            TodoViewHolder todoViewHolder = (TodoViewHolder) holder;
            todoViewHolder.textViewTask.setText(task.task);
            todoViewHolder.checkBox.setChecked(task.completed);

            todoViewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) { // If the task is marked as completed
                    task.completed = true;
                    databaseHelper.updateTask(task);
                    notifyItemChanged(position); // Refresh the current item

                    // Remove the task from the incomplete list
                    tasks.remove(position);
                    notifyItemRemoved(position);

                    // Add the task to the completed list (assuming you have a separate list)
                    completedTasks.add(task);
                    notifyItemInserted(completedTasks.size() - 1);
                }
            });

            todoViewHolder.buttonDelete.setOnClickListener(v -> {
                // ... (your existing delete logic)
            });
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox emptyCheckBox;
        Button deleteButton;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTextView = itemView.findViewById(R.id.taskTextView); // Find views by ID
            completedCheckBox = itemView.findViewById(R.id.completedCheckBox);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}