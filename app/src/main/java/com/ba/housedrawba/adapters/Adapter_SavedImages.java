package com.ba.housedrawba.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.ba.housedrawba.R;
import com.ba.housedrawba.activities.MyCreationsActivity;
import com.ba.housedrawba.models.SavedImagesModel;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class Adapter_SavedImages extends RecyclerView.Adapter<Adapter_SavedImages.ViewHolder> {

    private final Activity context;
    private final List<SavedImagesModel> imageList;
    RelativeLayout layout_del;
    Button cancelBtn, deleteBtn;
    CheckBox checkBox;
    boolean isChecked = false;
    int countChecked = 0;

    public Adapter_SavedImages(Activity context, List<SavedImagesModel> imageList,
                               RelativeLayout layout_del, Button cancelBtn, Button deleteBtn, CheckBox checkBox) {
        this.context = context;
        this.imageList = imageList;
        this.layout_del = layout_del;
        this.cancelBtn = cancelBtn;
        this.deleteBtn = deleteBtn;
        this.checkBox = checkBox;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_saved_images,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedImagesModel savedModel = this.imageList.get(position);
        if (savedModel!=null) {
            if (isChecked){
                holder.checkBox.setVisibility(View.VISIBLE);
            }else {
                holder.checkBox.setVisibility(View.GONE);
            }
            holder.checkBox.setChecked(savedModel.isChecked());

            if (checkBox!=null){
                checkBox.setOnClickListener(v -> {
                    if (checkBox.isChecked()){
                        for (int j=0; j<imageList.size(); j++){
                            imageList.get(j).setChecked(true);
//                    System.out.println("hhhhhhhhhhhhhki992--="+savedModel.isChecked());
                        }
                        countChecked = imageList.size();
                        checkBox.setText("Selected "+countChecked);
                    }else{
                        for (int j=0; j<imageList.size(); j++){
                            imageList.get(j).setChecked(false);
//                    System.out.println("hhhhhhhhhhhhhki993--="+savedModel.isChecked());
                        }
                        countChecked = 0;
                        checkBox.setText("Select All");
                    }
                    notifyDataSetChanged();
                });
                if (countChecked==0){
                    checkBox.setText("Select All");
                }
            }


            holder.checkBox.setOnClickListener(v -> {
                if (holder.checkBox.isChecked()) {
                    countChecked = countChecked+1;
                    if (countChecked==imageList.size()){
                        if (checkBox!=null) checkBox.setChecked(true);
                    }
                    savedModel.setChecked(true);
                }else{
                    countChecked = countChecked-1;
                    savedModel.setChecked(false);
                    if (checkBox!=null) checkBox.setChecked(false);
                }
                if (checkBox!=null) checkBox.setText("Selected "+countChecked);
            });

            Glide.with(context).load(savedModel.getImg()).into(holder.imageView);
            // Dialog
            holder.imageView.setOnClickListener(v -> {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_layout_saved_images_slider);
                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.black);

                ImageView closeBtn = dialog.findViewById(R.id.close_btn_sq_iv);
                funSlider(context, dialog,imageList,position);

                closeBtn.setOnClickListener(v1 -> dialog.dismiss());

                dialog.show();

            });

            holder.imageView.setOnLongClickListener(v -> {
                isChecked = true;
                countChecked = 0;
                countChecked = countChecked+1;
                imageList.get(position).setChecked(true);
                MyCreationsActivity savedQuotes = new MyCreationsActivity();
                savedQuotes.showDelLayout(context);
                notifyDataSetChanged();
                if (checkBox!=null) checkBox.setText("Selected "+countChecked);
                return true;
            });

            if (cancelBtn!=null) {
                cancelBtn.setOnClickListener(v -> {
                    countChecked=0;
                    layout_del.setVisibility(View.GONE);
                    isChecked = false;
                    for (int j=0; j<imageList.size(); j++){
                        imageList.get(j).setChecked(false);
                    }
                    if (checkBox!=null) {
                        checkBox.setChecked(false);
                        checkBox.setText("Select All");
                        notifyDataSetChanged();
                    }
                });
            }

            if (deleteBtn!=null){
                deleteBtn.setOnClickListener(v -> {
                    List<SavedImagesModel> removeList = new ArrayList<>();
                    for (int h=0; h<imageList.size(); h++){
                        if (imageList.get(h).isChecked()){
                            removeList.add(imageList.get(h));
                        }
                    }
                    removeImages(removeList);
                });
            }
        }
    }

    private void removeImages(List<SavedImagesModel> removeList) {
        for (int j=0; j<removeList.size(); j++) {
            SavedImagesModel savedModel = removeList.get(j);
            String imgPath = savedModel.getImg();
            File file = new File(imgPath);
            if (file.exists() && file.getPath().equals(imgPath)) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                callBroadCast(imgPath);
                removeAt1(removeList.get(j),j);
            }
        }
        isChecked = false;
        layout_del.setVisibility(View.GONE);
    }

    private void funSlider(Activity activity,Dialog dialog, List<SavedImagesModel> savedModels,int position){
        ViewPager2 myViewPager2 = dialog.findViewById(R.id.viewpager_dialog_saved);
        Adapter_ImgSlider2 adapterSlider = new Adapter_ImgSlider2(activity, savedModels,
                (savedModel, position1, isDeleted) -> {
                    System.out.println("gggggggghq6679--=" + isDeleted + "==" + position1);
                    File fdelete = new File(savedModel.getImg());
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            Log.e("-->", "file Deleted :" + savedModel.getImg());
                            callBroadCast(savedModel.getImg());
                            showToast("Deleted");
                            dialog.dismiss();
                            removeAt(position1);
                        } else {
                            Log.e("-->", "file not Deleted :" + savedModel.getImg());
                            showToast("Error");
                        }
                    }
                });
        myViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        myViewPager2.setAdapter(adapterSlider);
        myViewPager2.setOffscreenPageLimit(1);
        myViewPager2.clearAnimation();
        myViewPager2.setCurrentItem(position, false);
//        myViewPager2.setElevation(5f);
//        myViewPager2.setClipToPadding(true);

    }

    public void transformPage(View view, float position) {
        view.setTranslationX(view.getWidth() * -position);
        if(position <= -1.0F || position >= 1.0F) {
            view.setAlpha(0.0F);
        } else if( position == 0.0F ) {
            view.setAlpha(1.0F);
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            view.setAlpha(1.0F - Math.abs(position));
        }
    }


    public void removeAt(int position) {
        imageList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,imageList.size());
        if (imageList.size()==0){
            MyCreationsActivity fragment = new MyCreationsActivity();
            fragment.isListEmpty(context);
        }
    }

    public void removeAt1(SavedImagesModel savedModel, int index) {
        imageList.remove(savedModel);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index,imageList.size());
        if (imageList.size()==0){
            MyCreationsActivity fragment = new MyCreationsActivity();
            fragment.isListEmpty(context);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public void callBroadCast(String imagePath) {
        Log.e("-->", " >= 14");
        MediaScannerConnection.scanFile(context, new String[]{imagePath}, null, (path, uri) -> {
            Log.i("ExternalStorage", "Scanned " + path + ":");
            Log.i("ExternalStorage", "-> uri=" + uri);
        });
    }
    public void showToast(String msg){
        Toast toast =  Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        new Handler().postDelayed(() -> {
            if (toast!=null)
                toast.cancel();

        },1500);
        if (toast!=null)
            toast.show();
    }

    public Activity getContext() {
        return context;
    }
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_row_saved_quotes);
            checkBox = itemView.findViewById(R.id.checkbox_row_saved);
        }
    }

}

