package com.jhlc.material.utils;

/**
 *  URL字符串常量
 * */
public class MYURL {
//    public static String URL_HEAD = "http://zhixingli.shilehui.com/";
//    public static String img_HEAD = "http://zhixingli.shilehui.com";
//    public static String URL_HEAD = "http://logtest.shilehui.com/Default.aspx";
//    public static String img_HEAD = "http://logtest.shilehui.com";
//    public static String URL_HEAD = "http://202.101.180.220:9001/Default.aspx";
//    public static String img_HEAD = "http://202.101.180.220:9001";

    public static String URL_HEAD = "http://materialmarketzxl.201494.com/Default.aspx";
    public static String img_HEAD = "http://materialmarketzxl.201494.com";


}


/*    public void getSearchTopicData( ) {
        String url = getSearchUrl();
        DataWebService userService = DataWebService.getInstance();
        userService.getData(url, new DataWebCallBack() {

            @Override
            public void onSuccess(String responseBody, String successMsg) {
                UserInfoBean userInfoBean = new GsonBuilder().create().fromJson(strbody, UserInfoBean.class);
            }

            @Override
            public void onFailure(String responseBody, String failureMsg) {

            }
        });
    }*/

/*
private void loginAsyncHttpClientPost( ) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "get_login_userindex");
        params.put("Username", preferenceUtils.getUserName());
        params.put("eid", Constant.EID);
        params.put("typeid", 0);

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 设置值
                LogZ.d("getUpUserData--> ",new String(responseBody));

                UpUserBean upUserBean = new GsonBuilder().create().fromJson(new String(responseBody), UpUserBean.class);
                if(upUserBean==null) return;
                for (int i=0; i<upUserBean.getUpUserlists().length; i++) {
                    upUserlist.add(upUserBean.getUpUserlists()[i]);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();

            }

        });
    }
* */