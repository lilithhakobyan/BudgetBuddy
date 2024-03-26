package com.example.budgetbuddy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {

    private Paint paint;
    private RectF rectF;
    private float income, expense;

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        rectF = new RectF();
    }

    public void setData(float income, float expense) {
        this.income = income;
        this.expense = expense;
        invalidate(); // Redraw the view with new data
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float total = income + expense;
        float incomeAngle = 360 * income / total;
        float expenseAngle = 360 * expense / total;

        rectF.set(100, 100, getWidth() - 100, getHeight() - 100);

        paint.setColor(Color.GREEN); // Income color
        canvas.drawArc(rectF, 0, incomeAngle, true, paint);

        paint.setColor(Color.RED); // Expense color
        canvas.drawArc(rectF, incomeAngle, expenseAngle, true, paint);
    }
}
