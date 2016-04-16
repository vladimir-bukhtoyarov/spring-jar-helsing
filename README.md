# Spring Jar Helsing

[![Join the chat at https://gitter.im/vladimir-bukhtoyarov/spring-jar-helsing](https://badges.gitter.im/vladimir-bukhtoyarov/spring-jar-helsing.svg)](https://gitter.im/vladimir-bukhtoyarov/spring-jar-helsing?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
Spring Jar Helsing is addressed to solve problem called as "jar hell" for springframework based application.

## Disclaimer
<details> 
<summary>For persons older than 18 years:</summary>
This library contains the blood, guts and dismemberment. You should not use this library if you don not understand what is: class, classloader, classpath, resource, current classloader, context classloader, URLClassLoader, URLStreamHandler.
   
As the author of this library and as java developer who use springframework for seven year I strongly do not recommend you to use this library, 
because it looks as very strange when you are trying to combine incompatible libraries in same application. For most cases it would be better rewrite your application and solve incompatibilities in true way instead of hacking it via classloaders. 

You should use Spring Jar Helsing iff extremal cases, when by independent from you reasons you can not to change something: for example you can not to change third-party proprietary dependency or you have not time to do things in right way. Regardless of the reasons that made you use this library, you should clearly understand that you are doing something wrong.
</details>


## Get Spring Jar Helsing library
#### By direct link
[Download compiled jar, sources, javadocs](https://github.com/vladimir-bukhtoyarov/spring-jar-helsing/releases/tag/1.0.0)

#### You can build Spring Jar Helsing from sources
```bash
git clone https://github.com/vladimir-bukhtoyarov/spring-jar-helsing.git
cd spring-jar-helsin
mvn clean install
```

#### You can add Spring Jar Helsing library to your project as maven dependency
The Spring Jar Helsing library is distributed through [Bintray](http://bintray.com/), so you need to add Bintray repository to your `pom.xml`

```xml
     <repositories>
         <repository>
             <id>jcenter</id>
             <url>http://jcenter.bintray.com</url>
         </repository>
     </repositories>
```

Then include Spring Jar Helsing as dependency to your `pom.xml`

```xml
    <dependency>
        <groupId>com.github.spring-jar-helsing</groupId>
        <artifactId>spring-jar-helsing</artifactId>
        <version>1.0.0</version>
    </dependency>
```

## Key concepts of Spring Jar Helsing:
* Using [custom classloader implementation](https://github.com/vladimir-bukhtoyarov/spring-jar-helsing/blob/master/spring-jar-helsing/src/main/java/com/github/springjarhelsing/JarHelsingClassLoader.java) for solving problems with classpath.
In opposite to Oracle recommendation for classloders implementation JarHelsingClassLoader first trying to resolve class or resource by itself and delegates resolution to parent classloader when unable to resolve by itself.
It is not recommended way, but you should not wary about it because for example Tomcat's classloader acts in same manner and nobody care.  
* Separation of interface in implementation. It is not a concept of Spring Jar Helsing implementation, just it is rule which you must follow to use this library correctly.
Unfortunately separation of interface via decomposing by different class is not enough. I am sorry, but separation MUST be done by compilation unit with following rules:
  * Things which you want to do with classes from custom classpath should be decorated by interface which available in major application part, and this interface should be never available for JarHelsingClassLoader.
As example, you can see correctly defined interface [there](https://github.com/vladimir-bukhtoyarov/spring-jar-helsing/tree/master/examples/examples-api).
Also I recommend to you keep interfaces in separated compile unit always where is possible. 
  * Things you want to do with classes from custom should be implemented in separated compile unit, for example see [example](https://github.com/vladimir-bukhtoyarov/spring-jar-helsing/tree/master/examples/with-guava-r09).
Major part of your application should never depend on implementation directly, all communication should be implemented strongly through interfaces. 
Additionaly as described above interfaces should never be available for JarHelsingClassLoader.
* Using [custom BeanDefinitionRegistryPostProcessor implementation](https://github.com/vladimir-bukhtoyarov/spring-jar-helsing/blob/master/spring-jar-helsing/src/main/java/com/github/springjarhelsing/JarHelsingBeanFactoryPostProcessor.java) for manipulations with spring context.
JarHelsingBeanFactoryPostProcessor creates custom spring context using JarHelsingClassLoader and custom classpath wneh possible, then exports all singletons from custom context to context in which JarHelsingBeanFactoryPostProcessor was initially created.
Pay attention that nothing from launching context is accessible inside custom context because custom context is fully autonomous, and also only beans with "singleton" scope are transferred from custom context to main application context.
After declaring JarHelsingBeanFactoryPostProcessor inside your main application context, you are able to refer  to singleton-beans declared insed custom context by name during dependency resolution for beans declared in main context. Not backwards!
You can see this [example](https://github.com/vladimir-bukhtoyarov/spring-jar-helsing/blob/master/examples/j2se-test/src/test/resources/test-main-context.xml) in order to understand how to declare.
Also due to JarHelsingClassLoader extends URLClassLoader it is possible to use it for any resources which is supported by URLClassLoader, including: HTTP, HTTPS, FILE, JAR, or any custom URL protocol registered in JVM.
So declaration of classpath for JarHelsingBeanFactoryPostProcessor can look something like this:
```xml
<bean class="com.github.springjarhelsing.JarHelsingBeanFactoryPostProcessor">
        <property name="resourceLocations">
            <list>
                <value>classpath:context-for-beans-with-custom-classpath.xml</value>
            </list>
        </property>
        <property name="overridenClasspathUrls">
            <list>
                <value>file:/opt/mycompany/libs/something-lib.jar</value> <!-- Points to file -->
                <value>http:mycomapny.com/java-libs/yet-another-library.jar</value> <!-- Points to file in internet -->
                <value>classpath:custom-libs/killer-library-6.6.6.jar</value> <!-- Points to file accessible as resource. Pay double attention that this file should not be by itself a valid source for parent classloader which can be used by parent classloader to class resolution -->
            </list>
        </property>
    </bean>
``` 

## A little explanation of examples
Examples which located in [this director](https://github.com/vladimir-bukhtoyarov/spring-jar-helsing/tree/master/examples) describes how to use two incompatible versions of Guava(guava-r09 and guava-17.0) in same springframework based application.
Just debug [this unit test](https://github.com/vladimir-bukhtoyarov/spring-jar-helsing/blob/master/examples/j2se-test/src/test/java/com/github/springjarhelsing/SpringJarHelsingTest.java) in order to see all by yourself.


License
-------
Copyright 2015 Vladimir Bukhtoyarov
Licensed under the Apache Software License, Version 2.0: <http://www.apache.org/licenses/LICENSE-2.0>.

