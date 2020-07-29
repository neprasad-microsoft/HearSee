package com.microsoft.garage.hearsee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.garage.hearsee.service.SpeechService;

import java.util.ArrayList;

import javax.inject.Inject;

public class ImageCustomView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = ImageCustomView.class.getSimpleName();
    private static final String NO_OBJECT_FOUND = "Sorry! No object found.";

    private ArrayList<Rect> mSavedAreaList = new ArrayList<>();
    private ArrayList<String> mObjectDescriptionList = new ArrayList<>();
    private Paint mPaint;

    @Inject
    SpeechService speechService;


    public ImageCustomView(Context context) {
        super(context);
        init(context);
    }

    public ImageCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public boolean setAnalysedAreaList(ArrayList<Rect> boundList){
        return mSavedAreaList.addAll(boundList);
    }

    public  boolean setObjectDescriptionList(ArrayList<String> descriptionList){
        return mObjectDescriptionList.addAll(descriptionList);
    }

    private void init(Context context) {
        ((HearSeeApplication) ((AppCompatActivity) context).getApplication()).applicationComponent.inject(this);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.GREEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSavedAreaList.size() == 0) return;

        for (Rect rect : mSavedAreaList) {
            canvas.drawRect(rect, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            int i = 1;
            for (Rect rect: mSavedAreaList){
                if (rect.contains((int)event.getX(), (int)event.getY())){
                    break;
                }
                i++;
            }
            if (i > mSavedAreaList.size() || mSavedAreaList.size() != mObjectDescriptionList.size()){
                speechService.speak(NO_OBJECT_FOUND);
            } else {
                speechService.speak(mObjectDescriptionList.get(i-1));
            }
        }
        return true;
    }
}