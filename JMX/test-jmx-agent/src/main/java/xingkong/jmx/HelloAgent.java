package xingkong.jmx;

import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jdmk.comm.HtmlAdaptorServer;

import xingkong.config.Config;

public class HelloAgent {
	private final static Logger log = LoggerFactory.getLogger(HelloAgent.class);

	public static void testJmxAgent() {
		try {
			log.info("系统正在启动...");
			// 首先建立一个MBeanServer,MBeanServer用来管理我们的MBean

			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

			String domainName = Config.domainName;
			String ip = Config.ip;
			String port = Config.port;
			String httpPort = Config.httpPort;

			// 为MBean（下面的new Hello()）创建ObjectName实例
			ObjectName helloName = new ObjectName(domainName + ":name=HelloWorld");

			// 将new Hello()这个对象注册到MBeanServer上去
			mbs.registerMBean(new Hello(), helloName);

			// Distributed Layer, 提供了一个HtmlAdaptor。
			// 支持Http访问协议，并且有一个不错的HTML界面，这里的Hello就是用这个作为远端管理的界面
			// 事实上HtmlAdaptor是一个简单的HttpServer，它将Http请求转换为JMX Agent的请求
			ObjectName adapterName = new ObjectName(domainName + ":name=htmladapter,port="+httpPort);
			HtmlAdaptorServer adapter = new HtmlAdaptorServer();
			adapter.start();
			mbs.registerMBean(adapter, adapterName);

			
			Registry registry = LocateRegistry.createRegistry(Integer.parseInt(port));

			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://"+ip+":" + port + "/" + domainName);
			JMXConnectorServer jmxConnector = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
			jmxConnector.start();

			System.out.println("系统启动完毕");
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
		testJmxAgent();
		log.info("测试结束");
	}

}
