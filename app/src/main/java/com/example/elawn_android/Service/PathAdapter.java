package com.example.elawn_android.Service;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elawn_android.Database.DatabaseHelper;
import com.example.elawn_android.R;

import java.util.List;

public class PathAdapter extends RecyclerView.Adapter<PathAdapter.PathViewHolder> {

    private DatabaseHelper dbHelper;

    public interface OnItemClickListener {
        void onItemClicked(View v);
    }

    private OnItemClickListener listener;

    private List<Path> pathList;

    public static class PathViewHolder extends RecyclerView.ViewHolder {
        public TextView pathNameTV;
        public TextView pathAddressTV;
        public ImageButton deletePathButton;

        public PathViewHolder(View itemView){
            super(itemView);

            //textviews
            pathNameTV = itemView.findViewById(R.id.pathNameTV);
            pathAddressTV = itemView.findViewById(R.id.pathAddressTV);

        }

    }

    public PathAdapter(List<Path> mPathList) {
        pathList = mPathList;
    }

    @NonNull
    @Override
    public PathViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.path_item, parent, false);
        PathViewHolder pvh = new PathViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull PathViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Path currentItem = pathList.get(position);

        holder.pathNameTV.setText(currentItem.getPathName());
        holder.pathAddressTV.setText(currentItem.getPathAddress());

    }


    @Override
    public int getItemCount() {
        return pathList.size();
    }


}
