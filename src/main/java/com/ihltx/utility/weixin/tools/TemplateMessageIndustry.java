package com.ihltx.utility.weixin.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板消息所属行业定义类
 */
public class TemplateMessageIndustry {
    public static final Map<String , Map<Integer , String>> Industries = new HashMap<>();

    static {
        Map<Integer , String> maps= new HashMap<>();
        maps.put(1,"互联网/电子商务");
        maps.put(2,"IT软件与服务");
        maps.put(3,"IT硬件与设备");
        maps.put(4,"电子技术");
        maps.put(5,"通信与运营商");
        maps.put(6,"网络游戏");
        Industries.put("IT科技" , maps);

        maps= new HashMap<>();
        maps.put(7,"银行");
        maps.put(8,"基金理财信托");
        maps.put(9,"保险");
        Industries.put("金融业" , maps);

        maps= new HashMap<>();
        maps.put(10,"餐饮");
        Industries.put("餐饮" , maps);

        maps= new HashMap<>();
        maps.put(11,"酒店");
        maps.put(12,"旅游");
        Industries.put("酒店旅游" , maps);

        maps= new HashMap<>();
        maps.put(13,"快递");
        maps.put(14,"物流");
        maps.put(15,"仓储");
        Industries.put("运输与仓储" , maps);

        maps= new HashMap<>();
        maps.put(16,"培训");
        maps.put(17,"院校");
        Industries.put("教育" , maps);

        maps= new HashMap<>();
        maps.put(18,"学术科研");
        maps.put(19,"交警");
        maps.put(20,"博物馆");
        maps.put(21,"公共事业非盈利机构");
        Industries.put("政府与公共事业" , maps);

        maps= new HashMap<>();
        maps.put(22,"医药医疗");
        maps.put(23,"护理美容");
        maps.put(24,"保健与卫生");
        Industries.put("医药护理" , maps);

        maps= new HashMap<>();
        maps.put(25,"汽车相关");
        maps.put(26,"摩托车相关");
        maps.put(27,"火车相关");
        maps.put(28,"飞机相关");
        Industries.put("交通工具" , maps);

        maps= new HashMap<>();
        maps.put(29,"建筑");
        maps.put(30,"物业");
        Industries.put("房地产" , maps);

        maps= new HashMap<>();
        maps.put(31,"消费品");
        Industries.put("消费品" , maps);

        maps= new HashMap<>();
        maps.put(32,"法律");
        maps.put(33,"会展");
        maps.put(34,"中介服务");
        maps.put(35,"认证");
        maps.put(36,"审计");
        Industries.put("商业服务" , maps);

        maps= new HashMap<>();
        maps.put(37,"传媒");
        maps.put(38,"体育");
        maps.put(39,"娱乐休闲");
        Industries.put("文体娱乐" , maps);

        maps= new HashMap<>();
        maps.put(40,"印刷");
        Industries.put("印刷" , maps);

        maps= new HashMap<>();
        maps.put(41,"其它");
        Industries.put("其它" , maps);

    }

}
