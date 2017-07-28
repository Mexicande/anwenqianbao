package cn.com.stableloan.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import com.gyf.barlibrary.ImmersionBar;

import cn.com.stableloan.R;
import cn.com.stableloan.model.CardBean;

public class CarmeraResultActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carmera_result);
        ImmersionBar.with(this).statusBarColor(R.color.frame_color)
                .statusBarAlpha(0.8f)
                .fitsSystemWindows(true)
                .init();

      //  CardBean.OutputsBean.OutputValueBean.DataValueBean value = (CardBean.OutputsBean.OutputValueBean.DataValueBean) getIntent().getSerializableExtra("carmera");

        Bundle bundle = getIntent().getBundleExtra("bundle");

        CardBean.OutputsBean.OutputValueBean.DataValueBean value = (CardBean.OutputsBean.OutputValueBean.DataValueBean) bundle.getSerializable("carmera");


        String path = bundle.getString("path");
        imageView= (ImageView) findViewById(R.id.photoView);
        mTextView= (TextView) findViewById(R.id.Card);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        imageView.setImageBitmap(bitmap);

        mTextView.setText(value.getName()+"\n"+value.getSex()+"\n"+value.getNationality()+"\n"+value.getBirth()+"\n"+value.getNum()+"\n"+value.getAddress());




    }
}
