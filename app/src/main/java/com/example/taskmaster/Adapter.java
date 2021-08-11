package com.example.taskmaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {


    private final List<Task> taskItems;
    private onTaskClickedListener listener;

    // constructor
    public Adapter(List<Task> taskItems, onTaskClickedListener listener) {
        this.taskItems = taskItems;
        this.listener = listener;
    }

    // InterFace for Events listener
    public interface onTaskClickedListener{
        void addTaskToTheList();
        void onTaskClicked(int position);
        void onDeleteTask(int position);
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        return new ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull  Adapter.ViewHolder holder, int position) {
        Task taskItem = taskItems.get(position);
        holder.taskTitle.setText(taskItem.getTitle());
//        holder.taskBody.setText(taskItem.getBody());
//        holder.taskStatus.setText(taskItem.getState());

    }

    @Override
    public int getItemCount() {
        return taskItems.size();
    }

    // The  View Adapter
    static  class  ViewHolder extends RecyclerView.ViewHolder{

        private TextView taskTitle;
//        private TextView taskBody;
//        private TextView taskStatus;

        public ViewHolder(@NonNull  View itemView , onTaskClickedListener listener) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
//             taskBody = itemView.findViewById(R.id.task_body);
//            taskStatus = itemView.findViewById(R.id.task_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTaskClicked(getAdapterPosition());
                }
            });
        }
    }
}