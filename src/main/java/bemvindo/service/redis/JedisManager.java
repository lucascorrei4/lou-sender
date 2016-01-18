package bemvindo.service.redis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisManager {

	private static Logger logger = Logger.getLogger(JedisManager.class);

	private JedisConectionPool redisPool;

	public JedisManager(JedisConectionPool redisPool) {
		this.redisPool = redisPool;
	}

	public void set(String key, String value) {
		try {
			Jedis jedis = redisPool.getWriteJedisFromPool();

			jedis.set(key, value);
			// jedis.expire(key,
			// ApplicationConfiguration.getInstance().getRedisExpire());
			// redisPool.returnWriteJedisToPool(jedis);

			logger.debug(String.format("Set: %s", key));
		} catch (JedisConnectionException e) {
			logger.error("Failed to set key! " + "Message: " + e.getMessage() + " Cause: " + e.getCause());
		}
	}

	public String renameKey(String key, String newkey) {
		String result = null;
		try {
			Jedis jedis = redisPool.getWriteJedisFromPool();
			result = jedis.rename(key, newkey);

			// redisPool.returnWriteJedisToPool(jedis);

			logger.debug("Rename key: " + key + " to: " + newkey);
		} catch (JedisConnectionException e) {
			logger.error("Failed to rename key! " + "Message: " + e.getMessage() + " Cause: " + e.getCause());
		}
		return result;
	}

	public Long hSet(String key, String field, String value) {
		Long ret = 0L;
		try {
			Jedis jedis = redisPool.getWriteJedisFromPool();

			ret = jedis.hset(key, field, value);
			// jedis.expire(key,
			// ApplicationConfiguration.getInstance().getRedisExpire());
			redisPool.returnWriteJedisToPool(jedis);

			logger.debug(String.format("HSet: %s", key));
		} catch (JedisConnectionException e) {
			logger.error("Failed to hset key! " + "Message: " + e.getMessage() + " Cause: " + e.getCause());
		}

		return ret;
	}

	public String getKey(String key) {
		String result = null;
		try {
			Jedis jedis = redisPool.getReadJedisFromPool();
			result = jedis.get(key);
			redisPool.returnReadJedisToPool(jedis);

			logger.debug(String.format("Get: %s", key));
		} catch (JedisConnectionException e) {
			logger.error("Failed to get key! " + "Message: " + e.getMessage() + " Cause: " + e.getCause());
		}

		return result;
	}

	public Set<String> getKeysByPattern(String pattern) {
		Set<String> result = new HashSet<String>();
		try {
			Jedis jedis = redisPool.getReadJedisFromPool();
			result = jedis.keys(pattern);
			// redisPool.returnReadJedisToPool(jedis);

			logger.debug(String.format("Keys by pattern: %s", pattern));
		} catch (JedisConnectionException e) {
			logger.error("Failed to get keys by pattern: " + pattern + ". " + "Message: " + e.getMessage() + " Cause: " + e.getCause());
		}

		return result;
	}

	public void removeKeys(String key) {
		try {
			Jedis jedis = redisPool.getReadJedisFromPool();
			jedis.del(key);
			// redisPool.returnWriteJedisToPool(jedis);

			logger.debug(String.format("Delete: %s", key));
		} catch (JedisConnectionException e) {
			logger.error("Failed to remove key! " + "Message: " + e.getMessage() + " Cause: " + e.getCause());
		}
	}

	public List<String> listValuesOfKeysList(List<String> keys) {
		String[] keysArray = keys.toArray(new String[keys.size()]);
		List<String> result = null;
		try {
			Jedis jedis = redisPool.getReadJedisFromPool();
			result = jedis.mget(keysArray);
			// redisPool.returnReadJedisToPool(jedis);
		} catch (JedisConnectionException e) {
			logger.error("Failed to list keys! " + "Message: " + e.getMessage() + " Cause: " + e.getCause());
		}

		return result;
	}

	public static void main(String[] args) {
		JedisConectionPool redisPool = JedisConectionPool.getInstance();
		JedisManager redis = new JedisManager(redisPool);
		redis.set("familia", "lucas, thammy, louise");
		String retorno = redis.getKey("familia");

		if (retorno == null) {
			System.out.println("vazio");
		} else {
			System.out.println(retorno);
		}
	}

	public void close() {
		try {
			Jedis jedis = redisPool.getWriteJedisFromPool();
			jedis.close();
		} catch (JedisConnectionException e) {
			logger.error("Failed closing connection! " + "Message: " + e.getMessage() + " Cause: " + e.getCause());
		}

	}

}
