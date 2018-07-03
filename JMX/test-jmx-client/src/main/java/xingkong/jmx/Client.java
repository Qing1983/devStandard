package xingkong.jmx;

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
        int rmiPort = 61620;
        String ip = "127.0.0.1";//"10.13.11.29";
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+ip+":"+rmiPort+"/"+domainName);
       
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
        
//        //process attribute
//        ObjectName mBeanName = new ObjectName(domainName+":name=HelloWorld");
//        mbsc.setAttribute(mBeanName, new Attribute("Name","zzh"));//ע��������Name������name
//        System.out.println("Name = "+mbsc.getAttribute(mBeanName, "Name"));
//
//        //����ȥ��ִ��Hello�е�printHello�������ֱ�ͨ�������rmi�ķ�ʽִ��
//        //via proxy
//        HelloMBean proxy = MBeanServerInvocationHandler.newProxyInstance(mbsc, mBeanName, HelloMBean.class, false);
//        proxy.printHello();
//        proxy.printHello("jizhi boy");
//        //via rmi
//        mbsc.invoke(mBeanName, "printHello", null, null);
//        mbsc.invoke(mBeanName, "printHello", new String[]{"jizhi gril"}, new String[]{String.class.getName()});
//
//        //get mbean information
//        MBeanInfo info = mbsc.getMBeanInfo(mBeanName);
//        System.out.println("Hello Class: "+info.getClassName());
//        for(int i=0;i<info.getAttributes().length;i++){
//            System.out.println("Hello Attribute:"+info.getAttributes()[i].getName());
//        }
//        for(int i=0;i<info.getOperations().length;i++){
//            System.out.println("Hello Operation:"+info.getOperations()[i].getName());
//        }

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