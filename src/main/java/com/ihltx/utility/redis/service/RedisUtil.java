package com.ihltx.utility.redis.service;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;

public interface RedisUtil {


	/**
	 * 获取当前 RedisTemplate 对象
	 * @return
	 */
	RedisTemplate getCurrRedisTemplate();

	/**
	 * 修改当前 RedisTemplate 对象
	 * @param currRedisTemplate
	 */
	void setCurrRedisTemplate(RedisTemplate currRedisTemplate);


	/**
	 * 对象以json字符串放入
	 *
	 * @param key   键
	 * @param value 值
	 * @return true--成功 false--失败
	 * @throws Exception
	 */
	<T> Boolean set(String key, T value);

	/**
	 * 普通缓存放入并设置时间
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒) 如果time小于等于0 将设置为永不过期
	 * @return true--成功 false--失败
	 * @throws Exception
	 */
	public <T> Boolean set(String key, T value, Long time);

	/**
	 * 对 redis 中指定键对应的数据设置失效时间
	 *
	 * @param key  键
	 * @param time 时间(秒) 如果time小于等于0 将设置为永不过期
	 * @return true--成功 false--失败
	 * @throws Exception
	 */
	public Boolean expire(String key, Long time);

	/**
	 * 从 redis 中根据指定的 key 获取已设置的过期时间
	 *
	 * @param key 键，不能为null
	 * @return 时间（秒） -1-- 永不过期 -2--表示不存在或失败
	 * @throws Exception
	 */
	public Long getExpire(String key);

	/**
	 * 判断 redis 中是否存在指定的 key
	 *
	 * @param key 键，不能为null
	 * @return true -- 表示存在 false--表示不存在
	 * @throws Exception
	 */
	public Boolean exists(String key);

	/**
	 * 从 redis 中移除指定 key 对应的数据
	 *
	 * @param keys 可以传一个值或多个
	 * @return >=0 表示成功
	 * @throws Exception
	 */
	public Long remove(String... keys);

	/**
	 * 从 redis 中获取指定 key 对应的 string 数据
	 *
	 * @param key 键，不能为null
	 * @return key 对应的字符串数据 null--表示失败
	 * @throws Exception
	 */
	String get(String key);

	/**
	 * 从 redis 中获取指定 key 对应的 string 数据，并转换为 T 类型
	 *
	 * @param key   键，不能为null
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return key 对应的数据 null--表示失败
	 * @throws Exception
	 */
	<T> T get(String key, Class<T> clazz);

	/**
	 * 将 redis 中指定 key 对应数据的偏移位置的 bit 位设置为 0/1
	 *
	 * @param key    键，不能为null
	 * @param offset 偏移位置 >=0
	 * @param flag   true表示设置为1，false表示设置为0
	 * @return true--表示成功 false--表示失败
	 * @throws Exception
	 */
	Boolean setBit(String key, Long offset, Boolean flag);

	/**
	 * 判断 redis 中指定 key 的数据对应偏移位置的 bit 位是否为 1
	 *
	 * @param key    键，不能为null
	 * @param offset 偏移位置 >=0
	 * @return true--表示1 false--表示0或失败
	 * @throws Exception
	 */
	Boolean getBit(String key, Long offset);

	/**
	 * 对 redis 中指定 key 的数据递增1，并返回递增后的值
	 *
	 * @param key 键，不能为null
	 * @return Long 返回递增后的值
	 * @throws Exception
	 */
	Long incr(String key);

	/**
	 * 对 redis 中指定 key 的数据递增，并返回递增后的值 不存在key将自动创建并增加1
	 * 
	 * @param key   键，不能为null
	 * @param delta 要增加几（大于0）
	 * @return Long 返回递增后的值
	 * @throws Exception
	 */
	Long incr(String key, Long delta);

	/**
	 * 对 redis 中指定 key 的数据递减1，并返回递减后的值
	 *
	 * @param key 键，不能为null
	 * @return 返回递减后的值
	 * @throws Exception
	 */
	Long decr(String key);

	/**
	 * 对 redis 中指定 key 的数据递减，并返回递减后的值
	 *
	 * @param key   键，不能为null
	 * @param delta 要减少几（大于0）
	 * @return 返回递减后的值
	 * @throws Exception
	 */
	Long decr(String key, Long delta);

	/**
	 * 向hashtable表中放入数据,如果不存在将创建
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param value   值
	 * @return true--成功 false--不存在或失败
	 * @throws Exception
	 */

	<T> Boolean hSet(String key, String hashKey, T value);

	/**
	 * 向hashtable表中放入数据,如果不存在将创建，同时设置整张hashtable表的过期时间
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param value   值
	 * @param time    过期时间(秒) 针对key设置过期时间，如果time小于等于0 将永不过期
	 * @return true--成功 false--失败
	 */

	<T> Boolean hSet(String key, String hashKey, T value, Long time);

	/**
	 * 从hashtable表中获取数据,如果不存在将返回null
	 *
	 * @param key     键 不能为null
	 * @param hashKey 项 不能为null
	 * @return null--表示失败或不存在
	 * @throws Exception
	 */
	String hGet(String key, String hashKey);

	/**
	 * 从hashtable表中获取指定 key 指定hashKey对应的指定类型数据(T 类型)
	 *
	 * @param <T>     类型
	 * @param key     键，不能为null
	 * @param hashKey hashKey不能为空
	 * @param clazz   指定类型
	 * @return null--表示不存在或失败
	 * @throws Exception
	 */
	<T> T hGet(String key, String hashKey, Class<T> clazz);

	/**
	 * 删除hashtable表中的指定hashKey
	 *
	 * @param key     键 不能为null
	 * @param hashKey 项 可以传递多个hashKey
	 * @return true--成功 false--失败
	 * @throws Exception
	 */
	Boolean hDel(String key, Object... hashKey);

	/**
	 * 判断hashtable表中是否存在指定hashKey项
	 *
	 * @param key     键 不能为null
	 * @param hashKey 项 不能为null
	 * @return true--存在 false--不存在
	 * @throws Exception
	 */

	Boolean hHasKey(String key, String hashKey);

	/**
	 * 针对hashtable表中指定hashKey值递增1,不存在将创建，并返回新增后的值
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @return 新增之后的值
	 * @throws Exception
	 */

	Double hIncr(String key, String hashKey);

	/**
	 * 针对hashtable表中指定hashKey值增加指定值,不存在将创建，并返回新增后的值
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要增加几(大于0)
	 * @return 新增之后的值 -1--表示失败
	 * @throws Exception
	 */

	Double hIncr(String key, String hashKey, Double delta);

	/**
	 * 针对hashtable表中指定hashKey值递减1,不存在将创建，并返回递减后的值
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @return 递减之后的值
	 * @throws Exception
	 */

	Double hDecr(String key, String hashKey);

	/**
	 * 针对hashtable表中指定hashKey值减少指定值,不存在将创建，并返回减少后的值
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要减几(大于0)
	 * @return 并返回减少后的值
	 * @throws Exception
	 */

	Double hDecr(String key, String hashKey, Double delta);

	/**
	 * 将类型T 数据放入Set集合
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 成功个数 >=0--成功 -1--失败
	 * @throws Exception
	 */
	<T> Long sSet(String key, T... values);

	/**
	 * 将类型T 数据放入Set集合
	 *
	 * @param key    键
	 * @param time   过期时间(秒)
	 * @param values 值 可以是多个
	 * @return 成功个数 >=0--成功 -1--失败
	 * @throws Exception
	 */
	<T> Long sSet(String key, Long time, T... values);

	/**
	 * 根据key获取Set<String>集合中的所有值
	 *
	 * @param key 键
	 * @return Set<String>集合 null--表示不存在或失败
	 * @throws Exception
	 */
	Set<String> sGet(String key);

	/**
	 * 根据key获取Set<T>集合中的所有值
	 *
	 * @param key   键
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return Set<T>集合 null--表示不存在或失败
	 * @throws Exception
	 */
	<T> Set<T> sGet(String key, Class<T> clazz);

	/**
	 * 根据对象value从一个set中查询,是否存在
	 *
	 * @param key   键
	 * @param value 值
	 * @return true--存在 false--不存在或失败
	 * @throws Exception
	 */

	<T> Boolean sHasKey(String key, T value);

	/**
	 * 获取Set集合的长度
	 *
	 * @param key 键
	 * @return Set大小 0--表示不存在或失败
	 * @throws Exception
	 */

	Long sSize(String key);

	/**
	 * 移除值为value的 Set集合中的字符值
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 移除的个数 -1--表示失败
	 * @throws Exception
	 */

	<T> Long sRemove(String key, T... values);

	/**
	 * 将T值放入list集合尾部
	 *
	 * @param key   键
	 * @param value 值
	 * @return true--成功 false--失败
	 * @throws Exception
	 */

	<T> Boolean lSet(String key, T value);

	/**
	 * 将T值放入list集合尾部
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  过期时间(秒)
	 * @return true--成功 false--失败
	 * @throws Exception
	 */

	<T> Boolean lSet(String key, T value, Long time);

	/**
	 * 将List<T>集合放入list集合尾部
	 *
	 * @param key    键
	 * @param values 值
	 * @return true--成功 false--失败
	 * @throws Exception
	 */

	<T> Boolean lSet(String key, List<T> values);

	/**
	 * 将List<T>集合放入list集合尾部
	 *
	 * @param key    键
	 * @param values 值
	 * @param time   过期时间(秒)
	 * @return true--成功 false--失败
	 * @throws Exception
	 */

	<T> Boolean lSet(String key, List<T> values, Long time);

	/**
	 * 将T值放入list集合尾部
	 *
	 * @param key   键
	 * @param value 值
	 * @return true--成功  false--失败
	 * @throws Exception
	 */

	<T> Boolean lPush(String key, T value);

	/**
	 * 将T值放入list集合尾部
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  过期时间(秒)
	 * @return true--成功  false--失败
	 * @throws Exception
	 */

	<T> Boolean lPush(String key, T value, Long time);

	/**
	 * 将List<T>集合放入list集合尾部
	 *
	 * @param key    键
	 * @param values 值
	 * @return true--成功  false--失败
	 * @throws Exception
	 */

	<T> Boolean lPush(String key, List<T> values);

	/**
	 * 将List<String>集合放入list集合尾部
	 *
	 * @param key    键
	 * @param values 值
	 * @param time   过期时间(秒)
	 * @return true--成功  false--失败
	 * @throws Exception
	 */

	<T> Boolean lPush(String key, List<T> values, Long time);

	/**
	 * 将T值放入list集合首部
	 *
	 * @param key   键
	 * @param value 值
	 * @return true--成功  false--失败
	 * @throws Exception
	 */

	<T> Boolean lLeftPush(String key, T value);

	/**
	 * 将T值放入list集合首部
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  过期时间(秒)
	 * @return true--成功  false--失败
	 * @throws Exception
	 */

	<T> Boolean lLeftPush(String key, T value, Long time);

	/**
	 * 将List<T>集合放入list集合首部
	 *
	 * @param key    键
	 * @param values 值
	 * @return true--成功 false--失败
	 * @throws Exception
	 */

	<T> Boolean lLeftPush(String key, List<T> values);

	/**
	 * 将List<T>集合放入list集合首部
	 *
	 * @param key    键
	 * @param values 值
	 * @param time   过期时间(秒)
	 * @return true--成功 false--失败
	 * @throws Exception
	 */

	<T> Boolean lLeftPush(String key, List<T> values, Long time);

	/**
	 * 修改list集合指定索引元素
	 *
	 * @param key   键
	 * @param index 索引
	 * @param value 值
	 * @return true--成功 false--失败
	 * @throws Exception
	 */

	<T> Boolean lUpdateIndex(String key, Long index, T value);

	/**
	 * 修改list集合指定索引元素
	 *
	 * @param key   键
	 * @param index 索引
	 * @param value 值
	 * @param time  过期时间(秒)
	 * @return true--成功 false--失败
	 * @throws Exception
	 */

	<T> Boolean lUpdateIndex(String key, Long index, T value, Long time);

	/**
	 * 移除N个值为value
	 *
	 * @param key   键
	 * @param count 移除多少个
	 * @param value 值
	 * @return 移除的个数 -1--失败
	 * @throws Exception
	 */

	<T> Long lRemove(String key, Long count, T value);

	/**
	 * 从list集合弹出尾部字符串元素
	 *
	 * @param key 键
	 * @return 弹出的元素 null--表示不存在或失败
	 * @throws Exception
	 */

	String lPop(String key);

	/**
	 * 从list集合弹出尾部元素
	 *
	 * @param key   键
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return 弹出的元素 null--表示不存在或失败
	 * @throws Exception
	 */

	<T> T lPop(String key, Class<T> clazz);

	/**
	 * 从list集合弹出头部字符串元素
	 *
	 * @param key 键
	 * @return 弹出的元素 null--表示不存在或失败
	 * @throws Exception
	 */

	String lLeftPop(String key);

	/**
	 * 从list集合弹出头部元素
	 *
	 * @param key   键
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return 弹出的元素 null--表示不存在或失败
	 * @throws Exception
	 */

	<T> T lLeftPop(String key, Class<T> clazz);

	/**
	 * 获取list集合中指定索引的String内容
	 *
	 * @param key   键
	 * @param index 指定索引
	 * @return String null--表示不存在或失败
	 * @throws Exception
	 */

	String lGet(String key, Long index);

	/**
	 * 获取list集合中指定索引的T类型内容
	 *
	 * @param key   键
	 * @param index 指定索引
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return T类型 null--表示不存在或失败
	 * @throws Exception
	 */

	<T> T lGet(String key, Long index, Class<T> clazz);

	/**
	 * 获取list集合的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @return List<String> null--表示不存在或失败
	 * @throws Exception
	 */

	List<String> lGet(String key, Long start, Long end);

	/**
	 * 获取list集合的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return List<T> null--表示不存在或失败
	 * @throws Exception
	 */

	<T> List<T> lGet(String key, Long start, Long end, Class<T> clazz);

	/**
	 * 获取List集合长度
	 *
	 * @param key 键
	 * @return List集合长度 -1--表示失败
	 * @throws Exception
	 */
	Long lSize(String key);
	
	void flushDb();

}
