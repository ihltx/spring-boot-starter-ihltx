package com.ihltx.utility.httpclient.service.impl;

import com.ihltx.utility.httpclient.config.RestTemplateConfig;
import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.util.FileUtil;
import com.ihltx.utility.util.StringUtil;
import com.alibaba.fastjson.JSON;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class RestTemplateUtilImpl implements RestTemplateUtil{
	
	private RestTemplateConfig restTemplateConfig;
	

	public RestTemplateConfig getRestTemplateConfig() {
		return restTemplateConfig;
	}

	public void setRestTemplateConfig(RestTemplateConfig restTemplateConfig) {
		this.restTemplateConfig = restTemplateConfig;
	}

	private RestTemplate restTemplate;

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	/**
	 * 实例化 RestTemplateUtilImpl ，配置RestTemplate 支持http,https 调用，绕过ssl 验证。
	 * @param connectTimeout		指定连接超时时间，单位：ms
	 * @param readTimeout			指定读超时时间，单位：ms
	 */
	public RestTemplateUtilImpl(Integer connectTimeout , Integer readTimeout)  {
		try{
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(readTimeout) // 服务器返回数据(response)的时间，超时抛出read timeout
					.setConnectTimeout(connectTimeout) // 连接上服务器(握手成功)的时间，超时抛出connect timeout
					.setConnectionRequestTimeout(connectTimeout)// 从连接池中获取连接的超时时间，超时抛出ConnectionPoolTimeoutException
					.build();
			SSLContext sslContext = SSLContextBuilder.create().setProtocol(SSLConnectionSocketFactory.SSL).loadTrustMaterial((x, y) -> true).build();
			HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setSSLContext(sslContext).setSSLHostnameVerifier((x, y) -> true).build();
			ClientHttpRequestFactory requestFactory =  new HttpComponentsClientHttpRequestFactory(httpClient);
			this.restTemplate = new RestTemplate(requestFactory);
		}catch (Exception err){
			err.printStackTrace();
		}
	}

	/**
	 * 针对微信商户及指定apiclient_cert证书文件 实例化 RestTemplateUtilImpl以发起微信：企业付款到零钱/提现/微信退款请求业务
	 * @param connectTimeout		指定连接超时时间，单位：ms
	 * @param readTimeout			指定读超时时间，单位：ms
	 * @param mch_id				微信商户id
	 * @param apiclientCertFileName	微信商户apiclient_cert证书文件物理全路径名
	 * @throws Exception
	 */

	public RestTemplateUtilImpl(Integer connectTimeout , Integer readTimeout, String mch_id, String apiclientCertFileName) throws Exception  {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(apiclientCertFileName));
		keyStore.load(instream, mch_id.toCharArray());

		SSLContext sslcontext = SSLContextBuilder.create()
				.loadKeyMaterial(keyStore, mch_id.toCharArray())
				.build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, NoopHostnameVerifier.INSTANCE);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		factory.setConnectionRequestTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		this.restTemplate = new RestTemplate(factory);
	}



	/**
	 * 基于get请求获取url数据，返回字符串
	 * 
	 * @param url	要请求的url
	 * @return      字符串
	 */
	public String get(String url) {
		return getForObject(url, null , String.class);
	}
	
	/**
	 * 附带headers 基于get请求获取url数据，返回字符串
	 * 
	 * @param url		要请求的url
	 * @param headers   附带的头部信息
	 * @return          字符串
	 */
	public String get(String url , Map<String,String> headers) {		
        return getForObject(url, headers , String.class);
	}
	
	/**
	 * 基于get请求获取url数据，返回指定数据类型对象
	 * 
	 * @param <T>      指定数据类型泛型
	 * @param url	      要请求的url
	 * @param clazz    指定数据类型
	 * @return         指定数据类型对象
	 */
	public <T> T getForObject(String url, Class<T> clazz) {
		return getForObject(url, null , clazz);
	}
	
	/**
	 * 附带headers 基于get请求获取url数据，返回指定数据类型对象
	 * 
	 * @param <T>       指定数据类型泛型
	 * @param url		要请求的url
	 * @param headers   附带的头部信息
	 * @param clazz     指定数据类型
	 * @return          指定数据类型对象
	 */
	public <T> T getForObject(String url , Map<String,String> headers, Class<T> clazz) {
		// header填充
		HttpEntity<MultiValueMap<String, Object>> request = null;
		if(headers!=null && !headers.isEmpty()) {
			HttpHeaders httpHeaders = new HttpHeaders();
	        for(String key : headers.keySet()) {
	            httpHeaders.set(key, headers.get(key).toString());
	        }
	        request = new HttpEntity<MultiValueMap<String, Object>>(null, httpHeaders);
		}
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(handleUrl(url));
        return restTemplate.exchange(builder.build().toString(), HttpMethod.GET, request, clazz).getBody();
	}
	
	
	/**
	 * 基于options请求获取url数据，返回字符串
	 * 
	 * @param url	要请求的url
	 * @return      字符串
	 */
	public String options(String url) {
		return optionsForObject(url , null , String.class);
	}
	
	/**
	 * 附带headers 基于get请求获取url数据，返回字符串
	 * 
	 * @param url		要请求的url
	 * @param headers   附带的头部信息
	 * @return          字符串
	 */
	public String options(String url , Map<String,String> headers) {
		return optionsForObject(url , null , String.class);
	}
	
	/**
	 * 基于options请求获取url数据，返回指定数据类型对象
	 * 
	 * @param <T>      指定数据类型泛型
	 * @param url	      要请求的url
	 * @param clazz    指定数据类型
	 * @return         指定数据类型对象
	 */
	public <T> T optionsForObject(String url, Class<T> clazz) {
		return optionsForObject(url , null , clazz);
	}	

	
	/**
	 * 附带headers 基于get请求获取url数据，返回指定数据类型对象
	 * 
	 * @param <T>       指定数据类型泛型
	 * @param url		要请求的url
	 * @param headers   附带的头部信息
	 * @param clazz     指定数据类型
	 * @return          指定数据类型对象
	 */
	public <T> T optionsForObject(String url , Map<String,String> headers, Class<T> clazz) {
		// header填充
		HttpEntity<MultiValueMap<String, Object>> request = null;
		if(headers!=null && !headers.isEmpty()) {
			 HttpHeaders httpHeaders = new HttpHeaders();
		        for(String key : headers.keySet()) {
		            httpHeaders.set(key, headers.get(key).toString());
		        }
		        request = new HttpEntity<MultiValueMap<String, Object>>(null, httpHeaders);
		}
       
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(handleUrl(url));
        return restTemplate.exchange(builder.build().toString(), HttpMethod.OPTIONS, request, clazz).getBody();
	}
	

	/**
	 * 基于application/json内容类型发起post请求，返回字符串
	 * 
	 * @param url         要请求的url
	 * @param postObject  请求对象
	 * @return            字符串
	 */
	public String post(String url, Object postObject) {
		return postForObject(url , postObject , null , String.class);
	}
	
	/**
	 * 附带headers 基于application/json内容类型发起post请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param postObject   请求对象
	 * @param headers      附带的头部信息
	 * @return             字符串
	 */
	public String post(String url, Object postObject , Map<String, String> headers) {	
		return postForObject(url , postObject , headers , String.class);
	}	
	
	/**
	 * 基于application/json内容类型发起post请求，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param postObject   请求对象
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	public <T> T postForObject(String url, Object postObject, Class<T> clazz) {
		return postForObject(url , postObject , null , clazz);
	}
	
	
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
	public <T> T postForObject(String url, Object postObject , Map<String, String> headers, Class<T> clazz) {	
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		if(headers!=null && !headers.isEmpty()) {
			for(String key : headers.keySet()) {
	            httpHeaders.set(key, headers.get(key).toString());
			}
		}		
		HttpEntity<String> request = new HttpEntity<String>(JSON.toJSONString(postObject), httpHeaders);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(handleUrl(url));
	    return restTemplate.exchange(builder.build().toString(), HttpMethod.POST, request, clazz).getBody();
		
	}


	/**
	 * 基于application/json内容类型发起post请求，返回指定数据类型对象
	 *
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param xmlContent   xml内容
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	public <T> T postXmlForObject(String url, String xmlContent, Class<T> clazz) {
		return postXmlForObject(url , xmlContent , null , clazz);
	}

	/**
	 * 附带headers 基于application/json内容类型发起post请求，返回指定数据类型对象
	 *
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param xmlContent   xml内容
	 * @param headers      附带的头部信息
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	public <T> T postXmlForObject(String url, String xmlContent , Map<String, String> headers, Class<T> clazz) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_XML);
		if(headers!=null && !headers.isEmpty()) {
			for(String key : headers.keySet()) {
				httpHeaders.set(key, headers.get(key).toString());
			}
		}
		HttpEntity<String> request = new HttpEntity<String>(xmlContent, httpHeaders);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(handleUrl(url));
		return restTemplate.exchange(builder.build().toString(), HttpMethod.POST, request, clazz).getBody();

	}

	/**
	 * 基于post form请求url数据，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param postForm     请求Map对象
	 * @return             字符串
	 */
	public String postForm(String url, Map<String, Object> postForm) {
		return postForm(handleUrl(url), postForm, null , null);
	}
	
	/**
	 *  附带headers基于post form请求url数据，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param postForm     请求Map对象
	 * @param headers      附带的头部信息
	 * @return             字符串
	 */
	public String postForm(String url, Map<String, Object> postForm, Map<String, String> headers) {
		return postForm(handleUrl(url), postForm, null , headers);
	}
	

	/**
	 *  附带headers基于post form请求url数据，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param postForm     请求Map对象
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	public <T> T postFormForObject(String url, Map<String, Object> postForm, Class<T> clazz) {
		return postFormForObject(handleUrl(url), postForm, null , null , clazz);
	}
	
	
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
	public <T> T postFormForObject(String url, Map<String, Object> postForm, Map<String, String> headers, Class<T> clazz) {
		return postFormForObject(handleUrl(url), postForm, null , headers , clazz);
	}

	/**
	 *  附带headers 基于post form文件上传请求url数据，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param postForm     请求Map对象
	 * @param headers      附带的头部信息
	 * @param files        要上传的本地文件集合
	 * @return             字符串
	 */
	public String postForm(String url, Map<String, Object> postForm, Map<String, String> headers, Map<String, String> files) {
		return postFormForObject(url, postForm , headers , files , String.class);
	}	
	
	
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
	public <T> T postFormForObject(String url, Map<String, Object> postForm, Map<String, String> headers, Map<String, String> files, Class<T> clazz) {
		// 请求头设置
		HttpHeaders httpHeaders = new HttpHeaders();
		if (files != null && files.size()>0) {
			httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		} else {
			httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		}
		if(headers!=null && !headers.isEmpty()) {
			for (String key : headers.keySet()) {
				httpHeaders.set(key, headers.get(key));
			}
		}
		// 提交参数设置
		MultiValueMap<String, Object> p = new LinkedMultiValueMap<>();
		if(postForm!=null && !postForm.isEmpty()) {
			for (String key : postForm.keySet()) {
				p.add(key, postForm.get(key));
			}
		}
		if (files != null && files.size()>0) {
			for (String key : files.keySet()) {
				FileSystemResource resource = new FileSystemResource(files.get(key));
				p.add(key, resource);
			}
		}

		// 提交请求
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(p, httpHeaders);
		return restTemplate.postForObject(handleUrl(url), entity, clazz);
	}	
	

	/**
	 * 基于application/json内容类型发起http  put请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param putObject    请求对象
	 * @return             字符串
	 */
	public String put(String url, Object putObject) {		
	    return putForObject(url , putObject , null , String.class);
	}
	
	/**
	 * 基于application/json内容类型发起http  put请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param putObject    请求对象
	 * @param headers      附带的头部信息
	 * @return             字符串
	 */
	public String put(String url, Object putObject, Map<String, String> headers) {		
	    return putForObject(url , putObject , headers , String.class);
	}
	
	/**
	 * 基于application/json内容类型发起http  put请求，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param putObject    请求对象
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	public <T> T putForObject(String url, Object putObject, Class<T> clazz) {
		 return putForObject(url , putObject , null , clazz);
	}
	

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
	public <T> T putForObject(String url, Object putObject, Map<String, String> headers, Class<T> clazz) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		if(headers!=null && !headers.isEmpty()) {
			for(String key : headers.keySet()) {
	            httpHeaders.set(key, headers.get(key).toString());
			}
		}		
		HttpEntity<String> request = new HttpEntity<String>(JSON.toJSONString(putObject), httpHeaders);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(handleUrl(url));
	    return restTemplate.exchange(builder.build().toString(), HttpMethod.PUT, request, clazz).getBody();
	}
	
	
	/**
	 * 基于put form请求url数据，返回字符串
	 * 
	 * @param url         要请求的url
	 * @param putForm     请求对象
	 * @return            字符串
	 */
	public String putForm(String url, Map<String, Object> putForm) {
		return putFormForObject(handleUrl(url), putForm, null , null , String.class);
	}
	
	/**
	 *  附带headers基于put form请求url数据，返回字符串
	 * 
	 * @param url           要请求的url
	 * @param putForm       请求对象
	 * @param headers       附带的头部信息
	 * @return              字符串
	 */
	public String putForm(String url, Map<String, Object> putForm, Map<String, String> headers) {
		return putFormForObject(handleUrl(url), putForm, headers , null , String.class);
	}
	
	/**
	 *  附带headers基于put form请求url数据，返回字符串
	 * 
	 * @param url           要请求的url
	 * @param putForm       请求对象
	 * @param headers       附带的头部信息
	 * @param files         要上传的本地文件集合
	 * @return              字符串
	 */
	public String putForm(String url, Map<String, Object> putForm, Map<String, String> headers, Map<String, String> files) {
		return putFormForObject(handleUrl(url), putForm, headers , files , String.class);
	}
	
	/**
	 * 基于put form请求url数据，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param putForm      请求对象
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	public <T> T putFormForObject(String url, Map<String, Object> putForm, Class<T> clazz) {
		return putFormForObject(handleUrl(url), putForm, null , null , clazz);
	}
	
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
	public <T> T putFormForObject(String url, Map<String, Object> putForm, Map<String, String> headers, Class<T> clazz) {
		return putFormForObject(handleUrl(url), putForm, null , headers , clazz);
	}

	
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
	public <T> T putFormForObject(String url, Map<String, Object> putForm, Map<String, String> headers, Map<String, String> files, Class<T> clazz) {
		// 请求头设置
		HttpHeaders httpHeaders = new HttpHeaders();
		if (files != null && files.size()>0) {
			httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		} else {
			httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		}
		if(headers!=null && !headers.isEmpty()) {
			for (String key : headers.keySet()) {
				httpHeaders.set(key, headers.get(key));
			}
		}
		// 提交参数设置
		MultiValueMap<String, Object> p = new LinkedMultiValueMap<>();
		if(putForm!=null && !putForm.isEmpty()) {
			for (String key : putForm.keySet()) {
				p.add(key, putForm.get(key));
			}
		}
		if (files != null && files.size()>0) {
			for (String key : files.keySet()) {
				FileSystemResource resource = new FileSystemResource(files.get(key));
				p.add(key, resource);
			}
		}

		// 提交请求
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(p, httpHeaders);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(handleUrl(url));
	    return restTemplate.exchange(builder.build().toString(), HttpMethod.PUT, entity, clazz).getBody();
	}
	
	
	/**
	 * 基于application/json内容类型发起http  delete请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @return             字符串
	 */
	public String delete(String url) {
	    return deleteForObject(url , null , null , String.class);
	}		
	
	/**
	 * 基于application/json内容类型发起http  delete请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param deleteObject 请求对象
	 * @return             字符串
	 */
	public String delete(String url, Object deleteObject) {
		 return deleteForObject(url , deleteObject , null , String.class);
	}
	
	/**
	 * 基于application/json内容类型发起http  delete请求，返回字符串
	 * 
	 * @param url          要请求的url
	 * @param deleteObject 请求对象
	 * @param headers      附带的头部信息
	 * @return             字符串
	 */
	public String delete(String url, Object deleteObject ,  Map<String, String> headers) {
		 return deleteForObject(url , deleteObject , headers , String.class);
	}
	
	/**
	 * 基于application/json内容类型发起http  delete请求，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	public <T> T deleteForObject(String url, Class<T> clazz) {
	    return deleteForObject(url , null , null , clazz);
	}
	
	/**
	 * 基于application/json内容类型发起http  delete请求，返回指定数据类型对象
	 * 
	 * @param <T>          指定数据类型泛型
	 * @param url          要请求的url
	 * @param deleteObject 请求对象
	 * @param clazz        指定数据类型
	 * @return             指定数据类型对象
	 */
	public <T> T deleteForObject(String url, Object deleteObject, Class<T> clazz) {
	    return deleteForObject(url , deleteObject , null , clazz);
	}
	
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
	public <T> T deleteForObject(String url, Object deleteObject, Map<String, String> headers, Class<T> clazz) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		if(headers!=null && ! headers.isEmpty()) {
			for(String key : headers.keySet()) {
	            httpHeaders.set(key, headers.get(key).toString());
			}
		}
		String body = null;
		if(deleteObject!=null) {
			body = JSON.toJSONString(deleteObject);
		}
		
		HttpEntity<String> request = new HttpEntity<String>(body, httpHeaders);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(handleUrl(url));
	    return restTemplate.exchange(builder.build().toString(), HttpMethod.DELETE, request, clazz).getBody();
	}
	
	

	/**
	 * 处理url: 根据是否启用ribbon来决定是否开启负责负责均衡
	 * 
	 * @param url
	 * @return String url
	 */
	private String handleUrl(String url) {
//		if(restTemplateConfig.getEnableRibbon()) {
//			try {
//				if(loadBalancerClient==null) {
//					SpringClientFactory springClientFactory = new SpringClientFactory();
//					loadBalancerClient =new RibbonLoadBalancerClient(springClientFactory);
//				}
//				if(loadBalancerClient==null) return url;
//				URL ourl = new URL(url);
//				//if(ourl.getPort()==80 || ourl.getPort()==443)
//				{
//					ServiceInstance serviceInstance = loadBalancerClient.choose(ourl.getHost());
//					if(serviceInstance!=null) {
//						URI originalUri = null;
//						if(!Strings.isNullOrEmpty(ourl.getQuery())) {
//							originalUri = new URI(ourl.getPath() + "?" + ourl.getQuery());
//						}else {
//							originalUri = new URI(ourl.getPath());
//						}
//						return loadBalancerClient.reconstructURI(serviceInstance, originalUri).toString();
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		return url;
	}


	@Override
	public String download(String url, String targetDirPath) {

		try {
			URL urlResource =new URL(url);
			String fileName = FileUtil.getFileBaseName(url);
			targetDirPath = StringUtil.trim(targetDirPath.replaceAll("\\\\","/") , "/") + "/";
			InputStream fis = urlResource.openStream();
			if(fis!=null){
				FileOutputStream fos =new FileOutputStream(targetDirPath + fileName);
				byte[]  buffer = new byte[1024];
				int readNumber=0;
				while ((readNumber = fis.read(buffer))!=-1){
					fos.write(buffer,0,readNumber);
				}
				fos.close();
				fis.close();
				return targetDirPath + fileName;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}


	@Override
	public byte[] getContent(String url) {
		try {
			URL u = new URL(url);
			URLConnection con = u.openConnection();
			InputStream is = con.getInputStream();
			ByteArrayOutputStream bos =new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int rc = 0;
			while ((rc = is.read(buffer))>0){
				bos.write(buffer,0,rc);
			}
			is.close();
			return bos.toByteArray();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

