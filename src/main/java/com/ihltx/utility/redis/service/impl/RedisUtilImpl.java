package com.ihltx.utility.redis.service.impl;

import com.ihltx.utility.redis.service.RedisUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class RedisUtilImpl implements RedisUtil {


	private RedisTemplate currRedisTemplate;


	public RedisTemplate getCurrRedisTemplate() {
		return currRedisTemplate;
	}

	public void setCurrRedisTemplate(RedisTemplate currRedisTemplate) {
		this.currRedisTemplate = currRedisTemplate;
	}

	/**
	 * 检查当前currRedisTemplate对象是否为空
	 *
	 * @throws Exception
	 */

	private void checkcurrRedisTemplate() throws Exception {
		if (this.currRedisTemplate == null) {
			throw new Exception("StringRedisTemplate is null");
		}
	}

	/**
	 * 对象以json字符串放入
	 *
	 * @param key   键
	 * @param value 值
	 * @return true--成功 false--失败

	 */
	public <T> Boolean set(String key, T value) {
		try {
			checkcurrRedisTemplate();
			this.currRedisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 普通缓存放入并设置时间
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒) 如果time小于等于0 将设置为永不过期
	 * @return true--成功 false--失败

	 */
	public <T> Boolean set(String key, T value, Long time) {
		try {
			checkcurrRedisTemplate();
			if (time > 0) {
				this.currRedisTemplate.opsForValue().set(key, value, time,
						TimeUnit.SECONDS);
			} else {
				set(key, value);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 对 redis 中指定键对应的数据设置失效时间
	 *
	 * @param key  键
	 * @param time 时间(秒) 如果time小于等于0 将设置为永不过期
	 * @return true--成功 false--失败

	 */
	public Boolean expire(String key, Long time) {
		Boolean result = false;
		try {
			checkcurrRedisTemplate();
			if (!StringUtils.isEmpty(key) && time > 0) {
				result = this.currRedisTemplate.expire(key, time, TimeUnit.SECONDS);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	/**
	 * 从 redis 中根据指定的 key 获取已设置的过期时间
	 *
	 * @param key 键，不能为null
	 * @return 时间（秒） -1-- 永不过期  null--表示失败

	 */
	public Long getExpire(String key) {
		Long time = 0L;
		try {
			checkcurrRedisTemplate();
			if (!StringUtils.isEmpty(key)) {
				time = this.currRedisTemplate.getExpire(key, TimeUnit.SECONDS);
			}
		} catch (Exception ex) {
			time = null;
			ex.printStackTrace();
		}
		return time;
	}

	/**
	 * 判断 redis 中是否存在指定的 key
	 *
	 * @param key 键，不能为null
	 * @return true -- 表示存在 false--表示失败或不存在

	 */
	public Boolean exists(String key) {
		Boolean result = false;
		try {
			checkcurrRedisTemplate();
			if (!StringUtils.isEmpty(key)) {
				result = this.currRedisTemplate.hasKey(key);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 从 redis 中移除指定 key 对应的数据
	 *
	 * @param keys 可以传一个值或多个
	 * @return >=0 表示成功  null--表示失败

	 */
	public Long remove(String... keys) {
		Long count = 0L;
		try {
			checkcurrRedisTemplate();
			if (keys != null && keys.length > 0) {
				count = this.currRedisTemplate.delete(CollectionUtils.arrayToList(keys));
			}
		} catch (Exception ex) {
			count = null;
			ex.printStackTrace();
		}
		return count;
	}

	/**
	 * 从 redis 中获取指定 key 对应的 string 数据
	 *
	 * @param key 键，不能为null
	 * @return key 对应的字符串数据 null--表示不存在或失败

	 */
	public String get(String key) {
		String t = null;
		try {
			checkcurrRedisTemplate();
			if (!StringUtils.isEmpty(key)) {
				Object o = this.currRedisTemplate.opsForValue().get(key);
				if (o!=null) {
					t =o.toString();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return t;
	}



	/**
	 * 从 redis 中获取指定 key 对应的 string 数据，并转换为 T 类型
	 *
	 * @param key   键，不能为null
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return key 对应的数据 null--表示不存在或失败

	 */
	public <T> T get(String key, Class<T> clazz) {
		T t = null;
		try {
			checkcurrRedisTemplate();
			if (!StringUtils.isEmpty(key)) {
				Object o = this.currRedisTemplate.opsForValue().get(key);
				if (o!=null) {
					if(o instanceof String && clazz == Character.class){
						if(o.toString().length()>0){
							Character c = o.toString().charAt(0);
							t = (T)c;
						}
					}else {
						t = (T) o;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		return t;
	}

	/**
	 * 将 redis 中指定 key 对应数据的偏移位置的 bit 位设置为 0/1
	 *
	 * @param key    键，不能为null
	 * @param offset 偏移位置 >=0
	 * @param flag   true表示设置为1，false表示设置为0
	 * @return true--表示成功 false--表示失败

	 */
	public Boolean setBit(String key, Long offset, Boolean flag) {
		Boolean result = false;
		try {
			checkcurrRedisTemplate();
			if (!StringUtils.isEmpty(key)) {
				this.currRedisTemplate.opsForValue().setBit(key, offset, flag);
				result = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	/**
	 * 判断 redis 中指定 key 的数据对应偏移位置的 bit 位是否为 1
	 *
	 * @param key    键，不能为null
	 * @param offset 偏移位置 >=0
	 * @return true--表示1 false--表示0或失败

	 */
	public Boolean getBit(String key, Long offset) {
		Boolean result = false;
		try {
			checkcurrRedisTemplate();
			if (!StringUtils.isEmpty(key)) {
				result = this.currRedisTemplate.opsForValue().getBit(key, offset);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	/**
	 * 对 redis 中指定 key 的数据递增1，并返回递增后的值
	 *
	 * @param key 键，不能为null
	 * @return Long 返回递增后的值  null--表示失败

	 */
	public Long incr(String key) {
		return incr(key, 1L);
	}

	/**
	 * 对 redis 中指定 key 的数据递增，并返回递增后的值 不存在key将自动创建并增加1
	 *
	 * @param key   键，不能为null
	 * @param delta 要增加几（大于0）
	 * @return Long 返回递增后的值  null--表示失败

	 */
	public Long incr(String key, Long delta) {
		try{
			checkcurrRedisTemplate();
			if (delta <= 0) {
				throw new Exception("delta must be > 0");
			}
			return this.currRedisTemplate.opsForValue().increment(key, delta);
		}catch (Exception err){
			err.printStackTrace();
		}
		return null;
	}

	/**
	 * 对 redis 中指定 key 的数据递减1，并返回递减后的值
	 *
	 * @param key 键，不能为null
	 * @return 返回递减后的值  null--表示失败

	 */
	public Long decr(String key) {
		return decr(key, 1L);
	}

	/**
	 * 对 redis 中指定 key 的数据递减，并返回递减后的值
	 *
	 * @param key   键，不能为null
	 * @param delta 要减少几（大于0）
	 * @return 返回递减后的值  null--表示失败

	 */
	public Long decr(String key, Long delta) {
		try{
			checkcurrRedisTemplate();
			if (delta <= 0) {
				throw new Exception("delta must be > 0");
			}
			return this.currRedisTemplate.opsForValue().increment(key, -delta);
		}catch (Exception err){
			err.printStackTrace();
		}
		return null;
	}

	/**
	 * 向hashtable表中放入数据,如果不存在将创建
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param value   值
	 * @return true--成功 false--失败

	 */

	public <T> Boolean hSet(String key, String hashKey, T value) {
		try {
			checkcurrRedisTemplate();
			this.currRedisTemplate.opsForHash().put(key, hashKey,value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 向hashtable表中放入数据,如果不存在将创建，同时设置整张hashtable表的过期时间
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param value   值
	 * @param time    过期时间(秒) 针对key设置过期时间，如果time小于等于0 将永不过期
	 * @return true--成功 false--失败
	 */

	public <T> Boolean hSet(String key, String hashKey, T value, Long time) {
		try {
			hSet(key, hashKey, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 从hashtable表中获取数据,如果不存在将返回null
	 *
	 * @param key     键 不能为null
	 * @param hashKey 项 不能为null
	 * @return null--表示失败或不存在

	 */
	public String hGet(String key, String hashKey) {
		try {
			checkcurrRedisTemplate();
			Object o = this.currRedisTemplate.opsForHash().get(key, hashKey);
			if (o != null) {
				return o.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从hashtable表中获取指定 key 指定hashKey对应的指定类型数据(T 类型)
	 *
	 * @param <T>     类型
	 * @param key     键，不能为null
	 * @param hashKey hashKey不能为空
	 * @param clazz   指定类型
	 * @return null--表示不存在或失败

	 */
	public <T> T hGet(String key, String hashKey, Class<T> clazz) {
		T t = null;
		try {
			checkcurrRedisTemplate();
			if (!StringUtils.isEmpty(key)) {
				Object o = this.currRedisTemplate.opsForHash().get(key, hashKey);
				if (o != null) {
					t = (T)o;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return t;
	}

	/**
	 * 删除hashtable表中的指定hashKey
	 *
	 * @param key     键 不能为null
	 * @param hashKey 项 可以传递多个hashKey
	 * @return true--成功 false--失败

	 */
	public Boolean hDel(String key, Object... hashKey) {
		if (hashKey == null || hashKey.length <= 0) {
			return false;
		}
		try {
			checkcurrRedisTemplate();
			this.currRedisTemplate.opsForHash().delete(key, hashKey);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 判断hashtable表中是否存在指定hashKey项
	 *
	 * @param key     键 不能为null
	 * @param hashKey 项 不能为null
	 * @return true--存在 false--不存在

	 */

	public Boolean hHasKey(String key, String hashKey) {
		try {
			checkcurrRedisTemplate();
			return this.currRedisTemplate.opsForHash().hasKey(key, hashKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 针对hashtable表中指定hashKey值递增1,不存在将创建，并返回新增后的值
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @return 新增之后的值 null--表示失败

	 */

	public Double hIncr(String key, String hashKey) {
		return hIncr(key, hashKey, 1D);
	}

	/**
	 * 针对hashtable表中指定hashKey值增加指定值,不存在将创建，并返回新增后的值
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要增加几(大于0)
	 * @return 新增之后的值 null--表示失败

	 */

	public Double hIncr(String key, String hashKey, Double delta) {
		try {
			checkcurrRedisTemplate();
			if (delta <= 0) {
				throw new Exception("delta must be > 0");
			}
			return this.currRedisTemplate.opsForHash().increment(key, hashKey, delta);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 针对hashtable表中指定hashKey值递减1,不存在将创建，并返回递减后的值
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @return 递减之后的值    null--失败

	 */

	public Double hDecr(String key, String hashKey) {
		return hDecr(key, hashKey, 1D);
	}

	/**
	 * 针对hashtable表中指定hashKey值减少指定值,不存在将创建，并返回减少后的值
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要减几(大于0)
	 * @return 并返回减少后的值  null--失败

	 */

	public Double hDecr(String key, String hashKey, Double delta) {
		try {
			checkcurrRedisTemplate();
			return this.currRedisTemplate.opsForHash().increment(key, hashKey, -delta);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将类型T 数据放入Set集合
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 成功个数 >=0--成功 -1--失败

	 */
	public <T> Long sSet(String key, T... values) {
		if (values == null || values.length <= 0) {
			return -1L;
		}
		try {
			checkcurrRedisTemplate();
			return this.currRedisTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			return -1L;
		}
	}

	/**
	 * 将类型T 数据放入Set集合
	 *
	 * @param key    键
	 * @param time   过期时间(秒)
	 * @param values 值 可以是多个
	 * @return 成功个数 >=0--成功 -1--失败

	 */
	public <T> Long sSet(String key, Long time, T... values) {
		if (values == null || values.length <= 0) {
			return -1L;
		}
		try {
			checkcurrRedisTemplate();
			Long num = this.currRedisTemplate.opsForSet().add(key, values);
			if (time > 0) {
				expire(key, time);
			}
			return num;
		} catch (Exception e) {
			e.printStackTrace();
			return -1L;
		}
	}

	/**
	 * 根据key获取Set<String>集合中的所有值
	 *
	 * @param key 键
	 * @return Set<String>集合 null--表示不存在或失败

	 */
	public Set<String> sGet(String key) {
		try {
			checkcurrRedisTemplate();
			Set<String> set = this.currRedisTemplate.opsForSet().members(key);
			return set;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据key获取Set<T>集合中的所有值
	 *
	 * @param key   键
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return Set<T>集合 null--表示不存在或失败

	 */
	public <T> Set<T> sGet(String key, Class<T> clazz) {
		try {
			checkcurrRedisTemplate();
			return (Set<T>)this.currRedisTemplate.opsForSet().members(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据对象value从一个set中查询,是否存在
	 *
	 * @param key   键
	 * @param value 值
	 * @return true--存在 false--不存在或失败

	 */

	public <T> Boolean sHasKey(String key, T value) {
		try {
			checkcurrRedisTemplate();
			return this.currRedisTemplate.opsForSet().isMember(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取Set集合的长度
	 *
	 * @param key 键
	 * @return Set大小 -1--表示不存在或失败

	 */

	public Long sSize(String key) {
		try {
			checkcurrRedisTemplate();
			return this.currRedisTemplate.opsForSet().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return -1L;
		}
	}

	/**
	 * 移除值为value的 Set集合中的字符值
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 移除的个数 -1--表示失败

	 */

	public <T> Long sRemove(String key, T... values) {
		if (values == null || values.length <= 0) {
			return -1L;
		}
		try {
			checkcurrRedisTemplate();
			return this.currRedisTemplate.opsForSet().remove(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			return -1L;
		}
	}

	/**
	 * 将T值放入list集合尾部
	 *
	 * @param key   键
	 * @param value 值
	 * @return true--成功 false--失败

	 */

	public <T> Boolean lSet(String key, T value) {
		try {
			checkcurrRedisTemplate();
			this.currRedisTemplate.opsForList().rightPush(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将T值放入list集合尾部
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  过期时间(秒)
	 * @return true--成功 false--失败

	 */

	public <T> Boolean lSet(String key, T value, Long time) {
		try {
			checkcurrRedisTemplate();
			this.currRedisTemplate.opsForList().rightPush(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将List<T>集合放入list集合尾部
	 *
	 * @param key    键
	 * @param values 值
	 * @return true--成功 false--失败

	 */

	public <T> Boolean lSet(String key, List<T> values) {
		try {
			checkcurrRedisTemplate();
			for (T t : values) {
				this.currRedisTemplate.opsForList().rightPush(key, t);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将List<T>集合放入list集合尾部
	 *
	 * @param key    键
	 * @param values 值
	 * @param time   过期时间(秒)
	 * @return true--成功 false--失败

	 */

	public <T> Boolean lSet(String key, List<T> values, Long time) {
		try {
			checkcurrRedisTemplate();
			for (T t : values) {
				this.currRedisTemplate.opsForList().rightPush(key, t);
			}
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将T值放入list集合尾部
	 *
	 * @param key   键
	 * @param value 值
	 * @return true--成功  false--失败

	 */

	public <T> Boolean lPush(String key, T value) {
		return lSet(key, value);
	}

	/**
	 * 将T值放入list集合尾部
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  过期时间(秒)
	 * @return true--成功  false--失败

	 */

	public <T> Boolean lPush(String key, T value, Long time) {
		return lSet(key, value, time);
	}

	/**
	 * 将List<T>集合放入list集合尾部
	 *
	 * @param key    键
	 * @param values 值
	 * @return true--成功  false--失败

	 */

	public <T> Boolean lPush(String key, List<T> values) {
		return lSet(key, values);
	}

	/**
	 * 将List<String>集合放入list集合尾部
	 *
	 * @param key    键
	 * @param values 值
	 * @param time   过期时间(秒)
	 * @return true--成功  false--失败

	 */

	public <T> Boolean lPush(String key, List<T> values, Long time) {
		return lSet(key, values, time);
	}

	/**
	 * 将T值放入list集合首部
	 *
	 * @param key   键
	 * @param value 值
	 * @return true--成功  false--失败

	 */

	public <T> Boolean lLeftPush(String key, T value) {
		try {
			checkcurrRedisTemplate();
			this.currRedisTemplate.opsForList().leftPush(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将T值放入list集合首部
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  过期时间(秒)
	 * @return true--成功  false--失败

	 */

	public <T> Boolean lLeftPush(String key, T value, Long time) {
		try {
			checkcurrRedisTemplate();
			this.currRedisTemplate.opsForList().leftPush(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将List<T>集合放入list集合首部
	 *
	 * @param key    键
	 * @param values 值
	 * @return true--成功 false--失败

	 */

	public <T> Boolean lLeftPush(String key, List<T> values) {
		try {
			checkcurrRedisTemplate();
			for (T t : values) {
				this.currRedisTemplate.opsForList().leftPush(key, t);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将List<T>集合放入list集合首部
	 *
	 * @param key    键
	 * @param values 值
	 * @param time   过期时间(秒)
	 * @return true--成功 false--失败

	 */

	public <T> Boolean lLeftPush(String key, List<T> values, Long time) {
		try {
			checkcurrRedisTemplate();
			for (T t : values) {
				this.currRedisTemplate.opsForList().leftPush(key, t);
			}
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 修改list集合指定索引元素
	 *
	 * @param key   键
	 * @param index 索引
	 * @param value 值
	 * @return true--成功 false--失败

	 */

	public <T> Boolean lUpdateIndex(String key, Long index, T value) {
		try {
			checkcurrRedisTemplate();
			this.currRedisTemplate.opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 修改list集合指定索引元素
	 *
	 * @param key   键
	 * @param index 索引
	 * @param value 值
	 * @param time  过期时间(秒)
	 * @return true--成功 false--失败

	 */

	public <T> Boolean lUpdateIndex(String key, Long index, T value, Long time) {
		try {
			checkcurrRedisTemplate();
			this.currRedisTemplate.opsForList().set(key, index, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 移除N个值为value
	 *
	 * @param key   键
	 * @param count 移除多少个
	 * @param value 值
	 * @return 移除的个数 -1--失败

	 */

	public <T> Long lRemove(String key, Long count, T value) {
		try {
			checkcurrRedisTemplate();
			return this.currRedisTemplate.opsForList().remove(key, count, value);
		} catch (Exception e) {
			e.printStackTrace();
			return -1L;
		}
	}

	/**
	 * 从list集合弹出尾部字符串元素
	 *
	 * @param key 键
	 * @return 弹出的元素 null--表示不存在或失败

	 */

	public String lPop(String key) {
		try {
			checkcurrRedisTemplate();
			Object o = this.currRedisTemplate.opsForList().rightPop(key);
			if (o!=null) {
				return o.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从list集合弹出尾部元素
	 *
	 * @param key   键
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return 弹出的元素 null--表示不存在或失败

	 */

	public <T> T lPop(String key, Class<T> clazz) {
		try {
			checkcurrRedisTemplate();
			Object o = this.currRedisTemplate.opsForList().rightPop(key);
			if (o!=null) {
				return (T)o;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从list集合弹出头部字符串元素
	 *
	 * @param key 键
	 * @return 弹出的元素 null--表示不存在或失败

	 */

	public String lLeftPop(String key) {
		try {
			checkcurrRedisTemplate();
			Object o = this.currRedisTemplate.opsForList().leftPop(key);
			if (o!=null) {
				return o.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从list集合弹出头部元素
	 *
	 * @param key   键
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return 弹出的元素 null--表示不存在或失败

	 */

	public <T> T lLeftPop(String key, Class<T> clazz) {
		try {
			checkcurrRedisTemplate();
			Object o = this.currRedisTemplate.opsForList().leftPop(key);
			if (o!=null) {
				return (T)o;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取list集合中指定索引的String内容
	 *
	 * @param key   键
	 * @param index 指定索引
	 * @return String null--表示不存在或失败

	 */

	public String lGet(String key, Long index) {
		try {
			checkcurrRedisTemplate();
			Object o = this.currRedisTemplate.opsForList().index(key, index);
			if (o!=null) {
				return o.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取list集合中指定索引的T类型内容
	 *
	 * @param key   键
	 * @param index 指定索引
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return T类型 null--表示不存在或失败

	 */

	public <T> T lGet(String key, Long index, Class<T> clazz) {
		try {
			checkcurrRedisTemplate();
			Object o = this.currRedisTemplate.opsForList().index(key, index);
			if (o!=null) {
				return (T)o;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取list集合的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @return List<String> null--表示不存在或失败

	 */

	public List<String> lGet(String key, Long start, Long end) {
		try {
			checkcurrRedisTemplate();
			List<String> list = this.currRedisTemplate.opsForList().range(key, start, end);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取list集合的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @param clazz 类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return List<T> null--表示不存在或失败

	 */

	public <T> List<T> lGet(String key, Long start, Long end, Class<T> clazz) {
		try {
			checkcurrRedisTemplate();
			List<T> list = this.currRedisTemplate.opsForList().range(key, start, end);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取List集合长度
	 *
	 * @param key 键
	 * @return List集合长度 -1--表示失败

	 */
	public Long lSize(String key) {
		try {
			checkcurrRedisTemplate();
			return this.currRedisTemplate.opsForList().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return -1L;
		}
	}

	public void flushDb() {
		try {
			checkcurrRedisTemplate();
			this.currRedisTemplate.getConnectionFactory().getConnection().flushDb();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
