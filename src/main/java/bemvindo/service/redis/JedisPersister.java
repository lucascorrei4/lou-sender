package bemvindo.service.redis;

import java.util.Set;

import org.apache.log4j.Logger;

import bemvindo.service.model.JsonBody;
import bemvindo.service.utils.Utils;

public class JedisPersister {

	public static Logger logger = Logger.getLogger(JedisPersister.class);

	public String persist(JsonBody jsonBody, String type) {
		try {
			String receivedAt = null;
			String key = null;
			JedisConectionPool redisPool = JedisConectionPool.getInstance();
			JedisManager redis = new JedisManager(redisPool);
			if ("mail".equals(type)) {
				receivedAt = Utils.stringToDate(jsonBody.jsonBodyMail.sender.receivedAt, Utils.DEFAULT_DATE_FORMAT);
				key = type + ":" + jsonBody.jsonBodyMail.sender.key + ":" + receivedAt + ":waiting";
				logger.info("Trying to set key: " + key);
				redis.set(key, jsonBody.jsonBodyMail.toString());
			}
			if ("sms".equals(type)) {
				receivedAt = Utils.stringToDate(jsonBody.jsonBodySMS.sender.receivedAt, Utils.DEFAULT_DATE_FORMAT);
				key = type + ":" + jsonBody.jsonBodySMS.sender.key + ":" + receivedAt + ":waiting";
				logger.info("Trying to set key: " + key);
				redis.set(key, jsonBody.jsonBodySMS.toString());
			}
			logger.info("Trying to get key: " + key);
			if (redis.getKey(key) != null) {
				logger.info("Success! Key: " + key + " found!");
				return "SUCCESS";
			}
			logger.error("Problem on Jedis!");
			return "FAILED";
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			e.getCause();
			logger.error("Problem on Jedis!");
		}

		return null;
	}

	public String saveKey(String key, String value) {
		try {
			JedisConectionPool redisPool = JedisConectionPool.getInstance();
			JedisManager redis = new JedisManager(redisPool);
			logger.info("Trying to set key: " + key);
			redis.set(key, value);
			if (redis.getKey(key) != null) {
				logger.info("Success! Key: " + key + " found!");
				return redis.getKey(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			e.getCause();
			logger.error("Problem on Jedis!");
		}
		return "";
	}

	public String searchKey(String key) {
		try {
			JedisConectionPool redisPool = JedisConectionPool.getInstance();
			JedisManager redis = new JedisManager(redisPool);
			logger.info("Trying to get key: " + key);
			String returnKey = redis.getKey(key);
			if (returnKey != null) {
				logger.info("Success! Key: " + key + " found!");
				return returnKey;
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			e.getCause();
			logger.error("Problem on Jedis!");
		}
		return "";
	}
	
	public Set<String> searchKeysByPattern(String pattern) {
		try {
			JedisConectionPool redisPool = JedisConectionPool.getInstance();
			JedisManager redis = new JedisManager(redisPool);
			logger.info("Trying to get key: " + pattern);
			Set<String>	listKeys = redis.getKeysByPattern(pattern);
			if (listKeys != null) {
				logger.info("Success! Keys by: " + pattern + " found!");
				return listKeys;
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			e.getCause();
			logger.error("Problem on Jedis!");
		}
		return null;
	}
	
	public boolean validateSenderAuthorization(String authKey, JedisManager redis) {
		Set<String> keys = redis.getKeysByPattern("sender:*");
		for (String key : keys) {
			String securityKey = Utils.getJsonElement(redis.getKey(key), "security-key");
			if (authKey.equals(securityKey)) {
				return true;
			}
		}
		return false;
	}
}
