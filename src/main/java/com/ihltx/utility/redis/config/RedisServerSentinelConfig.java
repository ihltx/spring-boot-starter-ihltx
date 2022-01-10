package com.ihltx.utility.redis.config;


import lombok.Data;

/**
 * Redis 哨兵配置
 */
@Data
public class RedisServerSentinelConfig {
	//哨兵节点列表
    private String[] nodes;
    //主节点名称
    private String master;

}
