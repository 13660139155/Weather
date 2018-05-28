package com.example.asus.weather.Interface;

/**
 * 网络请求回调接口
 * Created by ASUS on 2018/5/18.
 */

public interface HttpCallBackListener {

    /**
     * 在这里对子线程返回的数据进行相应的操作
     * @param response  返回的数据
     */
    void onFinish(String response);

    /**
     * 在这里对子线程返回的异常进行相应的操作
     * @param e 返回的异常
     */
    void onError(Exception e);

}
