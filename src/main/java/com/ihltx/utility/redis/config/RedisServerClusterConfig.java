package com.ihltx.utility.redis.config;


import lombok.Data;

/**
 * Redis 集群配置
 */
@Data
public class RedisServerClusterConfig {
	//集群节点列表
    private String[] nodes;
    //重定向的最大数量，比如第一台挂了，连第二台，第二台挂了连第三台，这个重新连接的最大数量
    private Integer maxRedirects = 1;

}
