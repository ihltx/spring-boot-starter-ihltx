package com.ihltx.utility.apiversion.conditions;

import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.util.WebUtil;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;

public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {

    private String defaultVersion = "1.0.0.0";
    public String getDefaultVersion() {
        return defaultVersion;
    }

    public void setDefaultVersion(String defaultVersion) {
        this.defaultVersion = defaultVersion;
    }

    //api版本号
    private String apiVersion;


    private String versionName = "version";
    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    private String versionOrder = "header,request,cookie";
    public String getVersionOrder() {
        return versionOrder;
    }

    public void setVersionOrder(String versionOrder) {
        this.versionOrder = versionOrder;
    }


    public ApiVersionCondition(String apiVersion) {
        this.apiVersion = apiVersion;
    }
    /**
     * 将不同的筛选条件进行合并
     */
    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        // 采用最后定义优先原则，则方法上的定义覆盖类上面的定义
        return new ApiVersionCondition(other.getApiVersion());
    }
    /**
     * 版本比对，用于排序
     */
    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        //优先匹配最新版本号
        return compareTo(other.getApiVersion(),this.apiVersion)?1:-1;
    }
    /**
     * 根据request的header版本号进行查找匹配的筛选条件
     */
    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        if(StringUtil.isNullOrEmpty(this.versionOrder)){
            this.versionOrder = "header,request,cookie";
        }
        String[] versionOrders = this.versionOrder.split(",");
        String version = null;
        for(int i=0;i< versionOrders.length;i++){
            if(versionOrders[i].equals("header")){
                version = request.getHeader(this.versionName);
                if(!StringUtil.isNullOrEmpty(version)){
                    break;
                }
            }else if(versionOrders[i].equals("request")){
                version = request.getParameter(this.versionName);
                if(!StringUtil.isNullOrEmpty(version)){
                    break;
                }
            }else if(versionOrders[i].equals("cookie")){
                version = WebUtil.getCookie(request , this.versionName);
                if(!StringUtil.isNullOrEmpty(version)){
                    break;
                }
            }
        }

        if(version!= null){
            if (compareTo(version,this.apiVersion)){
                return this;
            }
        }else{
            if(this.apiVersion.equals(this.defaultVersion)){
                return this;
            }
        }
        return null;
    }
    private boolean compareTo(String version1,String version2){
        //自动转为四段版本号
        int versionSectionCount = (this.defaultVersion.split("\\.")).length;
        String[] version1s = new String[versionSectionCount];
        String[] version2s = new String[versionSectionCount];
        for(int i=0;i<versionSectionCount;i++){
            version1s[i] = version2s[i] = "0";
        }

        String[] split1 = version1.split("\\.");
        for(int i=0;i<split1.length;i++){
            version1s[i] = split1[i];
        }

        DecimalFormat df =new DecimalFormat("0000");
        for(int i=0;i<version1s.length;i++){
            version1s[i] = df.format(Integer.valueOf(version1s[i]));
        }
        StringBuffer sb1 = new StringBuffer();
        for(String s : version1s){
            sb1.append(s);
        }
        Long v1 = Long.valueOf(sb1.toString());

        String[] split2 = version2.split("\\.");
        for(int i=0;i<split2.length;i++){
            version2s[i] = split2[i];
        }

        for(int i=0;i<version2s.length;i++){
            version2s[i] = df.format(Integer.valueOf(version2s[i]));
        }
        StringBuffer sb2 = new StringBuffer();
        for(String s : version2s){
            sb2.append(s);
        }
        Long v2 = Long.valueOf(sb2.toString());
        if(v1 < v2){
            return false;
        }
        return true;
    }
    public String getApiVersion() {
        return apiVersion;
    }

}
