package cn.com.stableloan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.gson.Gson;
import com.meituan.android.walle.WalleChannelReader;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.stableloan.R;
import cn.com.stableloan.api.Urls;
import cn.com.stableloan.base.BaseActivity;
import cn.com.stableloan.model.InformationEvent;
import cn.com.stableloan.model.UpdateInfoBean;
import cn.com.stableloan.model.UserBean;
import cn.com.stableloan.ui.fragment.HomeFragment;
import cn.com.stableloan.ui.fragment.LotteryFragment;
import cn.com.stableloan.ui.fragment.ProductFragment;
import cn.com.stableloan.ui.fragment.UserFragment;
import cn.com.stableloan.utils.LogUtils;
import cn.com.stableloan.utils.SPUtils;
import cn.com.stableloan.utils.ToastUtils;
import cn.com.stableloan.view.NormalItemView;
import cn.com.stableloan.view.update.AppUpdateUtils;
import cn.com.stableloan.view.update.CProgressDialogUtils;
import cn.com.stableloan.view.update.OkGoUpdateHttpUtil;
import cn.com.stableloan.view.update.UpdateAppBean;
import cn.com.stableloan.view.update.UpdateAppManager;
import cn.com.stableloan.view.update.UpdateCallback;
import ezy.boost.update.IUpdateParser;
import ezy.boost.update.OnFailureListener;
import ezy.boost.update.UpdateError;
import ezy.boost.update.UpdateInfo;
import ezy.boost.update.UpdateManager;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class MainActivity extends BaseActivity implements ProductFragment.BackHandlerInterface{

    @Bind(R.id.tab)
    PageBottomTabLayout tab;
    // public  最好使用private /set get来获取
    private int NewVersionCode=0;

    public static NavigationController navigationController;
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private static final int FLAG_LOGIN = 3;
    private static final int SEND_LOGIN = 3000;
    private static final int LOTTERY_CODE = 500;
    private static final int LOTTERY_SNED = 5000;

    String content="1231313";

    public static final String EXIST = "exist";

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        ZhugeSDK.getInstance().init(getApplicationContext());
        updateDiy();
        initView();
    }
    public void updateDiy() {
        NewVersionCode = AppUpdateUtils.getVersionCode(this);
//      String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String  channel = WalleChannelReader.getChannel(this.getApplicationContext());
        Map<String, String> params = new HashMap<String, String>();
        params.put("url", channel);
        new UpdateAppManager
                .Builder()
                //必须设置，当前Activity
                .setActivity(this)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(new OkGoUpdateHttpUtil())
                //必须设置，更新地址
                .setUpdateUrl(Urls.Update.APP_UPDATA)
                //以下设置，都是可选
                //设置请求方式，默认get
                .setPost(true)
                //不显示通知栏进度条
//                .dismissNotificationProgress()
                //是否忽略版本
//                .showIgnoreVersion()
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
                .setParams(params)
                //设置点击升级后，消失对话框，默认点击升级后，对话框显示下载进度
                .hideDialogOnDownloading(false)
                //设置头部，不设置显示默认的图片，设置图片后自动识别主色调，然后为按钮，进度条设置颜色
                //为按钮，进度条设置颜色。
                //.setThemeColor(0xffffac5d)
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
//                .setTargetPath(path)
                //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
//                .setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {

                        UpdateAppBean updateAppBean = new UpdateAppBean();

                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int size = jsonObject.getInt("size");
                            Double i = (double) size / 1024/1024;
                            DecimalFormat df = new DecimalFormat("0.0");
                            String format = df.format(i);
                            int versioncode = jsonObject.getInt("versioncode");
                            String update="No";
                            if(versioncode>NewVersionCode){
                                update="Yes";
                            }
                            updateAppBean
                                    //（必须）是否更新Yes,No
                                    .setUpdate(update)
                                    //（必须）新版本号，
                                    .setNewVersion(jsonObject.getString("versionname"))
                                    //（必须）下载地址
                                    .setApkFileUrl(jsonObject.getString("url"))
                                    //测试下载路径是重定向路径
//                                    .setApkFileUrl("http://openbox.mobilem.360.cn/index/d/sid/3282847")
                                    //（必须）更新内容
//                                    .setUpdateLog(jsonObject.optString("update_log"))
                                    //测试内容过度
//                                    .setUpdateLog("测试")
                                    .setUpdateLog(jsonObject.getString("updatecontent"))
//                                    .setUpdateLog("今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说相对于其他行业来说今天我们来聊一聊程序员枯燥的编程生活，相对于其他行业来说\r\n")
                                    //大小，不设置不显示大小，可以不设置

                                    .setTargetSize(String.valueOf(format)+"M")
                                    //是否强制更新，可以不设置
                                    .setConstraint(jsonObject.getBoolean("isForce"))
                                    //设置md5，可以不设置
                                    .setNewMd5(jsonObject.getString("md5"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return updateAppBean;
                    }

                    @Override
                    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        updateAppManager.showDialogFragment();
                    }
                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        //CProgressDialogUtils.showProgressDialog(MainActivity.this);
                    }
                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(MainActivity.this);
                    }
                });

    }




    private String s="";
    public String getVersionCode(Context context){
        PackageManager packageManager=context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode="";
        try {
            packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
            versionCode=packageInfo.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
    private void initView() {


        mCurrentFragment = new HomeFragment();
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.app_item, mCurrentFragment).commitAllowingStateLoss();
        navigationController = tab.custom()
                .addItem(newItem(R.mipmap.ic_home_defaual, R.mipmap.ic_home_down,"首页"))
                .addItem(newItem(R.mipmap.ic_product_on, R.mipmap.ic_product_down,"贷款"))
                .addItem(newItem(R.mipmap.ic_lottery_on, R.mipmap.ic_lottery_down,"福利"))
                .addItem(newItem(R.mipmap.ic_my_defual, R.mipmap.ic_my_down,"我的"))
                .build();

        navigationController.addTabItemSelectedListener(listener);


    }

    @Subscribe
    public void onMessageEvent(InformationEvent event){
        if(event.message.equals("user2")){
            navigationController.setSelect(0);
        }else if(event.message.equals("user4")){
            navigationController.setSelect(3);
        }else if(event.message.equals("user3")){
            int selected = navigationController.getSelected();
            switchMenu(getFragmentName(selected+1));
        }else if(event.message.equals("userinform")){
            navigationController.setSelect(3);
        }else if(event.message.equals("userinfor")){
            navigationController.setSelect(3);
        }
    }

    OnTabItemSelectedListener listener = new OnTabItemSelectedListener() {
        @Override
        public void onSelected(int index, int old) {
            if(index==3){
                String token = (String) SPUtils.get(MainActivity.this, Urls.lock.TOKEN, "1");
                LogUtils.i("token",token+"---");
                if ("1".equals(token)) {
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class).putExtra("from", "user"), FLAG_LOGIN);
                }else {
                    switchMenu(getFragmentName(4));
                }
            }else {
                switchMenu(getFragmentName(index + 1));
            }
        }
        @Override
        public void onRepeat(int index) {


        }
    };

    private String getFragmentName(int menuId) {
        switch (menuId) {
            case 1:
                return HomeFragment.class.getName();
            case 2:
                return ProductFragment.class.getName();
            case 3:
                return LotteryFragment.class.getName();
            case 4:
                return UserFragment.class.getName();
            default:
                return null;
        }
    }

    private void switchMenu(String fragmentName) {

        Fragment fragment = mFragmentManager.findFragmentByTag(fragmentName);

        if (fragment != null) {
            if (fragment == mCurrentFragment) return;
            mFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss();
        } else {
            fragment = Fragment.instantiate(this, fragmentName);
            mFragmentManager.beginTransaction().add(R.id.app_item, fragment, fragmentName).commitAllowingStateLoss();
        }

        if (mCurrentFragment != null) {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).commitAllowingStateLoss();
        }
        mCurrentFragment = fragment;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case FLAG_LOGIN:
                if (SEND_LOGIN == resultCode) {
                    UserBean user = (UserBean) data.getSerializableExtra("user");
                    if(user!=null&&user.getNickname()!=null){
                        switchMenu(getFragmentName(4));
                    }else {
                        navigationController.setSelect(0);
                    }
                }
                break;
            case LOTTERY_SNED:
                if (LOTTERY_CODE == resultCode) {
                    String loffery = data.getStringExtra("Loffery");
                    if (!loffery.equals("1")) {
                        switchMenu(getFragmentName(3));
                    }else {
                       navigationController.setSelect(0);
                    }

                }
                break;

        }

    }

    //创建一个Item
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text){
        NormalItemView normalItemView = new NormalItemView(this);
        normalItemView.initialize(drawable,checkedDrawable,text);
        normalItemView.setTextDefaultColor(Color.GRAY);
        normalItemView.setTextCheckedColor(Color.rgb(253,32,33));
        return normalItemView;
    }
    private long mLastBackTime = 0;
    @Override
    public void onBackPressed() {
      /*  ProductFragment productFragment=new ProductFragment();
        if(productFragment!=null){
            SlideUp slideUp = productFragment.slideUp;
            if(slideUp!=null){
            }
            boolean visible = ProductFragment.slideUp.isVisible();
            if(visible){
                ProductFragment.slideUp.hide();
            }else {
                if ((System.currentTimeMillis() - mLastBackTime) < 1000) {
                    finish();
                } else {
                    mLastBackTime = System.currentTimeMillis();
                    ToastUtils.showToast(this, "再按一次退出");
                }
            }
        }else {
            if ((System.currentTimeMillis() - mLastBackTime) < 1000) {
                finish();
            } else {
                mLastBackTime = System.currentTimeMillis();
                ToastUtils.showToast(this, "再按一次退出");
            }
        }*/
        if(selectedFragment!=null ) {
            boolean visible = ProductFragment.slideUp.isVisible();
                if(visible){
                    ProductFragment.slideUp.hide();
                }else {
                    if ((System.currentTimeMillis() - mLastBackTime) < 1000) {
                        finish();
                    } else {
                        mLastBackTime = System.currentTimeMillis();
                        ToastUtils.showToast(this, "再按一次退出");
                    }
                }
        }else {

            if ((System.currentTimeMillis() - mLastBackTime) < 1000) {
                finish();
            } else {
                mLastBackTime = System.currentTimeMillis();
                ToastUtils.showToast(this, "再按一次退出");
            }
        }


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZhugeSDK.getInstance().flush(getApplicationContext());
        EventBus.getDefault().unregister(this);


    }
    private ProductFragment selectedFragment;
    @Override
    public void setSelectedFragment(ProductFragment backHandledFragment) {
        this.selectedFragment = backHandledFragment;
    }

}


