package cn.com.stableloan.ui.activity.integarl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.mancj.slideup.SlideUp;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.stableloan.AppApplication;
import cn.com.stableloan.R;
import cn.com.stableloan.api.Urls;
import cn.com.stableloan.base.BaseActivity;
import cn.com.stableloan.bean.ImageDataBean;
import cn.com.stableloan.model.InformationEvent;
import cn.com.stableloan.model.PicStatusEvent;
import cn.com.stableloan.model.UserBean;
import cn.com.stableloan.ui.activity.GestureLoginActivity;
import cn.com.stableloan.ui.activity.LoginActivity;
import cn.com.stableloan.ui.activity.PictureActivity;
import cn.com.stableloan.ui.activity.Verify_PasswordActivity;
import cn.com.stableloan.utils.LogUtils;
import cn.com.stableloan.utils.SPUtils;
import cn.com.stableloan.utils.TinyDB;
import cn.com.stableloan.utils.ToastUtils;
import cn.com.stableloan.utils.cache.ACache;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 图片材料上传
 */
public class UpImageIdentityActivity extends BaseActivity {
    @Bind(R.id.title_name)
    TextView titleName;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_save)
    TextView tvSave;
    @Bind(R.id.tv_IdentificationCard)
    TextView tvIdentificationCard;
    @Bind(R.id.tv_BusinessCard)
    TextView tvBusinessCard;
    @Bind(R.id.tv_JobCard)
    TextView tvJobCard;
    @Bind(R.id.tv_DebitCard)
    TextView tvDebitCard;
    @Bind(R.id.tv_CreditCard)
    TextView tvCreditCard;
    @Bind(R.id.iv_IdentityUp)
    ImageView ivIdentityUp;
    @Bind(R.id.iv_IdentityDown)
    ImageView ivIdentityDown;
    @Bind(R.id.iv_IdentityHead)
    ImageView ivIdentityHead;
    @Bind(R.id.iv_business)
    ImageView ivBusiness;
    @Bind(R.id.iv_job)
    ImageView ivJob;
    @Bind(R.id.iv_debit)
    ImageView ivDebit;
    @Bind(R.id.iv_credit)
    ImageView ivCredit;
    @Bind(R.id.slide_image)
    RelativeLayout slideImage;
    private String token = "";
    private String qiNiuToken = "";
    private SlideUp ImageSlideUp;
    private List<LocalMedia> selectList = new ArrayList<>();
    public static final int POSIVITIVE_PHOTO = 1;
    public static final int NEGATIVE_PHOTO = 2;
    public static final int AM_PHOTO = 3;
    public static final int DEBIT_CODE = 4;
    public static final int CREDIT_CODE = 5;
    public static final int LICENSE_CODE = 6;
    public static final int BRAND_PHOTO = 7;


    private Context mContext;
    private int HEIGHT;
    private int WIDTH;
    private static  final  int IMAGE_RESULT=110;

    private RequestOptions options;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, UpImageIdentityActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_identity);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mContext = this;
        initToolbar();
        getToken();
        getImageDate();
    }

    private void getImageDate() {

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        ivBusiness.measure(w, h);
        LogUtils.i("ivBusiness===", "\n" + HEIGHT + "," + WIDTH);

        token = (String) SPUtils.get(this, Urls.lock.TOKEN, "1");
        String signature = (String) SPUtils.get(this, "signature", "1");
        Map<String, String> parms1 = new HashMap<>();
        parms1.put("token", token);
        parms1.put("signature", signature);
        parms1.put("source", "");
        JSONObject json = new JSONObject(parms1);
        OkGo.<String>post(Urls.Ip_url + Urls.Pictrue.Get_Pictrue)
                .tag(this)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null) {
                            Gson gson = new Gson();
                            ImageDataBean imageDataBean = gson.fromJson(s, ImageDataBean.class);
                            if (imageDataBean.getError_code() == 0) {

                                ImageDataBean.DataBean data = imageDataBean.getData();
                                if (data.getStatus() == 1) {
                                    String license_photo = data.getLicense_photo();
                                    String credite_photo = data.getCredite_photo();
                                    String debit_photo = data.getDebit_photo();
                                    String brand_photo = data.getBrand_photo();
                                    String positive_photo = data.getPositive_photo();
                                    String negative_photo = data.getNegative_photo();
                                    String am_photo = data.getAm_photo();
                                    if (!license_photo.isEmpty()) {
                                        fillImageView(license_photo, ivBusiness, "营业执照");
                                    }
                                    if (!credite_photo.isEmpty()) {
                                        fillImageView(credite_photo, ivCredit, "信用卡");
                                    }
                                    if (!debit_photo.isEmpty()) {
                                        fillImageView(debit_photo, ivDebit, "借记卡");
                                    }
                                    if (!brand_photo.isEmpty()) {
                                        fillImageView(brand_photo, ivJob, "工作证");
                                    }
                                    if (!positive_photo.isEmpty()) {
                                        fillImageView(positive_photo, ivIdentityUp, "身份证正面");
                                    }
                                    if (!negative_photo.isEmpty()) {
                                        fillImageView(negative_photo, ivIdentityDown, "身份证反面");
                                    }
                                    if (!am_photo.isEmpty()) {
                                        fillImageView(am_photo, ivIdentityHead, "手持身份证");
                                    }
                                } else{
                                  /*  String userphone = (String) SPUtils.get(getApplicationContext(), Urls.lock.USER_PHONE, "1");
                                    final TinyDB tinyDB = new TinyDB(getApplicationContext());
                                    UserBean user = (UserBean) tinyDB.getObject(userphone, UserBean.class);
                                    String phone = user.getUserphone();
                                    String gesturePassword = aCache.getAsString(phone);
                                    String lock = aCache.getAsString("lock");

                                    if(gesturePassword == null || "".equals(gesturePassword)||"off".equals(lock)) {
                                        Intent intent = new Intent(getApplicationContext(), Verify_PasswordActivity.class).putExtra("from", "CardUpload");
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), GestureLoginActivity.class).putExtra("from", "CardUpload");
                                        startActivity(intent);
                                    }*/
                                }
                            }else if(imageDataBean.getError_code()==2){
                                Intent intent=new Intent(UpImageIdentityActivity.this,LoginActivity.class);
                                intent.putExtra("message",imageDataBean.getError_message());
                                intent.putExtra("from","UpImageError");
                                startActivity(intent);

                            } else {
                                ToastUtils.showToast(mContext, imageDataBean.getError_message());
                            }
                        }
                    }
                });

    }

    @Subscribe
    public void onMessageEvent(InformationEvent event) {
        String message = event.message;
        if ("CardUpload".equals(message)) {
            getImageDate();
        }
    }

    /**
     * @param picturePath 七牛图片地址
     * @param imageView   填充的View
     */
    private void fillImageView(String picturePath, ImageView imageView, String title) {
        if (!picturePath.isEmpty()) {
             options = new RequestOptions()
                    .centerCrop()
                    .error(R.mipmap.iv_error_image)
                    .placeholder(R.mipmap.iv_holder_image)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(mContext).load(picturePath).apply(options).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = PictureActivity.newIntent(mContext, picturePath, title);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            UpImageIdentityActivity.this, imageView, PictureActivity.TRANSIT_PIC);
                    try {
                        ActivityCompat.startActivity(mContext, intent, optionsCompat.toBundle());
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void initToolbar() {

         options = new RequestOptions()
                .centerCrop()
                .error(R.mipmap.iv_error_image)
                .placeholder(R.mipmap.iv_holder_image)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);



        ivBack.setVisibility(View.VISIBLE);
        titleName.setText("图片材料");
        setTextViewColor(tvIdentificationCard, tvIdentificationCard.getText().toString());
        setTextViewColor(tvBusinessCard, tvBusinessCard.getText().toString());
        setTextViewColor(tvJobCard, tvJobCard.getText().toString());
        setTextViewColor(tvDebitCard, tvDebitCard.getText().toString());
        setTextViewColor(tvCreditCard, tvCreditCard.getText().toString());

        ImageSlideUp = new SlideUp.Builder(slideImage)
                .withStartGravity(Gravity.BOTTOM)
                .withLoggingEnabled(true)
                .withAutoSlideDuration(10)
                .withStartState(SlideUp.State.HIDDEN)
                .build();
    }

    private void setTextViewColor(TextView view, String s) {

        int indexOf = s.indexOf("(");
        SpannableString spanString = new SpannableString(s);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.product_limit));
        spanString.setSpan(span, indexOf, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new AbsoluteSizeSpan(15, true), indexOf, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(spanString);
    }

    @OnClick({R.id.take_identity, R.id.take_business, R.id.take_job, R.id.take_debit,
            R.id.take_credit, R.id.image_Visiable, R.id.tv_SlideUp, R.id.tv_SlideDown,
            R.id.tv_SlideHead, R.id.bt_cancel, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.take_identity:
                ImageSlideUp.show();
                break;
            case R.id.take_business:
                takePicture(LICENSE_CODE);
                break;
            case R.id.take_job:
                takePicture(BRAND_PHOTO);
                break;
            case R.id.take_debit:
                takePicture(DEBIT_CODE);
                break;
            case R.id.take_credit:
                takePicture(CREDIT_CODE);
                break;
            case R.id.image_Visiable:
                ImageSlideUp.hide();
                break;
            case R.id.tv_SlideUp:
                takePicture(POSIVITIVE_PHOTO);
                ImageSlideUp.hide();
                break;
            case R.id.tv_SlideDown:
                takePicture(NEGATIVE_PHOTO);
                ImageSlideUp.hide();
                break;
            case R.id.tv_SlideHead:
                takePicture(AM_PHOTO);
                ImageSlideUp.hide();
                break;
            case R.id.bt_cancel:
                ImageSlideUp.hide();
                break;
            case R.id.iv_back:
                Intent intent=new Intent();
                intent.putExtra("ok", 1);
                setResult(IMAGE_RESULT,intent);
                finish();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case POSIVITIVE_PHOTO:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    upQiNiuPicture(selectList, ivIdentityUp, POSIVITIVE_PHOTO, "positive_photo");
                    break;
                case NEGATIVE_PHOTO:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    upQiNiuPicture(selectList, ivIdentityDown, NEGATIVE_PHOTO, "negative_photo");
                    break;
                case AM_PHOTO:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    upQiNiuPicture(selectList, ivIdentityHead, AM_PHOTO, "am_photo");
                    break;
                case BRAND_PHOTO:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    upQiNiuPicture(selectList, ivJob, BRAND_PHOTO, "brand_photo");
                    break;
                case CREDIT_CODE:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    upQiNiuPicture(selectList, ivCredit, CREDIT_CODE, "credite_photo");
                    break;
                case DEBIT_CODE:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    upQiNiuPicture(selectList, ivDebit, DEBIT_CODE, "debit_photo");
                    break;
                case LICENSE_CODE:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    upQiNiuPicture(selectList, ivBusiness, LICENSE_CODE, "license_photo");
                    break;
            }
        }
    }

    private void upQiNiuPicture(List<LocalMedia> selectList, ImageView iv, int position, String type) {
        final String path = selectList.get(selectList.size() - 1).getCompressPath();
        UploadOptions uploadOptions = new UploadOptions(null, null, false,
                new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        LogUtils.i("response", "key" + key + "----" + (int) percent * 100);
                    }
                }, null);
        AppApplication.getUploadManager().put(path, null, qiNiuToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

                LogUtils.i("qiNiuToken====",qiNiuToken+"---");
                if (info.isOK()) {
                    LogUtils.i("response", response.toString());
                    String key1 = null;
                    try {
                        key1 = response.getString("key");
                        UpLoadImage(key1, position, type);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Glide.with(getApplicationContext()).load(path).apply(options).into(iv);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PictureSelector.create(UpImageIdentityActivity.this).externalPicturePreview(selectList.size() - 1, selectList);
                        }
                    });
                } else {
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .error(R.mipmap.iv_error_image)
                            .placeholder(R.mipmap.iv_holder_image)
                            .override(WIDTH, HEIGHT)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                    Glide.with(getApplicationContext()).load(R.mipmap.iv_error_image).apply(options).into(iv);

                    ToastUtils.showToast(getApplicationContext(), "上传失败");
                }
            }
        }, uploadOptions);
    }

    /**
     * 七牛picture地址
     *
     * @param key
     */
    private void UpLoadImage(String key, int position, String type) {
        Map<String, String> parms = new HashMap<>();
        parms.put("type", String.valueOf(position));
        parms.put(type, key);
        parms.put("token", token);
        parms.put("source", "");
        JSONObject jsonObject = new JSONObject(parms);
        OkGo.<String>post(Urls.Ip_url + Urls.Pictrue.UpLoadImage)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject object = new JSONObject(s);
                            int error_code = object.getInt("error_code");
                            String error_message = object.getString("error_message");
                            if (error_code==0) {
                                ToastUtils.showToast(getApplicationContext(), "您的营业执照上传成功");
                            } else {
                                ToastUtils.showToast(getApplicationContext(), error_message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                });

    }

    /**
     * 相册调取
     */
    private void takePicture(int potision) {

        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .isZoomAnim(true)
                .compressGrade(Luban.THIRD_GEAR)// luban压缩档次，默认3档 Luban.FIRST_GEAR、Luban.CUSTOM_GEAR
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                .enableCrop(false)// 是否裁剪
                .compress(true)// 是否压缩
                .compressMode(PictureConfig.LUBAN_COMPRESS_MODE)//系统自带 or 鲁班压缩 PictureConfig.SYSTEM_COMPRESS_MODE or LUBAN_COMPRESS_MODE
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                //.withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //.hideBottomControls(cb_hide.isChecked() ? false : true)// 是否显示uCrop工具栏，默认不显示
                .isGif(false)// 是否显示gif图片
                .previewEggs(true)
                .openClickSound(true)// 是否开启点击声音
                // .selectionMedia(t)// 是否传入已选图片
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                //.compressMaxKB()//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效
                //.compressWH() // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled() // 裁剪是否可旋转图片
                //.scaleEnabled()// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.videoSecond()//显示多少秒以内的视频or音频也可适用
                //.recordVideoSecond()//录制视频秒数 默认60s
                .forResult(potision);//结果回调onActivityResult code
    }

    private void getToken() {
        token = (String) SPUtils.get(this, Urls.lock.TOKEN, "1");
        String signature = (String) SPUtils.get(this, "signature", "1");
        Map<String, String> parms = new HashMap<>();
        parms.put("token", token);
        parms.put("signature", signature);
        parms.put("source", "");
        JSONObject jsonObject = new JSONObject(parms);
        OkGo.<String>post(Urls.NEW_URL + Urls.Pictrue.GET_QINIUTOKEN)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject object = new JSONObject(s);
                            String isSuccess = object.getString("isSuccess");
                            if ("1".equals(isSuccess)) {
                                String status = object.getString("status");
                                if ("1".equals(status)) {
                                    qiNiuToken = object.getString("token");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (slideImage.getVisibility() == View.VISIBLE) {
            ImageSlideUp.hide();
            return false;
        } else {
            Intent intent=new Intent();
            intent.putExtra("ok", 1);
            setResult(IMAGE_RESULT,intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
