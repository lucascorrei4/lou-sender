package bemvindo.service.redis;

import org.apache.log4j.Logger;

import bemvindo.service.model.JsonBody;
import bemvindo.service.utils.Utils;

public class JedisPersister {

	public static Logger logger = Logger.getLogger(JedisPersister.class);

	public String persist(JsonBody jsonBody, String type) {
		try {
			String postedAt = null;
			String key = null;
			JedisConectionPool redisPool = JedisConectionPool.getInstance();
			JedisManager redis = new JedisManager(redisPool);
			if ("mail".equals(type)) {
				postedAt = Utils.stringToDate(jsonBody.jsonBodyMail.sender.postedAt, Utils.DEFAULT_DATE_FORMAT);
				key = type + ":" + jsonBody.jsonBodyMail.sender.key + ":" + postedAt + ":waiting";
				logger.info("Trying to set key: " + key);
				redis.set(key, jsonBody.jsonBodyMail.toString());
			}
			if ("sms".equals(type)) {
				postedAt = Utils.stringToDate(jsonBody.jsonBodySMS.sender.postedAt, Utils.DEFAULT_DATE_FORMAT);
				key = type + ":" + jsonBody.jsonBodySMS.sender.key + ":" + postedAt + ":waiting";
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
				return "<h1>Opa! Chave " + key + " gravada com sucesso!</h1>";
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
			if (redis.getKey(key) != null) {
				logger.info("Success! Key: " + key + " found!");
				return "<h1>Já que você requisitou a chave " + key + ", toma: </h1>";
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			e.getCause();
			logger.error("Problem on Jedis!");
		}
		return "";
	}
}
