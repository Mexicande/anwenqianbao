package cn.com.stableloan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.gson.Gson;
import com.mancj.slideup.SlideUp;
import com.meituan.android.walle.WalleChannelReader;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.stableloan.R;
import cn.com.stableloan.api.Urls;
import cn.com.stableloan.base.BaseActivity;
import cn.com.stableloan.bean.UpdateEvent;
import cn.com.stableloan.model.InformationEvent;
import cn.com.stableloan.model.UpdateInfoBean;
import cn.com.stableloan.model.UserBean;
import cn.com.stableloan.ui.fragment.HomeFragment;
import cn.com.stableloan.ui.fragment.LotteryFragment;
import cn.com.stableloan.ui.fragment.ProductFragment;
import cn.com.stableloan.ui.fragment.UserFragment;
import cn.com.stableloan.ui.fragment.UserInformationFragment;
import cn.com.stableloan.utils.AppUtils;
import cn.com.stableloan.utils.LogUtils;
import cn.com.stableloan.utils.SPUtils;
import cn.com.stableloan.utils.ToastUtils;
import cn.com.stableloan.view.NormalItemView;
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
    public static NavigationController navigationController;

    private FragmentManager mFragmentManager;

    private Fragment mCurrentFragment;



    private static final int FLAG_LOGIN = 3;
    private static final int SEND_LOGIN = 3000;

    private static final int LOTTERY_CODE = 500;
    private static final int LOTTERY_SNED = 5000;

    String content="1231313";
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
        VisionTest();
        initView();
    }

    private void VisionTest() {
        String  channel = WalleChannelReader.getChannel(this.getApplicationContext());

        UpdateManager.create(this).setUrl(Urls.Update.APP_UPDATA).setPostData(Urls.Update.value+channel).setParser(new IUpdateParser() {
            @Override
            public UpdateInfo parse(String source) throws Exception {

                LogUtils.i("source",source);
                UpdateInfo info = new UpdateInfo();
                Gson gson=new Gson();
                UpdateInfoBean infoBean = gson.fromJson(source, UpdateInfoBean.class);
                LogUtils.i("Update",infoBean.toString());
                int code =Integer.parseInt(getVersionCode(MainActivity.this));
                LogUtils.i("code",code);
                if(Integer.parseInt(infoBean.getVersionCode())>code){
                    info.hasUpdate = true;
                }else {
                    info.hasUpdate = false;
                }
                info.updateContent =infoBean.getUpdateContent();
                info.versionCode = Integer.parseInt(infoBean.getVersionCode());
                info.versionName = infoBean.getVersionName();
                info.url = infoBean.getUrl();
                info.md5 =infoBean.getMd5();
                info.size = infoBean.getSize();
                info.isForce = infoBean.isForce();
                info.isIgnorable = infoBean.isIgnorable();
                info.isAutoInstall=infoBean.isAutoInstall();
                info.isSilent =  false;
                info.maxTimes=infoBean.getMaxTimes();
                return info;
            }
        }).setManual(true).setManual(true).setWifiOnly(false).setOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(UpdateError error) {
                LogUtils.i("update",error.getMessage());
            }
        }).check();

    }
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
           // switchMenu(getFragmentName(3));
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
                Boolean login = (Boolean) SPUtils.get(MainActivity.this, "login", false);
                if (!login) {
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
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
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


