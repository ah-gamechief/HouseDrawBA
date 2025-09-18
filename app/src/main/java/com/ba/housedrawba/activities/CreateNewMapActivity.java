package com.ba.housedrawba.activities;

import static com.ba.housedrawba.adspackage.AppController.adClickCounter;
import static com.ba.housedrawba.adspackage.AppController.adsShowInterval;
import static com.ba.housedrawba.adspackage.AppController.createAndGetAdaptiveAd;
import static com.ba.housedrawba.adspackage.AppController.showInterstitialAdWithLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;

import com.ba.housedrawba.BaseActivity;
import com.ba.housedrawba.R;
import com.ba.housedrawba.Utils.BitmapUtil;
import com.ba.housedrawba.Utils.MyEditor.DrawingBoard;
import com.ba.housedrawba.Utils.MyEditor.DrawingBoardManager;
import com.ba.housedrawba.Utils.MyEditor.OnDrawItemSelectedListener;
import com.ba.housedrawba.Utils.MyEditor.board.LayerManager;
import com.ba.housedrawba.Utils.MyEditor.element.PhotoElement;
import com.ba.housedrawba.Utils.MyEditor.element.shape.LineElement;
import com.ba.housedrawba.Utils.MyEditor.element.shape.RectElement;
import com.ba.housedrawba.Utils.MyEditor.mode.DrawingMode;
import com.ba.housedrawba.Utils.MyEditor.mode.InsertPhotoMode;
import com.ba.housedrawba.Utils.MyEditor.mode.InsertShapeMode;
import com.ba.housedrawba.Utils.MyEditor.mode.PointerMode;
import com.ba.housedrawba.Utils.MyEditor.mode.eraser.ObjectEraserMode;
import com.ba.housedrawba.Utils.MyEditor.operation.ArrangeOperation;
import com.ba.housedrawba.databinding.ActivityCreateNewMapBinding;
public class CreateNewMapActivity extends BaseActivity implements View.OnClickListener, OnDrawItemSelectedListener {
    ActivityCreateNewMapBinding binding;
    Activity activity = this;
    Context context = this;
    DrawingBoard drawingBoard;

    PointerMode pointerMode;
    InsertShapeMode insertShapeMode;
    ArrangeOperation arrangeOperation;
    DrawingMode deleteMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNewMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createAndGetAdaptiveAd(activity,binding.adViewContainer);

        if (adClickCounter>0 && adClickCounter%adsShowInterval==0)
            showInterstitialAdWithLoader(activity,null);

        initViews();
    }

    private void initViews(){
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        binding.button1.setOnClickListener(this);
        binding.button2.setOnClickListener(this);
        binding.button3.setOnClickListener(this);
        binding.button4.setOnClickListener(this);
        binding.button5.setOnClickListener(this);
        binding.button6.setOnClickListener(this);

        binding.undoBtn.setOnClickListener(this);
        binding.redoBtn.setOnClickListener(this);
        binding.saveBtn.setOnClickListener(this);
        binding.fabErase.setOnClickListener(this);
        binding.fabSendBack.setOnClickListener(this);
        binding.fabSendFront.setOnClickListener(this);
        binding.doneDrawBtn.setOnClickListener(this);
        binding.expandBtn.setOnClickListener(this);

        drawingBoard = DrawingBoardManager.getInstance().createDrawingBoard();
        setupDrawingBoard();
        drawingBoard.getElementManager().createNewLayer();
        drawingBoard.getElementManager().selectFirstVisibleLayer();
        pointerMode = new PointerMode();
        pointerMode.setClickCallbackInterface(this);
        insertShapeMode = new InsertShapeMode();
        arrangeOperation = new ArrangeOperation();
        deleteMode = new ObjectEraserMode();
    }

    private void setupDrawingBoard() {
        drawingBoard.setupDrawingView(binding.drawingView);
        drawingBoard.getDrawingContext().getPaint().setColor(Color.BLACK);
        drawingBoard.getDrawingContext().getPaint().setStrokeWidth(10);
        drawingBoard.getDrawingContext().setDrawingMode(new PointerMode());

        /*layerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                drawingBoard.getDrawingView().notifyViewUpdated();
            }
        });
        layerAdapter.setOnItemClick(new LayerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Layer layer) {
                drawingBoard.getElementManager().selectLayer(layer);
                layerAdapter.notifyDataSetChanged();
            }
        });*/
        drawingBoard.getElementManager().addLayerChangeListener(new LayerManager.LayerChangeListener() {
            @Override
            public void onLayerChanged() {
//                layerAdapter.setLayers(Arrays.asList(drawingBoard.getElementManager().getLayers()));
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button1){
            drawingBoard.getDrawingContext().setDrawingMode(insertShapeMode);
            insertShapeMode.setShapeType(LineElement.class);
            viewSlideXDirection(false,binding.saveBtn,false,true);
            buttonSelector(1);

            binding.fabMenu.collapse();
            viewSlideXDirection(false,binding.fabMenu,true,true);

        }
        else if (id == R.id.button2){
            buttonSelector(2);
            drawingBoard.getDrawingContext().setDrawingMode(insertShapeMode);
            insertShapeMode.setShapeType(RectElement.class);
            viewSlideXDirection(false,binding.saveBtn,false,true);
            binding.fabMenu.collapse();
            viewSlideXDirection(false,binding.fabMenu,true,true);
        }else if (id == R.id.button3){
            buttonSelector(3);
            insertPhoto(R.drawable.bed_);
            viewFadinAnim(false,binding.doneDrawBtn);
            viewSlideXDirection(true,binding.fabMenu,true,true);
            binding.fabMenu.collapse();
        }else if (id == R.id.button4){
            insertPhoto(R.drawable.sofa_);
            buttonSelector(4);
            viewFadinAnim(false,binding.doneDrawBtn);
            viewSlideXDirection(true,binding.fabMenu,true,true);
            binding.fabMenu.collapse();
        }else if (id == R.id.button5){
            buttonSelector(5);
            insertPhoto(R.drawable.kitchen_);
            viewFadinAnim(false,binding.doneDrawBtn);
            viewSlideXDirection(true,binding.fabMenu,true,true);
            binding.fabMenu.collapse();
        }else if (id == R.id.button6){
            buttonSelector(6);
            insertPhoto(R.drawable.bath_);
            viewFadinAnim(false,binding.doneDrawBtn);
            viewSlideXDirection(true,binding.fabMenu,true,true);
            binding.fabMenu.collapse();
        }else if (id == R.id.undoBtn){
            if (drawingBoard!=null) drawingBoard.getOperationManager().undo();
            binding.fabMenu.collapse();
        }else if (id == R.id.redoBtn){
            if (drawingBoard!=null) drawingBoard.getOperationManager().redo();
            binding.fabMenu.collapse();
        }
        else if (id == R.id.doneDrawBtn){
            viewFadinAnim(false,binding.doneDrawBtn);
            binding.fabMenu.collapse();
            if (pointerMode!=null && drawingBoard!=null)
                drawingBoard.getDrawingContext().setDrawingMode(pointerMode);
        }
        else if (id == R.id.fabSendBack){
            arrangeOperation.setArrangeType(ArrangeOperation.ArrangeType.SendToBack);
            drawingBoard.getOperationManager().executeOperation(arrangeOperation);
            binding.fabMenu.collapse();
        }
        else if (id == R.id.fabSendFront){
            arrangeOperation.setArrangeType(ArrangeOperation.ArrangeType.BringToFront);
            drawingBoard.getOperationManager().executeOperation(arrangeOperation);
            binding.fabMenu.collapse();
        }
        else if (id == R.id.fab_erase){
            binding.fabMenu.setIconDrawable(context, R.drawable.del_icon);
            binding.fabMenu.collapse();
            drawingBoard.getDrawingContext().setDrawingMode(deleteMode);
        }
        else if (id == R.id.expandBtn){
//            binding.fabMenu.collapse();
//            binding.llAllOptions.setVisibility(View.VISIBLE);

            binding.expandBtn.animate().rotationBy(180)
//                    .scaleX(binding.expandBtn.getRotation()/90%2==0?(w*1.0f/h):1)
//                    .scaleY(binding.expandBtn.getRotation()/90%2==0?(w*1.0f/h):1)
                    .setDuration(300).setInterpolator(new LinearInterpolator()).start();

            if (binding.llAllOptions.getVisibility() == View.GONE){
                binding.llAllOptions.setVisibility(View.VISIBLE);
            }else{
                binding.llAllOptions.setVisibility(View.GONE);
            }

        }
        else if (id == R.id.saveBtn){
            triggerFirebaseInAppEvent(activity,"SaveMapBtnClicked");
            if (binding.fabMenu.isExpanded()) binding.fabMenu.collapse();

            viewSlideXDirection(false,binding.fabMenu,true,false);

            if (pointerMode!=null && drawingBoard!=null)
                drawingBoard.getDrawingContext().setDrawingMode(pointerMode);

            Bitmap bitmap = captureView(binding.drawingView);
            if (bitmap!=null) checkAppPermission(activity,Bitmap.CompressFormat.PNG,100,bitmap);

        }
    }

    float alphaDown = 0.35f;
    private void buttonSelector(int val){
        switch (val){
            case 1:
                binding.button1.setAlpha(1f);
                binding.button2.setAlpha(alphaDown);
                binding.button3.setAlpha(alphaDown);
                binding.button4.setAlpha(alphaDown);
                binding.button5.setAlpha(alphaDown);
                binding.button6.setAlpha(alphaDown);
                break;
            case 2:
                binding.button1.setAlpha(alphaDown);
                binding.button2.setAlpha(1f);
                binding.button3.setAlpha(alphaDown);
                binding.button4.setAlpha(alphaDown);
                binding.button5.setAlpha(alphaDown);
                binding.button6.setAlpha(alphaDown);
                break;
            case 3:
                binding.button1.setAlpha(alphaDown);
                binding.button2.setAlpha(alphaDown);
                binding.button3.setAlpha(1f);
                binding.button4.setAlpha(alphaDown);
                binding.button5.setAlpha(alphaDown);
                binding.button6.setAlpha(alphaDown);
                break;
            case 4:
                binding.button1.setAlpha(alphaDown);
                binding.button2.setAlpha(alphaDown);
                binding.button3.setAlpha(alphaDown);
                binding.button4.setAlpha(1f);
                binding.button5.setAlpha(alphaDown);
                binding.button6.setAlpha(alphaDown);
                break;
            case 5:
                binding.button1.setAlpha(alphaDown);
                binding.button2.setAlpha(alphaDown);
                binding.button3.setAlpha(alphaDown);
                binding.button4.setAlpha(alphaDown);
                binding.button5.setAlpha(1f);
                binding.button6.setAlpha(alphaDown);
                break;
            case 6:
                binding.button1.setAlpha(alphaDown);
                binding.button2.setAlpha(alphaDown);
                binding.button3.setAlpha(alphaDown);
                binding.button4.setAlpha(alphaDown);
                binding.button5.setAlpha(alphaDown);
                binding.button6.setAlpha(1f);
                break;
            case 7:
                binding.button1.setAlpha(1f);
                binding.button2.setAlpha(1f);
                binding.button3.setAlpha(1f);
                binding.button4.setAlpha(1f);
                binding.button5.setAlpha(1f);
                binding.button6.setAlpha(1f);
                break;
        }
    }

    public void insertPhoto(int resId) {
        InsertPhotoMode mode = new InsertPhotoMode();
        Bitmap bitmap = getBitmapFromVectorDrawable(activity,resId);
        if (bitmap!=null) {
            PhotoElement element = new PhotoElement();
            element.setBitmap(bitmap);
            element.setLockAspectRatio(true);
            drawingBoard.getDrawingContext().setDrawingMode(mode);
            int x = binding.drawingView.getWidth()/2;
            int y = binding.drawingView.getHeight()/2;

            mode.setPhotoElement(element,x,y);
            if (pointerMode!=null && drawingBoard!=null)
                drawingBoard.getDrawingContext().setDrawingMode(pointerMode);
        }
    }

    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Bitmap bitmap = null;
        if (drawable != null) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    /*public Bitmap captureView(View view) {
        if (view.getWidth()>0 && view.getHeight()>0) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
//            return BitmapUtil.removeTransparency(bitmap);
            return bitmap;
        }
        else
            return null;
    }*/
    public Bitmap captureView(View view) {
        if (view.getWidth() > 0 && view.getHeight() > 0) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // Set the canvas background color to white
            canvas.drawColor(Color.WHITE);

            view.draw(canvas);
//            return bitmap;
            return BitmapUtil.removeTransparency(bitmap);
        } else {
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void stickerItemSelected(boolean value) {
        Log.e("PhotoElement_07","Sticker Clicked "+value);
        binding.fabMenu.collapse();
    }

    int durationAnim = 300;
    private void viewSlideYDirection(boolean isShow, View viewToShowHide){
        if(viewToShowHide!=null) {
            if (isShow) {
                viewToShowHide.setVisibility(View.VISIBLE);
                viewToShowHide.animate().translationY(0).setDuration(durationAnim).start();
            } else {
                if (viewToShowHide.getVisibility()==View.VISIBLE) {
                    float mY = viewToShowHide.getHeight();
                    viewToShowHide.animate().translationY(mY).setDuration(durationAnim)
                            .withEndAction(() -> viewToShowHide.setVisibility(View.GONE)).start();
                }
            }
        }
    }
    private void viewSlideXDirection(boolean isShow, View viewToShow,boolean isNegDir,boolean isShowDoneDrawBtn){
        if(viewToShow!=null) {
            if (isShow) {
                viewToShow.setVisibility(View.VISIBLE);
                viewToShow.animate().translationX(0).setDuration(durationAnim).start();
            } else {
                if (viewToShow.getVisibility()==View.VISIBLE) {
                    float mX = viewToShow.getWidth();
                    if (isNegDir) mX = -mX;
                    viewToShow.animate().translationX(mX).setDuration(durationAnim)
                            .withEndAction(() -> {
                                viewToShow.setVisibility(View.GONE);
                                if (isShowDoneDrawBtn) viewFadinAnim(true,binding.doneDrawBtn);
                            }).start();


                }
            }
        }
    }
    private void viewFadinAnim(boolean isShow, View viewToShow){
        if(viewToShow!=null) {
            if (isShow) {
                viewToShow.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {}
                }).start();
                viewToShow.setVisibility(View.VISIBLE);
            } else {
                viewToShow.animate().alpha(0).setDuration(300).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        buttonSelector(7);
                        viewToShow.setVisibility(View.GONE);
                        viewSlideXDirection(true,binding.saveBtn,false,true);
                    }
                }).start();
            }
        }
    }
}