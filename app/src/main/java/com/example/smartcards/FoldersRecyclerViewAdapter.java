package com.example.smartcards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FoldersRecyclerViewAdapter extends RecyclerView.Adapter<FoldersRecyclerViewAdapter.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private ArrayList<Folder> folders;

    public FoldersRecyclerViewAdapter(Context context, ArrayList<Folder> folders, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.folders = folders;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public FoldersRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row_folder, parent, false);

        return new FoldersRecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull FoldersRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.button.setText(folders.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        Button button;
        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            button = itemView.findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null) {
                        int position = getAdapterPosition();

                        if(position != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
