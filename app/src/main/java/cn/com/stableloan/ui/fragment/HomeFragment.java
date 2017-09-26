package cn.com.stableloan.ui.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.gyf.barlibrary.ImmersionFragment;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.uuch.adlibrary.AdConstant;
import com.uuch.adlibrary.AdManager;
import com.uuch.adlibrary.bean.AdInfo;
import com.uuch.adlibrary.transformer.DepthPageTransformer;
import com.uuch.adlibrary.transformer.RotateDownPageTransformer;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.com.stableloan.R;
import cn.com.stableloan.api.Urls;
import cn.com.stableloan.bean.IdentityProduct;
import cn.com.stableloan.bean.ProductListBean;
import cn.com.stableloan.model.Banner_HotBean;
import cn.com.stableloan.model.News_ClassBean;
import cn.com.stableloan.model.NoticeBean;
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
import cn.com.stableloan.view.ScrollSpeedLinearLayoutManger;
import cn.com.stableloan.view.SpacesItemDecoration;
import okhttp3.Call;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends ImmersionFragment implements View.OnClickListener {


    @Bind(R.id.recylerview)
    RecyclerView recylerview;
    @Bind(R.id.easylayout)
    EasyRefreshLayout easylayout;
    @Bind(R.id.iv_notice)
    ImageView ivNotice;

    private String P_id;
    private ListProductAdapter productAdapter;
    private ArrayList<ProductListBean.ProductBean> list;

    int ACTION = 1;

    public HomeFragment() {

    }

    @Override
    protected void immersionInit() {
        ImmersionBar.with(getActivity())
                .statusBarDarkFont(false)
                .navigationBarColor(R.color.md_grey_900)
                .statusBarAlpha(0.3f)
                .init();
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
        ZhugeSDK.getInstance().track(getActivity(), "homepage",  eventObject);
        getDate();
        setListener();
        return view;
    }

    private void initDialog() {


        HashMap<String, String> params = new HashMap<>();
        params.put("position", "1");
        params.put("type", "1");
        JSONObject object=new JSONObject(params);
        OkGo.post(Urls.Ip_url+Urls.Dialog.advertising)
                .tag(this)
                .upJson(object)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if(s!=null){
                            Gson gson=new Gson();
                            AdvertisingBean bean = gson.fromJson(s, AdvertisingBean.class);
                            if(bean.getError_code()==0){
                                showAdvertising(bean.getData().getImg(),bean.getData().getUrl());
                            }
                        }
                    }
                });

    }

    private void showAdvertising(String img,String url){

        AdInfo adInfo = new AdInfo();
        long date = (long) SPUtils.get(getActivity(), "AdvertTime", 1111111111111L);
        boolean today = TimeUtils.isToday(date);
        if(today){

        }else {
            adInfo.setActivityImg(img);
            advList = new ArrayList<>();
            advList.add(adInfo);
            AdManager adManager = new AdManager(getActivity(), advList);
            adManager.setOverScreen(true)
                    .setWidthPerHeight(1f)
                    .setPageTransformer(new DepthPageTransformer());
            adManager.showAdDialog(AdConstant.ANIM_DOWN_TO_UP);

            adManager.setPadding(50)
                    .setBackViewColor(Color.parseColor("#AA333333"));
            adManager.setOnImageClickListener(new AdManager.OnImageClickListener() {
                @Override
                public void onImageClick(View view, AdInfo advInfo) {
                    if(!url.isEmpty()){
                        startActivity(new Intent(getActivity(),HtmlActivity.class).putExtra("advertising",url));
                    }
                    // Toast.makeText(getActivity(), "您点击了ViewPagerItem...", Toast.LENGTH_SHORT).show();
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
                //horizontal 水平 滑动位置
                re_View.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!rc_adapter.getData().isEmpty()){
                            if (rc_adapter.getData().size() > 2) {
                                re_View.smoothScrollToPosition(rc_adapter.getData().size() - 1);
                            }
                        }
                    }
                }, 100);
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
                ZhugeSDK.getInstance().track(getActivity(), "新上线产品",  eventObject);

               // HtmlActivity.launch(getActivity());
                startActivity(new Intent(getActivity(), ProductDesc.class).putExtra("pid", productAdapter.getData().get(position).getId()));

            }
        });
    }

    /**
     * 首页新品
     */
    private List<AdInfo> advList = null;

    private void getDate() {

        ivNotice.setOnClickListener(this);
        View view = setHeaderView();
        productAdapter = new ListProductAdapter(null);
        recylerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        productAdapter.addHeaderView(view, 0);
        SpacesItemDecoration decoration = new SpacesItemDecoration(5);
        recylerview.addItemDecoration(decoration);
        recylerview.setAdapter(productAdapter);

    }

    private NoticeBean noticeBean;




    private BGABanner banner;


    private RecyclerView re_View, classify_recyclView;
    private Recycler_Adapter rc_adapter;
    private Classify_Recycler_Adapter classify_recycler_adapter;

    private ImageView iv_work, iv_student, iv_free, iv_enterprise;

    private News_ClassBean newBean;

    private Banner_HotBean hotBean;

    private View setHeaderView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.head_layout, null);
        banner = (BGABanner) view.findViewById(R.id.banner_fresco_demo_content);
        banner.setAdapter(new BGABanner.Adapter<ImageView, Banner_HotBean.AdvertisingBean>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, Banner_HotBean.AdvertisingBean model, int position) {

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        ;

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
        getBannerDate(ACTION);
        //热门推荐
        re_View = (RecyclerView) view.findViewById(R.id.linear_recyclerView);
        rc_adapter = new Recycler_Adapter(null);
        re_View.setLayoutManager(new ScrollSpeedLinearLayoutManger(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        re_View.setAdapter(rc_adapter);


        re_View.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                String app = hotBean.getRecommends().get(position).getApp();
                JSONObject eventObject = new JSONObject();
                try {
                    eventObject.put("remen1", hotBean.getRecommends().get(position).getName());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ZhugeSDK.getInstance().track(getActivity(), "rementuijian",  eventObject);
                if (app.startsWith("http")) {
                    startActivity(new Intent(getActivity(), HtmlActivity.class).putExtra("hotbean", hotBean.getRecommends().get(position)));
                } else if (app.startsWith("product")) {
                    String[] split = app.split("id");
                    startActivity(new Intent(getActivity(), ProductDesc.class).putExtra("pid", split[1]));
                }
            }
        });


        // 分类
        classify_recyclView = (RecyclerView) view.findViewById(R.id.classify_recycler);

        classify_recycler_adapter = new Classify_Recycler_Adapter(null);
        classify_recyclView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        classify_recyclView.setAdapter(classify_recycler_adapter);
        classify_recyclView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(newBean.getClassX().get(position).getName()!=null){
                    JSONObject eventObject = new JSONObject();
                    try {
                        eventObject.put("fenleizhuanti", newBean.getClassX().get(position).getName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ZhugeSDK.getInstance().track(getActivity(), "分类专题",  eventObject);
                    startActivity(new Intent(getActivity(), ProductClassifyActivity.class).putExtra("class_product", newBean.getClassX().get(position)));
                }
            }
        });

        // 职业选择
        iv_free = (ImageView) view.findViewById(R.id.iv_free);
        iv_student = (ImageView) view.findViewById(R.id.iv_student);
        iv_work = (ImageView) view.findViewById(R.id.iv_work);
        iv_enterprise = (ImageView) view.findViewById(R.id.iv_enterprise);

        iv_free.setOnClickListener(this);
        iv_student.setOnClickListener(this);
        iv_work.setOnClickListener(this);
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
                                        rc_adapter.setNewData(hotBean.getRecommends());
                                        banner.setData(hotBean.getAdvertising(), null);
                                        easylayout.refreshComplete();
                                    } else {
                                        banner.setData(hotBean.getAdvertising(), null);
                                        if(hotBean.getRecommends()!=null){
                                            rc_adapter.addData(hotBean.getRecommends());
                                        }
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
                        easylayout.refreshComplete();
                        ToastUtils.showToast(getActivity(), "网络异常");
                        super.onError(call, response, e);
                    }
                });
        //分类专题和新品
        OkGo.post(Urls.puk_URL + Urls.HOME_FRAGMENT.PRODUCT_LIST)
                .tag(getActivity())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (s != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                String success = jsonObject.getString("isSuccess");
                                if (success.equals("1")) {
                                    Gson gson = new Gson();
                                    newBean = gson.fromJson(s, News_ClassBean.class);
                                    List<News_ClassBean.ClassBean> list = newBean.getClassX();

                                    if(list.size()%2==1){
                                        News_ClassBean.ClassBean bean=new News_ClassBean.ClassBean();
                                        bean.setHome_image("http://orizavg5s.bkt.clouddn.com/classify_04.png");
                                        list.add(bean);
                                    }

                                    classify_recycler_adapter.setNewData(list);
                                    LogUtils.i("classX-size", newBean.getClassX().size());
                                    productAdapter.setNewData(newBean.getProduct());

                                } else {
                                    String msg = jsonObject.getString("msg");
                                    ToastUtils.showToast(getActivity(), msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            ToastUtils.showToast(getActivity(), "网络异常");
                        }
                    }

                });


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
                professional="xiaoyaoke";
                EventBus.getDefault().post(new IdentityProduct(3));
                MainActivity.navigationController.setSelect(1);
                break;
            case R.id.iv_student:
                professional="xueshengdang";
                EventBus.getDefault().post(new IdentityProduct(2));
                MainActivity.navigationController.setSelect(1);
                break;
            case R.id.iv_work:
                professional="shangbanzu";
                EventBus.getDefault().post(new IdentityProduct(1));
                MainActivity.navigationController.setSelect(1);
                break;
            case R.id.iv_enterprise:
                professional="qiyezhu";
                EventBus.getDefault().post(new IdentityProduct(4));
                MainActivity.navigationController.setSelect(1);
                break;
            case R.id.iv_notice:
                NoticeActivity.launch(getActivity());
                ivNotice.setImageResource(R.mipmap.icon_unnotice);
                break;

        }
        try {
            eventObject.put("职业", professional);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//记录事件
        ZhugeSDK.getInstance().track(getActivity(), "职业搜索",  eventObject);

    }


}
