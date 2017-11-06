package cn.com.stableloan.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGABannerUtil;
import cn.com.stableloan.R;
import cn.com.stableloan.api.Urls;
import cn.com.stableloan.model.InformationEvent;
import cn.com.stableloan.model.UserBean;
import cn.com.stableloan.model.integarl.StatusBean;
import cn.com.stableloan.ui.activity.integarl.UpImageIdentityActivity;
import cn.com.stableloan.utils.LogUtils;
import cn.com.stableloan.utils.SPUtils;
import cn.com.stableloan.utils.TinyDB;
import cn.com.stableloan.utils.ToastUtils;
import cn.com.stableloan.view.supertextview.SuperTextView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 申请材料
 */
public class UserInformationActivity extends Activity {
    private ImmersionBar immersionBar;
    @Bind(R.id.title_name)
    TextView titleName;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.User_information)
    SuperTextView UserInformation;
    @Bind(R.id.User_Authorization)
    SuperTextView UserAuthorization;
    @Bind(R.id.User_Pic)
    SuperTextView UserPic;
    private List<View> views;
    private static  final  int IMAGE_REQUEST=100;
    private static  final  int IMAGE_RESULT=110;
    /**
     * #17326b
     *
     * @param context
     */
    public static void launch(Context context) {
        context.startActivity(new Intent(context, UserInformationActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        ButterKnife.bind(this);
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.information_title1)
                .fitsSystemWindows(true)
                .statusBarAlpha(0.3f)
                .init();
        EventBus.getDefault().register(this);
        initToolbar();
        getStatus();
    }

    private void initToolbar() {
        ivBack.setVisibility(View.VISIBLE);
        titleName.setText("申请材料");

        views = new ArrayList<>();

        views.add(BGABannerUtil.getItemImageView(this, R.drawable.user_infomation_top1));
        views.add(BGABannerUtil.getItemImageView(this, R.drawable.user_information_top_bg));
        BGABanner viewById = (BGABanner) findViewById(R.id.informationBgabanner);
        viewById.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(UserInformationActivity.this)
                        .load(Integer.parseInt(model))
                        .apply(options)
                        .into(itemView);
            }
        });
        viewById.setData(Arrays.asList(String.valueOf(R.drawable.user_infomation_top1),String.valueOf(R.drawable.user_information_top_bg)),null);

        viewById.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    immersionBar.statusBarColor(R.color.information_title1)
                            .init();
                }else if(position==1){
                    immersionBar.statusBarColor(R.color.information_title)
                            .init();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {



            }
        });
        JSONObject eventObject = new JSONObject();
        try {
            eventObject.put("mymaterials", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//记录事件
        ZhugeSDK.getInstance().track(this, "申请材料", eventObject);


    }

   /* @Override
    protected void onRestart() {
        super.onRestart();
        getStatus();
    }*/

    private void getStatus() {
        Map<String, String> parms = new HashMap<>();
        String token = (String) SPUtils.get(this, "token", "1");
        parms.put("token", token);
        JSONObject jsonObject = new JSONObject(parms);
        OkGo.<String>post(Urls.Ip_url + Urls.user.USER_STATUS)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogUtils.i("Status--", s);
                        if (s != null) {
                            Gson gson = new Gson();
                            StatusBean statusBean = gson.fromJson(s, StatusBean.class);
                            if (statusBean.getError_code() == 0) {
                                StatusBean.DataBean data = statusBean.getData();
                                if ("1".equals(data.getStep1())) {
                                    Drawable drawable = ContextCompat.getDrawable(UserInformationActivity.this, R.drawable.button_succeed);
                                    UserInformation.setTextBackground(drawable);
                                    UserInformation.setRightString("已完成");
                                } else {
                                    Drawable drawable = ContextCompat.getDrawable(UserInformationActivity.this, R.drawable.button_fail);
                                    UserInformation.setTextBackground(drawable);
                                    UserInformation.setRightString("未完成");
                                }
                                if ("1".equals(data.getStep2())) {
                                    Drawable drawable = ContextCompat.getDrawable(UserInformationActivity.this, R.drawable.button_succeed);
                                    UserAuthorization.setTextBackground(drawable);
                                    UserAuthorization.setRightString("已完成");
                                } else {
                                    Drawable drawable = ContextCompat.getDrawable(UserInformationActivity.this, R.drawable.button_fail);
                                    UserAuthorization.setTextBackground(drawable);
                                    UserAuthorization.setRightString("未完成");
                                }
                                if ("1".equals(data.getStep3())) {
                                    Drawable drawable = ContextCompat.getDrawable(UserInformationActivity.this, R.drawable.button_succeed);
                                    UserPic.setTextBackground(drawable);
                                    UserPic.setRightString("已完成");
                                } else {
                                    Drawable drawable = ContextCompat.getDrawable(UserInformationActivity.this, R.drawable.button_fail);
                                    UserPic.setTextBackground(drawable);
                                    UserPic.setRightString("未完成");
                                }

                            }else if(statusBean.getError_code()==2){
                                Intent intent=new Intent(UserInformationActivity.this,LoginActivity.class);
                                intent.putExtra("message",statusBean.getError_message());
                                intent.putExtra("from","UserInformationError");
                                startActivity(intent);
                            } else{
                                ToastUtils.showToast(UserInformationActivity.this, statusBean.getError_message());
                            }

                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                    }
                });

    }


    @OnClick({R.id.iv_back, R.id.User_information, R.id.User_Authorization, R.id.User_Pic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.User_information:
                String userphone = (String) SPUtils.get(this, Urls.lock.USER_PHONE, "1");
                TinyDB tinyDB = new TinyDB(this);
                UserBean user = (UserBean) tinyDB.getObject(userphone, UserBean.class);
                int identity = user.getIdentity();
                if (identity == 0) {
                    startActivity(new Intent(this, UpdataProfessionActivity.class).putExtra("from", "identity"));
                } else {
                    Intent identityintent=new Intent(this,IdentityinformationActivity.class);
                    startActivityForResult(identityintent,IMAGE_REQUEST);
                }
                break;
            case R.id.User_Authorization:
                Intent autointent=new Intent(this,CertificationActivity.class);
                startActivityForResult(autointent,IMAGE_REQUEST);
                break;
            case R.id.User_Pic:
                Intent intent=new Intent(this,UpImageIdentityActivity.class);
                startActivityForResult(intent,IMAGE_REQUEST);
                break;
        }
    }

    @Subscribe
    public void onMessageEvent(InformationEvent event) {
        String message = event.message;
        if ("informationStatus".equals(message)) {
            getStatus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST){
            switch (resultCode){
                case IMAGE_RESULT:
                    int ok = data.getIntExtra("ok", 0);
                    if(ok==1){
                        getStatus();
                    }
                    break;

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
