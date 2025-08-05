package com.leadexchange.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Component
public class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // =============================Common============================

    /**
     * 指定缓存失效时间
     * 
     * @param key 键
     * @param time 时间(秒)
     * @return 是否成功
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置缓存失效时间失败: key={}, time={}", key, time, e);
            return false;
        }
    }

    /**
     * 根据key获取过期时间
     * 
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("获取缓存过期时间失败: key={}", key, e);
            return 0;
        }
    }

    /**
     * 判断key是否存在
     * 
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            logger.error("判断key是否存在失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 删除缓存
     * 
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        try {
            if (key != null && key.length > 0) {
                if (key.length == 1) {
                    redisTemplate.delete(key[0]);
                } else {
                    redisTemplate.delete(Arrays.asList(key));
                }
            }
        } catch (Exception e) {
            logger.error("删除缓存失败: key={}", Arrays.toString(key), e);
        }
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     * 
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return key == null ? null : redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("获取缓存失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 普通缓存放入
     * 
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("设置缓存失败: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     * 
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置缓存失败: key={}, value={}, time={}", key, value, time, e);
            return false;
        }
    }

    /**
     * 设置键值对并指定过期时间（Redis SETEX命令）
     * 
     * @param key 键
     * @param value 值
     * @param seconds 过期时间（秒）
     * @return true成功 false失败
     */
    public boolean setex(String key, Object value, long seconds) {
        return set(key, value, seconds);
    }

    /**
     * 获取List类型的缓存数据
     * 
     * @param key 键
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return List集合
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> clazz) {
        try {
            Object value = get(key);
            if (value instanceof List) {
                return (List<T>) value;
            }
            return null;
        } catch (Exception e) {
            logger.error("获取List缓存失败: key={}, clazz={}", key, clazz.getSimpleName(), e);
            return null;
        }
    }

    /**
     * 递增
     * 
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return 递增后的值
     */
    public long incr(String key, long delta) {
        try {
            if (delta < 0) {
                throw new RuntimeException("递增因子必须大于0");
            }
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            logger.error("递增操作失败: key={}, delta={}", key, delta, e);
            throw new RuntimeException("递增操作失败", e);
        }
    }

    /**
     * 递减
     * 
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return 递减后的值
     */
    public long decr(String key, long delta) {
        try {
            if (delta < 0) {
                throw new RuntimeException("递减因子必须大于0");
            }
            return redisTemplate.opsForValue().increment(key, -delta);
        } catch (Exception e) {
            logger.error("递减操作失败: key={}, delta={}", key, delta, e);
            throw new RuntimeException("递减操作失败", e);
        }
    }

    // ================================Map=================================

    /**
     * HashGet
     * 
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        try {
            return redisTemplate.opsForHash().get(key, item);
        } catch (Exception e) {
            logger.error("Hash获取失败: key={}, item={}", key, item, e);
            return null;
        }
    }

    /**
     * 获取hashKey对应的所有键值
     * 
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            logger.error("Hash批量获取失败: key={}", key, e);
            return new HashMap<>();
        }
    }

    /**
     * HashSet
     * 
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            logger.error("Hash批量设置失败: key={}, map={}", key, map, e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * 
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("Hash批量设置失败: key={}, map={}, time={}", key, map, time, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * 
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            logger.error("Hash设置失败: key={}, item={}, value={}", key, item, value, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * 
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("Hash设置失败: key={}, item={}, value={}, time={}", key, item, value, time, e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * 
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        try {
            redisTemplate.opsForHash().delete(key, item);
        } catch (Exception e) {
            logger.error("Hash删除失败: key={}, item={}", key, Arrays.toString(item), e);
        }
    }

    /**
     * 判断hash表中是否有该项的值
     * 
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        try {
            return redisTemplate.opsForHash().hasKey(key, item);
        } catch (Exception e) {
            logger.error("Hash判断key是否存在失败: key={}, item={}", key, item, e);
            return false;
        }
    }

    // ============================Set=============================

    /**
     * 根据key获取Set中的所有值
     * 
     * @param key 键
     * @return Set集合
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            logger.error("Set获取失败: key={}", key, e);
            return new HashSet<>();
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * 
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            logger.error("Set判断成员是否存在失败: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * 
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            logger.error("Set添加失败: key={}, values={}", key, Arrays.toString(values), e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     * 
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            logger.error("Set添加失败: key={}, time={}, values={}", key, time, Arrays.toString(values), e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * 
     * @param key 键
     * @return 长度
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            logger.error("Set获取大小失败: key={}", key, e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     * 
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            logger.error("Set移除失败: key={}, values={}", key, Arrays.toString(values), e);
            return 0;
        }
    }

    // ===============================List=================================

    /**
     * 获取list缓存的内容
     * 
     * @param key 键
     * @param start 开始
     * @param end 结束 0 到 -1代表所有值
     * @return List集合
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logger.error("List获取失败: key={}, start={}, end={}", key, start, end, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取list缓存的长度
     * 
     * @param key 键
     * @return 长度
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            logger.error("List获取大小失败: key={}", key, e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * 
     * @param key 键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return 值
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            logger.error("List根据索引获取失败: key={}, index={}", key, index, e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key 键
     * @param value 值
     * @return 是否成功
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            logger.error("List添加失败: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return 是否成功
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("List添加失败: key={}, value={}, time={}", key, value, time, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key 键
     * @param value 值
     * @return 是否成功
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            logger.error("List批量添加失败: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return 是否成功
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("List批量添加失败: key={}, value={}, time={}", key, value, time, e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * 
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return 是否成功
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            logger.error("List根据索引更新失败: key={}, index={}, value={}", key, index, value, e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     * 
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            logger.error("List移除失败: key={}, count={}, value={}", key, count, value, e);
            return 0;
        }
    }

    // ===============================分布式锁=================================

    /**
     * 获取分布式锁
     * 
     * @param lockKey 锁的key
     * @param requestId 请求标识
     * @param expireTime 过期时间(秒)
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);
            
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            logger.error("获取分布式锁失败: lockKey={}, requestId={}, expireTime={}", lockKey, requestId, expireTime, e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     * 
     * @param lockKey 锁的key
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);
            
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
            return Long.valueOf(1).equals(result);
        } catch (Exception e) {
            logger.error("释放分布式锁失败: lockKey={}, requestId={}", lockKey, requestId, e);
            return false;
        }
    }
}