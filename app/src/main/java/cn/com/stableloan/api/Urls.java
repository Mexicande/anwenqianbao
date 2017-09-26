package cn.com.stableloan.api;

/**
 * Created by apple on 2017/5/20.
 */

public interface Urls {

    String Ip_url="http://api.anwenqianbao.com/v1/";

    String puk_URL="http://api.shoujijiekuan.com/Home/ApiLogin/";

    String NEW_URL="http://api.shoujijiekuan.com/Home/";

    String PUCLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDnj+RwgwDlUXlD3xUHXp6yQa6" +
            "D1rqD8hg3ucR61D7XA60WpgxacPxfH8ubw3hfS8Jk75qCq98T+mkrRJ91y3N06Oi" +
            "PUE5kRgnF33m9uoihTNcX9o0GXx17QslDH9TjXhcLHIkIXDZtbE415UdT0GnC6q4" +
            "DkeI2HZ45KR+7qkSXQIDAQAB";
    String PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANSzGmJ7eD51Oc+/" +
            "q+NG4I5UHqFzRXqoVcBvONISzUdkcKj8lGBQEcr58LJz4spSFyxh7TRpOfMNE25b" +
            "QwCEbWVci74mjQSffKIUHhriNwxROFXHWzun8KaqT4pQCYz0UsGtPpSgrNss0qL3" +
            "1iHrJtIwm7Jfgetcr+VTtDjhvh8zAgMBAAECgYEAs0fjzW7VA5A7kmi0sXVkgZNV" +
            "3jATODf7T6Bv/GHstWhrrYR4bFYRKU1THJehaXeYIMjJ74tiVQOIhVRXPXBh46ve" +
            "/jUBt/WUUB8Cc5OkX/ll293ZuUpedUlRi6jYNiXU7yjAfTIqFLMwKrpXjMnIMSwV" +
            "NebPk9qNs9VT3gENGgECQQD4e5Ozmmj2fBW/4aKA+LtObz/Hu75XaA5Kd9WV+30r" +
            "SGxxTh7QsHXl17xVXhMYrHcdJ9YhOWk69+sKQRlKLZWhAkEA2yJlmfzRT3J9L+2j" +
            "PzWM0o++hbuiEte2xs3brlvGUAO/Y69UZVzecx0E6bc8rWFakv8j7Ul1vSpJPeJB" +
            "7/kcUwJAPq1VNWGGhl4IUm1Ew0l6Xa98JBJ8UaniqPAPRRS5nvhWukHdTgCkzIQd" +
            "cl8XbArcxNLulVTY8VHlzKFdErPq4QJBAJ4lT8/2/hPpG2G4jcTzX6MibCxVgp04" +
            "oscNEArgXtmmKrzFbxIMGNpYyg/l1tuF0/kcOxBnoJoZZ2xK2q1WSdMCQQCQDV6/" +
            "y0puJ4EGhEa+jwiSi8rSD/vw09CNUNTieVGqx5DzxvPUzTyvRQ9y4dpU8zufqq5p" +
            "BI1hNZWOmpTyld2F";
    String Api_Id="3846f6f1e99e4f1b958111fda257c368";
    //
    interface Update{
        String APP_UPDATA="http://api.shoujijiekuan.com/Home/ApiLogin/Version";
        String value="url=";
    }
    interface  times{
        String MESSAGE_SEND="sms/getcode";
    }
    interface Login{
        String SettingPassWord="set/password";
        //验证码发送
        String LOGIN="login/login";
        String USER_INFOMATION="Mine/Locking";
        String GET_SIGNATURE="Mine/GetSignature";
        String Immunity="Mine/Immunity";
        String QUICK_LOGIN="quick/login";
        String captchaURL = "geetes/captcha";
        String validateURL = "geetes/verification";

    }
    interface CreditrePort{
        String PRIVATE_KEY  = "MIICXAIBAAKBgQCRMcGMrZIKu956ubKD2kDFzRtSP13ycgRtPVz+MvseTje0YgGl" +
                "ht+zGuPgCD9KgW44FFdwGS2Wp7Ir/zzEpInTPlKWjXGZP06iwlu+7p04ktgK7TB6" +
                "NjF83gHB/T//OrliHrX1l/Jzh3LFHwCAxT6fJt1Ja6eziGvxtLPT94TkcwIDAQAB" +
                "AoGAKsYYugPsUUM1cLxCLfvfNyaMlPdcCu+yBCieu7hzKGNsn7R7vbL1NgOG/FoR" +
                "ozZsLRM5CyovtwFiSPnhgiDjBSnvugLBPLKcQJX3PUb9dHOpfwXZCOg/CMwbb8fg" +
                "9bPt36oBfJYIydQjP3/9iM9t1sdtIbd/fHCL6Ry/iGcsvyECQQDA1ezZ0DlV25QX" +
                "FbL1IvufITnvcfK2KwGc4By71cWZ/tnczxGh4/YiTym7V58IR+XW326UwPdW4Mi0" +
                "xtuJagmxAkEAwMDo8VzvysfxDs0hdwxFjksZ0htPEG9FLjhp2Vu5jm+swyPBt+eW" +
                "pFvstM6YIq570y6D3Mkq2zu20F5nJnm1YwJAdxur0F3tDDs0nY2pnACfqwq63ktj" +
                "v2GQ/XTwSpUgGJ5xsxGzsms7/LUo8a6NbG/8Z1xa0Ubff6oYTpEFyTrWAQJBAJPa" +
                "phSWpH2Y1yjyYswtxqD6rKjFN+W0ZI2qyk7nlDNVKGFbaTpHU/9pX+3lVz+rNeJt" +
                "GMrgKJaYfIfjEh6qV18CQAOSaCWwd26ZE5zed045dz01Yf9zPvxYXeFm4cqK584M" +
                "Z6J84qIslgp9fhOrLv/Q4mos1CZTWzDhFbDM73NlabQ=";
        String APP_ID=                                                                   "1000053";
        String APP_NAME=                                                                 "anwenqianbao";
    }

    interface  Identity{
        String GetIdentity="person/getIdentity";
        String Getbank="person/getBank ";

        String GetOccupation="person/getOccupation";

        String AddIdentity="person/addIdentity";

        String Addbank="person/addBank";

        String AddOccupation="person/addOccupation";

    }
    interface STATUS{
        String GetCerftication="accredit/status";
        String GetPictrueStatus="status/getImageStatus";
        String Getsetting="SaveSetting/Getsetting";
        String Save_Setting="SaveSetting/setting";


    }

    interface  HOME_FRAGMENT{
        String BANNER_HOT="GetBanner";
        String PRODUCT_LIST="GetProducts";
    }
    interface register{

        String REGSTER  = "Registered";

        String FORGETWORD="set/forgetpassword";
    }

    interface product{
        //产品详情
        String Productdetail="product/productStatus";
        String GetSlotdetail ="GetSlotdetail";
        String ClassProduct="ClassificationList";
        String ProductList="ProductList";
        String ProTagFlow="product/getlabels";
        String ProductSelect="product/screening";
        String ProductCollectionList="product/getlist";
        String CollectionDesc="product/collection";
    }

    interface update{
        String UPDATE_NICK="Modify";
        String UPDATE_Word="Modify";
        String UPDATE_PROFRSSION="Modify";
    }
    interface user{
        String USERT_INFO="person/getPerson";
        String USER_STATUS="status/getStatus";
        String FEEDBACK="feedback/opinion";
    }
    interface  notice{
        String Announcement="Announcement";
    }
    interface  LOTTERY{
        String GetLottery="welfare/getwelfare";

        String Welfare="welfare/shunt";
    }

    interface Pictrue{
        String GET_QINIUTOKEN="Mine/GetToken";

        String UpLoadImage="picture/addPicture";

        String Get_Pictrue="picture/getPicture";
    }
    interface KEY{
        String WEICHAT_APPID="wxd8a11cffdc98f84b";
        String QQ_APPID="1106239350";

        String PageWeb="http://test.m.anwenqianbao.com/#/details?pId=";
        String SHARE_INCODE="http://test.m.anwenqianbao.com/#/login?inviteCode=";
    }
    interface  Integarl{
        String getAccumulatePoints="point/getAccumulatePoints";
        String GETCASH="cash/getCash";
        String OUTCASH="cash/getCashOut";
        String EXCHANGEPOINTS="point/exchangePoints";
    }
    interface Dialog{
        String advertising="popup/getPopup";
        String GETUSERPOPUP="popup/getUserPopup";
    }
    interface Invite{
        String INVITE_FRIENDS="invite/smsInvite";
        String INVITE_LIST="invite/inviteList";
    }

    String  TOKEN=                                                                                  "token";
    String NUMBER_ZERO=                                                                             "0";
    String NUMBER_ONE=                                                                              "1";
    String NUMBER_TWO=                                                                              "2";
}
