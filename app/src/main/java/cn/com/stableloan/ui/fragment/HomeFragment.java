package cn.com.stableloan.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.uuch.adlibrary.AdConstant;
import com.uuch.adlibrary.AdManager;
import com.uuch.adlibrary.bean.AdInfo;
import com.uuch.adlibrary.transformer.DepthPageTransformer;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.com.stableloan.R;
import cn.com.stableloan.api.Urls;
import cn.com.stableloan.bean.IdentityProduct;
import cn.com.stableloan.model.Banner_HotBean;
import cn.com.stableloan.model.home.Hot_New_Product;
import cn.com.stableloan.model.home.Seckill_Bean;
import cn.com.stableloan.model.home.SpecialClassBean;
import cn.com.stableloan.model.integarl.AdvertisingBean;
import cn.com.stableloan.ui.activity.HtmlActivity;
import cn.com.stableloan.ui.activity.MainActivity;
import cn.com.stableloan.ui.activity.NoticeActivity;
import cn.com.stableloan.ui.activity.ProductClassifyActivity;
import cn.com.stableloan.ui.activity.ProductDesc;
import cn.com.stableloan.ui.adapter.Classify_Recycler_Adapter;
import cn.com.stableloan.ui.adapter.ListProductAdapter;
import cn.com.stableloan.ui.adapter.Recycler_Adapter;
import cn.com.stableloan.utils.LogUtils;
import cn.com.stableloan.utils.SPUtils;
import cn.com.stableloan.utils.TimeUtils;
import cn.com.stableloan.utils.ToastUtils;
import cn.com.stableloan.view.EasyRefreshLayout;
import cn.com.stableloan.view.MyDecoration;
import cn.com.stableloan.view.ScrollSpeedLinearLayoutManger;
import cn.com.stableloan.view.SpacesItemDecoration;
import cn.com.stableloan.view.countdownview.CountdownView;
import cn.com.stableloan.view.countdownview.EasyCountDownView;
import cn.com.stableloan.view.countdownview.SimpleCountDownTimer;
import cn.com.stableloan.view.supertextview.SuperTextView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{


    @Bind(R.id.recylerview)
    RecyclerView recylerview;
    @Bind(R.id.easylayout)
    EasyRefreshLayout easylayout;
    @Bind(R.id.iv_notice)
    ImageView ivNotice;
    @Bind(R.id.select_money)
    RelativeLayout selectMoney;

    private ListProductAdapter productAdapter;
    private Seckill_Bean seckillBean;
    int ACTION = 1;
    private List<String> list;
    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        initDialog();


        JSONObject eventObject = new JSONObject();
        try {
            eventObject.put("首页", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//记录事件
        ZhugeSDK.getInstance().track(getActivity(), "homepage", eventObject);

        getDate();
        setListener();
        return view;
    }



    /**
     * 秒杀活动
     */

    private void getSeckill() {


        OkGo.post(Urls.NEW_Ip_url + Urls.HOME_FRAGMENT.SPECKILL)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        if (s != null) {
                            Gson gson = new Gson();
                            seckillBean = gson.fromJson(s, Seckill_Bean.class);
                            if (seckillBean.getError_code() == 0) {
                                if (seckillBean.getData().size() != 0) {
                                    long time = System.currentTimeMillis();
                                    Calendar mCalendar = Calendar.getInstance();
                                    mCalendar.setTimeInMillis(time);
                                    int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                                    int minute = mCalendar.get(Calendar.MINUTE);
                                    int second = mCalendar.get(Calendar.SECOND);
                                    int millisecond = mCalendar.get(Calendar.MILLISECOND);
                                    long h = (long) (24 - hour) * 60 * 60 * 1000;
                                    long m = (long) (60 - minute) * 60 * 1000;
                                    long se = (long) (60 - second) * 1000;

                                    long time12 = h + m + se + (1000 - millisecond);

                                    LogUtils.i("time21===", time12);
                                    mSeckill_layout.setVisibility(View.VISIBLE);
                                    mCountdownView.start(time12);
                                    countdownhour.setTime(time12);
                                    //mCountdownView.updateShow(time12);
                                    // 总时间
                                    // 初始化并启动倒计时
                                }else {
                                    mSeckill_layout.setVisibility(View.GONE);
                                }
                                switch (seckillBean.getData().size()) {
                                    case 1:
                                        re_View.setVisibility(View.GONE);
                                        Seckill_Bean.DataBean dataBean = seckillBean.getData().get(0);
                                        RequestOptions options = new RequestOptions()
                                                .centerCrop()
                                                .dontAnimate()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL);
                                        Glide.with(getActivity()).load(dataBean.getProduct_logo()).apply(options).into(product_logo);
                                        tv_amout.setText("最高" + dataBean.getAmount() + "元");
                                        tv_activity_desc.setText(dataBean.getActivity_desc());
                                        tv_pname.setText(dataBean.getProduct_name());
                                        mCardView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(getActivity(), ProductDesc.class).putExtra("pid", seckillBean.getData().get(0).getProduct_id()));
                                            }
                                        });
                                        break;
                                    case 2:
                                        mCardView.setVisibility(View.GONE);
                                        re_View.setVisibility(View.VISIBLE);
                                        re_View.setLayoutManager(new GridLayoutManager(getActivity(), 2));

                                        re_View.setAdapter(rc_adapter);
                                        rc_adapter.setNewData(seckillBean.getData());
                                        break;
                                    case 3:
                                        mCardView.setVisibility(View.GONE);
                                        re_View.setVisibility(View.VISIBLE);
                                        re_View.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
                                        re_View.setAdapter(rc_adapter);
                                        rc_adapter.setNewData(seckillBean.getData());
                                        break;
                                }
                            }
                        }
                    }
                });

    }

    private void initDialog() {
        String[] stringArray = getResources().getStringArray(R.array.home_money);
        list = Arrays.asList(stringArray);

        HashMap<String, String> params = new HashMap<>();
        params.put("position", "1");
        params.put("type", "1");
        JSONObject object = new JSONObject(params);
        OkGo.post(Urls.Ip_url + Urls.Dialog.advertising)
                .tag(this)
                .upJson(object)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null) {
                            Gson gson = new Gson();
                            AdvertisingBean bean = gson.fromJson(s, AdvertisingBean.class);
                            if (bean.getError_code() == 0) {
                                showAdvertising(bean);
                            }
                        }
                    }
                });

    }

    private void showAdvertising(AdvertisingBean bean) {

        AdInfo adInfo = new AdInfo();
        long date = (long) SPUtils.get(getActivity(), "AdvertTime", 1111111111111L);
        boolean today = TimeUtils.isToday(date);
        if (today) {

        } else {
            adInfo.setActivityImg(bean.getData().getImg());
            advList = new ArrayList<>();
            advList.add(adInfo);
            AdManager adManager = new AdManager(getActivity(), advList);
            adManager.setOverScreen(true)
                    .setWidthPerHeight(Float.parseFloat(bean.getData().getAspectRatio()))
                    .setPageTransformer(new DepthPageTransformer());
            adManager.showAdDialog(AdConstant.ANIM_DOWN_TO_UP);

            adManager.setPadding(50)
                    .setBackViewColor(Color.parseColor("#AA333333"));
            adManager.setOnImageClickListener(new AdManager.OnImageClickListener() {
                @Override
                public void onImageClick(View view, AdInfo advInfo) {
                    if (!bean.getData().getUrl().isEmpty()) {
                        startActivity(new Intent(getActivity(), HtmlActivity.class).putExtra("advertising", bean.getData().getUrl()));
                    }
                    adManager.dismissAdDialog();
                }
            });
            long timeMillis = System.currentTimeMillis();
            SPUtils.put(getActivity(), "AdvertTime", timeMillis);
        }
      /*
        if(!today){

        }*/
    }

    /**
     * 下拉刷新
     */
    private void setListener() {
        //easylayout.autoRefresh();


        ivNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticeActivity.launch(getActivity());

            }
        });

        //Money选择
        selectMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPickerView( );
            }
        });

        easylayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onRefreshing() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ACTION = 2;
                        getBannerDate(ACTION);
                    }
                }, 1000);

            }
        });
        easylayout.setEnableLoadMore(false);

        recylerview.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

                JSONObject eventObject = new JSONObject();
                try {
                    eventObject.put("xinshangxian", productAdapter.getData().get(position).getPname());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ZhugeSDK.getInstance().track(getActivity(), "新上线产品", eventObject);

                startActivity(new Intent(getActivity(), ProductDesc.class).putExtra("pid", productAdapter.getData().get(position).getId()));

            }
        });
        //分类专题

        classify_recyclView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (specialClassBean.getData().get(position).getProject_name() != null) {
                    JSONObject eventObject = new JSONObject();
                    try {
                        eventObject.put("fenleizhuanti", specialClassBean.getData().get(position).getProject_name());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ZhugeSDK.getInstance().track(getActivity(), "分类专题", eventObject);
                    startActivity(new Intent(getActivity(), ProductClassifyActivity.class).putExtra("class_product", specialClassBean.getData().get(position)));
                }
            }
        });

    }

    /**
     * 首页新品
     */
    private List<AdInfo> advList = null;

    private void getDate() {

        productAdapter = new ListProductAdapter(null);
        recylerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recylerview.setAdapter(productAdapter);
        recylerview.addItemDecoration(new MyDecoration(getActivity(), MyDecoration.VERTICAL_LIST));
        View view = setHeaderView();
        productAdapter.addHeaderView(view);

    }



    private BGABanner banner;

    private RecyclerView re_View, classify_recyclView;
    private Recycler_Adapter rc_adapter;
    private Classify_Recycler_Adapter classify_recycler_adapter;

    private ImageView iv_work, iv_student, iv_free, iv_enterprise;

    private CardView mCardView;
    //秒杀
    private LinearLayout mSeckill_layout;
    private TextView tv_pname, tv_amout, tv_activity_desc, tvDisplay;

    private Banner_HotBean hotBean;

    private ImageView product_logo;
    private EasyCountDownView countdownhour;
    private SpecialClassBean specialClassBean;
    private CountdownView  mCountdownView;
    private View setHeaderView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.head_layout, null);
        banner = (BGABanner) view.findViewById(R.id.banner_fresco_demo_content);
        product_logo = (ImageView) view.findViewById(R.id.product_logo);
        mSeckill_layout = (LinearLayout) view.findViewById(R.id.layout_kill);
        tv_pname = (TextView) view.findViewById(R.id.pname);
        tv_activity_desc = (TextView) view.findViewById(R.id.activity_desc);
        tv_amout = (TextView) view.findViewById(R.id.amount);
        mCountdownView = (CountdownView) view.findViewById(R.id.cv_countdownViewTest1);
        mCardView = (CardView) view.findViewById(R.id.seckill_item_one);
        countdownhour = (EasyCountDownView) view.findViewById(R.id.countdownhour);
        banner.setAdapter(new BGABanner.Adapter<ImageView, Banner_HotBean.AdvertisingBean>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, Banner_HotBean.AdvertisingBean model, int position) {

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                Glide.with(getActivity())
                        .load(model.getPictrue())
                        .apply(options)
                        .into(itemView);
            }
        });

        banner.setDelegate(new BGABanner.Delegate<ImageView, Banner_HotBean.AdvertisingBean>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, Banner_HotBean.AdvertisingBean model, int position) {

                startActivity(new Intent(getContext(), HtmlActivity.class).putExtra("Advertising", hotBean.getAdvertising().get(position)));
            }
        });
        //秒杀
        re_View = (RecyclerView) view.findViewById(R.id.speckill_recycyler);

        rc_adapter = new Recycler_Adapter(null);
        getBannerDate(ACTION);

        re_View.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

                JSONObject eventObject = new JSONObject();
                try {
                    eventObject.put("miaosha", seckillBean.getData().get(position).getProduct_name());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ZhugeSDK.getInstance().track(getActivity(), "rementuijian", eventObject);
                startActivity(new Intent(getActivity(), ProductDesc.class).putExtra("pid", seckillBean.getData().get(position).getProduct_id()));
            }
        });


        // 分类
        classify_recyclView = (RecyclerView) view.findViewById(R.id.recycler_special);
        classify_recycler_adapter = new Classify_Recycler_Adapter(null);

        classify_recyclView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
           SpacesItemDecoration decoration = new SpacesItemDecoration(10);
        classify_recyclView.addItemDecoration(decoration);
        classify_recyclView.setAdapter(classify_recycler_adapter);

        // 职业选择
        iv_free = (ImageView) view.findViewById(R.id.iv_free);
        iv_work = (ImageView) view.findViewById(R.id.iv_work);
        iv_student = (ImageView) view.findViewById(R.id.iv_other);
        iv_enterprise = (ImageView) view.findViewById(R.id.bussiones);

        iv_free.setOnClickListener(this);
        iv_work.setOnClickListener(this);
        iv_student.setOnClickListener(this);
        iv_enterprise.setOnClickListener(this);
        return view;
    }

    /**
     * Banner_Hot 数据填充
     * <p>
     * 分类专题和新品
     */


    private void getBannerDate(final int Action) {

        OkGo.post(Urls.puk_URL + Urls.HOME_FRAGMENT.BANNER_HOT)
                .tag(getActivity())
                .connTimeOut(5000)      // 设置当前请求的连接超时时间
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                String success = jsonObject.getString("isSuccess");
                                if (success.equals("1")) {
                                    Gson gson = new Gson();
                                    hotBean = gson.fromJson(s, Banner_HotBean.class);
                                    if (Action == 2) {
                                        banner.setData(hotBean.getAdvertising(), null);
                                        easylayout.refreshComplete();
                                    } else {
                                        banner.setData(hotBean.getAdvertising(), null);
                                    }
                                } else {
                                    easylayout.refreshComplete();
                                    String msg = jsonObject.getString("msg");
                                    ToastUtils.showToast(getActivity(), msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            easylayout.refreshComplete();
                            ToastUtils.showToast(getActivity(), "网络异常");
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        //easylayout.setRefreshing(false);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                easylayout.refreshComplete();
                                ToastUtils.showToast(getActivity(), "网络异常");
                            }
                        });
                        super.onError(call, response, e);
                    }
                });


        OkGo.post(Urls.NEW_Ip_url + Urls.HOME_FRAGMENT.HOT_NEW_PRODUCT)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null) {
                            Gson gson = new Gson();
                            try {
                                Hot_New_Product hotNewProduct = gson.fromJson(s, Hot_New_Product.class);
                                if (hotNewProduct.getError_code() == 0) {
                                    productAdapter.setNewData(hotNewProduct.getData());
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


        //分类专题
        OkGo.post(Urls.NEW_Ip_url + Urls.HOME_FRAGMENT.Class_Product_List)
                .tag(getActivity())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null) {
                            Gson gson = new Gson();
                            specialClassBean = gson.fromJson(s, SpecialClassBean.class);
                            if (specialClassBean.getError_code() == 0) {
                                classify_recycler_adapter.setNewData(specialClassBean.getData());
                            } else {
                                ToastUtils.showToast(getActivity(), specialClassBean.getError_message());
                            }

                        } else {
                            ToastUtils.showToast(getActivity(), "网络异常");
                        }
                    }
                });
        getSeckill();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private String professional;

    @Override
    public void onClick(View v) {
        JSONObject eventObject = new JSONObject();

        switch (v.getId()) {
            case R.id.iv_free:
                professional = "xiaoyaoke";
                EventBus.getDefault().post(new IdentityProduct(3,100));
                MainActivity.navigationController.setSelect(1);
                break;
            case R.id.iv_other:
                professional = "xueshengdang";
                EventBus.getDefault().post(new IdentityProduct(2,100));
                MainActivity.navigationController.setSelect(1);
                break;
            case R.id.iv_work:
                professional = "shangbanzu";
                EventBus.getDefault().post(new IdentityProduct(1,100));
                MainActivity.navigationController.setSelect(1);
                break;
            case R.id.bussiones:
                professional = "qiyezhu";
                EventBus.getDefault().post(new IdentityProduct(4,100));
                MainActivity.navigationController.setSelect(1);
                break;


        }
        try {
            eventObject.put("职业", professional);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//记录事件
        ZhugeSDK.getInstance().track(getActivity(), "职业搜索", eventObject);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countdownhour != null) {
            countdownhour.cancelTime();
            mCountdownView.stop();
        }
    }
    private  void setPickerView( ){
        int color = getActivity().getResources().getColor(R.color.colorPrimary);
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(getActivity(), new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                if(options1==list.size()-1){
                    EventBus.getDefault().post(new IdentityProduct(0,10000));
                    MainActivity.navigationController.setSelect(1);
                }else {
                    String s = list.get(options1);
                    String substring = s.substring(0, s.length() - 1);
                    int i = Integer.parseInt(substring);
                    EventBus.getDefault().post(new IdentityProduct(0,i));
                    MainActivity.navigationController.setSelect(1);
                }
            }
        })

                .setTitleText("我要借多少钱")
                .setTitleColor(color)//标题文字颜色
                .setDividerColor(color)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setSubmitColor(color)
                .setCancelColor(color)
                .build();
        pvOptions.setPicker(list);//一级选择器
        pvOptions.show();
    }

}
