package com.ihltx.utility.httpclient.service;

import java.util.Map;

import com.ihltx.utility.httpclient.config.RestTemplateConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public interface RestTemplateUtil{

	/**
	 * 获取 RestTemplateConfig 配置
	 * @return
	 */
	RestTemplateConfig getRestTemplateConfig();

	/**
	 * 设置 RestTemplateConfig 配置
	 * @param restTemplateConfig
	 */
	void setRestTemplateConfig(RestTemplateConfig restTemplateConfig);

	/**
	 * 设置 RestTemplate 组件
	 *
	 * @param restTemplate
	 */
	void setRestTemplate(RestTemplate restTemplate);

	/**
	 * 获取 RestTemplate 组件
	 * @return
	 */
	RestTemplate getRestTemplate();

	/**
	 * 基于get请求获取url数据，返回字符串
	 * 
	 * @param url	要请求的url
	 * @return      字符串
	 */
	String get(String url);
	
	/**
	 * 附带headers 基于get请求获取url数据，返回字符串
	 * 
	 * @param url		要请求的url
	 * @param headers   附带的头部信息
	 * @return          字符串
	 */
	String get(String url, Map<String, String> headers);
	
	/**
	 * 基于get请求获取url数据，返回指定数据类型对象
	 * 
	 * @param <T>      指定数据类型泛型
	 * @param url	      要请求的url
	 * @param clazz    指定数据类型
	 * @return         指定数据类型对象
	 */
	<T> T getForObject(String url, Class<T> clazz);
	
	/**
	 * 附带headers 基于get请求获取url数据，返回指定数据类型对象
	 * 
	 * @param <T>       指定数据类型泛型
	 * @param url		要请求的url
	 * @param headers   附带的头部信息
	 * @param clazz     指定数据类型
	 * @return          指定数据类型对象
	 */
	<T> T getForObject(String url, Map<String, String> headers, Class<T> clazz);
	
	
	/**
	 * 基于options请求获取url数据，返回字符串
	 * 
	 * @param url	要请求的url
	 * @return      字符串
	 */
	String options(String url);
	
	/**
	 * 附带headers 基于get请求获取url数据，返回字符串
	 * 
	 * @param url		要请求的url
	 * @param headers   附带的头部信息
	 * @return          字符串
	 */
	String options(String url, Map<String, String> headers);
	
	/**
	 * 基于options请求获取url数据，返回指定数据类型对象
	 * 
	 * @param <T>      指定数据类型泛型
	 * @param url	      要请求的url
	 * @param clazz    指定数据类型
	 * @return         指定数据类型对象
	 */
	<T> T optionsForObject(String url, Class<T> clazz);

	
	/**
	 * 附带headers 基于get请求获取url数据，返回指定数据类型对象
	 * 
	 * @param <T>       指定数据类型泛型
	 * @param url		要请求的url
	 * @param headers   附带的头部信息
	 * @param clazz     指定数据类型
	 * @return          指定数据类型对象
	 */
	<T> T optionsForObject(String url, Map<String, String> headers, Class<T> clazz);
	

	/**
	 * 基于application/json内容类型发起post请求，返回字符串
	 * 
	 * @param url         要请求的url
	 * @param postObject  请求对象
	 * @return            字符串
	 */
	String post(String url, Object postObject);
	
	/**
	 * 附带headers 基于application/json内容类型发起post请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param postObject   请求对象
	 * @param headers      附带的头部信息
	 * @return             字符串
	 */
	String post(String url, Object postObject, Map<String, String> headers);
	
	/**
	 * 基于application/json内容类型发起post请求，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param postObject   请求对象
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T postForObject(String url, Object postObject, Class<T> clazz);
	
	
	/**
	 * 附带headers 基于application/json内容类型发起post请求，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param postObject     请求对象
	 * @param headers      附带的头部信息
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T postForObject(String url, Object postObject, Map<String, String> headers, Class<T> clazz);


	/**
	 * 基于application/xml内容类型发起post请求，返回指定数据类型对象
	 *
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param xmlContent   xml内容
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T postXmlForObject(String url, String xmlContent, Class<T> clazz);

	/**
	 * 附带headers 基于application/xml内容类型发起post请求，返回指定数据类型对象
	 *
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param xmlContent   xml内容
	 * @param headers      附带的头部信息
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T postXmlForObject(String url, String xmlContent, Map<String, String> headers, Class<T> clazz);

	/**
	 * 基于post form请求url数据，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param postForm     请求Map对象
	 * @return             字符串
	 */
	String postForm(String url, Map<String, Object> postForm);
	
	/**
	 *  附带headers基于post form请求url数据，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param postForm     请求Map对象
	 * @param headers      附带的头部信息
	 * @return             字符串
	 */
	String postForm(String url, Map<String, Object> postForm, Map<String, String> headers);
	

	/**
	 *  附带headers基于post form请求url数据，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param postForm     请求Map对象
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T postFormForObject(String url, Map<String, Object> postForm, Class<T> clazz);
	
	
	/**
	 *  附带headers基于post form请求url数据，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param postForm     请求Map对象
	 * @param headers      附带的头部信息
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T postFormForObject(String url, Map<String, Object> postForm, Map<String, String> headers, Class<T> clazz);

	/**
	 *  附带headers 基于post form文件上传请求url数据，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param postForm     请求Map对象
	 * @param headers      附带的头部信息
	 * @param files        要上传的本地文件集合
	 * @return             字符串
	 */
	String postForm(String url, Map<String, Object> postForm, Map<String, String> headers, Map<String, String> files);
	
	
	/**
	 *  附带headers 基于post form文件上传请求url数据，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param postForm     请求Map对象
	 * @param headers      附带的头部信息
	 * @param files        要上传的本地文件集合
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T postFormForObject(String url, Map<String, Object> postForm, Map<String, String> headers, Map<String, String> files, Class<T> clazz);
	

	/**
	 * 基于application/json内容类型发起http  put请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param putObject    请求对象
	 * @return             字符串
	 */
	String put(String url, Object putObject);
	
	/**
	 * 基于application/json内容类型发起http  put请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param putObject    请求对象
	 * @param headers      附带的头部信息
	 * @return             字符串
	 */
	String put(String url, Object putObject, Map<String, String> headers);
	
	/**
	 * 基于application/json内容类型发起http  put请求，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param putObject    请求对象
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T putForObject(String url, Object putObject, Class<T> clazz);
	

	/**
	 * 基于application/json内容类型发起http  put请求，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param putObject   请求对象
	 * @param headers      附带的头部信息
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T putForObject(String url, Object putObject, Map<String, String> headers, Class<T> clazz);
	
	
	/**
	 * 基于put form请求url数据，返回字符串
	 * 
	 * @param url         要请求的url
	 * @param putForm     请求对象
	 * @return            字符串
	 */
	String putForm(String url, Map<String, Object> putForm);
	
	/**
	 *  附带headers基于put form请求url数据，返回字符串
	 * 
	 * @param url           要请求的url
	 * @param putForm       请求对象
	 * @param headers       附带的头部信息
	 * @return              字符串
	 */
	String putForm(String url, Map<String, Object> putForm, Map<String, String> headers);
	
	/**
	 *  附带headers基于put form请求url数据，返回字符串
	 * 
	 * @param url           要请求的url
	 * @param putForm       请求对象
	 * @param headers       附带的头部信息
	 * @param files         要上传的本地文件集合
	 * @return              字符串
	 */
	String putForm(String url, Map<String, Object> putForm, Map<String, String> headers, Map<String, String> files);
	
	/**
	 * 基于put form请求url数据，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param putForm      请求对象
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T putFormForObject(String url, Map<String, Object> putForm, Class<T> clazz);
	
	/**
	 *  附带headers基于put form请求url数据，返回指定数据类型对象
	 * 
	 * @param <T>           指定数据类型泛型
	 * @param url           要请求的url
	 * @param putForm       请求对象
	 * @param headers       附带的头部信息
	 * @param clazz         指定数据类型
	 * @return              指定数据类型对象
	 */
	<T> T putFormForObject(String url, Map<String, Object> putForm, Map<String, String> headers, Class<T> clazz);

	
	/**
	 *  附带headers 基于put form文件上传请求url数据，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param putForm      请求对象
	 * @param headers      附带的头部信息
	 * @param files        要上传的本地文件集合
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T putFormForObject(String url, Map<String, Object> putForm, Map<String, String> headers, Map<String, String> files, Class<T> clazz);
	
	
	/**
	 * 基于application/json内容类型发起http  delete请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @return             字符串
	 */
	String delete(String url);
	
	/**
	 * 基于application/json内容类型发起http  delete请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param deleteObject 请求对象
	 * @return             字符串
	 */
	String delete(String url, Object deleteObject);
	
	/**
	 * 基于application/json内容类型发起http  delete请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param deleteObject 请求对象
	 * @param headers      附带的头部信息
	 * @return             字符串
	 */
	String delete(String url, Object deleteObject, Map<String, String> headers);
	
	/**
	 * 基于application/json内容类型发起http  delete请求，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T deleteForObject(String url, Class<T> clazz);
	
	/**
	 * 基于application/json内容类型发起http  delete请求，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param deleteObject 请求对象
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T deleteForObject(String url, Object deleteObject, Class<T> clazz);
	
	/**
	 * 基于application/json内容类型发起http  delete请求，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param deleteObject 请求对象
	 * @param headers      附带的头部信息
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	<T> T deleteForObject(String url, Object deleteObject, Map<String, String> headers, Class<T> clazz);

	/**
	 * 下载网络文件
	 * @param url 						网络文件URL
	 * @param targetDirPath 			下载目标文件夹物理路径
	 * @return  String  null  -- 下载失败   否则返回下载文件物理路径
	 */
	String download(String url, String targetDirPath);

	/**
	 * 获取网络文件二进制内容
	 * @param url			网络文件URL
	 * @return  byte[]
	 */
	byte[] getContent(String url);
}

