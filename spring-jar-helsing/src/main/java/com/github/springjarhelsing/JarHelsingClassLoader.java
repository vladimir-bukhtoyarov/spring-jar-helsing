package com.github.springjarhelsing;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.access.BootstrapException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Objects;


public class JarHelsingClassLoader extends URLClassLoader {

    private final ClassLoader parent;

    public JarHelsingClassLoader(Collection<String> classpath, ClassLoader parent) {
        super(buildUrls(classpath, parent), null);
        this.parent = Objects.requireNonNull(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
            Class<?> clazz = parent.loadClass(name);
            return parent.loadClass(name);
        }
    }

    @Override
    public URL getResource(String name) {
        URL url = super.getResource(name);
        if (url == null) {
            url = parent.getResource(name);
        }
        return url;
    }

    private static URL[] buildUrls(Collection<String> classpath, ClassLoader parent) throws BeansException {
        URL[] classpathUrls = new URL[classpath.size()];
        int i = 0;
        for (String classpathElement : classpath) {
            classpathUrls[i++] = buildUrl(classpathElement, parent);
        }
        return classpathUrls;
    }

    private static URL buildUrl(String classpathElement, ClassLoader parent) {
        validateUrl(classpathElement);
        if (classpathElement.startsWith("classpath:")) {
            return buildUrlFromResourceInClasspath(classpathElement, parent);
        } else {
            return buildUrlFromGenericResource(classpathElement);
        }
    }

    private static URL buildUrlFromGenericResource(String classpathElement) {
        try {
            return new URL(classpathElement);
        } catch (MalformedURLException e) {
            String msg = "Fail to create url from [" + classpathElement + "]";
            throw new BootstrapException(msg, e);
        }
    }

    private static URL buildUrlFromResourceInClasspath(String classpathElement, ClassLoader parent) {
        classpathElement = classpathElement.substring("classpath:".length(), classpathElement.length());
        URL resource = parent.getResource(classpathElement);
        if (resource == null) {
            String msg = "Resource not found resource in classpath [" + classpathElement + "]";
            throw new BootstrapException(msg);
        }
        return resource;
    }

    private static void validateUrl(String classpathElement) {
        if (classpathElement == null || classpathElement.isEmpty()) {
            throw new IllegalArgumentException("[" + classpathElement + "] is wrong URL.");
        }
    }



}
