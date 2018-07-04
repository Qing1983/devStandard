package xingkong.jmx;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xingkong.config.Config;

public class Client {
	private final static Logger log = LoggerFactory.getLogger(Client.class);

	public static void testJmxClient() {
		try {
			String domainName = Config.domainName;
			String port = Config.port;
			String ip = Config.ip;
			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://" + ip + ":" + port + "/" + domainName);

			JMXConnector jmxc = JMXConnectorFactory.connect(url);
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

			// print domains
			log.info("Domains:------------------");
			String domains[] = mbsc.getDomains();
			for (int i = 0; i < domains.length; i++) {
				log.info("Domain[" + i + "] = " + domains[i]);
			}
			// MBean count
			log.info("MBean count = " + mbsc.getMBeanCount());

			// ObjectName of MBean
			log.info("all ObjectName:--------------");
			Set<ObjectInstance> set = mbsc.queryMBeans(null, null);
			for (Iterator<ObjectInstance> it = set.iterator(); it.hasNext();) {
				ObjectInstance oi = it.next();
				log.info(""+ oi.getObjectName());
			}
			jmxc.close();
		} catch (Exception e) {
			log.error("[ERROR]tmxclient occur a error by " + e.getClass(), e);
		}
	}

	public static void main(String[] args) {
		if (!Config.init()) {
			log.info("配置文件加载失败");
			return;
		}
		log.info("开始测试");
		testJmxClient();
		log.info("测试结束");
	}
}