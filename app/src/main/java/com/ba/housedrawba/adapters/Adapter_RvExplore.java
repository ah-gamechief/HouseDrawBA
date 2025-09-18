package com.ba.housedrawba.adapters;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.housedrawba.R;
import com.ba.housedrawba.databinding.RvRowExploreBtmBinding;
import com.ba.housedrawba.interfaces.OnRvItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import java.util.List;
public class Adapter_RvExplore extends RecyclerView.Adapter<Adapter_RvExplore.ViewHolder> {
    private final List<String> pathsData;
    private final OnRvItemClickListener selectedPosition;
    public int selected=-1;
    public boolean isFromClick = false;
    /*,,,,,,,,,,,,,,,,,,,,,,,,,,,,*/

    public Adapter_RvExplore(List<String> pathsData, OnRvItemClickListener selectedPosition) {
        this.pathsData = pathsData;
        this.selectedPosition = selectedPosition;
    }

    public void setSelected(int selected,boolean value) {
        isFromClick = value;
        if (isFromClick) {
            this.selected = selected;
            notifyDataSetChanged();
        }
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RvRowExploreBtmBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = pathsData.get(position);
        if (path!=null) {
            Glide.with(holder.binding.ivIcon).asBitmap().load(path)
                    .override(256).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.binding.ivIcon.setImageBitmap(resource);
                    }@Override public void onLoadCleared(@Nullable Drawable placeholder) {}});
            if (selected == position) {
                holder.binding.mcvItem.setStrokeWidth(7);
                holder.binding.mcvItem.setStrokeColor(
                        holder.binding.mcvItem.getContext().getResources().getColor(R.color.primaryColor));
            } else {
                holder.binding.mcvItem.setStrokeWidth(0);
            }

            holder.binding.mcvItem.setOnClickListener(v ->
                    selectedPosition.onItemClicked(holder.getAdapterPosition()));
        }
    }

    @Override public int getItemCount() {
        return pathsData.size();
    }

    @Override public long getItemId(int position) {
        return position;
    }
    @Override public int getItemViewType(int position) {
        return position;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        RvRowExploreBtmBinding binding;
        public ViewHolder(@NonNull RvRowExploreBtmBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
