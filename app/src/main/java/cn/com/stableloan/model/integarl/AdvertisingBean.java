package cn.com.stableloan.model.integarl;

import java.io.Serializable;

/**
 * Created by apple on 2017/8/14.
 */

public class AdvertisingBean implements Serializable{

    /**
     * code : 200
     * message : 200
     * data : {"id":1,"name":"","type":1,"description":null,"do_count":1,"url":"http://www.baidu.com","position":1,"status":1,"create_time":"1970-01-02 00:00:00","update_time":"1970-01-02 00:00:00","img":"https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage1.png","push_times":1}
     * error_code : 0
     * error_message :
     * time : 2017-08-14 12:33:49
     */

    private int code;
    private String message;
    private DataBean data;
    private int error_code;
    private String error_message;
    private String time;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static class DataBean implements Serializable{
        /**
         * id : 1
         * name :
         * type : 1
         * description : null
         * do_count : 1
         * url : http://www.baidu.com
         * position : 1
         * status : 1
         * create_time : 1970-01-02 00:00:00
         * update_time : 1970-01-02 00:00:00
         * img : https://raw.githubusercontent.com/yipianfengye/android-adDialog/master/images/testImage1.png
         * push_times : 1
         */
        private String aspectRatio;
        private int id;
        private String name;
        private int type;
        private String description;
        private int do_count;
        private String url;
        private int position;
        private String status;
        private String create_time;
        private String update_time;
        private String img;
        private int push_times;

        public String getAspectRatio() {
            return aspectRatio;
        }

        public void setAspectRatio(String aspectRatio) {
            this.aspectRatio = aspectRatio;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getDo_count() {
            return do_count;
        }

        public void setDo_count(int do_count) {
            this.do_count = do_count;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getPush_times() {
            return push_times;
        }

        public void setPush_times(int push_times) {
            this.push_times = push_times;
        }
    }
}
