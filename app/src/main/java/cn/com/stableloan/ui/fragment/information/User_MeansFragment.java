package cn.com.stableloan.ui.fragment.information;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.zaaach.citypicker.CityPickerActivity;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.stableloan.R;
import cn.com.stableloan.api.Urls;
import cn.com.stableloan.bean.CameraEvent;
import cn.com.stableloan.model.CardBean;
import cn.com.stableloan.model.Identity;
import cn.com.stableloan.model.InformationEvent;
import cn.com.stableloan.model.UserBean;
import cn.com.stableloan.ui.activity.Camera2Activity;
import cn.com.stableloan.ui.activity.CameraActivity;
import cn.com.stableloan.ui.activity.GestureLoginActivity;
import cn.com.stableloan.ui.activity.LoginActivity;
import cn.com.stableloan.ui.activity.SafeActivity;
import cn.com.stableloan.ui.activity.Verify_PasswordActivity;
import cn.com.stableloan.ui.activity.integarl.DateChangeActivity;
import cn.com.stableloan.ui.activity.integarl.UpImageIdentityActivity;
import cn.com.stableloan.utils.CommonUtils;
import cn.com.stableloan.utils.LogUtils;
import cn.com.stableloan.utils.RegexUtils;
import cn.com.stableloan.utils.SPUtils;
import cn.com.stableloan.utils.TinyDB;
import cn.com.stableloan.utils.ToastUtils;
import cn.com.stableloan.utils.cache.ACache;
import cn.com.stableloan.view.RoundButton;
import cn.com.stableloan.view.pickerview.PickerViewUtils;
import cn.com.stableloan.view.supertextview.SuperTextView;
import okhttp3.Call;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class User_MeansFragment extends Fragment {


    @Bind(R.id.et_name)
    SuperTextView etName;
    @Bind(R.id.user_phone)
    SuperTextView userPhone;
    @Bind(R.id.et_IDCard)
    SuperTextView etIDCard;
    @Bind(R.id.et_Sex)
    SuperTextView etSex;
    @Bind(R.id.et_Age1)
    SuperTextView etAge1;
    @Bind(R.id.et_Address)
    SuperTextView etAddress;
    @Bind(R.id.et_Marriage)
    SuperTextView etMarriage;
    @Bind(R.id.et_City)
    SuperTextView etCity;
    @Bind(R.id.et_Contact_name)
    SuperTextView etContactName;
    @Bind(R.id.et_Contact1)
    SuperTextView etContact1;
    @Bind(R.id.et_Between1)
    SuperTextView etBetween1;
    @Bind(R.id.et_Contact_name2)
    SuperTextView etContactName2;
    @Bind(R.id.et_Contact)
    SuperTextView etContact;
    @Bind(R.id.et_Between2)
    SuperTextView etBetween2;
    @Bind(R.id.save)
    RoundButton save;
    @Bind(R.id.layout_camera)
    SuperTextView layoutCamera;

    private ACache aCache;
    private UserBean user;

    private List<String> marrieList;
    private List<String> relationList;
    private String[] list1;
    private String[] list;
    private File mFile;


    private static final int REQUEST_CODE_PICK_CITY = 0;

    private Identity.DataBean.IdentityBean identityBean;


    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();


    public User_MeansFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user__means, container, false);
        ButterKnife.bind(this, view);
        aCache = ACache.get(getActivity());
        EventBus.getDefault().register(this);
        getDate();
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        initSpinner();
        setListenter();
        return view;
    }

    private void initSpinner() {
        list1 = getResources().getStringArray(R.array.spingarr);
        marrieList = Arrays.asList(list1);

        list = getResources().getStringArray(R.array.between);

        relationList = Arrays.asList(list);

    }


    /**
     * 诸葛io
     */
    private void setListenter() {

        JSONObject eventObject = new JSONObject();
        try {
            eventObject.put("persmaterials1", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//记录事件
        ZhugeSDK.getInstance().track(getActivity(), "身份信息", eventObject);

        etCity.setFocusableInTouchMode(false);
        etCity.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView v) {
                startActivityForResult(new Intent(getActivity(), CityPickerActivity.class),
                        REQUEST_CODE_PICK_CITY);
            }
        });

        etMarriage.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                PickerViewUtils.setPickerView(etMarriage, marrieList, getActivity(), "婚姻状况");

            }
        });
        etBetween1.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                PickerViewUtils.setPickerView(etBetween1, relationList, getActivity(), "与您的关系");
            }
        });
        etBetween2.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                PickerViewUtils.setPickerView(etBetween2, relationList, getActivity(), "与您的关系");
            }
        });
        // 城市定位
        etCity.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                getLocationPermission();
            }
        });
        //相机拍照
        layoutCamera.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                getCameraPermission();

            }
        });
        //联系人数据

            etContactName.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
                @Override
                public void onClickListener(SuperTextView superTextView) {
                    getPermission(1);

                }
            });
            etContactName2.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
                @Override
                public void onClickListener(SuperTextView superTextView) {
                    getPermission(2);

                }
            });

        setSuperText(etName, Urls.DateChange.NAME);
        setSuperText(etIDCard, Urls.DateChange.IDCARD);
        setSuperText(etAge1, Urls.DateChange.AGE);
        setSuperText(etContact, Urls.DateChange.CONTACT_PHONE1);
        setSuperText(etContact1, Urls.DateChange.CONTACT_PHONE2);
        setSuperText(etAddress, Urls.DateChange.ADDRESS);


    }

    private void setSuperText(SuperTextView superText, int type) {
        superText.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                Intent intent = new Intent(getActivity(), DateChangeActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("hint",superText.getRightString());
                startActivityForResult(intent, 10000);
            }
        });

    }


    private void getCameraPermission() {
        AndPermission.with(getActivity())
                .requestCode(500)
                .permission(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .callback(listener)
                .start();

    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if (requestCode == 500) {
                Intent intent = new Intent();
                mFile = CommonUtils.createImageFile("mFile");
                //文件保存的路径和名称
                intent.putExtra("file", mFile.toString());
                //拍照时的提示文本
                intent.putExtra("hint", "请将证件放入框内。将裁剪图片，只保留框内区域的图像");
                //是否使用整个画面作为取景区域(全部为亮色区域)
                intent.putExtra("hideBounds", false);
                //最大允许的拍照尺寸（像素数）
                intent.putExtra("maxPicturePixels", 3840 * 2160);
                //startActivityForResult(intent, AppApplication.TAKE_PHOTO_CUSTOM);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    intent.setClass(getActivity(), Camera2Activity.class);
                    startActivity(intent);
                }else {
                    intent.setClass(getActivity(), CameraActivity.class);
                    startActivity(intent);
                    //ToastUtils.showToast(getActivity(),"暂时不支持"+ Build.VERSION.SDK_INT);
                }
            }

        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == 500) {
                // TODO ...
                ToastUtils.showToast(getActivity(),"获取权限失败，请在设置中手动添加拍照权限");

            }
        }
    };

    public static int IdNOToAge(String IdNO) {
        int leh = IdNO.length();
        String dates = "";
        if (leh == 18) {
            dates = IdNO.substring(6, 10);
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            String year = df.format(new Date());
            int u = Integer.parseInt(year) - Integer.parseInt(dates);
            return u;
        } else {
            dates = IdNO.substring(6, 8);

            return Integer.parseInt(dates);
        }

    }

    private void getDate() {
        String userphone = (String) SPUtils.get(getActivity(), Urls.lock.USER_PHONE, "1");

        TinyDB tinyDB = new TinyDB(getActivity());

        user = (UserBean) tinyDB.getObject(userphone, UserBean.class);

        userPhone.setRightString(user.getUserphone());

        Map<String, String> parms = new HashMap<>();
        String token = (String) SPUtils.get(getActivity(), "token", "1");
        String signature = (String) SPUtils.get(getActivity(), "signature", "1");
        parms.put("token", token);
        parms.put("signature", signature);
        parms.put("source", "");

        JSONObject jsonObject = new JSONObject(parms);
        OkGo.<String>post(Urls.Ip_url + Urls.Identity.GetIdentity)
                .tag(getActivity())
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        if (s != null) {
                            Gson gson = new Gson();
                            Identity identity = gson.fromJson(s, Identity.class);
                            if (identity.getError_code() == 0) {
                                if (identity.getData().getIsSuccess().equals("1")) {
                                    if (identity.getData().getStatus().equals("1")) {
                                        identityBean = identity.getData().getIdentity();
                                        etName.setRightString(identityBean.getName());
                                        etIDCard.setRightString(identityBean.getIdcard());
                                        String sex = identityBean.getSex();
                                        if ("0".equals(sex)) {
                                            etSex.setRightString("女");
                                        } else if ("1".equals(sex)) {
                                            etSex.setRightString("男");
                                        }
                                        etAge1.setRightString(identityBean.getAge());

                                        etAddress.setRightString(identityBean.getIdaddress());
                                        String marriage = identityBean.getMarriage();
                                        if ("0".equals(marriage)) {
                                            etMarriage.setRightString("未婚");
                                        } else if ("1".equals(marriage)) {
                                            etMarriage.setRightString("已婚");
                                        }
                                        etCity.setRightString(identityBean.getCity());
                                        Identity.DataBean.IdentityBean.ContactBean bean = identityBean.getContact().get(0);

                                        etContact1.setRightString(bean.getUserphone());

                                        etContactName.setRightString(bean.getContact());

                                        String bet = bean.getRelation();

                                        if (!bet.isEmpty()) {
                                            int i2 = Integer.parseInt(bet);
                                            etBetween1.setRightString(list[i2]);
                                        }

                                        Identity.DataBean.IdentityBean.ContactBean bean1 = identityBean.getContact().get(1);

                                        etContact.setRightString(bean1.getUserphone());
                                        etContactName2.setRightString(bean1.getContact());

                                        String bet2 = bean1.getRelation();

                                        if (!bet2.isEmpty()) {
                                            int i1 = Integer.parseInt(bet2);
                                            etBetween2.setRightString(list[i1]);
                                        }
                                    } else {

                                        Intent intent = new Intent(getActivity(), Verify_PasswordActivity.class).putExtra("from", "UserInformation");
                                        startActivity(intent);

                                      /*  if (gesturePassword == null || "".equals(gesturePassword)) {
                                            Intent intent = new Intent(getActivity(), Verify_PasswordActivity.class).putExtra("from", "UserInformation");
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getActivity(), GestureLoginActivity.class).putExtra("from", "UserInformation");
                                            startActivity(intent);
                                        }*/

                                    }
                                }
                            }else if(identity.getError_code()==2){
                                Intent intent=new Intent(getActivity(),LoginActivity.class);
                                intent.putExtra("message",identity.getError_message());
                                intent.putExtra("from","IdentityError");
                                startActivity(intent);

                            } else {
                                ToastUtils.showToast(getActivity(), identity.getError_message());
                            }
                        }

                    }
                });

    }


    @Subscribe
    public void onMessageEvent(InformationEvent event) {
        String message = event.message;
        if ("ok".equals(message)) {
            getDate();
        }
    }

    @Subscribe
    public void onCameraEvent(CameraEvent event) {
        CardBean.OutputsBean.OutputValueBean.DataValueBean value = event.value;
        if (value != null) {
            if (value.getName() != null) {
                etName.setRightString(value.getName());
            }
            if (value.getNum() != null) {
                etIDCard.setRightString(value.getNum());

            }
            if (value.getSex() != null) {
                etSex.setRightString(value.getSex());

            }
            if (value.getAddress() != null) {
                etAddress.setRightString(value.getAddress());
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({ R.id.save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save:
                if(identityBean!=null){
                    saveDate();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    } else {
                        //处理返回的data,获取选择的联系人信息
                        Uri uri = data.getData();
                        String[] contacts = getPhoneContacts(uri);
                        if (contacts != null && contacts.length > 1) {
                            etContact1.setRightString(contacts[1]);
                        } else {
                            ToastUtils.showToast(getActivity(), "获取号码失败，请手动添加");
                        }
                        if (contacts != null) {
                            etContactName.setRightString(contacts[0]);

                        } else {


                            ToastUtils.showToast(getActivity(), "获取联系人失败，请手动添加");
                        }
                    }
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    } else {
                        //处理返回的data,获取选择的联系人信息
                        Uri uri = data.getData();
                        String[] contacts = getPhoneContacts(uri);
                        if (contacts != null && contacts.length > 1) {
                            etContact.setRightString(contacts[1]);

                        } else {
                            ToastUtils.showToast(getActivity(), "获取号码失败，请手动添加");
                        }
                        if (contacts != null) {
                            etContactName2.setRightString(contacts[0]);

                        } else {
                            ToastUtils.showToast(getActivity(), "获取联系人失败，请手动添加");

                        }
                    }
                }
                break;
            case REQUEST_CODE_PICK_CITY:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                        etCity.setRightString(city);
                    }
                }
                break;

            case 10000:
                switch (resultCode){
                    case Urls.DateChange.NAME:
                        if(data!=null){
                            String type = data.getStringExtra("type");
                            LogUtils.i("name===",type);
                            etName.setRightString(type);
                        }
                        break;
                    case Urls.DateChange.AGE:
                        if(data!=null){
                            String type = data.getStringExtra("type");
                            LogUtils.i("name===",type);
                            etAge1.setRightString(type);
                        }
                        break;
                    case Urls.DateChange.IDCARD:
                        if(data!=null){
                            String type = data.getStringExtra("type");
                            LogUtils.i("name===",type);
                            StringBuilder sb = new StringBuilder(type);
                            StringBuilder replace = sb.replace(10, 15, "***");
                            etIDCard.setRightString(replace);
                            if (type.length() == 18) {
                                boolean idCard18 = RegexUtils.isIDCard18(type);
                                if (idCard18) {
                                    char c = type.charAt(16);
                                    int i = Integer.parseInt(String.valueOf(c));
                                    if (i % 2 == 0) {
                                        etSex.setRightString("女");
                                    } else {
                                        etSex.setRightString("男");
                                    }
                                    int age = IdNOToAge(type);
                                    etAge1.setRightString(String.valueOf(age));
                                }
                            }
                        }
                        break;
                    case Urls.DateChange.CONTACT_PHONE1:
                        if(data!=null){
                            String type = data.getStringExtra("type");
                            LogUtils.i("name===",type);
                            etContact.setRightString(type);
                        }
                        break;
                    case Urls.DateChange.CONTACT_PHONE2:
                        if(data!=null){
                            String type = data.getStringExtra("type");
                            LogUtils.i("name===",type);
                            etContact1.setRightString(type);
                        }
                        break;
                    case Urls.DateChange.ADDRESS:
                        if(data!=null){
                            String type = data.getStringExtra("type");
                            LogUtils.i("name===",type);
                            etAddress.setRightString(type);
                        }
                        break;
                }
                break;


        /*    case AppApplication.TAKE_PHOTO_CUSTOM:
                mFile = new File(data.getStringExtra("file"));
                Flowable.just(mFile)
                        //将File解码为Bitmap
                        .map(file -> BitmapUtils.compressToResolution(file, 1920 * 1080))
                        //裁剪Bitmap
                        .map(BitmapUtils::crop)
                        //将Bitmap写入文件
                        .map(bitmap -> BitmapUtils.writeBitmapToFile(bitmap, "mFile"))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(file -> {
                            mFile = file;
                            Uri uri = Uri.parse("file://" + mFile.toString());
                            *//*ImagePipeline imagePipeline = Fresco.getImagePipeline();
                            //清除该Uri的Fresco缓存. 若不清除，对于相同文件名的图片，Fresco会直接使用缓存而使得Drawee得不到更新.
                            imagePipeline.evictFromMemoryCache(uri);
                            imagePipeline.evictFromDiskCache(uri);
                            FrescoUtils.load("file://" + mFile.toString()).resize(240, 164).into(mImageView);
                            mBtnTakePicture.setText("重新拍照");
                            mHasSelectedOnce = true;*//*
                        });
                break;*/

        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void saveDate() {
        final Identity.DataBean.IdentityBean identity1 = new Identity.DataBean.IdentityBean();
        String userName = etName.getRightString();
        String IdCard = etIDCard.getRightString();
        String address = etAddress.getRightString();
        String age = etAge1.getRightString();
        String city = etCity.getRightString();
        String between1 = etBetween1.getRightString();
        String between2 = etBetween2.getRightString();
        String contact1 = etContact1.getRightString();
        String contact2 = etContact.getRightString();
        String contactName1 = etContactName.getRightString();
        String contactName2 = etContactName2.getRightString();


        Identity.DataBean.IdentityBean.ContactBean identity2 = new Identity.DataBean.IdentityBean.ContactBean();
        identity2.setUserphone(contact1);
        identity2.setContact(contactName1);
        identity2.setRelation("");

        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(between1)) {
                String s = String.valueOf(i);
                identity2.setRelation(s);
            }
        }

        Identity.DataBean.IdentityBean.ContactBean identity3 = new Identity.DataBean.IdentityBean.ContactBean();
        identity3.setUserphone(contact2);
        identity3.setContact(contactName2);
        identity3.setRelation("");

        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(between2)) {
                String s = String.valueOf(i);
                identity3.setRelation(s);
            }
        }
        String s1 = etSex.getRightString();
        if ("女".equals(s1)) {
            identity1.setSex("0");
        } else if ("男".equals(s1)) {
            identity1.setSex("1");
        } else {
            identity1.setSex("");
        }

        String marr = etMarriage.getRightString();
        identity1.setMarriage("");
        for (int i = 0; i < list1.length; i++) {
            if (list1[i].equals(marr)) {
                String s = String.valueOf(i);
                identity1.setMarriage(s);
            }
        }

        Identity.DataBean.IdentityBean bean = new Identity.DataBean.IdentityBean();
        bean.setSex(identity1.getSex());
        bean.setMarriage(identity1.getMarriage());
        bean.setAge(age);
        bean.setCity(city);
        bean.setIdaddress(address);
        bean.setIdcard(IdCard);
        bean.setName(userName);

        if (!changeTest()) {
            commitChange(identity1, identity2, identity3);
        } else {
            ToastUtils.showToast(getActivity(), "无修改内容");
        }

    }

    private void commitChange(Identity.DataBean.IdentityBean identity1, Identity.DataBean.IdentityBean.ContactBean identity2, Identity.DataBean.IdentityBean.ContactBean identity3) {
        if (!etContact.getRightString().isEmpty() && !etMarriage.getRightString().isEmpty() && !etContactName.getRightString().isEmpty()
                && !etCity.getRightString().isEmpty() && !etAddress.getRightString().isEmpty() && !etAge1.getRightString().isEmpty()
                && !etContact1.getRightString().isEmpty() && !etContactName2.getRightString().isEmpty() && !etIDCard.getRightString().isEmpty()
                && !etSex.getRightString().isEmpty() && !etBetween1.getRightString().isEmpty() && !etBetween2.getRightString().isEmpty()
                && !userPhone.getRightString().isEmpty() && !etName.getRightString().isEmpty()) {
            identity1.setIstatus("1");
        } else {
            identity1.setIstatus("0");
        }
        identity1.setName(etName.getRightString());
        identity1.setAge(etAge1.getRightString());
        identity1.setCity(etCity.getRightString());
        identity1.setIdcard(etIDCard.getRightString());
        identity1.setIdaddress(etAddress.getRightString());
        identity1.setUserphone(userPhone.getRightString());

        ArrayList<Identity.DataBean.IdentityBean.ContactBean> list = new ArrayList<>();
        list.add(identity2);
        list.add(identity3);
        identity1.setContact(list);
        String token = (String) SPUtils.get(getActivity(), "token", "1");
        Identity.DataBean date=new Identity.DataBean();
        date.setIdentity(identity1);
        date.setToken(token);

        if(user!=null){
            identity1.setUserphone(user.getUserphone());
        }
        JSONObject object=new JSONObject();
        try {
            object.put("identity",identity1);
            object.put("token",token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Gson gson=new Gson();
        String json = gson.toJson(date);

        OkGo.<String>post(Urls.Ip_url + Urls.Identity.AddIdentity)
                .tag(this)
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject object = new JSONObject(s);
                            int isSuccess = object.getInt("error_code");
                            if (isSuccess==0) {
                                identityBean = identity1;
                                EventBus.getDefault().post(new InformationEvent("informationStatus"));
                                String data = object.getString("data");
                                JSONObject object1 = new JSONObject(data);
                                String msg = object1.getString("msg");
                                ToastUtils.showToast(getActivity(), msg);
                            } else {
                                String msg = object.getString("error_message");
                                ToastUtils.showToast(getActivity(), msg);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
    /**
     * 内容有无修改
     *
     * @return
     */
    private boolean changeTest() {
        boolean flag;

        final Identity.DataBean.IdentityBean identity1 = new Identity.DataBean.IdentityBean();
        String userName = etName.getRightString();
        String IdCard = etIDCard.getRightString();
        String address = etAddress.getRightString();
        String age = etAge1.getRightString();
        String city = etCity.getRightString();
        String between1 = etBetween1.getRightString();
        String between2 = etBetween2.getRightString();
        String contact1 = etContact.getRightString();
        String contact2 = etContact1.getRightString();
        String contactName1 = etContactName.getRightString();
        String contactName2 = etContactName2.getRightString();


        Identity.DataBean.IdentityBean.ContactBean identity2 = new Identity.DataBean.IdentityBean.ContactBean();
        identity2.setUserphone(contact1);
        identity2.setContact(contactName1);
        identity2.setRelation("");

        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(between1)) {
                String s = String.valueOf(i);
                identity2.setRelation(s);
            }
        }

        Identity.DataBean.IdentityBean.ContactBean identity3 = new Identity.DataBean.IdentityBean.ContactBean();
        identity3.setUserphone(contact2);
        identity3.setContact(contactName2);
        identity3.setRelation("");

        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(between2)) {
                String s = String.valueOf(i);
                identity3.setRelation(s);
            }
        }

        if (identityBean.getMarriage().equals(identity1.getMarriage())
                && identityBean.getName().equals(userName)
                && identityBean.getIdcard().equals(IdCard)
                && identityBean.getIdaddress().equals(address)
                && identityBean.getAge().equals(age)
                && identityBean.getSex().equals(identity1.getSex())
                && identityBean.getCity().equals(city)
                && identityBean.getContact().get(0).getRelation().equals(identity2.getRelation())
                && identityBean.getContact().get(0).getUserphone().equals(contact1)
                && identityBean.getContact().get(0).getContact().equals(contactName1)
                && identityBean.getContact().get(1).getRelation().equals(identity3.getRelation())
                && identityBean.getContact().get(1).getUserphone().equals(contact2)
                && identityBean.getContact().get(1).getContact().equals(contactName2)) {
            return true;
        }
        return false;
    }

    private void getPermission(final int i) {
        AndPermission.with(getActivity())
                .requestCode(100)
                .permission(Manifest.permission.READ_CONTACTS)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(getActivity(), rationale)
                                .show();
                    }
                })
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        if (requestCode == 100) {
                            Uri uri = Uri.parse("content://contacts/people");
                            Intent intent = new Intent(Intent.ACTION_PICK, uri);
                            startActivityForResult(intent, i);
                        }
                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                    }
                })
                .start();


    }


    private void getLocationPermission() {
        AndPermission.with(getActivity())
                .requestCode(200)
                .permission(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).rationale(new RationaleListener() {
            @Override
            public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                AndPermission.rationaleDialog(getActivity(), rationale)
                        .show();

            }
        }).callback(new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                if (requestCode == 200) {
                    LogUtils.i("location", "权限");
                    initLocation();
                    mLocationClient.start();


                }
            }

            @Override
            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                startActivityForResult(new Intent(getActivity(), CityPickerActivity.class),
                        REQUEST_CODE_PICK_CITY);

            }
        })
                .start();


    }

    /**
     * 联系人获取
     *
     * @param uri
     * @return
     */
    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];
        //得到ContentResolver对象
        ContentResolver cr = getActivity().getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            //取得联系人姓名
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            if (phone!=null&&phone.getCount() > 0) {
                phone.moveToFirst();
                contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            if(phone!=null){
                phone.close();
            }
            cursor.close();
        } else

        {
            return null;
        }
        return contact;
    }

    /**
     * 城市定位
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        int span = 1000;
        option.setScanSpan(0);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setIgnoreKillProcess(true);
        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            LogUtils.i("city", bdLocation.getCity());
            final String city = bdLocation.getCity();
            if (city != null) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        etCity.setRightString(city);
                    }
                });
            } else {
                ToastUtils.showToast(getActivity(), bdLocation.getLocType() + "");
                LogUtils.i("getLocType", bdLocation.getLocType() + "");

            }

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {


        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
