package cn.com.stableloan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.stableloan.AppApplication;
import cn.com.stableloan.R;
import cn.com.stableloan.base.BaseActivity;
import cn.com.stableloan.interfaceutils.Touch_login;
import cn.com.stableloan.model.InformationEvent;
import cn.com.stableloan.model.UserBean;
import cn.com.stableloan.ui.adapter.MyViewPagerAdapter;
import cn.com.stableloan.ui.fragment.LoginFragment;
import cn.com.stableloan.ui.fragment.MessageFragment;
import cn.com.stableloan.utils.ActivityStackManager;
import cn.com.stableloan.utils.LogUtils;
import cn.com.stableloan.utils.ToastUtils;

public class LoginActivity extends BaseActivity implements Touch_login {


    private static final String[] CHANNELS = new String[]{"短信快捷登录", "账号登录"};
    @Bind(R.id.back)
    ImageView back;
    public static CommonNavigator commonNavigator ;
   private MagicIndicator loginMagicindicator;
    private FragmentManager mFragmentManager;

    private Fragment mCurrentFragment;


    private boolean Flag = false;
    private final int Flag_User = 3000;
    private final int LOTTERY_CODE = 500;
    private List<String> mDataList = Arrays.asList(CHANNELS);

    private FragmentContainerHelper mFragmentContainerHelper = new FragmentContainerHelper();

    public static void launch(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_login2);
        ButterKnife.bind(this);
        //AppApplication.addDestoryActivity(this, "login");
        transparentStatusBar();
        initView();
        initFragments();
        String message = getIntent().getStringExtra("message");
        if(message!=null){
            ToastUtils.showToast(this,message);
        }
        mFragmentContainerHelper.attachMagicIndicator(loginMagicindicator);
    }

     protected void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
//            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS); // 新增滑动返回，舍弃过渡动效

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initFragments() {
        loginMagicindicator= (MagicIndicator) findViewById(R.id.login_magicindicator);
        commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int i) {
                BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);

                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(i));
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#333333"));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchMenu(getFragmentName(i+1));
                        LogUtils.i("LogUtils----",i);

                    }
                });
                badgePagerTitleView.setInnerPagerTitleView(simplePagerTitleView);
                return badgePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setColors(Color.parseColor("#fd2021"));
                indicator.setLineWidth(UIUtil.dip2px(context, 100));
                indicator.setLineHeight(UIUtil.dip2px(context, 2));

                return indicator;
            }

        });
        loginMagicindicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer();
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerPadding(UIUtil.dip2px(this, 15));
        titleContainer.setDividerDrawable(getResources().getDrawable(R.drawable.simple_splitter));

        mFragmentContainerHelper.setInterpolator(new OvershootInterpolator(1.0f));
        mFragmentContainerHelper.setDuration(300);
    }

    private void initView() {
        mCurrentFragment = new MessageFragment();
        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction().add(R.id.fragment, mCurrentFragment).commitAllowingStateLoss();


    }

    private String getFragmentName(int menuId) {
        switch (menuId) {
            case 1:
               mFragmentContainerHelper.handlePageSelected(0);
                return MessageFragment.class.getName();
            case 2:
                mFragmentContainerHelper.handlePageSelected(1);
                return LoginFragment.class.getName();
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
            mFragmentManager.beginTransaction().add(R.id.fragment, fragment, fragmentName).commitAllowingStateLoss();
        }

        if (mCurrentFragment != null) {
            mFragmentManager.beginTransaction().hide(mCurrentFragment).commitAllowingStateLoss();
        }
        mCurrentFragment = fragment;
    }

    private long mLastBackTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String message = getIntent().getStringExtra("message");
            String from = getIntent().getStringExtra("from");
            if (from != null) {
                if (from.equals("user")) {
                    UserBean userBean = new UserBean();
                    setResult(Flag_User, new Intent().putExtra("user", userBean));
                    finish();
                } else if (from.equals("123")) {
                    setResult(LOTTERY_CODE, new Intent().putExtra("Loffery", "1"));
                    finish();
                } else if (from.equals("user1")) {
                    UserBean userBean = new UserBean();
                    setResult(4000, new Intent().putExtra("user", userBean));
                    finish();
                } else if (from.equals("user2")) {
                    EventBus.getDefault().post(new InformationEvent("user2"));
                    finish();
                }else {
                    if(message!=null){
                        int count = ActivityStackManager.getInstance().getCount();
                        LogUtils.i("ActivityStackManager===",count+"");
                        ActivityStackManager.getInstance().popAllActivity();
                        MainActivity.launch(this);
                        finish();
                    }else {
                        finish();
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event) ;
    }

    @OnClick(R.id.layout)
    public void onViewClicked() {
        String from = getIntent().getStringExtra("from");
        String message = getIntent().getStringExtra("message");

        if (from != null) {
            if (from.equals("user")) {
                UserBean userBean = new UserBean();
                setResult(Flag_User, new Intent().putExtra("user", userBean));
                finish();
            } else if (from.equals("123")) {
                setResult(LOTTERY_CODE, new Intent().putExtra("Loffery", "1"));
                finish();
            } else if (from.equals("user1")) {
                UserBean userBean = new UserBean();
                setResult(4000, new Intent().putExtra("user", userBean));
                finish();
            } else if (from.equals("user2")) {
                EventBus.getDefault().post(new InformationEvent("user2"));
                finish();
            }else {
                if(message!=null){
                    ActivityStackManager.getInstance().popAllActivity();

                    MainActivity.launch(this);
                    finish();
                }else {
                    finish();
                }
            }
        } else {
            finish();
        }
    }



    @Override
    public void showProByName(int index) {
        switchMenu(getFragmentName(index));
    }
}