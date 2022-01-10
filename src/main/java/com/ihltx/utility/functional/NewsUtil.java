package com.ihltx.utility.functional;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.httpclient.service.impl.RestTemplateUtilImpl;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.weixin.entity.ArticleItem;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 京东头条实用工具
 */
public class NewsUtil {

    private static String API_HOST = "https://way.jd.com";
    private static String API_URL = API_HOST + "/jisuapi/get?channel=头条&num=10&start=0&appkey=e61ea08206439db9cb30910865faad7c" ;

    private static RestTemplateUtil restTemplateUtil =  new RestTemplateUtilImpl(3000, 10000);

    /**
     * 根据获取当前条头新闻
     * @return  List<Map<String, String>>
     *     title        -- 标题
     *     description  -- 描述
     *     picurl       -- 图片
     *     url          -- 链接
     */
    public static List<Map<String, String>> getDatas() {
        List<Map<String, String>> list = null;
        Map<String, String> result = null;
        JSONObject jsonObject = null;
        String content = restTemplateUtil.get(API_URL);
        if(!StringUtil.isNullOrEmpty(content)){
            jsonObject = JSONObject.parseObject(content);
            if(jsonObject.containsKey("result") &&
                    jsonObject.getJSONObject("result").containsKey("result") &&
                    jsonObject.getJSONObject("result").getJSONObject("result").containsKey("list")
            ){
                JSONArray jsonArray =  jsonObject.getJSONObject("result").getJSONObject("result").getJSONArray("list");
                list = new ArrayList<>();
                for (int i=0;i<jsonArray.size();i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    result =new HashMap<>();
                    result.put("title" , jsonObject1.getString("title"));
                    result.put("picurl" , jsonObject1.getString("pic"));
                    result.put("url" , jsonObject1.getString("url"));
                    result.put("description" ,"");
                    list.add(result);
                }

            }

        }
        return list;
    }

    /**
     * 根据获取当前条头新闻
     * @return  List<ArticleMsg>
     */
    public static List<ArticleItem> getWeiXinDatas(){
        List<ArticleItem>  result = null;
        List<Map<String, String>> list = getDatas();
        if(list!=null && list.size()>0){
            result =new ArrayList<>();
            for (Map<String,String> map : list){
                ArticleItem articleItem =new ArticleItem();
                articleItem.setTitle(map.get("title"));
                articleItem.setDigest(map.get("description"));
                articleItem.setThumb_url(map.get("picurl"));
                articleItem.setUrl(map.get("url"));
                result.add(articleItem);
            }
        }
        return  result;
    }
}
