package com.ihltx.utility.redis.config;


import lombok.Data;

@Data
public class RedisServerConfig {
    private String host = "127.0.0.1";
    private Integer port = 6379;
    private String password = "";
    private Integer database = 0;
	private Boolean ssl = false;
	private Integer timeout = 3000;


	private Integer maxActive = 8;
	private Integer maxIdle = 8;
	private Integer minIdle = 0;
	private Long maxWait = -1L;
	private Long shutDownTimeout = 100L;
	private Boolean testOnBorrow = false;

	private Long minEvictableIdleTimeMillis = 60000L;
	private Long timeBetweenEvictionRunsMillis = 30000L;
	private Integer numTestsPerEvictionRun = -1;

	private Boolean lifo = true;
	private Boolean fairness = false;
	private Long softMinEvictableIdleTimeMillis = -1L;
	private Long evictorShutdownTimeoutMillis = 10000L;
	private Boolean testOnCreate = false;
	private Boolean testOnReturn = false;
	private Boolean testWhileIdle = true;

	private Boolean blockWhenExhausted = true;
	private Boolean jmxEnabled = true;
	private String jmxNameBase = null;
	private String jmxNamePrefix = "pool";


	/**
	 * Redis服务器模式， 0--单点/主从模式   1--集群模式     2--哨兵模式
	 */
	private Integer redisServerMode = 0;

	/**
	 * 集群模式配置
	 */
	private RedisServerClusterConfig cluster;

	/**
	 * 哨兵模式配置
	 */
	private RedisServerSentinelConfig sentinel;

}
