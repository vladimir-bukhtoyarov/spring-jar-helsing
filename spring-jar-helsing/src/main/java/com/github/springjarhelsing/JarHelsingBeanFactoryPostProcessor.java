package com.github.springjarhelsing;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.List;

public class JarHelsingBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor, DisposableBean {

    private Collection<String> resourceLocations;
    private Collection<String> overridenClasspathUrls;
    private JarHelsingClassLoader jarHelsingClassLoader;
    private GenericXmlApplicationContext subcontext;

    public void setResourceLocations(Collection<String> resourceLocations) {
        assertNotNull("resourceLocations", resourceLocations);
        assertNotEmpty("resourceLocations", resourceLocations);
        this.resourceLocations = resourceLocations;
    }

    public void setOverridenClasspathUrls(List<String> overridenClasspathUrls) {
        assertNotNull("overridenClasspathUrls", overridenClasspathUrls);
        assertNotEmpty("overridenClasspathUrls", overridenClasspathUrls);
        this.overridenClasspathUrls = overridenClasspathUrls;
    }

    @Override
    public void destroy() throws Exception {
        try {
            if (subcontext != null) {
                subcontext.close();
            }
        } finally {
            if (jarHelsingClassLoader != null) {
                jarHelsingClassLoader.close();
            }
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        // do nothing
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        assertConfigured("overridenClasspathUrls", overridenClasspathUrls);
        assertConfigured("resourceLocations", resourceLocations);

        ClassLoader previousContextClassLoader = getContextClassLoader();
        try {
            ClassLoader parentClassLoader = getClass().getClassLoader();

            this.jarHelsingClassLoader = new JarHelsingClassLoader(overridenClasspathUrls, parentClassLoader);

            setContextClassloader(jarHelsingClassLoader);

            this.subcontext = createSubcontext(resourceLocations);
            exportSingletons(configurableListableBeanFactory, subcontext);
        } finally {
            setContextClassloader(previousContextClassLoader);
        }
    }

    private void exportSingletons(ConfigurableListableBeanFactory configurableListableBeanFactory, GenericXmlApplicationContext subcontext) {
        for (String beanName: subcontext.getBeanDefinitionNames()) {
            if (subcontext.getBeanDefinition(beanName).isSingleton()) {
                Object singleton = subcontext.getBean(beanName);
                configurableListableBeanFactory.registerSingleton(beanName, singleton);
            }
        }
    }

    private static GenericXmlApplicationContext createSubcontext(Collection<String> resourceLocations) {
        GenericXmlApplicationContext subcontext = new GenericXmlApplicationContext();
        subcontext.load(resourceLocations.toArray(new String[0]));
        subcontext.refresh();
        return subcontext;
    }

    private ClassLoader getContextClassLoader() {
        PrivilegedAction<ClassLoader> action = new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        };
        return AccessController.doPrivileged(action);
    }

    private void setContextClassloader(final ClassLoader previousContextClassloader) {
        PrivilegedAction<Void> action = new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                Thread.currentThread().setContextClassLoader(previousContextClassloader);
                return null;
            }
        };
        AccessController.doPrivileged(action);
    }

    private static void assertNotNull(String name, Collection<String> collection) {
        if (collection == null) {
            throw new IllegalArgumentException(name + " can not be null");
        }
    }

    private static void assertNotEmpty(String name, Collection<String> collection) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(name + " can not be empty");
        }
    }

    private static void assertConfigured(String name, Collection<String> collection) {
        if (collection == null) {
            throw new IllegalStateException(name + " should be configured");
        }
    }

}
