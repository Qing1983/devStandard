package xingkong.jmx;

import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HelloAgent {
	public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException,
			InstanceAlreadyExistsException, MBeanRegistrationException, IOException {
		
		System.out.println("系统正在启动...");
		// 首先建立一个MBeanServer,MBeanServer用来管理我们的MBean
		
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		String domainName = "MyMBean";

		//为MBean（下面的new Hello()）创建ObjectName实例
		ObjectName helloName = new ObjectName(domainName + ":name=HelloWorld");
		
		
		// 将new Hello()这个对象注册到MBeanServer上去
		mbs.registerMBean(new Hello(), helloName);

		// Distributed Layer, 提供了一个HtmlAdaptor。
        //支持Http访问协议，并且有一个不错的HTML界面，这里的Hello就是用这个作为远端管理的界面
        // 事实上HtmlAdaptor是一个简单的HttpServer，它将Http请求转换为JMX Agent的请求
		ObjectName adapterName = new ObjectName(domainName + ":name=htmladapter,port=8082");
		HtmlAdaptorServer adapter = new HtmlAdaptorServer();
		adapter.start();
		mbs.registerMBean(adapter, adapterName);

		
		
		int rmiPort = 61620;
		Registry registry = LocateRegistry.createRegistry(rmiPort);

		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://0.0.0.0:" + rmiPort + "/" + domainName);
		JMXConnectorServer jmxConnector = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
		jmxConnector.start();
		
		System.out.println("系统启动完毕");

	}
}
