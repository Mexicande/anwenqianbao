package cn.com.stableloan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.zhuge.analysis.stat.ZhugeSDK;

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
import cn.com.stableloan.R;
import cn.com.stableloan.api.DateStatisticsUtils;
import cn.com.stableloan.api.Urls;
import cn.com.stableloan.base.BaseActivity;
import cn.com.stableloan.bean.DescEvent;
import cn.com.stableloan.bean.ProcuctCollectionEvent;
import cn.com.stableloan.bean.UpdateEvent;
import cn.com.stableloan.model.Product_DescBean;
import cn.com.stableloan.ui.adapter.SuperTextAdapter;
import cn.com.stableloan.utils.LogUtils;
import cn.com.stableloan.utils.SPUtils;
import cn.com.stableloan.utils.ToastUtils;
import cn.com.stableloan.utils.top_menu.MenuItem;
import cn.com.stableloan.utils.top_menu.TopRightMenu;
import cn.com.stableloan.view.MyLayoutManager;
import cn.com.stableloan.view.RecyclerViewDecoration;
import cn.com.stableloan.view.SpacesItemDecoration;
import cn.com.stableloan.view.dialog.DescDialog;
import cn.com.stableloan.view.share.StateListener;
import cn.com.stableloan.view.share.TPManager;
import cn.com.stableloan.view.share.WXManager;
import cn.com.stableloan.view.share.WXShareContent;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 产品desc
 */
public class ProductDesc extends BaseActivity {


    @Bind(R.id.title_name)
    TextView titleName;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.review)
    TextView review;
    @Bind(R.id.arrive)
    TextView arrive;
    @Bind(R.id.repayment)
    TextView repayment;
    @Bind(R.id.repayment_channels)
    TextView repaymentChannels;
    @Bind(R.id.interest_algorithm)
    TextView interestAlgorithm;
    @Bind(R.id.prepayment)
    TextView prepayment;
    @Bind(R.id.platform_desc)
    TextView platformDesc;
    @Bind(R.id.min_algorithm)
    TextView minAlgorithm;

    @Bind(R.id.flow)
    RecyclerView flowRecyclerView;
    @Bind(R.id.tv_rate)
    TextView tvRate;
    @Bind(R.id.product_details)
    TextView productDetails;
    @Bind(R.id.product_logo)
    ImageView productLogo;
    @Bind(R.id.tv_pname)
    TextView tvPname;
    @Bind(R.id.product_introduction)
    TextView productIntroduction;
    @Bind(R.id.layout3)
    RelativeLayout layout3;
    @Bind(R.id.view)
    View view;
    @Bind(R.id.crowd)
    TextView crowd;
    @Bind(R.id.linla)
    LinearLayout linla;
    @Bind(R.id.apply)
    Button apply;
    @Bind(R.id.bt_share)
    ImageView btShare;
    @Bind(R.id.tv_DescAmount)
    TextView tvDescAmount;
    @Bind(R.id.tv_cycle)
    TextView tvCycle;
    @Bind(R.id.fastest_time)
    TextView fastestTime;
    @Bind(R.id.minMax_algorithm)
    TextView minMaxAlgorithm;
    @Bind(R.id.tv_interest_algorithm)
    TextView tvInterestAlgorithm;
    @Bind(R.id.et_MaxLimit)
    EditText etMaxLimit;
    @Bind(R.id.et_MaxTime)
    EditText etMaxTime;
    @Bind(R.id.tv_platform)
    TextView tvPlatform;
    @Bind(R.id.tv_Desccharge)
    TextView tvDesccharge;
    @Bind(R.id.zero_algorithm)
    TextView zeroAlgorithm;
    @Bind(R.id.out_view)
    RelativeLayout outView;
    @Bind(R.id.tv_DescTerminally)
    TextView tvDescTerminally;
    @Bind(R.id.desc_advertising)
    ImageView descAdvertising;
    @Bind(R.id.iv_news)
    ImageView ivNews;
    @Bind(R.id.iv_hots)
    ImageView ivHots;
    private int pid;

    private Product_DescBean descBean;

    private boolean shareFlag = false;
    private KProgressHUD hud;
    private static final int COLLECTION = 2000;
    private static final int APPLY_VAIL = 1000;
    private static final int Token_Fail = 3000;



    private boolean flag = false;


    public static void launch(Context context) {
        context.startActivity(new Intent(context, ProductDesc.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        ZhugeSDK.getInstance().init(getApplicationContext());
        initToolbar();
        pid = getIntent().getIntExtra("pid", 0);
        if (pid != 0) {
            getProductDate();
        }
        setListener();

    }


    /**
     * 微信分享
     * ediText
     */
    private void setListener() {

        outView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

            }
        });

        TPManager.getInstance().initAppConfig(Urls.KEY.WEICHAT_APPID, null, null, null);
        wxManager = new WXManager(this);
        StateListener<String> wxStateListener = new StateListener<String>() {
            @Override
            public void onComplete(String s) {
                ToastUtils.showToast(ProductDesc.this, s);
            }

            @Override
            public void onError(String err) {
                ToastUtils.showToast(ProductDesc.this, err);
            }

            @Override
            public void onCancel() {
                ToastUtils.showToast(ProductDesc.this, "取消");
            }
        };

        wxManager.setListener(wxStateListener);
        //贷款金额

        etMaxTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    computations();
                }
            }
        });

        etMaxLimit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    computations();
                }
            }
        });


    }

    private void computations() {

        String hint = etMaxTime.getText().toString();
        String str = etMaxLimit.getText().toString();
        String minimum_amount = descBean.getData().getMinimum_amount();
        String maximum_amount = descBean.getData().getMaximum_amount();
        if (!hint.isEmpty() && !str.isEmpty() && minimum_amount != null && maximum_amount != null) {
            int lim = Integer.parseInt(str);
            if (lim < Integer.parseInt(minimum_amount)) {
                etMaxLimit.setText(minimum_amount);
            }
            if (lim > Integer.parseInt(maximum_amount)) {
                etMaxLimit.setText(maximum_amount);
            }
            int time = Integer.parseInt(hint);

            int mony = Integer.parseInt(str);
            String min_algorithm = descBean.getData().getMin_algorithm();

            Double aDouble = Double.valueOf(min_algorithm);

            LogUtils.i("aDouble====", aDouble / 100 + "");
            double v1 = aDouble * time / 100 * mony + descBean.getData().getFee();
            int zeroRate = (int) v1;

            String valueOf = String.valueOf(zeroRate);

            zeroAlgorithm.setText(valueOf + "元");


            double v2 = (mony + v1) / time;

            int everyRate = (int) v2;
            String everyTime = String.valueOf(everyRate);

            tvDescTerminally.setText(everyTime + "元");
        }
    }

    private void getProductDate() {
        String token = (String) SPUtils.get(this, Urls.lock.TOKEN, "1");

        HashMap<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(pid));
        if (!"1".equals(token)) {
            params.put("token", token);
        }
        params.put("terminal", "1");
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait.....")
                .setCancellable(true)
                .show();
        final JSONObject jsonObject = new JSONObject(params);
        OkGo.post(Urls.NEW_Ip_url + Urls.product.Productdetail)
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null) {
                            try {
                                JSONObject object = new JSONObject(s);
                                int error_code = object.getInt("error_code");
                                String error_message = object.getString("error_message");

                                if (error_code == 0) {
                                    Gson gson = new Gson();
                                    descBean = gson.fromJson(s, Product_DescBean.class);
                                    if (descBean != null) {
                                        dateInset(descBean);
                                    }
                                } else if(error_code==2){
                                    Intent intent=new Intent(ProductDesc.this,LoginActivity.class);
                                    intent.putExtra("message",error_message);
                                    intent.putExtra("from","DescError");
                                    startActivityForResult(intent,3000);
                                } else {
                                    ToastUtils.showToast(ProductDesc.this, error_message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        hud.dismiss();

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        hud.dismiss();

                    }
                });
    }


    private String substringmin = "";
    private String substringmax = "";

    private SuperTextAdapter superTextAdapter;

    private void dateInset(Product_DescBean date) {


        Product_DescBean.DataBean product = date.getData();
        int collectioStatus = product.getCollectioStatus();

        if (collectioStatus == 1) {
            shareFlag = true;
        }
        JSONObject eventObject = new JSONObject();
        try {
            eventObject.put("产品", product.getPname() + "detail");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//记录事件
        ZhugeSDK.getInstance().track(this, "产品详情贷款", eventObject);

        List<Product_DescBean.DataBean.LabelsBean> labels = product.getLabels();

      /*  FlexboxLayoutManager manager = new FlexboxLayoutManager();
        //设置主轴排列方式
        manager.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        manager.setFlexWrap(FlexWrap.WRAP);
        flowRecyclerView.setLayoutManager(manager);
        flowRecyclerView.setItemAnimator(new DefaultItemAnimator());

*/

       /* FlexboxLayoutManager manager = new FlexboxLayoutManager();

        manager.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        manager.setFlexWrap(FlexWrap.WRAP);
        manager.setAlignItems(AlignItems.STRETCH);

        flowRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        flowRecyclerView.setLayoutManager(manager);*/

       /* MyLayoutManager layout = new MyLayoutManager();
        flowRecyclerView.setLayoutManager(layout);*/
        flowRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(5,StaggeredGridLayoutManager.VERTICAL));
        RecyclerViewDecoration decoration = new RecyclerViewDecoration(0,0);
        flowRecyclerView.addItemDecoration(decoration);
        superTextAdapter = new SuperTextAdapter(null);
        flowRecyclerView.setAdapter(superTextAdapter);
        superTextAdapter.setNewData(labels);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this).load(product.getProduct_logo()).apply(options)
                .into(productLogo);
        if(product.getAd_image()!=null&&!product.getAd_image().isEmpty()){
            descAdvertising.setVisibility(View.VISIBLE);
            Glide.with(this).load(product.getAd_image())
                    .apply(options).into(descAdvertising);
        }else {
            descAdvertising.setVisibility(View.GONE);
        }

        if(product.getActivity()==1){
            ivHots.setVisibility(View.VISIBLE);
        }else {
            ivHots.setVisibility(View.GONE);
        }
        if(product.getOnline()==1){
            ivNews.setVisibility(View.VISIBLE);
        }else {
            ivNews.setVisibility(View.GONE);
        }
        tvPname.setText(product.getPname());
        productIntroduction.setText(product.getProduct_introduction());
        String minAl = product.getMin_algorithm();
        minAlgorithm.setText(minAl + "%");
        String product_crowd = product.getCrowd();
        String product_review = product.getReview();
        String product_repayment = product.getRepayment();
        String product_repayment_channels = product.getRepayment_channels();
        String min = product.getMin_algorithm();
        String max = product.getMax_algorithm();
        String product_prepayment = product.getPrepayment();
        String minimum_amount = product.getMinimum_amount();
        String maximum_amount = product.getMaximum_amount();
        int interest_algorithm = product.getInterest_algorithm();
        if (interest_algorithm == 0) {
            tvInterestAlgorithm.setText("参考日利率");
            tvCycle.setText(product.getMin_cycle() + "~" + product.getMax_cycle()+"日");
        } else {
            tvInterestAlgorithm.setText("参考月利率");
            tvCycle.setText(product.getMin_cycle() + "~" + product.getMax_cycle()+"月");

        }
        if (minimum_amount.length() > 4) {
            substringmin = minimum_amount.substring(0, minimum_amount.length() - 4);
            substringmin = substringmin + "万";
        } else {
            substringmin = minimum_amount;
        }
        fastestTime.setText(product.getFastest_time());
        if (max == null || min == null) {
            minMaxAlgorithm.setVisibility(View.GONE);
        } else if (max.equals(min)) {
            setTextViewColor(minMaxAlgorithm, "利息额度: " + product.getMin_algorithm() + "%");
        } else {
            setTextViewColor(minMaxAlgorithm, "利息额度: " + product.getMin_algorithm() + "%" + "~" + product.getMax_algorithm() + "%");
        }
        if (maximum_amount.length() > 4) {
            substringmax = maximum_amount.substring(0, maximum_amount.length() - 4);
            substringmax = substringmax + "万";
        } else {
            substringmax = maximum_amount;
        }

        etMaxLimit.setText(minimum_amount);
        etMaxTime.setText(product.getMin_cycle());
        tvPlatform.setText(product.getPlatformdetail().getPl_name());
        if (product.getActual_account() != null && !product.getActual_account().isEmpty()) {
            setTextViewColor(arrive, "到账方式: " + product.getActual_account());
        } else {
            arrive.setVisibility(View.GONE);
        }
        tvDescAmount.setText(substringmin + "~" + substringmax+"元");
        if (product_crowd != null) {
            setTextViewColor(crowd, "面向人群: " + product_crowd);
        } else {
            crowd.setVisibility(View.GONE);
        }
        if (product_review != null && !product_review.isEmpty()) {
            setTextViewColor(review, "审核方式: " + product_review);
        } else {
            review.setVisibility(View.GONE);
        }
        if (product_repayment != null && !product_repayment.isEmpty()) {
            setTextViewColor(repayment, "还款方式: " + product_repayment);
        } else {
            repayment.setVisibility(View.GONE);
        }
        if (product_repayment_channels != null && !product_repayment_channels.isEmpty()) {
            setTextViewColor(repaymentChannels, "还款渠道: " + product_repayment_channels);
        } else {
            repaymentChannels.setVisibility(View.GONE);

        }
        if (min != null && max != null) {
            if (interest_algorithm == 0) {
                setTextViewColor(interestAlgorithm, "利息算法: 日利率");
            } else {
                setTextViewColor(interestAlgorithm, "利息算法: 月利率");
            }
        } else {
            interestAlgorithm.setVisibility(View.GONE);

        }
        if (product_prepayment != null && !product_prepayment.isEmpty()) {
            prepayment.setVisibility(View.VISIBLE);
            setTextViewColor(prepayment, "提前还款: " + product_prepayment);
        } else {
            prepayment.setVisibility(View.GONE);
        }

        if (product.getProduct_details() != null) {
            String details = product.getProduct_details();
            String replace = details.replace("aaa", "\n");
            productDetails.setText(replace);
        }
        if (product.getFee() != 0) {
            tvDesccharge.setText(product.getFee() + "元");
        }
        computations();

    }

    private void setTextViewColor(TextView view, String s) {

        SpannableString spanString = new SpannableString(s);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.gay));
        spanString.setSpan(span, 0, 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        view.setText(spanString);
    }

    private void initToolbar() {
        titleName.setText("产品详情");
    }

    private DescDialog descDialog;

    @OnClick({R.id.iv_back, R.id.platform_desc, R.id.apply, R.id.bt_share
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.platform_desc:
                startActivity(new Intent(this, PlatformInfoActivity.class).putExtra("pid", String.valueOf(descBean.getData().getPl_id())));
                break;
            case R.id.apply:
                String token = (String) SPUtils.get(ProductDesc.this, Urls.lock.TOKEN, "1");
                if (token==null||"1".equals(token)) {
                    LoginActivity.launch(this);

                } else {
                        String userphone = (String) SPUtils.get(getApplicationContext(), Urls.lock.USER_PHONE, "1");
                        int apply = (int) SPUtils.get(this, userphone + Urls.lock.APPLY, Urls.lock.NO_VERIFICATION);
                        switch (apply) {
                            case Urls.lock.NO_VERIFICATION:
                                Boolean dialog = (Boolean) SPUtils.get(this, "dialog", false);
                                if (dialog != null && dialog) {
                                    sendIO();
                                    startActivity(new Intent(ProductDesc.this, HtmlActivity.class).putExtra("product", descBean));
                                }else {
                                    showApplyDialog();


                                }

                                break;
                            case Urls.lock.PW_VERIFICATION:
                                Intent intent = new Intent(this, Verify_PasswordActivity.class);
                                intent.putExtra("from", "apply");
                                startActivityForResult(intent, APPLY_VAIL);
                                break;
                            case Urls.lock.GESTURE_VERIFICATION:
                                Intent intent1 = new Intent(this, GestureLoginActivity.class);
                                intent1.putExtra("from", "apply");
                                intent1.putExtra("product", descBean);
                                startActivityForResult(intent1, APPLY_VAIL);
                                break;
                        }
                }
                break;
            case R.id.bt_share:
                setShared_Collection();
                break;
            default:
                break;
        }
    }

    private void showApplyDialog() {

        descDialog = new DescDialog(this);
        descDialog.setTitle("提示", descBean.getData().getProduct_logo());
        descDialog.setMessage("确定退出登陆?");
        descDialog.setYesOnclickListener("确定", new DescDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                descDialog.dismiss();
                sendIO();
                startActivity(new Intent(ProductDesc.this, HtmlActivity.class).putExtra("product", descBean));
            }
        });

        descDialog.show();
    }

    private void setShared_Collection() {

        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(R.mipmap.iv_share_wechat, "分享到微信"));
        menuItems.add(new MenuItem(R.mipmap.iv_share_friend, "分享到朋友圈"));
        if (shareFlag) {
            menuItems.add(new MenuItem(R.mipmap.iv_collectioning, "取消收藏"));
        } else {
            menuItems.add(new MenuItem(R.mipmap.iv_collection, "收藏"));
        }
        TopRightMenu mTopRightMenu = new TopRightMenu(this);
        mTopRightMenu
                .showIcon(true)     //显示菜单图标，默认为true
                .dimBackground(true)           //背景变暗，默认为true
                .needAnimationStyle(true)   //显示动画，默认为true
                .setAnimationStyle(R.style.TRM_ANIM_STYLE)  //默认为R.style.TRM_ANIM_STYLE
                .addMenuList(menuItems)
                .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(int position) {
                        switch (position) {
                            case 0:
                                shareWechat(WXShareContent.WXSession);
                                break;
                            case 1:
                                shareWechat(WXShareContent.WXTimeline);
                                break;
                            case 2:
                                String token = (String) SPUtils.get(ProductDesc.this, Urls.lock.TOKEN, "1");
                                if (token == null||"1".equals(token)) {
                                    startActivityForResult(new Intent(ProductDesc.this, LoginActivity.class).putExtra("from", "collection"), COLLECTION);
                                } else {
                                    if (shareFlag) {
                                        CollectionProduct("2");
                                    } else {
                                        CollectionProduct("1");
                                    }
                                }
                                break;
                        }

                    }
                })
                .showAsDropDown(btShare, -245, 0);


    }

    private WXManager wxManager;

    private void shareWechat(int scence) {


        WXShareContent contentWX = new WXShareContent();
        contentWX.setScene(scence)
                .setType(WXShareContent.share_type.WebPage)
                .setWeb_url(Urls.KEY.PageWeb + descBean.getData().getId())
                .setTitle("安稳钱包")
                .setDescription(descBean.getData().getProduct_introduction())
                .setImage_url("http://orizavg5s.bkt.clouddn.com/logo.png");
        wxManager.share(contentWX);

    }


    private void CollectionProduct(final String status) {

        String token = (String) SPUtils.get(this, "token", "1");
        Map<String, String> parms1 = new HashMap<>();

        parms1.put("token", token);
        parms1.put("status", status);
        parms1.put("product_id", String.valueOf(pid));
        JSONObject jsonObject = new JSONObject(parms1);

        OkGo.post(Urls.Ip_url + Urls.product.CollectionDesc)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null) {
                            try {
                                JSONObject json = new JSONObject(s);
                                int error_code = json.getInt("error_code");
                                if (error_code == 0) {
                                    flag = true;
                                    EventBus.getDefault().post(new ProcuctCollectionEvent("ok"));
                                    if (status.equals("1")) {
                                        shareFlag = true;
                                        ToastUtils.showToast(ProductDesc.this, "收藏成功");
                                    } else {
                                        shareFlag = false;
                                        ToastUtils.showToast(ProductDesc.this, "取消成功");
                                    }
                                }else if(error_code==2){
                                    Intent intent=new Intent(ProductDesc.this,LoginActivity.class);
                                    intent.putExtra("message",json.getString("error_message"));
                                    intent.putExtra("from","ProductDescError");
                                    intent.putExtra("collection",status);
                                    startActivity(intent);
                                }else {
                                    String error_message = json.getString("error_message");
                                    ToastUtils.showToast(ProductDesc.this, error_message);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(ProductDesc.this, "服务器异常");
                            }
                        });
                    }
                });

    }

    private void sendIO() {
        JSONObject eventObject = new JSONObject();
        try {
            eventObject.put("产品名称", descBean.getData().getPname());
            ZhugeSDK.getInstance().track(getApplicationContext(), "立即申请",
                    eventObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public  void updateEvent(DescEvent msg){
        if(msg!=null){
            CollectionProduct(msg.collection);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        ZhugeSDK.getInstance().flush(getApplicationContext());
        if (flag) {
            EventBus.getDefault().post(new ProcuctCollectionEvent("ok"));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case COLLECTION:
                if (resultCode == 2000) {
                    String ok = data.getStringExtra("ok");
                    if (ok != null) {
                        getProductDate();
                        if (!shareFlag) {
                            shareFlag = true;
                        } else {
                            ToastUtils.showToast(this, "已经收藏过了");
                        }
                       // startActivity(new Intent(ProductDesc.this, HtmlActivity.class).putExtra("product", descBean));
                    }
                }
                break;
            case APPLY_VAIL:
                if (resultCode == 1000) {
                    String ok = data.getStringExtra("ok");
                    if ("ok".equals(ok)) {
                        Boolean dialog = (Boolean) SPUtils.get(this, "dialog", false);
                        if (dialog != null && dialog) {
                            sendIO();
                            startActivity(new Intent(this, HtmlActivity.class).putExtra("product", descBean));

                            addDateApply();
                        } else {
                            showApplyDialog();
                        }
                    }
                }
                break;
            case Token_Fail:
                if(resultCode==3000){
                    int desc = data.getIntExtra("desc", 0);
                    if(desc==1){
                        if(pid!=0){
                            getProductDate();
                        }
                    }
                }
                break;
        }
    }

    private void addDateApply() {


    }

}
