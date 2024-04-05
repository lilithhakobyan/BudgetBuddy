package com.example.budgetbuddy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private OnSwipeToDeleteListener swipeToDeleteListener;
    private Drawable deleteIcon;
    private int iconMargin;
    private boolean initiated;

    public SwipeToDeleteCallback(OnSwipeToDeleteListener listener) {
        super(0, ItemTouchHelper.LEFT);
        this.swipeToDeleteListener = listener;
    }

    private void init(RecyclerView recyclerView) {
        deleteIcon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.baseline_delete_white);
        iconMargin = recyclerView.getContext().getResources().getDimensionPixelSize(R.dimen.icon_margin);
        initiated = true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Not needed for swipe-to-delete
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            swipeToDeleteListener.onSwipedLeft(position);
        } else if (direction == ItemTouchHelper.RIGHT) {
            swipeToDeleteListener.onSwipedRight(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (!initiated) {
            init(recyclerView);
        }
        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        if (dX < 0) {
            Paint paint = new Paint();
            paint.setColor(Color.rgb(139, 0, 0));
            RectF background = new RectF(
                    (float) itemView.getRight() + dX,
                    (float) itemView.getTop(),
                    (float) itemView.getRight(),
                    (float) itemView.getBottom()
            );
            c.drawRect(background, paint);

            int iconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
            int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;

            // Make sure the icon is within the bounds of the item
            if (iconLeft < itemView.getRight() - iconMargin) {
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
