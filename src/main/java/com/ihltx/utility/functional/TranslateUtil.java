package com.ihltx.utility.functional;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.httpclient.service.impl.RestTemplateUtilImpl;
import com.ihltx.utility.util.StringUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 有道翻译工具
 */
public class TranslateUtil {

    private static String API_HOST = "https://dict.youdao.com";
    private static String API_URL = API_HOST + "/search?q=@keywords@" ;

    private static RestTemplateUtil restTemplateUtil =  new RestTemplateUtilImpl(3000, 10000);

    /**
     * 根据关键词获取有道翻译信息
     * @param keyWords   关键词
     * @return  Map<String, Set<String>>
     *     phonetic       -- 发音
     *     translate      -- 翻译
     */
    public static Map<String, Set<String>> getDatas(String keyWords){
        Map<String, Set<String>> list = null;
        String line = null;
        String content = restTemplateUtil.get(API_URL.replaceAll("@keywords@", keyWords));
        if(!StringUtil.isNullOrEmpty(content)){
            content = content.replaceAll("\n" , "").replaceAll("\r","");
            Pattern pattern = Pattern.compile("<h2 class=\"wordbook-js\">.+?<\\/h2>" , Pattern.CASE_INSENSITIVE);
            Pattern pattern1 = Pattern.compile("<div class=\"trans-container\">.*?(<ul>.+?<\\/ul>).*?<\\/div>" , Pattern.CASE_INSENSITIVE);

            Matcher matcher = pattern.matcher(content);
            Matcher matcher1 = pattern1.matcher(content);

            if(matcher.find() && matcher1.find()){
                String block = matcher.group();
                Pattern pattern2 = Pattern.compile("<span class=\"keyword\">(.+?)<\\/span>" , Pattern.CASE_INSENSITIVE);
                Matcher matcher2 = pattern2.matcher(block);
                if (matcher2.find()){
                    Set<String> rs = new HashSet<>();
                    Set<String> ds = new HashSet<>();
                    Pattern pattern3 = Pattern.compile("<span class=\"(pronounce|phonetic)\">(.+?)<\\/span>" , Pattern.CASE_INSENSITIVE);
                    Matcher matcher3 = pattern3.matcher(block);
                    while (matcher3.find()){
                        line = matcher3.group(2).replaceAll("<.+?>","").trim().replaceAll("\\s+" , " ");
                        if(!StringUtil.isNullOrEmpty(line)){
                            rs.add(line);
                        }
                    }
                    String trans = matcher1.group(1);
                    Pattern pattern4 = Pattern.compile("<p class=\"wordGroup\">(.+?)<\\/p>|<li>(.+?)<\\/li>" , Pattern.CASE_INSENSITIVE);
                    Matcher matcher4 = pattern4.matcher(trans);
                    while (matcher4.find()){
                        if(matcher4.group(1)!=null){
                            line = matcher4.group(1).replaceAll("<.+?>","").trim().replaceAll("\\s+" , " ");
                        }else{
                            line = matcher4.group(2).replaceAll("<.+?>","").trim().replaceAll("\\s+" , " ");
                        }
                        if(!StringUtil.isNullOrEmpty(line)){
                            ds.add(line);
                        }
                    }
                    if(!rs.isEmpty() || !ds.isEmpty()){
                        list = new HashMap<>();
                        list.put("phonetic" , rs);
                        list.put("translate" , ds);
                    }
                }
            }

        }
        return  list;
    }

    /**
     * 根据关键词获取有道翻译微信文本信息
     * @param keyWords   关键词
     * @return  String
     */
    public static String getWeiXinDatas(String keyWords){
        Map<String, Set<String>> result =  getDatas(keyWords);
        StringBuffer sb =new StringBuffer();
        String rs = null;
        String split = "==============";
        if(result!=null && !result.isEmpty()){
            sb.append(keyWords).append("\n").append(split).append("\n");
            sb.append("发音：").append("\n");
            if(result.containsKey("phonetic") && result.get("phonetic")!=null && !result.get("phonetic").isEmpty()){
                for (String phonetic : result.get("phonetic")){
                    sb.append(phonetic).append("\n");
                }
            }
            sb.append(split).append("\n");
            if(result.containsKey("translate") && result.get("translate")!=null && !result.get("translate").isEmpty()){
                for (String translate : result.get("translate")){
                    sb.append(translate).append("\n");
                }
            }
            rs = sb.toString();
        }
        return rs;
    }

}
