package jp.hamcheesedev.outlinedtextview;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import jp.hamcheesedev.R;

class OutlinedTextViewHelper {

    private Paint strokePaint = new Paint();

    public void init(@NonNull final TextView textView, final AttributeSet attrs) {

        final TypedArray array = textView.getContext()
                .obtainStyledAttributes(attrs, R.styleable.OutlinedTextView);
        final float strokeWidth = array.getDimension(R.styleable.OutlinedTextView_strokeWidth, 0.0f);
        final int strokeColor = array.getColor(R.styleable.OutlinedTextView_strokeColor, Color.TRANSPARENT);
        array.recycle();

        strokePaint.setColor(strokeColor);
        strokePaint.setTextAlign(Paint.Align.LEFT);
        strokePaint.setTextSize(textView.getTextSize());
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setAntiAlias(true);
        strokePaint.setTypeface(textView.getPaint().getTypeface());
    }

    @SuppressWarnings("unused")
    public void setStrokeWidth(float strokeWidth) {
        strokePaint.setStrokeWidth(strokeWidth);

    }

    @SuppressWarnings("unused")
    public void setStrokeColor(int strokeColor) {
        strokePaint.setColor(strokeColor);
    }

    public void setTextSize(final float textSize) {
        strokePaint.setTextSize(textSize);
    }

    public void drawStrokeIfNeeded(@NonNull final TextView textView, @NonNull final Canvas canvas) {

        if (strokePaint.getStrokeWidth() > 0 && strokePaint.getColor() != Color.TRANSPARENT) {
            drawStroke(textView, canvas);
        }
    }

    private void drawStroke(@NonNull final TextView textView, @NonNull final Canvas canvas) {

        strokePaint.setTypeface(textView.getPaint().getTypeface());

        final Layout layout = textView.getLayout();

        final int lineCount = textView.getLineCount();
        final int layoutHeight = layout.getHeight();
        final int viewWidth = textView.getWidth();
        final int viewHeight = textView.getHeight();
        final int paddingLeft = textView.getCompoundPaddingLeft();
        final int paddingRight = textView.getCompoundPaddingRight();
        final int paddingTop = textView.getCompoundPaddingTop();
        final int paddingBottom = textView.getCompoundPaddingBottom();

        final int spaceHeight = Math.max((viewHeight - paddingTop - paddingBottom) - layoutHeight, 0);
        final int verticalGravity = getVerticalGravity(textView);

        final int clipTop, clipBottom;

        if (verticalGravity == Gravity.BOTTOM) {
            clipBottom = (viewHeight > layoutHeight) ? viewHeight + paddingTop : layoutHeight + paddingTop;
            final int yLayoutStart = viewHeight - paddingBottom - layoutHeight;
            clipTop = (yLayoutStart > paddingTop) ? yLayoutStart : (paddingTop << 1) - yLayoutStart;
        } else {
            clipBottom = viewHeight - paddingBottom;
            clipTop = paddingTop;
        }

        if (clipTop > clipBottom) {
            return;
        }
        if (paddingLeft > viewWidth - paddingRight) {
            return;
        }

        canvas.save();
        canvas.clipRect(paddingLeft, clipTop, viewWidth - paddingRight, clipBottom);

        final String originalText = textView.getText().toString();
        final int start = (verticalGravity != Gravity.BOTTOM) ? 0 : lineCount - 1;
        final int add = (verticalGravity != Gravity.BOTTOM) ? 1 : -1;

        for (int i = start; getLoopCondition(i, lineCount, verticalGravity); i += add) {
            final String text = originalText.substring(
                    layout.getLineStart(i), layout.getLineEnd(i));

            final int x = (int) layout.getLineLeft(i) + paddingLeft;
            int y = layout.getLineBaseline(i) + paddingTop;

            switch (verticalGravity) {
                case Gravity.BOTTOM:
                    y += spaceHeight;
                    break;
                case Gravity.CENTER_VERTICAL:
                    y += (spaceHeight >> 1);
                    break;
                default:
                    break;
            }

            if (verticalGravity != Gravity.BOTTOM) {
                if (y - textView.getLineHeight() >= clipBottom) {
                    break;
                }
            } else {
                if (y + textView.getLineHeight() <= clipTop) {
                    break;
                }
            }

            canvas.drawText(text, x, y, strokePaint);
        }
        canvas.restore();

    }

    private int getVerticalGravity(@NonNull final TextView textView) {
        if ((textView.getGravity() & Gravity.TOP) == Gravity.TOP) {
            return Gravity.TOP;
        } else if ((textView.getGravity() & Gravity.BOTTOM) == Gravity.BOTTOM) {
            return Gravity.BOTTOM;
        } else {
            return Gravity.CENTER_VERTICAL;
        }
    }

    private boolean getLoopCondition(final int idx, final int count, final int verticalGravity) {
        if (verticalGravity == Gravity.BOTTOM) {
            return idx >= 0;
        } else {
            return idx < count;
        }
    }
}
