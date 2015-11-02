package com.github.springvanhelsing;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class VanHelsingBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private Collection<String> contextPaths;
    private Collection<String> classpath;
    private InvertedURLClassLoader classLoader;

    public void setContextPaths(Collection<String> contextPaths) {
        assertNotNull("contextPaths", contextPaths);
        assertNotEmpty("contextPaths", contextPaths);
        this.contextPaths = contextPaths;
    }

    public void setClasspath(List<String> classpath) {
        assertNotNull("classpath", classpath);
        assertNotEmpty("classpath", classpath);
        this.classpath = classpath;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        assertConfigured("classpath", classpath);
        assertConfigured("contextPaths", contextPaths);
        
        ClassLoader contextClassloader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader parentClassLoader = contextClassloader != null? contextClassloader : getClass().getClassLoader();
            InvertedURLClassLoader invertedClassloader = buildInvertedClassloader(parentClassLoader, classpath);
            GenericXmlApplicationContext subcontext = createSubcontext(contextPaths, invertedClassloader);
            exportSingletons(configurableListableBeanFactory, subcontext);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassloader);
        }
    }

    private void exportSingletons(ConfigurableListableBeanFactory configurableListableBeanFactory, GenericXmlApplicationContext subcontext) {
        // TODO
    }

    private GenericXmlApplicationContext createSubcontext(Collection<String> contextPaths, InvertedURLClassLoader invertedClassloader) {
        // TODO
    }

    public static InvertedURLClassLoader buildInvertedClassloader(ClassLoader parentClassLoader, Collection<String> classpath) throws BeansException {
        URL[] classpathUrls = new URL[classpath.size()];
        int i = 0;
        for (String classpathElement : classpath) {
            try {
                classpathUrls[i] = new URL(classpathElement);
            } catch (MalformedURLException e) {
                String msg = "Fail to create url from [" + classpathElement + "]";
                throw new BootstrapException(msg, e);
            }
            i++;
        }
        return new InvertedURLClassLoader(classpathUrls, parentClassLoader);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        // do nothing
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
