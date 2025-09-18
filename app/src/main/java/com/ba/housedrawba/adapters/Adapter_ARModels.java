package com.ba.housedrawba.adapters;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.housedrawba.databinding.RvRowExploreArBinding;
import com.ba.housedrawba.interfaces.OnRvItemClickListener;
import com.bumptech.glide.Glide;
public class Adapter_ARModels extends RecyclerView.Adapter<Adapter_ARModels.ViewHolder> {
    private final int[] dataPath;
    private final OnRvItemClickListener listener;

    public Adapter_ARModels(int[] dataList, OnRvItemClickListener listener) {
        this.dataPath = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RvRowExploreArBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int data = this.dataPath[position];
        Glide.with(holder.itemView.getContext()).asBitmap().load(data).into(holder.bindingRowItem.ivIcon);
        holder.bindingRowItem.mcvItem.setOnClickListener(v -> listener.onItemClicked(holder.getAdapterPosition()));
    }

    @Override public long getItemId(int position) {
        return position;
    }
    @Override public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataPath.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RvRowExploreArBinding bindingRowItem;
        public ViewHolder(@NonNull RvRowExploreArBinding itemView) {
            super(itemView.getRoot());
            bindingRowItem = itemView;
        }
    }
}
