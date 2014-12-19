package org.atmosphere.samples.chat.jersey;

import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.BroadcasterFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

/**
 * @author Ilya Sadykov
 */
@Component
public class BroadcastersInitializer implements BeanFactoryPostProcessor, ApplicationContextAware {
    public static final String ATMOSPHERE_SERVLET = "AtmosphereServlet";
    public static final String ATMOSPHERE_FRAMEWORK = "AtmosphereFramework";
    public static final String ATMOSPHERE_BROADCASTER_FACTORY = "AtmosphereBroadcasterFactory";
    private ApplicationContext context;
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        if (context instanceof WebApplicationContext) {
            final AtmosphereFramework framework = (AtmosphereFramework)
                    ((WebApplicationContext) context).getServletContext().getAttribute(ATMOSPHERE_SERVLET);
            final BroadcasterFactory factory = framework.getAtmosphereConfig().getBroadcasterFactory();

            // appending Atmosphere to Spring Context
            beanFactory.registerSingleton(ATMOSPHERE_FRAMEWORK, framework);
            beanFactory.registerSingleton(ATMOSPHERE_BROADCASTER_FACTORY, factory);

            // initializing our boradcasters
            addBroadcaster(framework, factory, "room1/chat");
            addBroadcaster(framework, factory, "room2/chat");
        }
    }

    private void addBroadcaster(AtmosphereFramework framework, BroadcasterFactory factory, String broadcasterId) {
        final CustomBroadcaster broadcaster = new CustomBroadcaster();
        broadcaster.initialize(broadcasterId, URI.create("http://localhost/" + broadcasterId), framework.getAtmosphereConfig());
        factory.add(broadcaster, broadcasterId);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
