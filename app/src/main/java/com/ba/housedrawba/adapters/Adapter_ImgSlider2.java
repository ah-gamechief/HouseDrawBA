package com.ba.housedrawba.adapters;
import static com.ba.housedrawba.adspackage.AppController.isInAppPurchased;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ba.housedrawba.models.SavedImagesModel;
import com.bumptech.glide.Glide;
import com.ba.housedrawba.R;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;
public class Adapter_ImgSlider2 extends RecyclerView.Adapter<Adapter_ImgSlider2.ViewHolder> {

    private final Activity context;
    private final List<SavedImagesModel> imageList;
    private final OnItemClickListener listener;
    private final boolean isDeleted = false;
    private boolean isShow;
    private int oldPosition=0;

    /*,,,,,,,,,,,,,,,,,,,,,,,,,,,,*/
    public interface OnItemClickListener {
        void onItemClick(SavedImagesModel item, int position, boolean isDeleted);
    }

    public Adapter_ImgSlider2(Activity context, List<SavedImagesModel> imageList, OnItemClickListener listener) {
        this.context = context;
        this.imageList = imageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_row_img_slider2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedImagesModel savedModel = imageList.get(position);
        holder.bind(savedModel, listener, position);
        if (savedModel!=null) {
            Glide.with(context.getApplicationContext()).load(savedModel.getImg()).into(holder.imageView);

            String fpath = savedModel.getImg().substring(savedModel.getImg().lastIndexOf("/") + 1);
            holder.tv_title.setText(fpath);

            if (oldPosition!=position){
                isShow=false;
            }

            if (!isShow){
                holder.rel_items.setVisibility(View.INVISIBLE);
            }

            holder.itemView.setOnClickListener(v -> {
                if (!isShow) {
                    isShow = true;
                    oldPosition = holder.getAdapterPosition();
                    holder.rel_items.setVisibility(View.VISIBLE);
                    holder.rel_items.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_anim));
                }else {
                    holder.rel_items.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out_anim));
                    holder.rel_items.setVisibility(View.INVISIBLE);
                    isShow = false;
                }
            });

            holder.shareBtn.setOnClickListener(v -> {
                Uri bitmapUri = Uri.parse(savedModel.getImg());
                String url = context.getResources().getString(R.string.app_url);
                String appTitle = context.getResources().getString(R.string.app_name);
                Intent intent = new Intent(Intent.ACTION_SEND);
                if (!isInAppPurchased) intent.putExtra(Intent.EXTRA_TEXT, url);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                context.startActivity(Intent.createChooser(intent, appTitle));
            });
        }
    }

    @Override
    public int getItemViewType(int position) { return position; }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        RelativeLayout rel_items;
        TextView tv_title,del_btn, shareBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_img_slider);
            tv_title = itemView.findViewById(R.id.tv_title_sq_iv);
            del_btn = itemView.findViewById(R.id.btn_delete);
            shareBtn = itemView.findViewById(R.id.btn_share);
            rel_items = itemView.findViewById(R.id.rel_items_saved);
        }
        public void bind(final SavedImagesModel savedModel, final OnItemClickListener listener,int position) {
            del_btn.setOnClickListener(v -> listener.onItemClick(savedModel, position,isDeleted));
        }
    }
}

