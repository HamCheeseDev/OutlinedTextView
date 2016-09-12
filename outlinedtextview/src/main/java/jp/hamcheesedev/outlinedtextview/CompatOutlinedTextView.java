package jp.hamcheesedev.outlinedtextview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class CompatOutlinedTextView extends AppCompatTextView {

    private OutlinedTextViewHelper helper = new OutlinedTextViewHelper();

    public CompatOutlinedTextView(final Context context) {
        this(context, null);
    }

    public CompatOutlinedTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompatOutlinedTextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        helper.init(this, attrs);
    }

    @SuppressWarnings("unused")
    public void setStrokeWidth(float strokeWidth) {
        helper.setStrokeWidth(strokeWidth);
    }

    @SuppressWarnings("unused")
    public void setStrokeColor(int strokeColor) {
        helper.setStrokeColor(strokeColor);
    }

    @Override
    public void setTextSize(final int unit, final float size) {
        super.setTextSize(unit, size);
        helper.setTextSize(getTextSize());
    }

    @Override
    protected void onDraw(final Canvas canvas) {

        helper.drawStrokeIfNeeded(this, canvas);

        super.onDraw(canvas);
    }
}
