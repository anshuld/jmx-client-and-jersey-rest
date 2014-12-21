package com.virginholidays;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Anshul Dutta on 18/12/2014.
 */
public class JMXClient {
    private static final String CONNECTOR_ADDRESS =
            "com.sun.management.jmxremote.localConnectorAddress";

    //    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JMXClient.class);
    public static void main(String[] args) {

        try {
            new JMXClient().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() throws Exception {
        final VirtualMachine vm = findWebApi();

        // get the connector address
        String connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);

        // no connector address, so we start the JMX agent
        if (connectorAddress == null) {
            String agent = vm.getSystemProperties().getProperty("java.home") +
                    File.separator + "lib" + File.separator + "management-agent.jar";
            vm.loadAgent(agent);

            // agent is started, get the connector address
            connectorAddress = vm.getAgentProperties().getProperty(CONNECTOR_ADDRESS);
        }

        // establish connection to connector server
        JMXServiceURL url = new JMXServiceURL(connectorAddress);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(url);
        try {
            MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
            listAllMBeans(connection);
            final ObjectInstance mBean = connection.getObjectInstance(new ObjectName("bean:name=tbxWebService"));
//            final ObjectInstance mBean = connection.getObjectInstance(new ObjectName("bean:name=tbxWebService"));
        } finally {
            jmxConnector.close();
        }
    }

    private void listAllMBeans(MBeanServerConnection mbeanConn) throws IOException {
        Set<ObjectName> beanSet = mbeanConn.queryNames(null, null);
        for (ObjectName objectName : beanSet) {
            System.out.println(objectName);
        }
        System.out.println("Total number of MBeans: " + beanSet.size());
    }

    private void attachAll() throws IOException, AttachNotSupportedException {
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        for (VirtualMachineDescriptor virtualMachineDescriptor : vms) {
            System.out.println("============ VM PID: " + virtualMachineDescriptor.id() + ". Display Name: " + virtualMachineDescriptor.displayName());
            try {
                VirtualMachine virtualMachine = VirtualMachine.attach(("5870"));
                if (virtualMachine != null) {
                    readSystemProperties(virtualMachine);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("TRYING next.....");
            }
        }
    }

    private VirtualMachine findWebApi() {
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        for (VirtualMachineDescriptor virtualMachineDescriptor : vms) {
            final Properties systemProperties;
            try {
                final VirtualMachine virtualMachine = attach(virtualMachineDescriptor);
                systemProperties = virtualMachine.getSystemProperties();
                if (isWebApi(systemProperties)) {
                    return virtualMachine;
                } else {
                    detachSilently(virtualMachine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private VirtualMachine findWebApiConnectAddress() {
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        for (VirtualMachineDescriptor virtualMachineDescriptor : vms) {
            final Properties systemProperties;
            try {
                final VirtualMachine virtualMachine = attach(virtualMachineDescriptor);
                systemProperties = virtualMachine.getSystemProperties();
                if (isWebApi(systemProperties)) {
                    return virtualMachine;
                } else {
                    detachSilently(virtualMachine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean isWebApi(Properties systemProperties) {
        for (String key : systemProperties.stringPropertyNames()) {
            try {
                if (key.equals("catalina.base")) {
                    return systemProperties.get(key).toString().contains("web-api");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private VirtualMachine attach(VirtualMachineDescriptor virtualMachineDescriptor) {
        VirtualMachine virtualMachine = null;
        try {
            virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return virtualMachine;
    }

    private static void readSystemProperties(VirtualMachine virtualMachine) throws IOException {
        String propertyValue = null;
        final Properties systemProperties = virtualMachine.getSystemProperties();
        for (String key : systemProperties.stringPropertyNames()) {
            try {
                System.out.println(key + ":" + systemProperties.get(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void detachSilently(VirtualMachine virtualMachine) {
        if (virtualMachine != null) {
            try {
                virtualMachine.detach();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ;
    }
}
