package com.example.taskmaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.amplifyframework.datastore.generated.model.Task;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {


    private final List<Task> taskItems;
    private final onTaskClickedListener listener;

    // constructor
    public Adapter(List<Task> taskItems, onTaskClickedListener listener) {
        this.taskItems = taskItems;
        this.listener = listener;
    }

    // InterFace for Events listener
    public interface onTaskClickedListener {
//        void addTaskToTheList();

        void onTaskClicked(int position);

        void onDeleteTask(int position);
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        Task taskItem = taskItems.get(position);
        holder.taskTitle.setText(taskItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return taskItems.size();
    }

    // The  View Adapter
    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView taskTitle;
        //        private TextView taskBody;
//        private TextView taskStatus;

        public ViewHolder(@NonNull View itemView, onTaskClickedListener listener) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            TextView delete = itemView.findViewById(R.id.delete);

//             taskBody = itemView.findViewById(R.id.task_body);
//            taskStatus = itemView.findViewById(R.id.task_status);

            itemView.setOnClickListener(v -> listener.onTaskClicked(getBindingAdapterPosition()));

            delete.setOnClickListener(v -> listener.onDeleteTask(getBindingAdapterPosition()));
        }
    }
}
