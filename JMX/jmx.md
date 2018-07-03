# JMX

## JMX介绍

JMX的全称为Java Management Extensions。

是Java的一种扩展。

JMX可以方便的管理、监控正在运行中的JAVA程序。

常常用于管理线程、内存、日志Level、服务重启、系统环境等等。



## JMS架构图

![nfh-task-005-001](img\nfh-task-005\nfh-task-005-001.png)



## 管理一个最简单的MBean

#### JMX服务器

```
1. 定义一个HelloMBean接口，这里注意必须使用MBean来结尾。
package com.test.jmx;

public interface HelloMBean {
    public String getName();
    public void setName(String name);
    public void printHello();
    public void printHello(String whoName);
}

2. 定义Hello, 这里注意类名必须是去掉MBean的前半部分。
package com.test.jmx;
public class Hello implements HelloMBean {
    private String name;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
  
    public void printHello() {
        System.out.println("Hello world, "+ name);
    }
    
    public void printHello(String whoName) {
        System.out.println("Hello, "+whoName);
    }
}

3. 创建Agent类, 同时启动http服务器和rmi服务器
package com.test.jmx;

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
    public static void main(String[] args) throws MalformedObjectNameException,
            NotCompliantMBeanException, InstanceAlreadyExistsException,
            MBeanRegistrationException, IOException {

        // 首先建立一个MBeanServer,MBeanServer用来管理我们的MBean,
        // 通常是通过MBeanServer来获取我们MBean的信息，间接
        // 调用MBean的方法，然后生产我们的资源的一个对象。
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        String domainName = "MyMBean";

        //为MBean（下面的new Hello()）创建ObjectName实例
        ObjectName helloName = new ObjectName(domainName+":name=HelloWorld");
        // 将new Hello()这个对象注册到MBeanServer上去
        mbs.registerMBean(new Hello(),helloName);

        // Distributed Layer, 提供了一个HtmlAdaptor。
        //支持Http访问协议，并且有一个不错的HTML界面，这里的Hello就是用这个作为远端管理的界面
        // 事实上HtmlAdaptor是一个简单的HttpServer，它将Http请求转换为JMX Agent的请求
        ObjectName adapterName = new ObjectName(domainName+":name=htmladapter,port=8082");
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();
        adapter.start();
        mbs.registerMBean(adapter,adapterName);

        int rmiPort = 1099;
        Registry registry = LocateRegistry.createRegistry(rmiPort);

        JMXServiceURL url = new     JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:"+rmiPort+"/"+domainName);
        JMXConnectorServer jmxConnector = 
        JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
        jmxConnector.start();
    }
}
```

#### JMX客户端

```
package com.test.jmx;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Client {

    public static void main(String[] args) throws IOException,
            MalformedObjectNameException, InstanceNotFoundException,
            AttributeNotFoundException, InvalidAttributeValueException,
            MBeanException, ReflectionException, IntrospectionException 
    {
        String domainName = "MyMBean";
        int rmiPort = 1099;
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:"+rmiPort+"/"+domainName);
        // 可以类比HelloAgent.java中的那句：
        // JMXConnectorServer 
        // jmxConnector = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbs);
        JMXConnector jmxc = JMXConnectorFactory.connect(url);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        // print domains
        System.out.println("Domains:------------------");
        String domains[] = mbsc.getDomains();
        for(int i=0;i<domains.length;i++){
            System.out.println("\tDomain["+i+"] = "+domains[i]);
        }
        //MBean count
        System.out.println("MBean count = "+mbsc.getMBeanCount());
        //process attribute
        ObjectName mBeanName = new ObjectName(domainName+":name=HelloWorld");
        mbsc.setAttribute(mBeanName, new Attribute("Name","zzh"));//注意这里是Name而不是name
        System.out.println("Name = "+mbsc.getAttribute(mBeanName, "Name"));

        //接下去是执行Hello中的printHello方法，分别通过代理和rmi的方式执行
        //via proxy
        HelloMBean proxy = MBeanServerInvocationHandler.newProxyInstance(mbsc, mBeanName, HelloMBean.class, false);
        proxy.printHello();
        proxy.printHello("jizhi boy");
        //via rmi
        mbsc.invoke(mBeanName, "printHello", null, null);
        mbsc.invoke(mBeanName, "printHello", new String[]{"jizhi gril"}, new String[]{String.class.getName()});

        //get mbean information
        MBeanInfo info = mbsc.getMBeanInfo(mBeanName);
        System.out.println("Hello Class: "+info.getClassName());
        for(int i=0;i<info.getAttributes().length;i++){
            System.out.println("Hello Attribute:"+info.getAttributes()[i].getName());
        }
        for(int i=0;i<info.getOperations().length;i++){
            System.out.println("Hello Operation:"+info.getOperations()[i].getName());
        }

        //ObjectName of MBean
        System.out.println("all ObjectName:--------------");
        Set<ObjectInstance> set = mbsc.queryMBeans(null, null);
        for(Iterator<ObjectInstance> it = set.iterator();it.hasNext();){
            ObjectInstance oi = it.next();
            System.out.println("\t"+oi.getObjectName());
        }
        jmxc.close();
    }
}
```



maven缺少本地依赖

首先我们确认一下官方的版本应该是这个样子的

```
<dependency>
    <groupId>com.sun.jdmk</groupId>
    <artifactId>jmxtools</artifactId>
    <version>1.2.1</version>
</dependency>

<dependency>
    <groupId>com.sun.jmx</groupId>
    <artifactId>jmxri</artifactId>
    <version>1.2.1</version>
</dependency>
```

然后我们下载好jar包，使用下面的语句安装jar包到本地

```
mvn install:install-file -Dfile=./jmxri.jar -DgroupId=com.sun.jmx -DartifactId=jmxri -Dversion=1.2.1 -Dpackaging=jar

mvn install:install-file -Dfile=./jmxtools.jar -DgroupId=com.sun.jdmk -DartifactId=jmxtools -Dversion=1.2.1 -Dpackaging=jar
```

后续扩展



https://docs.oracle.com/javase/8/docs/technotes/guides/jmx/tutorial/preface.html#wp146

需要变成一个项目。

讲解JAVA爬虫项目。

讲解ActiveMQ，收费。

讲解JMX，免费。

讲解搭建虚拟机集群。

