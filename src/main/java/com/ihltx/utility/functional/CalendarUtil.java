package com.ihltx.utility.functional;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.httpclient.service.impl.RestTemplateUtilImpl;
import com.ihltx.utility.util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.var;
import springfox.documentation.spring.web.json.Json;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 万年历实用工具
 */
public class CalendarUtil {

    private static String API_HOST = "https://hl.zdic.net";
    private static String API_URL = API_HOST + "/j/gl/@date@.php" ;

    private static RestTemplateUtil restTemplateUtil =  new RestTemplateUtilImpl(3000, 10000);

    /**
     * 获取指定年份及月份万年历数据
    * @param date   指定日期
     * @return  Map<String, String>
     *     date         -- 日期
     *     xingqi       -- 星期
     *     history      -- ,间隔的历史上的今天
     *     nongliYear   -- 农历年
     *     nongliDate   -- 农历日
     *     nongliDay    -- 农历初几
     *     jieri        -- 节日
     *     xingzuo      -- 星座
     */
    public static Map<String, String> getDatas(Date date){
        SimpleDateFormat sdfYear =new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfMonth =new SimpleDateFormat("MM");
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        String d = sdf.format(date);
        Map<String, String> result = null;
        String content = restTemplateUtil.get(API_URL.replaceAll("@date@", sdfYear.format(date) +"/" + Integer.valueOf(sdfMonth.format(date))));
        if(!StringUtil.isNullOrEmpty(content)){
            Pattern pattern = Pattern.compile("\\[\\{.*\\}\\]");
            Matcher matcher = pattern.matcher(content);
            if(matcher.find()){
                content = matcher.group(0);
                JSONArray jsonArray = JSONObject.parseArray(content);
                for(int i=0;i< jsonArray.size();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(d.equals(jsonObject.getString("date"))){
                        result = new HashMap<>();
                        result.put("date" , jsonObject.getString("date"));
                        result.put("xingqi" , jsonObject.getString("xingqi"));
                        result.put("nongliYear" , jsonObject.getString("nongliYear"));
                        result.put("nongliDate" , jsonObject.getString("nongliDate"));
                        result.put("nongliDay" , jsonObject.getString("nongliDay"));
                        if(jsonObject.containsKey("jieri")){
                            result.put("jieri" , jsonObject.getString("jieri"));
                        }else{
                            result.put("jieri" , "");
                        }
                        result.put("xingzuo" , jsonObject.getString("xingzuo"));
                        if(jsonObject.containsKey("history")){
                            JSONObject jsonObject1 = jsonObject.getJSONObject("history");
                            Iterator iterator = jsonObject1.entrySet().iterator();
                            StringBuffer stringBuffer = new StringBuffer();
                            while (iterator.hasNext()){
                                Map.Entry entry = (Map.Entry)iterator.next();
                                JSONObject jsonObject2 = (JSONObject)entry.getValue();
                                stringBuffer.append(jsonObject2.getString("title")).append(",");
                            }
                            result.put("history" , StringUtil.trim(stringBuffer.toString(),","));
                        }else{
                            result.put("history" , "");
                        }
                        break;
                    }
                }

            }
        }
        return  result;
    }

    /**
     * 根据关键词获取有道翻译微信文本信息
     * @param date   指定日期
     * @return  String
     */
    public static String getWeiXinDatas(Date date){
        Map<String, String> result =  getDatas(date);
        String rs = null;
        StringBuffer sb =new StringBuffer();
        if(result!=null && !result.isEmpty()){
            sb.append(result.get("date")) .append(" ").append(result.get("xingqi")).append("\n");
            sb.append("农历：").append(result.get("nongliYear")).append(" ").append(result.get("nongliDay")).append(" ").append(result.get("nongliDate")).append("\n");
            sb.append("星座：") .append(result.get("xingzuo")).append("\n");
            if(!StringUtil.isNullOrEmpty(result.get("jieri"))){
                sb.append("节日：") .append(result.get("jieri")).append("\n");
            }
            if(!StringUtil.isNullOrEmpty(result.get("history"))){
                sb.append("历史上的今天：") .append("\n");
                String[] histories = result.get("history").split(",");
                for (String h : histories){
                    sb.append(h) .append("\n");
                }
            }
            rs = sb.toString();
        }
        return rs;
    }

}
