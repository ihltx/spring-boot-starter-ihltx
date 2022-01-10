package com.ihltx.utility.functional;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.httpclient.service.impl.RestTemplateUtilImpl;
import com.ihltx.utility.util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * 天气实用工具
 */
public class WeatherUtil {

    private static String API_HOST = "https://way.jd.com";
    private static String API_URL = API_HOST + "/he/freeweather?appkey=e61ea08206439db9cb30910865faad7c&city=@city@" ;

    private static RestTemplateUtil restTemplateUtil =  new RestTemplateUtilImpl(3000, 10000);

    /**
     * 根据城市获取有当天天气信息
     * @param city   城市
     * @return  JSONObject
     *     now.cond.txt         -- 今日天气
     *     aqi.city.qlty        -- 空气质量
     *     aqi.city.pm25        -- pm25
     *     now.tmp              -- 温度 摄氏度
     *     now.hum              -- 湿度
     *     now.vis              -- 能见度
     *     now.pcpn             -- 降水量
     *     now.fl               -- 感冒指数
     *     now.wind.sc          -- 风级
     *     now.wind.dir         -- 风向
     */
    public static JSONObject getDatas(String city) {
        JSONObject jsonObject = null;
        String content = restTemplateUtil.get(API_URL.replaceAll("@city@", city));
        if(!StringUtil.isNullOrEmpty(content)){
            jsonObject = JSONObject.parseObject(content);
            if(jsonObject!=null && jsonObject.containsKey("result") && jsonObject.getJSONObject("result").containsKey("HeWeather5")){
                JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("HeWeather5");
                if(jsonArray.size()>0){
                    jsonObject = jsonArray.getJSONObject(0);
                }
            }
        }
        return jsonObject;
    }

    /**
     * 根据关键词获取有道翻译微信文本信息
     * @param city   城市
     * @return  String
     */
    public static String getWeiXinDatas(String city){
        JSONObject result =  getDatas(city);
        StringBuffer sb =new StringBuffer();
        String rs = null;
        String split = "==============";
        if(result!=null && !result.isEmpty()){
            sb.append(city).append("\n").append(split).append("\n");
            sb.append("今日天气：").append(result.getJSONObject("now").getJSONObject("cond").getString("txt")).append("\n");
            sb.append("空气质量：").append(result.getJSONObject("aqi").getJSONObject("city").getString("qlty")).append("\n");
            sb.append("pm25：").append(result.getJSONObject("aqi").getJSONObject("city").getString("pm25")).append("\n");
            sb.append("温度：").append(result.getJSONObject("now").getString("tmp")).append("摄氏度\n");
            sb.append("湿度：").append(result.getJSONObject("now").getString("hum")).append("\n");
            sb.append("能见度：").append(result.getJSONObject("now").getString("vis")).append("\n");
            sb.append("降水量：").append(result.getJSONObject("now").getString("pcpn")).append("\n");
            sb.append("感冒指数：").append(result.getJSONObject("now").getString("fl")).append("\n");
            sb.append("风级：").append(result.getJSONObject("now").getJSONObject("wind").getString("sc")).append("\n");
            sb.append("风向：").append(result.getJSONObject("now").getJSONObject("wind").getString("dir")).append("\n");
            rs = sb.toString();
        }
        return rs;
    }
}
