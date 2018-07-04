package xingkong.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Config {

	private final static Logger log = LoggerFactory.getLogger(Config.class);

	public static String ip;  // 服务器启动监听的ip地址
	public static String port; // 客户端端口号
	public static String domainName; // 域名
	
	public static String httpPort; 

	/**
	 * @return
	 * @desc 初始化加载配置
	 */
	public static boolean init() {

		Configuration config;
		try {
			String env = System.getProperty("env");
			if (env == null) {
				log.info("没有配置环境，使用本地配置dev");
				env = "dev";
			}
			log.info("当前的环境是: " + env);
			String fileName = "/application" + "-" + env + ".properties";
			log.info("fileName is "+ fileName);
			config = new PropertiesConfiguration(new File(fileName));
			
			ip = config.getString("ip", "");
			port = config.getString("port", "");
			domainName = config.getString("domainName", "");

			log.info("==========================================");
			log.info("                    CONFIG                ");
			log.info("==========================================");
			log.info("ip=" + ip);
			log.info("port=" + port);
			log.info("domainName=" + domainName);

			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
}
