package com.ihltx.utility.sensitive.service.impl;

import com.ihltx.utility.sensitive.config.SensitiveConfig;
import com.ihltx.utility.sensitive.service.SensitiveWordService;
import com.ihltx.utility.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(prefix = "ihltx.sensitive", name = "enable" , havingValue = "true")
public class DFAUtil {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @Autowired
    private SensitiveConfig sensitiveConfig;


    private static Map<Long ,Map<String, Object>> shopDfaMap = new ConcurrentHashMap<>();

    public static final int minMatchType=1;

    public static final int maxMatchType=2;


    public int getMatchType(){
        return  sensitiveConfig.getMatchType();
    }

    /**
     * 初始化 shopId 敏感词数据，如果已经初始化过，则不再进行初始化
     * @param shopId       shopId
     */
    public void init(Long shopId){
        if(!shopDfaMap.containsKey(shopId) || shopDfaMap.get(shopId)==null || shopDfaMap.get(shopId).isEmpty()){
            createDFAHashMap(shopId , sensitiveWordService.getSensitiveWords(shopId));
        }
    }

    /*{日=
     *  {本=
     *      {人={isEnd=1},
     *      鬼=
     *          {子={isEnd=1},
     *          isEnd=0},
     *      isEnd=0},
     *  isEnd=0},
     *
     * 大=
     *  {汉=
     *      {民={isEnd=0,
     *          族={isEnd=1}},
     *      isEnd=0},
     *  isEnd=0,
     *  中={isEnd=0,
     *      华={isEnd=1,
     *          帝={isEnd=0,
     *              国={isEnd=1}}}}}}*/
    /**set作为敏感词，创建出对应的dfa的Map，以供检验敏感词
     * @param shopId
     * @param wrods
     */
    public void createDFAHashMap(Long shopId , Set<String> wrods){
        if(wrods==null){
            wrods = new HashSet<>();
        }
        if(wrods.isEmpty()){
            wrods.add("法轮功");
        }
        Map<String, Object> nowMap;
        //根据set的大小，创建map的大小
        Map<String, Object> dfaMap=new ConcurrentHashMap<>(wrods.size());
        //对set里的字符串进行循环
        for(String key:wrods){
            //对每个字符串最初，nowMap就是dfaMap
            nowMap=dfaMap;
            for(int i=0;i<key.length();i++){
                //一个个字符循环
                String nowChar=String.valueOf(key.charAt(i));
                //根据nowChar得到nowMap里面对应的value
                Map<String, Object> map=(Map<String, Object>)nowMap.get(nowChar);
                //如果map为空，则说明nowMap里面没有以nowChar开头的东西，则创建一个新的hashmap，
                //以nowChar为key，新的map为value，放入nowMap
                if(map==null){
                    map=new ConcurrentHashMap<String,Object>();
                    nowMap.put(nowChar, map);
                }
                //nowMap=map，就是nowChar对应的对象
                nowMap=map;
                //最后在nowMap里设置isEnd
                //如果nowMap里面已经有isEnd，并且为1，说明以前已经有关键字了，就不再设置isEnd
                //因为如果没有这一步，大中华和大中华帝国，先设置大中华
                //在大中华帝国设置的时候，华对应的map有isEnd=1，如果这时对它覆盖，就会isEnd=0，导致大中华这个关键字失效
                if(nowMap.containsKey("isEnd")&&nowMap.get("isEnd").equals("1")){
                    continue;
                }
                if(i!=key.length()-1){
                    nowMap.put("isEnd", "0");
                }
                else{
                    nowMap.put("isEnd", "1");
                }
            }
        }

        shopDfaMap.put(shopId , dfaMap);
    }


    /** 用创建的dfaMap，根据matchType检验字符串string是否包含敏感词，返回包含所有对于敏感词的set
     * @param shopId  shopId
     * @param string 要检查是否有敏感词在内的字符串
     * @param matchType 检查类型，如: 大中华帝国牛逼 , 对应: 大中华和大中华帝国 , 两个关键字，1为最小检查，会检查出大中华，2位最大，会检查出大中华帝国
     * @return
     */
    public Set<String> getSensitiveWordByDFAMap(Long shopId , String string,int matchType){
        Set<String> set=new HashSet<>();
        if(string==null || StringUtil.isNullOrEmpty(string.trim())){
            return set;
        }

        for(int i=0;i<string.length();i++){
            //matchType是针对同一个begin的后面，在同一个begin匹配最长的还是最短的敏感词
            int length=getSensitiveLengthByDFAMap(shopId , string,i,matchType);
            if(length>0){
                set.add(string.substring(i,i+length));
                //这个对应的是一个敏感词内部的关键字（不包括首部），如果加上，大中华帝国，对应大中华和中华两个敏感词，只会对应大中华而不是两个
                //i=i+length-1;//减1的原因，是因为for会自增
            }
        }
        return set;
    }

    /**如果存在，则返回敏感词字符的长度，不存在返回0
     * @param shopId  shopId
     * @param string
     * @param beginIndex
     * @param matchType  1：最小匹配规则，2：最大匹配规则
     * @return
     */
    public int getSensitiveLengthByDFAMap(Long shopId ,String string,int beginIndex,int matchType){
        //当前匹配的长度
        int nowLength=0;
        //最终匹配敏感词的长度，因为匹配规则2，如果大中华帝，对应大中华，大中华帝国，在华的时候，nowLength=3，因为是最后一个字，将nowLenth赋给resultLength
        //然后在帝的时候，now=4，result=3，然后不匹配，resultLength就是上一次最大匹配的敏感词的长度
        int resultLength=0;
        if(!shopDfaMap.containsKey(shopId) || shopDfaMap.get(shopId)==null){
            return 0;
        }
        Map<String, Object> nowMap= shopDfaMap.get(shopId);
        for(int i=beginIndex;i<string.length();i++){
            String nowChar=String.valueOf(string.charAt(i));
            //根据nowChar得到对应的map，并赋值给nowMap
            nowMap=(Map<String, Object>)nowMap.get(nowChar);
            //nowMap里面没有这个char，说明不匹配，直接返回
            if(nowMap==null){
                break;
            }
            else{
                nowLength++;
                //如果现在是最后一个，更新resultLength
                if("1".equals(nowMap.get("isEnd"))){
                    resultLength=nowLength;
                    //如果匹配模式是最小，直接匹配到，退出
                    //匹配模式是最大，则继续匹配，resultLength保留上一次匹配到的length
                    if(matchType==minMatchType){
                        break;
                    }
                }
            }
        }
        return resultLength;
    }

    /**
     * 清除 DFAUtil中相关的缓存
     * @param shopId
     */
    public void clearCache(Long shopId) {
       sensitiveWordService.clearCache(shopId);
        shopDfaMap.remove(shopId);
    }
}
