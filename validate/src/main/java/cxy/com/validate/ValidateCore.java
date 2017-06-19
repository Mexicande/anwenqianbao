package cxy.com.validate;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cxy.com.validate.bean.AttrBean;
import cxy.com.validate.bean.LengthBean;
import cxy.com.validate.bean.MoneyBean;
import cxy.com.validate.bean.PasswordBean;
import cxy.com.validate.bean.REBean;

/*****************************************
 * @author cxy
 *         created at  2016/11/9 11:51
 ****************************************/
public class ValidateCore {
    private static final String msg = " content is null or \"\" ,  You can add 【@NotNull】 ";

    private static void setEditText(Object view, boolean isEt, String msg, IValidateResult validateResult) {
        if (isEt) {
            if (validateResult.onValidateErrorAnno() != null) {
                ((EditText) view).startAnimation(validateResult.onValidateErrorAnno());
            }
            validateResult.onValidateError(msg, ((EditText) view));
        } else {
            validateResult.onValidateError(msg, null);
        }
    }

    private static boolean isNull(Object view, boolean isEt, IValidateResult validateResult) {
        if (TextUtils.isEmpty(((TextView) view).getText().toString())) {
            if (isEt) {
                if (validateResult.onValidateErrorAnno() != null) {
                    ((EditText) view).startAnimation(validateResult.onValidateErrorAnno());
                }
                validateResult.onValidateError("EditText " + msg, ((EditText) view));
            } else {
                validateResult.onValidateError("TextView " + msg, null);
            }
            return true;
        }
        return false;
    }


    public static boolean notNull(Object view, boolean isEt, String msg, IValidateResult validateResult) {

        if (TextUtils.isEmpty(((TextView) view).getText().toString())) {
            setEditText(view, isEt, msg, validateResult);
            return true;
        }
        return false;
    }


    public static boolean re(Object view, boolean isEt, REBean bean, IValidateResult validateResult) {
        if (isNull(view, isEt, validateResult)) return true;

        Pattern r = Pattern.compile(bean.re);
        Matcher m = r.matcher(((TextView) view).getText().toString());
        if (!m.matches()) {
            setEditText(view, isEt, bean.msg, validateResult);
            return true;
        }
        return false;
    }


    public static boolean max(Object view, boolean isEt, LengthBean bean, IValidateResult validateResult) {
        if (isNull(view, isEt, validateResult)) return true;

        if (((TextView) view).getText().toString().length() > bean.length) {
            setEditText(view, isEt, bean.msg, validateResult);
            return true;
        }
        return false;
    }

    public static boolean min(Object view, boolean isEt, LengthBean bean, IValidateResult validateResult) {
        if (isNull(view, isEt, validateResult)) return true;

        if (((TextView) view).getText().toString().length() < bean.length) {
            setEditText(view, isEt, bean.msg, validateResult);
            return true;
        }
        return false;
    }

    public static boolean money(Object view, boolean isEt, MoneyBean bean, IValidateResult validateResult) {
        if (isNull(view, isEt, validateResult)) return true;

        String keep = null;
        keep = String.valueOf(bean.keep);
        if (bean.keep > 2) {
            keep = String.valueOf(2);
        }
        if (bean.keep <= 0) {
            keep = String.valueOf(1);
        }

        String re = "^(?!0+(?:\\.0+)?$)(?:[1-9]\\d*|0)\\.(?:\\d{" + keep + "})?$";
        Pattern r = Pattern.compile(re);
        Matcher m = r.matcher(((TextView)view).getText().toString());
        if (!m.matches()) {
            setEditText(view, isEt, bean.msg, validateResult);
            return true;
        }
        return false;
    }

    public static boolean password1(Object view, boolean isEt, IValidateResult validateResult) {
        if (isNull(view, isEt, validateResult)) {
            return true;
        }
        return false;
    }

    public static boolean password2(AttrBean view, AttrBean pwd1Attr, boolean isEt, PasswordBean bean, IValidateResult validateResult) {
        if (isNull(view.view, isEt, validateResult)) {
            return true;
        }
        if (!((TextView) view.view).getText().toString().equals(((EditText) pwd1Attr.view).getText().toString())) {
            setEditText(view.view, view.isEt, bean.msg, validateResult);
            return true;
        }
        return false;
    }
}
