package com.ihltx.utility.es;

import com.ihltx.utility.es.config.ElasticSearchConfig;
import com.ihltx.utility.es.service.ElasticSearchUtil;
import com.ihltx.utility.es.service.impl.ElasticSearchUtilImpl;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.logging.log4j.util.Strings;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(ElasticSearchConfig.class)
@ConditionalOnProperty(prefix = "ihltx.elasticsearch" , name = "enable" , havingValue = "true")
public class ElasticSearchAutoConfiguration {

	@Autowired
	private ElasticSearchConfig elasticSearchConfig;
	
	@Bean(name = "elasticSearchUtil")
	@Primary
	@ConditionalOnMissingBean(ElasticSearchUtil.class)
	public ElasticSearchUtil getElasticSearchUtil() {
		ElasticSearchUtil elasticSearchUtil =new ElasticSearchUtilImpl();
		elasticSearchUtil.setElasticSearchConfig(elasticSearchConfig);
		String[] clusterNodes = elasticSearchConfig.getClusterNodes().split(",");
		
		HttpHost[] httpHosts = new HttpHost[clusterNodes.length];
		for(int i=0;i<clusterNodes.length;i++) {
			String node = clusterNodes[i];
			if(!Strings.isEmpty(node)) {
				String[] nodes = node.split(":");
				if(nodes.length==2) {
					httpHosts[i] = new HttpHost(nodes[0], Integer.valueOf(nodes[1]), elasticSearchConfig.getClusterNodeSchema());
				}
			}
		}
		
		RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
				 RestClient.builder(
						 httpHosts
				 ).setRequestConfigCallback(
						 new RestClientBuilder.RequestConfigCallback() {
							 // ?????????????????????RequestConfig.Builder???????????????????????????????????????????????????
							 @Override
							 public RequestConfig.Builder customizeRequestConfig(
									 RequestConfig.Builder requestConfigBuilder) {
								 return requestConfigBuilder.setConnectTimeout(elasticSearchConfig.getConnectTimeout() * 1000) // ????????????????????????1??????
										 .setSocketTimeout(elasticSearchConfig.getSocketTimeout() * 1000)
										 .setConnectionRequestTimeout(elasticSearchConfig.getConnectionRequestTimeout() * 1000);// ???????????????????????????30??????//????????????????????????????????????30???????????????100*1000??????
							 }
						 }
				 )
	    );
		Boolean rs = false;
		try {
			rs = restHighLevelClient.ping(RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(rs){
			elasticSearchUtil.setRestHighLevelClient(restHighLevelClient);
			return elasticSearchUtil;
		}else{
			return  null;
		}
	}


}