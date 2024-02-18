package javaiscoffee.groomy.ide.websocket;

import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.server.ServerEndpointConfig;
import org.apache.naming.factory.BeanFactory;
import org.apache.tomcat.websocket.server.DefaultServerEndpointConfigurator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerEndpointConfigurator extends jakarta.websocket.server.ServerEndpointConfig.Configurator implements ApplicationContextAware {
    private static volatile ApplicationContext context;

    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return context.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ServerEndpointConfigurator.context = applicationContext;
    }
}
