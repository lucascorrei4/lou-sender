package bemvindo.service.redis;

import java.util.List;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import bemvindo.service.configuration.ApplicationConfiguration;

public class JedisConectionPool {

	private Logger logger = Logger.getLogger(JedisConectionPool.class);

	private Integer retries;

	private static JedisPool masterPool;
	private static JedisPool slavePool;

	private static JedisConectionPool instance;

	public synchronized static JedisConectionPool getInstance() {
		if (instance == null) {
			instance = new JedisConectionPool();
		}

		return instance;
	}

	private JedisConectionPool() {
		logger.info("Initializing redis connection pool. ");
		try {
			retries = ApplicationConfiguration.getInstance().getJedisRetries();
			masterPool = getPool("master");
		} catch (Exception e) {
			try {
				slavePool = getPool("slave");
			} catch (Exception e2) {
				logger.error("Failed to initialize redis connection pool. ", e2);
			}
		}
	}

	private JedisPoolConfig configureJedisPoolConfig(String masterOrSlave) {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(Integer.valueOf(ApplicationConfiguration.getInstance().getJedisMaxActive()));
		poolConfig.setMinIdle(ApplicationConfiguration.getInstance().getJedisMinIdle());
		poolConfig.setMaxIdle(ApplicationConfiguration.getInstance().getJedisMaxIdle());
		poolConfig.setMaxWaitMillis(ApplicationConfiguration.getInstance().getJedisMaxWait());
		poolConfig.setTestOnBorrow(ApplicationConfiguration.getInstance().getJedisTestOnBorrow());
		poolConfig.setTestOnReturn(ApplicationConfiguration.getInstance().getJedisTestWhileIdle());
		poolConfig.setTestWhileIdle(ApplicationConfiguration.getInstance().getJedisTestWhileIdle());
		poolConfig.setNumTestsPerEvictionRun(ApplicationConfiguration.getInstance().getJedisTestPerEviction());
		poolConfig.setTimeBetweenEvictionRunsMillis(ApplicationConfiguration.getInstance().getJedisTimeBetweenEviction());

		return poolConfig;
	}

	private JedisPool getPool(String masterOrSlave) {
		JedisPool jedisPool = null;
		try {
			List<String> servers = ApplicationConfiguration.getInstance().getJedisServers();
			String server = masterOrSlave.equals("master") ? servers.get(0) : servers.get(1);
			int port = ApplicationConfiguration.getInstance().getJedisPort();
			jedisPool = new JedisPool(configureJedisPoolConfig(masterOrSlave), server.trim(), port);
		} catch (Exception e) {
			logger.error("Failed to initialize redis connection pool. ", e);
		}
		return jedisPool;
	}

	public Jedis connectionHealthCheck(JedisPool jedisPool) {
		return connectionHealthCheck(jedisPool, retries);
	}

	public Jedis connectionHealthCheck(JedisPool jedisPool, int retries) throws IllegalStateException {

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			if (retries > 0 && !("pong").equalsIgnoreCase(jedis.ping())) {
				retries--;
				if (jedis != null) {
					jedisPool.returnBrokenResource(jedis);
				}
				return connectionHealthCheck(jedisPool, retries);
			}

		} catch (Exception e) {
			this.masterPool = getPool("slave");

			retries--;

			if (jedis != null)
				jedisPool.returnBrokenResource(jedis);

			if (retries > 0)
				connectionHealthCheck(jedisPool, retries);

			else {
				logger.error("Error during the health check from redis connection pool. ", e);

				throw new IllegalStateException("Failed to return a redis connection valid. ", e);
			}
		} finally {

			jedisPool.returnResource(jedis);
		}

		return jedis;
	}

	// public Jedis connectionHealthCheck(JedisPool jedisPool, int retries)
	// throws IllegalStateException {
	// if (jedisPool == null)
	// jedisPool = getPool("master");
	//
	// Jedis jedis = null;
	// try {
	// jedis = jedisPool.getResource();
	//
	// if (retries > 0 && !("pong").equalsIgnoreCase(jedis.ping())) {
	// retries--;
	// if (jedis != null) {
	// jedisPool.returnBrokenResource(jedis);
	// }
	// return connectionHealthCheck(jedisPool, retries);
	// }
	//
	// } catch (Exception e) {
	// this.masterPool = getPool("slave");
	//
	// retries--;
	//
	// if (jedis != null)
	// jedisPool.returnBrokenResource(jedis);
	//
	// if (retries > 0)
	// connectionHealthCheck(jedisPool, retries);
	//
	// else {
	// logger.error("Error during the health check from redis connection pool. ",
	// e);
	//
	// throw new
	// IllegalStateException("Failed to return a redis connection valid. ", e);
	// }
	// } finally {
	// jedisPool.returnResource(jedis);
	// jedisPool.destroy();
	// }
	//
	// return jedis;
	// }

	public Jedis getReadJedisFromPool() {
		return connectionHealthCheck(masterPool);
	}

	public void returnReadJedisToPool(Jedis jedis) {
		masterPool.returnResource(jedis);
	}

	public Jedis getWriteJedisFromPool() {
		return connectionHealthCheck(masterPool);
	}

	public void returnWriteJedisToPool(Jedis jedis) {
		masterPool.returnResource(jedis);
	}

	public JedisPool getJedisPoolMaster() {
		return masterPool;
	}

	public JedisPool getJedisPoolSlave() {
		return slavePool;
	}

}
