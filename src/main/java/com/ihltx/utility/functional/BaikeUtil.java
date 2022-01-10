package com.ihltx.utility.functional;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.httpclient.service.impl.RestTemplateUtilImpl;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.weixin.entity.ArticleItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 百度百科实用工具
 */
public class BaikeUtil {

    private static String API_HOST = "https://wapbaike.baidu.com";
    private static String API_URL = API_HOST + "/searchresult?word=@keywords@" ;
    private static String LOGO = "https://www.baidu.com/img/flexible/logo/pc/result@2.png";

    private static RestTemplateUtil restTemplateUtil =  new RestTemplateUtilImpl(3000, 10000);

    /**
     * 根据关键词获取百度百科数据
     * @param keyWords   关键词
     * @return  List<Map<String, String>>
     *     title        -- 标题
     *     description  -- 描述
     *     picurl       -- 图片
     *     url          -- 链接
     */
    public static List<Map<String, String>> getDatas(String keyWords){
        Map<String, String> result = null;
        List<Map<String, String>> list = null;
        String content = restTemplateUtil.get(API_URL.replaceAll("@keywords@", keyWords));
        if(!StringUtil.isNullOrEmpty(content)){
            content = content.replaceAll("\n" , "").replaceAll("\r","");
            Pattern pattern = Pattern.compile("<ul.*id=\"searchList\">.*</ul>" , Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);
            if(matcher.find()){
                list = new ArrayList<>();
                content = matcher.group().replaceAll("(<em>|<em.*?>|<\\/em>)" ,"");
                Pattern pattern1 = Pattern.compile("<li>.*?<a href=\"(.+?)\">.*?<span>(.+?) *(_|-) *百度百科</span>.*?<p>(.+?)</p>.*?</a>.*?</li>" , Pattern.CASE_INSENSITIVE);
                Matcher matcher1 = pattern1.matcher(content);
                int i=0;
                while (matcher1.find()){
                    String url = matcher1.group(1);
                    if(url.startsWith("/")){
                        url = API_HOST + url;
                    }
                    String title = matcher1.group(2);
                    String description = matcher1.group(4);
                    result =new HashMap<>();
                    result.put("title" , title);
                    result.put("description" , description);
                    result.put("picurl" , i==0?LOGO:"");
                    result.put("url" , url);
                    list.add(result);
                    i++;
                }
            }

        }
        return  list;
    }


    /**
     * 根据关键词获取百度百科微信图文数据
     * @param keyWords   关键词
     * @return  List<ArticleItem>
     */
    public static List<ArticleItem> getWeiXinDatas(String keyWords){
        List<ArticleItem>  result = null;
        List<Map<String, String>> list = getDatas(keyWords);
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
