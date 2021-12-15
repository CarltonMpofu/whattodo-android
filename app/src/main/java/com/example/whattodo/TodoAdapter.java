package com.example.whattodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {



    public interface ItemClicked {
        void onItemClicked(int index);

        void onEditClicked(int index);

        void onDeleteClicked(int index);
    }

    ArrayList<Todo> myList;

    ItemClicked activity;


    public TodoAdapter(Context context, ArrayList<Todo> todoList){

        myList = todoList;
        activity = (ItemClicked)context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTodo;
        ImageView ivEdit, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTodo = itemView.findViewById(R.id.tvTodo);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onEditClicked(myList.indexOf(v.getTag()));
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onDeleteClicked(myList.indexOf(v.getTag()));
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClicked(myList.indexOf(v.getTag()));

//                    ivEdit.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @NonNull
    @Override
    public TodoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.row_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(myList.get(position));

        holder.ivEdit.setTag(myList.get(position));
        holder.ivDelete.setTag(myList.get(position));

        holder.tvTodo.setText(myList.get(position).getTodo());
//        holder.ivEdit.setVisibility(View.GONE);
//        holder.ivDelete.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }
}
