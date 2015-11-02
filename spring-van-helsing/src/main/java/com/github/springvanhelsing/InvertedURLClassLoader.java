package com.github.springvanhelsing;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;


public class InvertedURLClassLoader extends URLClassLoader {

    private final ClassLoader parent;

    public InvertedURLClassLoader(URL[] classpath, ClassLoader parent) {
        super(classpath, null);
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
            return parent.loadClass(name);
        }
    }

    @Override
    public URL getResource(String name) {
        URL url = super.getResource(name);
        if (url == null) {
            return parent.getResource(name);
        }
        return url;
    }

}
