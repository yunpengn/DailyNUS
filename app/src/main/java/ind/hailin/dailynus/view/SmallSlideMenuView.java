package ind.hailin.dailynus.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.zip.Inflater;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.utils.MyUtils;

import static android.R.attr.bitmap;

public class SmallSlideMenuView extends RelativeLayout implements View.OnClickListener {

    public final static String TAG = "SmallSlideMenuView";

    private LinearLayout subLayout;
    private ImageView ivDelete, ivClear, ivMenu;
    private TextView tvMenuUnder;
    private boolean isOpen = true;
    private onDeleteItemListener deleteItemListener;

    public SmallSlideMenuView(Context context) {
        super(context);
        init();
    }

    public SmallSlideMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmallSlideMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_smallslidemenu, this, true);
        subLayout = (LinearLayout) view.findViewById(R.id.view_layout);
        ivDelete = (ImageView) view.findViewById(R.id.view_delete);
        ivClear = (ImageView) findViewById(R.id.view_clear);
        ivMenu = (ImageView) findViewById(R.id.view_menu);
        tvMenuUnder = (TextView) findViewById(R.id.view_menu_under);

        ivDelete.setOnClickListener(this);
        ivClear.setOnClickListener(this);
        ivMenu.setOnClickListener(this);
        tvMenuUnder.setOnClickListener(this);

        closeNoAnimate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_menu:
            case R.id.view_menu_under:
                if (!isOpen) openMenu();
                else closeMenu();
                break;
            case R.id.view_delete:
                if(deleteItemListener != null)
                    deleteItemListener.onDelete();
                break;
            case R.id.view_clear:
                tvMenuUnder.setText("");
                break;
        }
    }

    public void openMenu() {
        openMenu(300);
    }

    public void closeMenu() {
        closeMenu(300);
    }

    public void openMenu(int duration) {
        if (isOpen) return;
        subLayout.setEnabled(true);
        //animate for layout
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setInterpolator(new AccelerateInterpolator(0.3f));
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1);
        alphaAnimation.setInterpolator(new AccelerateInterpolator(0.3f));
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        subLayout.startAnimation(animationSet);
        //animate for menu button
        RotateAnimation rotateAnimation = new RotateAnimation(90, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        ivMenu.startAnimation(animationSet);

        isOpen = true;
        Log.d(TAG, "open");
    }

    public void closeMenu(int duration) {
        if (!isOpen) return;
        subLayout.setEnabled(false);
        //animate for layout
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setInterpolator(new DecelerateInterpolator(0.3f));
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.1f);
        alphaAnimation.setInterpolator(new DecelerateInterpolator(0.3f));
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        subLayout.startAnimation(animationSet);
        //animate for menu button
        RotateAnimation rotateAnimation = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        ivMenu.startAnimation(animationSet);

        isOpen = false;
        Log.d(TAG, "close");
    }

    private void closeNoAnimate() {
        closeMenu(1);
    }

    public void setMenuShow(int number) {
        if (number < 100) {
            tvMenuUnder.setText(number + "");
            tvMenuUnder.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            tvMenuUnder.setText("99+");
            tvMenuUnder.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }
    }

    public void setMenuShow(int number, int color) {
        setMenuShow(number);
        tvMenuUnder.setTextColor(color);
    }

    public void setOnDeleteItemListener(onDeleteItemListener listener) {
        deleteItemListener = listener;
    }

    public interface onDeleteItemListener {
        void onDelete();
    }

}
