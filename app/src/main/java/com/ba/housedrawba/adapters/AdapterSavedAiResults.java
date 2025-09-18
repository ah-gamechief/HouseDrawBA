package com.ba.housedrawba.adapters;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.housedrawba.Utils.CacheUtils;
import com.ba.housedrawba.databinding.RvRowAiDesignsBinding;
import com.ba.housedrawba.interfaces.OnRvItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;
public class AdapterSavedAiResults extends RecyclerView.Adapter<AdapterSavedAiResults.ViewHolder> {
    private final List<CacheUtils.BitmapPair> dataList;
    private final OnRvItemClickListener selectedPosition;

    public AdapterSavedAiResults(List<CacheUtils.BitmapPair> dataList, OnRvItemClickListener selectedPosition) {
        this.dataList = dataList;
        this.selectedPosition = selectedPosition;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RvRowAiDesignsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (dataList.get(position)!=null) {
            Bitmap before = dataList.get(position).withoutWatermark;
            Bitmap after = dataList.get(position).withWatermark;

            Glide.with(holder.binding.ivIcon).asBitmap().load(after)
                    .override(256).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.binding.ivIcon.setImageBitmap(resource);
                        }@Override public void onLoadCleared(@Nullable Drawable placeholder) {}});

            holder.binding.mcvItem.setOnClickListener(v -> selectedPosition.onItemClicked(holder.getAdapterPosition()));
        }
    }

    @Override public int getItemCount() {
        return dataList.size();
    }

    @Override public long getItemId(int position) {
        return position;
    }
    @Override public int getItemViewType(int position) {
        return position;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder{
        RvRowAiDesignsBinding binding;
        public ViewHolder(@NonNull RvRowAiDesignsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
