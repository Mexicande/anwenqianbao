package cn.com.stableloan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.stableloan.R;
import cn.com.stableloan.api.Urls;
import cn.com.stableloan.base.BaseActivity;
import cn.com.stableloan.bean.UpdateEvent;
import cn.com.stableloan.bean.UpdateProfessionEvent;
import cn.com.stableloan.model.UserBean;
import cn.com.stableloan.utils.SPUtils;
import cn.com.stableloan.utils.TinyDB;
import cn.com.stableloan.utils.ToastUtils;
import cn.com.stableloan.view.dialog.IdentityDialog;
import cxy.com.validate.IValidateResult;
import cxy.com.validate.Validate;
import cxy.com.validate.annotation.Index;
import cxy.com.validate.annotation.NotNull;
import cxy.com.validate.annotation.RE;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 用户身份选择
 *
 */
public class UpdataProfessionActivity extends BaseActivity implements IValidateResult {



    @Bind(R.id.title_name)
    TextView titleName;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_work)
    ImageView ivWork;
    @Bind(R.id.tick)
    ImageView tick;
    @Bind(R.id.view1)
    View view1;
    @Bind(R.id.iv_free)
    ImageView ivFree;
    @Bind(R.id.tick_free)
    ImageView tickFree;
    @Bind(R.id.iv_student)
    ImageView ivStudent;
    @Bind(R.id.tick_student)
    ImageView tickStudent;
    @Bind(R.id.iv_company)
    ImageView ivCompany;
    @Bind(R.id.tick_company)
    ImageView tickCompany;
    @Bind(R.id.iv_back)
    ImageView ivBack;


    @Index(1)
    @NotNull(msg = "昵称不为能空！")
    @RE(re = RE.number_letter_nick, msg = "昵称格式不正确")
    @Bind(R.id.et_nick)
    EditText etNick;

    private  int Flge = 0;
    public static void launch(Context context) {
        context.startActivity(new Intent(context, UpdataProfessionActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updata_profession);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Validate.reg(this);
        ivBack.setVisibility(View.VISIBLE);
        titleName.setText("个人资料");
        getIdentity();

    }

    private void getIdentity() {


        String token = (String) SPUtils.get(this, "token", "1");
        HashMap<String, String> params = new HashMap<>();
        params.put("token",token);
        JSONObject jsonObject = new JSONObject(params);
        OkGo.post(Urls.NEW_Ip_url+Urls.update.GET_IDENTITY)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject object=new JSONObject(s);

                            int error_code = object.getInt("error_code");
                            if(error_code==0){
                                String data = object.getString("data");
                                JSONObject dateObject=new JSONObject(data);
                                Flge = dateObject.getInt("identity");
                                String nickname = dateObject.getString("nickname");
                                etNick.setHint(nickname);
                                switch (Flge) {
                                    case 1:
                                        ivWork.setColorFilter(getResources().getColor(R.color.mask));
                                        tick.setVisibility(View.VISIBLE);
                                        break;
                                    case 4:
                                        ivFree.setColorFilter(getResources().getColor(R.color.mask));
                                        tickFree.setVisibility(View.VISIBLE);
                                        break;
                                    case 2:
                                        ivStudent.setColorFilter(getResources().getColor(R.color.mask));
                                        tickStudent.setVisibility(View.VISIBLE);
                                        break;
                                    case 3:
                                        ivCompany.setColorFilter(getResources().getColor(R.color.mask));
                                        tickCompany.setVisibility(View.VISIBLE);
                                        break;
                                }
                            }else if(error_code==2){
                                int error_message = object.getInt("error_message");

                                Intent intent=new Intent(UpdataProfessionActivity.this,LoginActivity.class);
                                intent.putExtra("message",error_message);
                                intent.putExtra("from","UpdataProfessionError");
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });



    }

    @Subscribe
    public  void updateEvent(UpdateProfessionEvent msg){
        if(msg!=null){
            if(msg.updateProfession==1){
                getIdentity();
            }
        }

    }

    @OnClick({R.id.iv_work, R.id.iv_free, R.id.iv_student, R.id.iv_company, R.id.iv_back,R.id.bt_Save})
    public void onViewClicked(View view) {
        SettingProfession();
        SettingTick();
        switch (view.getId()) {
            case R.id.iv_work:
                ivWork.setColorFilter(getResources().getColor(R.color.mask));
                tick.setVisibility(View.VISIBLE);
                Flge=1;
                break;
            case R.id.iv_free:
                ivFree.setColorFilter(getResources().getColor(R.color.mask));
                tickFree.setVisibility(View.VISIBLE);
                Flge=4;
                break;
            case R.id.iv_student:
                ivStudent.setColorFilter(getResources().getColor(R.color.mask));
                tickStudent.setVisibility(View.VISIBLE);
                Flge=2;
                break;
            case R.id.iv_company:
                ivCompany.setColorFilter(getResources().getColor(R.color.mask));
                tickCompany.setVisibility(View.VISIBLE);
                Flge=3;
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_Save:
                Validate.check(UpdataProfessionActivity.this, UpdataProfessionActivity.this);
                break;
            default:
                break;
        }

    }
    private  KProgressHUD hud;
    private void updateIdentity(final String identity ) {
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("加载中.....")
                .setCancellable(true)
                .show();
        String token = (String) SPUtils.get(this, "token", "1");
        if(token!=null){
            HashMap<String, String> params = new HashMap<>();
            params.put("identity",identity);
            params.put("token",token);
            params.put("nickname",etNick.getText().toString());
            JSONObject jsonObject = new JSONObject(params);
            OkGo.post(Urls.NEW_Ip_url+Urls.update.UPDATE_PROFRSSION)
                    .tag(this)
                    .upJson(jsonObject.toString())
                    .execute( new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            try {
                                JSONObject object=new JSONObject(s);
                                int error_code = object.getInt("error_code");
                                if(error_code==0){
                                    hud.dismiss();
                                    SPUtils.put(UpdataProfessionActivity.this,"identity",identity);
                                    ToastUtils.showToast(UpdataProfessionActivity.this,"保存成功");
                                    TinyDB tinyDB=new TinyDB(UpdataProfessionActivity.this);
                                    String userphone = (String) SPUtils.get(getApplicationContext(), Urls.lock.USER_PHONE, "1");

                                    UserBean user = (UserBean) tinyDB.getObject(userphone, UserBean.class);
                                    user.setIdentity(Integer.parseInt(identity));
                                    user.setNickname(etNick.getText().toString());
                                    if(getIntent().getStringExtra("from")!=null){
                                        IdentityinformationActivity.launch(UpdataProfessionActivity.this);
                                        finish();
                                    }else {
                                        setResult(100, getIntent().putExtra("nick", etNick.getText().toString()));
                                       /* EventBus.getDefault().post(new MessageEvent(etNick.getText().toString(),null));
                                        setResult(1000,new Intent().putExtra("HeadPhoto",identity));*/
                                        finish();
                                    }
                                }else {
                                    hud.dismiss();
                                    String string = object.getString("error_message");
                                    ToastUtils.showToast(UpdataProfessionActivity.this,string);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            hud.dismiss();
                            ToastUtils.showToast(UpdataProfessionActivity.this,"网络异常，请检测网络");

                        }
                    });
        }else {
            hud.dismiss();
            ToastUtils.showToast(UpdataProfessionActivity.this,"系统异常,请稍后再试");
        }

    }

    /**
     * 选择对勾
     */
    private void SettingTick() {
        tick.setVisibility(View.GONE);
        tickCompany.setVisibility(View.GONE);
        tickFree.setVisibility(View.GONE);
        tickStudent.setVisibility(View.GONE);
    }

    /**
     * 图片选择添加蒙版
     */
    private void SettingProfession() {
        ivWork.setColorFilter(null);
        ivFree.setColorFilter(null);
        ivStudent.setColorFilter(null);
        ivCompany.setColorFilter(null);
    }

    @Override
    public void onValidateSuccess() {
        if(Flge!=0){
            updateIdentity(Flge+"");
        }else {
            IdentityDialog dialog=new IdentityDialog(this);
            dialog.setYesOnclickListener("确定", new IdentityDialog.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }


    @Override
    public void onValidateError(String msg, EditText editText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showToast(UpdataProfessionActivity.this,msg);

            }
        });

    }

    @Override
    public Animation onValidateErrorAnno() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
