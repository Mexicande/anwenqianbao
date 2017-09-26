package cn.com.stableloan.view;


import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.stableloan.R;
import me.majiajie.pagerbottomtabstrip.internal.RoundMessageView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;

public class NormalItemView extends BaseTabItem {

    private ImageView mIcon;
    private final TextView mTitle;

    private int mDefaultDrawable;
    private int mCheckedDrawable;

    private int mDefaultTextColor = 0x56000000;
    private int mCheckedTextColor = 0x56000000;

    public NormalItemView(Context context) {
        this(context,null);
    }

    public NormalItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NormalItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.item_only_icon, this, true);

        mIcon = (ImageView) findViewById(R.id.icon);
        mTitle = (TextView) findViewById(R.id.title);
    }

    /**
     * 方便初始化的方法
     * @param drawableRes           默认状态的图标
     * @param checkedDrawableRes    选中状态的图标
     * @param title                 标题
     */
    public void initialize(@DrawableRes int drawableRes,@DrawableRes  int checkedDrawableRes, String title)
    {
        mDefaultDrawable = drawableRes;
        mCheckedDrawable = checkedDrawableRes;
        mTitle.setText(title);
    }

    @Override
    public void setChecked(boolean checked) {
        if(checked){
            mIcon.setImageResource(mCheckedDrawable);
            mTitle.setTextColor(mCheckedTextColor);
        } else {
            mIcon.setImageResource(mDefaultDrawable);
            mTitle.setTextColor(mDefaultTextColor);
        }
    }

    @Override
    public String getTitle() {
        return mTitle.getText().toString();
    }

    public void setTextDefaultColor(@ColorInt int color){
        mDefaultTextColor = color;
    }

    public void setTextCheckedColor(@ColorInt int color){
        mCheckedTextColor = color;
    }
}
