package cn.com.stableloan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.barlibrary.ImmersionBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.stableloan.R;

public class PictureActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_IMAGE_TITLE = "image_title";
    public static final String TRANSIT_PIC = "picture";
    @Bind(R.id.picture)
    ImageView picture;
    @Bind(R.id.title_name)
    TextView titleName;
    @Bind(R.id.layout_go)
    LinearLayout layoutGo;
    private String mImageUrl, mImageTitle;


    public static Intent newIntent(Context context, String url, String desc) {
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra(PictureActivity.EXTRA_IMAGE_URL, url);
        intent.putExtra(PictureActivity.EXTRA_IMAGE_TITLE, desc);
        return intent;
    }

    private void parseIntent() {
        mImageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        mImageTitle = getIntent().getStringExtra(EXTRA_IMAGE_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ButterKnife.bind(this);
        ImmersionBar.with(this).statusBarColor(R.color.frame_color)
                .statusBarAlpha(0.8f)
                .fitsSystemWindows(true)
                .init();
        parseIntent();
        ViewCompat.setTransitionName(picture, TRANSIT_PIC);
        titleName.setText(mImageTitle);
        RequestOptions options = new RequestOptions()
                .centerInside()
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(this).load(mImageUrl).apply(options).into(picture);
    }

    @OnClick(R.id.layout_go)
    public void onViewClicked() {
        finish();
    }
}
