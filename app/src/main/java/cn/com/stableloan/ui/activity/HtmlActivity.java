package cn.com.stableloan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.stableloan.R;
import cn.com.stableloan.api.DateStatisticsUtils;
import cn.com.stableloan.api.Urls;
import cn.com.stableloan.base.BaseActivity;
import cn.com.stableloan.model.Banner_HotBean;
import cn.com.stableloan.model.Product_DescBean;
import cn.com.stableloan.model.WelfareBean;
import cn.com.stableloan.model.WelfareShutBean;
import cn.com.stableloan.model.clsaa_special.Class_Special;
import cn.com.stableloan.ui.js.JsInteration;
import cn.com.stableloan.utils.DownAPKService;
import cn.com.stableloan.utils.NetworkUtils;
import cn.com.stableloan.utils.SPUtils;
import cn.com.stableloan.utils.ToastUtils;
import okhttp3.Call;
import okhttp3.Response;

public class HtmlActivity extends BaseActivity {

    @Bind(R.id.title_name)
    TextView titleName;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.web_progress_bar)
    ProgressBar mProgressBar;

    @Bind(R.id.web_container)
    FrameLayout mContainer;
    @Bind(R.id.error_content)
    LinearLayout errorContent;

    private WebView mWebView;
   private WelfareBean.DataBean welfare;


    public static void launch(Context context) {
        context.startActivity(new Intent(context, HtmlActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        ButterKnife.bind(this);
        mWebView = new WebView(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(lp);
        mContainer.addView(mWebView, 0);
        CheckInternet();
    }

    private void CheckInternet() {

        boolean available = NetworkUtils.isAvailable(this);
        if (available) {
            Product_DescBean desc = (Product_DescBean) getIntent().getSerializableExtra("product");
            if (desc != null) {
                DateStatisticsUtils.addApplyDate(this,String.valueOf(desc.getData().getId()));
                titleName.setText(desc.getData().getPname());
                ivBack.setVisibility(View.VISIBLE);
                String link = desc.getData().getLink();
                getDate(link);
            }
            Banner_HotBean.AdvertisingBean extra = (Banner_HotBean.AdvertisingBean) getIntent().getSerializableExtra("Advertising");
            if (extra != null) {

                titleName.setText(extra.getAdvername());
                ivBack.setVisibility(View.VISIBLE);
                getDate(extra.getApp());
            }
            Banner_HotBean.RecommendsBean hotbean = (Banner_HotBean.RecommendsBean) getIntent().getSerializableExtra("hotbean");
            if (hotbean != null) {

                titleName.setText(hotbean.getName());
                ivBack.setVisibility(View.VISIBLE);
                getDate(hotbean.getApp());
            }
            Product_DescBean desc1 = (Product_DescBean) getIntent().getSerializableExtra("Strate");
            if (desc1 != null) {
                titleName.setText(desc1.getData().getPlatformdetail().getPl_name() + "攻略");
                ivBack.setVisibility(View.VISIBLE);
                String url = desc1.getData().getRaiders_connection();
                getDate(url);
            }
            String advertising = getIntent().getStringExtra("advertising");
            if(advertising!=null){
                getDate(advertising);
            }
            welfare = (WelfareBean.DataBean) getIntent().getSerializableExtra("welfare");
            if (welfare != null) {
                titleName.setText(welfare.getName());
                ivBack.setVisibility(View.VISIBLE);
                getUrl(welfare.getId());
            }
            Class_Special.DataBean.MdseBean aClass = (Class_Special.DataBean.MdseBean) getIntent().getSerializableExtra("class");
            if(aClass!=null){
                DateStatisticsUtils.addApplyDate(this,String.valueOf(aClass.getId()));

                titleName.setText(aClass.getMdse_name());
                getDate(aClass.getMdse_h5_link());
            }
            String safe = getIntent().getStringExtra("safe");
            if(safe!=null){
                titleName.setText("安全小贴士");
                getDate(Urls.HTML_URL+safe);
            }
        } else {
            ToastUtils.showToast(this,"网络异常");
        }
    }

    private void getUrl(int id) {
        String token = (String) SPUtils.get(this, "token", "1");
        HashMap<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("id", String.valueOf(id));
        final JSONObject jsonObject = new JSONObject(params);
        OkGo.post(Urls.Ip_url + Urls.LOTTERY.Welfare)
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!s.isEmpty()) {
                            Gson gson = new Gson();
                            WelfareShutBean shutBean = gson.fromJson(s, WelfareShutBean.class);
                            if (shutBean.getError_code() == 0) {
                                errorContent.setVisibility(View.GONE);
                                getDate(shutBean.getData().getLink());
                            } else {
                                errorContent.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });


    }

    private void getDate(String url) {
      // url="https://b.jianbing.com/hd/20170913_jdhh_2?channel=xjd51";
        if (url != null) {
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setBlockNetworkImage(false);
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            webSettings.setGeolocationEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setDatabaseEnabled(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setSupportZoom(false);
            webSettings.setNeedInitialFocus(false);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setBuiltInZoomControls(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mWebView.getSettings().setMixedContentMode(
                        WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            mWebView.loadUrl(url);
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (parseScheme(url)) {

                    }else {

                        WebView.HitTestResult hitTestResult = view.getHitTestResult();
                        if (!TextUtils.isEmpty(url) && hitTestResult == null) {
                            view.loadUrl(url);
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();

                }
            });

            mWebView.setWebChromeClient(new MyWebChromeClient());
            mWebView.addJavascriptInterface(new JsInteration(), "android");

        }
    }
    public boolean parseScheme(String url) {
        if (url.contains("platformapi/startapp")) {
            try {
                Uri uri = Uri.parse(url);
                Intent intent;
                intent = Intent.parseUri(url,
                        Intent.URI_INTENT_SCHEME);
                intent.addCategory("android.intent.category.BROWSABLE");
                intent.setComponent(null);
                startActivity(intent);
            } catch (Exception e) {
                ToastUtils.showToast(this, "请安装最新版支付宝");
            }
            return true;
        } else if (url.contains("qqapi")) {
            try {
                Uri uri = Uri.parse(url);
                Intent intent;
                intent = Intent.parseUri(url,
                        Intent.URI_INTENT_SCHEME);
                intent.addCategory("android.intent.category.BROWSABLE");
                intent.setComponent(null);
                startActivity(intent);
            } catch (Exception e) {
                ToastUtils.showToast(this, "请安装最新版腾讯QQ");
            }
            return true;
        } else if (url.contains("tmast://appdetails?")) {
            try {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(HtmlActivity.this, "请安装最新版应用宝", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if(url.endsWith(".apk")){
            downloadApk(url);
            return true;

        }
        else {
            return false;
        }
    }

    private void downloadApk(String url) {

        Intent intent = new Intent(this, DownAPKService.class);
        intent.putExtra("apk_url",url);
        startService(intent);
    }

    @OnClick({R.id.iv_back, R.id.web_container})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.web_container:
                if(welfare!=null){
                    getUrl(welfare.getId());
                }
                break;
        }
    }


    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                if (mProgressBar.getVisibility() != View.VISIBLE)
                    mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContainer.removeAllViews();
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
    }

}
