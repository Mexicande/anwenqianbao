package cn.com.stableloan.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gt3unbindsdk.GT3GeetestButton;
import com.example.gt3unbindsdk.GT3GeetestUtils;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.stableloan.R;
import cn.com.stableloan.api.Urls;
import cn.com.stableloan.bean.CashEvent;
import cn.com.stableloan.bean.DescEvent;
import cn.com.stableloan.bean.IntegarlEvent;
import cn.com.stableloan.bean.ProcuctCollectionEvent;
import cn.com.stableloan.bean.UpdateEvent;
import cn.com.stableloan.bean.UpdateProfessionEvent;
import cn.com.stableloan.bean.UserEvent;
import cn.com.stableloan.interfaceutils.Touch_login;
import cn.com.stableloan.model.DesBean;
import cn.com.stableloan.model.InformationEvent;
import cn.com.stableloan.model.MessageCode;
import cn.com.stableloan.model.UserBean;
import cn.com.stableloan.model.UserInfromBean;
import cn.com.stableloan.model.WelfareBean;
import cn.com.stableloan.model.clsaa_special.Class_Special;
import cn.com.stableloan.ui.activity.ForgetWordActivity;
import cn.com.stableloan.ui.activity.HtmlActivity;
import cn.com.stableloan.ui.activity.MainActivity;
import cn.com.stableloan.utils.AppUtils;
import cn.com.stableloan.utils.EncryptUtils;
import cn.com.stableloan.utils.LogUtils;
import cn.com.stableloan.utils.SPUtils;
import cn.com.stableloan.utils.TimeUtils;
import cn.com.stableloan.utils.TinyDB;
import cn.com.stableloan.utils.ToastUtils;
import cn.com.stableloan.utils.aes.Des4;
import cn.com.stableloan.utils.editext.PowerfulEditText;
import cn.com.stableloan.utils.ras.RSA;
import cn.com.stableloan.view.dialog.Login_DeviceDialog;
import cn.com.stableloan.view.supertextview.SuperButton;
import okhttp3.Call;
import okhttp3.Response;

public class LoginFragment extends Fragment {

    GT3GeetestUtils gt3GeetestUtils;
    @Bind(R.id.et_phone)
    cn.com.stableloan.utils.editext.PowerfulEditText etPhone;
    @Bind(R.id.et_passWord)
    cn.com.stableloan.utils.editext.PowerfulEditText etPassWord;
    @Bind(R.id.tv_messagelogin)
    TextView tv_messagelogin;
    @Bind(R.id.tv_forgetPassWord)
    TextView tvForgetPassWord;
    @Bind(R.id.view_passWord)
    LinearLayout viewPassWord;
    @Bind(R.id.ll_btn_type)
    GT3GeetestButton llBtnType;
    @Bind(R.id.bt_login)
    SuperButton btLogin;
    private Login_DeviceDialog dialog;
    private boolean Atest = false;
    private boolean PowerfulEditText = false;

    private String gtcode = "";
    private String status = "2";
    private String AdressIp = "";

    private String times = "";
    private final int Flag_User = 3000;
    private final int LOTTERY_CODE = 500;
    private static final int TOKEN_FAIL = 120;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        gt3GeetestUtils = GT3GeetestUtils.getInstance(getActivity());
        ButterKnife.bind(this, view);
        initView();
        setGt3GeetestUtilsListener();
        long date = (long) SPUtils.get(getActivity(), "date", 1111111111111L);
        boolean today = TimeUtils.isToday(date);
        if (today) {
            llBtnType.setVisibility(View.VISIBLE);
        } else {
            Atest = true;
            SPUtils.remove(getActivity(), "date");
            llBtnType.setVisibility(View.GONE);
        }
        setListener();
        setVisibleInput(view);
        return view;

    }

    private void initView() {

        dialog = new Login_DeviceDialog(getActivity());
        dialog.setYesOnclickListener("知道了", new Login_DeviceDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                dialog.dismiss();
            }
        });

    }

    private void setVisibleInput(View view) {

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus().getWindowToken() != null) {
                        manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void setListener() {
        etPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPassWord.getText().toString().length() >= 6) {
                    if (!PowerfulEditText) {
                        PowerfulEditText = true;
                    } else {
                        btLogin.setEnabled(true);
                        btLogin.setUseShape();

                    }
                } else {
                    btLogin.setEnabled(false);
                    btLogin.setUseShape();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etPassWord.getText().toString().length() < 6) {
                    btLogin.setEnabled(false);
                    btLogin.setUseShape();
                }
            }
        });

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPhone.getText().toString().length() == 11) {
                    if (!PowerfulEditText) {
                        PowerfulEditText = true;
                    } else {
                        btLogin.setEnabled(true);
                        btLogin.setUseShape();
                    }
                } else {
                    btLogin.setEnabled(false);
                    btLogin.setUseShape();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setGt3GeetestUtilsListener() {


        gt3GeetestUtils.getGeetest(Urls.Ip_url + Urls.Login.captchaURL, Urls.Ip_url + Urls.Login.validateURL, null);
        gt3GeetestUtils.getGeetest(Urls.Ip_url + Urls.Login.captchaURL, Urls.Ip_url + Urls.Login.validateURL, null);
        String ip = (String) SPUtils.get(getActivity(), "ip", "");

        if (ip != null) {
            AdressIp = ip;
        }
        String unique = (String) SPUtils.get(getActivity(), "unique", "1212");
        LogUtils.i("ipAddress", AdressIp);
        times = unique;
        gt3GeetestUtils.setGtListener(new GT3GeetestUtils.GT3Listener() {
            /**
             * Api1可以在这添加参数
             */
            @Override
            public Map<String, String> captchaHeaders() {
                HashMap<String, String> params = new HashMap<>();
                params.put("ip", AdressIp);
                params.put("type", "mobile");
                params.put("unique", times);
                return params;
            }

            /**
             * 验证过程中有错误会走这里
             */
            @Override
            public void gt3DialogOnError(String error) {
                LogUtils.i("极验------", error);
                gt3GeetestUtils.getGeetest(Urls.Ip_url + Urls.Login.captchaURL, Urls.Ip_url + Urls.Login.validateURL, null);

            }

            /**
             * 点击验证码的关闭按钮来关闭验证码
             */
            @Override
            public void gt3CloseDialog() {

            }

            /**
             * 点击屏幕关闭验证码
             */
            @Override
            public void gt3CancelDialog() {

            }

            /**
             * 拿到二次验证需要的数据
             */
            @Override
            public void gt3GetDialogResult(String result) {


            }


            /**
             * 自定义二次验证，当gtSetIsCustom为ture时执行这里面的代码
             */

            @Override
            public void gt3GetDialogResult(boolean success, String result) {

                if (success) {
                    HashMap<String, String> params = null;
                    try {
                        params = new HashMap<>();
                        JSONObject jsonObject = new JSONObject(result);
                        params.put("ip", AdressIp);
                        params.put("type", "mobile");
                        params.put("unique", times);
                        params.put("g_challenge", jsonObject.getString("geetest_challenge"));
                        params.put("g_seccode", jsonObject.getString("geetest_seccode"));
                        params.put("g_validate", jsonObject.getString("geetest_validate"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONObject object = new JSONObject(params);
                    OkGo.post(Urls.Ip_url + Urls.Login.validateURL)
                            .tag(this)
                            .upJson(object)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, Call call, Response response) {
                                    Gson gson = new Gson();
                                    MessageCode messageCode = gson.fromJson(s, MessageCode.class);
                                    if (messageCode.getError_code() == 0) {
                                        Atest = true;
                                        status = "1";
                                        gt3GeetestUtils.gt3TestFinish();
                                        gtcode = messageCode.getData().getGtcode();
                                    } else {
                                        gt3DialogOnError("验证失败，请重新验证");

                                    }

                                }
                            });
                    /**
                     *  利用异步进行解析这result进行二次验证，结果成功后调用gt3GeetestUtils.gt3TestFinish()方法调用成功后的动画，然后在gt3DialogSuccess执行成功之后的结果
                     * //
                     //          JSONObject res_json = new JSONObject(result);
                     //
                     //                Map<String, String> validateParams = new HashMap<>();
                     //
                     //                validateParams.put("geetest_challenge", res_json.getString("geetest_challenge"));
                     //
                     //                validateParams.put("geetest_validate", res_json.getString("geetest_validate"));
                     //
                     //                validateParams.put("geetest_seccode", res_json.getString("geetest_seccode"));
                     在二次验证结果验证完成之后，执行gt3GeetestUtils.gt3TestFinish()方法进行动画执行
                     */

                }

            }

            /**
             * 第一次次请求后数据
             */
            @Override
            public void gt3FirstResult(JSONObject jsonObject) {

            }


            /**
             * 往二次验证里面put数据，是map类型,注意map的键名不能是以下三个：geetest_challenge，geetest_validate，geetest_seccode
             */
            @Override
            public Map<String, String> gt3SecondResult() {

                return null;
            }


            /**
             * 验证全部走完的回调，result为验证后的数据
             */
            //请求成功数据
            @Override
            public void gt3DialogSuccessResult(String result) {
//                Gt3GeetestTestMsg.setCandotouch(false);//这里设置验证成功后是否可以关闭

            }


            /**
             * 设置网络的头部信息
             */
            @Override
            public Map<String, String> validateHeaders() {
                return null;
            }


            /**
             * 设置是否自定义第二次验证ture为是 默认为false(不自定义)
             * 如果为false这边的的完成走gt3GetDialogResult(String result) ，后续流程SDK帮忙走完，不需要做操作
             * 如果为true这边的的完成走gt3GetDialogResult(boolean a, String result)，同时需要完成gt3GetDialogResult里面的二次验证，验证完毕记得关闭dialog,调用gt3GeetestUtils.gt3TestFinish();
             * 完成方法统一是gt3DialogSuccess
             */

            @Override
            public boolean gtSetIsCustom() {

                return true;
            }


            /**
             * 判断自定义按键是否被点击
             */

            @Override
            public void gtIsClick(boolean a) {
                if (a) {
                    //被点击
                }
            }

            /**
             * 当验证码放置10分钟后，重新启动验证码
             */
            @Override
            public void rege() {

//                gt3GeetestUtils.getGeetest(captchaURL,validateURL,null);

            }
        });

    }

    /**
     * 登陆
     */

    private KProgressHUD hud;

    private void loginUser() {


        String phone = AppUtils.getPhone(getActivity());
        String model = AppUtils.getModel();
        String androidVersion = AppUtils.getSDKVersion();

        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait.....")
                .setCancellable(true)
                .show();
        HashMap<String, String> params = new HashMap<>();
        String md5ToString = EncryptUtils.encryptMD5ToString(etPassWord.getText().toString());
        params.put("userphone", etPhone.getText().toString());
        params.put("password", md5ToString);
        params.put("gtcode", gtcode);
        params.put("status", status);
        params.put("unique", times);
        params.put("validatePhone", phone);
        params.put("device", model);
        params.put("version_number", "android " + androidVersion);
        JSONObject object = new JSONObject(params);
        String Deskey = null;
        String sign = null;
        String deskey = null;
        try {
            int random = new Random().nextInt(10000000) + 89999999;
            LogUtils.i("random", random);
            Deskey = Des4.encode(object.toString(), String.valueOf(random));
            String public_key = getResources().getString(R.string.public_key);

            deskey = RSA.encrypt(String.valueOf(random), Urls.PUCLIC_KEY+public_key);
            String private_key = getResources().getString(R.string.private_key);
            sign = RSA.sign(deskey, Urls.PRIVATE_KEY+private_key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DesBean bean = new DesBean();
        bean.setData(Deskey);
        bean.setDeskey(deskey);
        final Gson gson = new Gson();
        String json = gson.toJson(bean);

        OkGo.<String>post(Urls.NEW_Ip_url + Urls.Login.LOGIN)
                .tag(this)
                .headers("sign", sign)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        hud.dismiss();
                        UserInfromBean infromBean = gson.fromJson(s, UserInfromBean.class);
                        if (infromBean.getError_code() == 0) {
                            SPUtils.put(getActivity(), Urls.lock.TOKEN, infromBean.getData().getToken());
                            UserBean userBean = new UserBean();
                            userBean.setNickname(infromBean.getData().getNickname());
                            userBean.setUserphone(infromBean.getData().getUserphone());
                            SPUtils.put(getActivity(), infromBean.getData().getUserphone() + Urls.lock.LOGIN, Urls.lock.PW_VERIFICATION);

                            SPUtils.put(getActivity(), Urls.lock.USER_PHONE, infromBean.getData().getUserphone());
                            userBean.setIdentity(infromBean.getData().getIdentity());
                            EventBus.getDefault().post(new UserEvent(infromBean));
                            TinyDB tinyDB = new TinyDB(getActivity());
                            tinyDB.putObject(infromBean.getData().getUserphone(), userBean);
                            String from = getActivity().getIntent().getStringExtra("from");
                            WelfareBean.DataBean welfare = (WelfareBean.DataBean) getActivity().getIntent().getSerializableExtra("welfare");

                            Class_Special.DataBean.MdseBean id = (Class_Special.DataBean.MdseBean) getActivity().getIntent().getSerializableExtra("ProductClassifyActivity");

                            if (from != null) {
                                if (from.equals("user")) {
                                    getActivity().setResult(Flag_User, new Intent().putExtra("user", userBean));
                                    getActivity().finish();
                                } else if (from.equals("123")) {
                                    getActivity().setResult(LOTTERY_CODE, new Intent().putExtra("Loffery", "123"));
                                    getActivity().finish();
                                } else if (from.equals("user1")) {
                                    getActivity().setResult(4000, new Intent().putExtra("user", userBean));
                                    getActivity().finish();
                                } else if (from.equals("user2")) {
                                    getActivity().setResult(4000, new Intent().putExtra("user", userBean));
                                    getActivity().finish();
                                } else if (from.equals("collection")) {
                                    Intent intent = new Intent();
                                    intent.putExtra("ok", "ok");
                                    getActivity().setResult(2000, intent);
                                    getActivity().finish();
                                } else if ("error".equals(from)) {
                                    MainActivity.launch(getActivity());
                                    getActivity().finish();
                                } else if ("error_UserFragment".equals(from)) {
                                    EventBus.getDefault().post(new UpdateEvent("user"));
                                    getActivity().finish();
                                } else if ("CollectionError".equals(from)) {
                                    EventBus.getDefault().post(new ProcuctCollectionEvent("ok"));
                                    getActivity().finish();
                                } else if ("UserInformationError".equals(from)) {
                                    EventBus.getDefault().post(new InformationEvent("informationStatus"));
                                    getActivity().finish();
                                } else if ("ProductDescError".equals(from)) {
                                    String collection = getActivity().getIntent().getStringExtra("collection");
                                    EventBus.getDefault().post(new DescEvent(collection));
                                    getActivity().finish();
                                } else if ("CashError".equals(from)) {
                                    EventBus.getDefault().post(new CashEvent(1));
                                    getActivity().finish();
                                } else if ("IntegarlError".equals(from)) {
                                    EventBus.getDefault().post(new IntegarlEvent(1));
                                    getActivity().finish();
                                } else if ("UpImageError".equals(from)) {
                                    EventBus.getDefault().post(new InformationEvent("CardUpload"));
                                    getActivity().finish();

                                } else if ("IdentityError".equals(from)) {
                                    EventBus.getDefault().post(new InformationEvent("ok"));
                                    getActivity().finish();

                                } else if ("UpdataProfessionError".equals(from)) {
                                    EventBus.getDefault().post(new UpdateProfessionEvent(1));
                                    getActivity().finish();
                                } else if ("DeviceError".equals(from)) {
                                    Intent intent = new Intent();
                                    intent.putExtra("device", 1);
                                    getActivity().setResult(100, intent);
                                    getActivity().finish();
                                } else if ("DescError".equals(from)) {
                                    Intent intent = new Intent();
                                    intent.putExtra("desc", 1);
                                    getActivity().setResult(3000, intent);
                                    getActivity().finish();
                                } else if ("SafeDate".equals(from)) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Urls.TOKEN, 1);
                                    getActivity().setResult(TOKEN_FAIL, intent);
                                    getActivity().finish();
                                } else if ("Friend".equals(from)) {
                                    Intent intent = new Intent();
                                    intent.putExtra(Urls.TOKEN, 1);
                                    getActivity().setResult(TOKEN_FAIL, intent);
                                    getActivity().finish();
                                }

                            } else if (welfare != null) {
                                startActivity(new Intent(getActivity(), HtmlActivity.class).putExtra("welfare", welfare));
                                getActivity().finish();

                            } else if (id != null) {
                                startActivity(new Intent(getActivity(), HtmlActivity.class).putExtra("class", id));
                                getActivity().finish();

                            } else {
                                getActivity().finish();
                            }
                        } else if (infromBean.getError_code() == 1130) {
                            dialog.show();
                        } else {
                            long timeMillis = System.currentTimeMillis();
                            SPUtils.put(getActivity(), "date", timeMillis);
                            llBtnType.setVisibility(View.VISIBLE);
                            Atest = false;
                            gt3GeetestUtils.getGeetest(Urls.Ip_url + Urls.Login.captchaURL, Urls.Ip_url + Urls.Login.validateURL, null);
                            ToastUtils.showToast(getActivity(), infromBean.getError_message());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hud.dismiss();
                            }
                        });
                    }
                });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.tv_messagelogin, R.id.tv_forgetPassWord, R.id.bt_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_messagelogin:
                mCallback.showProByName(1);
                break;
            case R.id.tv_forgetPassWord:
                startActivity(new Intent(getActivity(),ForgetWordActivity.class).putExtra("from","login"));
                //ForgetWordActivity.launch(getActivity());
                break;
            case R.id.bt_login:
                if (Atest) {
                    AndPermission.with(getActivity())
                            .requestCode(200)
                            .permission(Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .callback(listener)
                            .start();
                } else {
                    ToastUtils.showToast(getActivity(), "为了你的账户安全，请点击按钮进行验证");
                }
                break;
        }
    }

    private Touch_login mCallback;


    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。

            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if (requestCode == 200) {
                // TODO ...

                loginUser();

            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            ToastUtils.showToast(getActivity(), "为了您的账号安全,请打开设备权限");
            if (requestCode == 200) {
                if ((AndPermission.hasAlwaysDeniedPermission(getActivity(), deniedPermissions))) {
                    AndPermission.defaultSettingDialog(getActivity(), 500).show();

                }
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null) {
            mCallback = (Touch_login) context;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 500: // 这个400就是上面defineSettingDialog()的第二个参数。
                // 你可以在这里检查你需要的权限是否被允许，并做相应的操作。
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    loginUser();
                } else {
                    ToastUtils.showToast(getActivity(), "获取权限失败");
                }
                break;
        }
    }

}
