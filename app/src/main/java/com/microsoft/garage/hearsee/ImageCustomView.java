package com.microsoft.garage.hearsee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;

public class ImageCustomView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = ImageCustomView.class.getSimpleName();

    private ArrayList<Rect> mSavedAreaList = new ArrayList<>();
    private ArrayList<String> mObjectDescriptionList = new ArrayList<>();
    private Paint mPaint;

    public ImageCustomView(Context context) {
        super(context);
        init();
    }

    public ImageCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public boolean setAnalysedAreaList(ArrayList<Rect> boundList){
        return mSavedAreaList.addAll(boundList);
    }

    public  boolean setObjectDescriptionList(ArrayList<String> descriptionList){
        return mObjectDescriptionList.addAll(descriptionList);
    }

    private void init() {
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
        StringBuilder sb = new StringBuilder();
        sb.append("Rectangle: ");
        int i = 1;
        for (Rect rect: mSavedAreaList){
            if (rect.contains((int)event.getX(), (int)event.getY())){
                sb.append(i);
                break;
            }
            i++;
        }
        if (i > mSavedAreaList.size() || mSavedAreaList.size() != mObjectDescriptionList.size()){
            sb.append("Not Found Or Size mismatch");
        } else {
            sb.append(" Description: ");
            sb.append(mObjectDescriptionList.get(i-1));
        }

        Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_LONG).show();
        return true;
    }
}