package com.leon.datalink.core.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassUtil {

    public static void loadJar(String jarPath) {
        File jarFile = new File(jarPath);
        // 从URLClassLoader类中获取类所在文件夹的方法，jar也可以认为是一个文件夹
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException | SecurityException e) {
            Loggers.CORE.info("load class error ", e);
        }
        if (method == null) return;
        //获取方法的访问权限以便写回
        boolean accessible = method.isAccessible();
        try {
            method.setAccessible(true);
            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
            URL url = jarFile.toURI().toURL();
            method.invoke(classLoader, url);
        } catch (Exception e) {
            Loggers.CORE.info("load class error ", e);
        } finally {
            method.setAccessible(accessible);
        }
        Loggers.CORE.info("load jar file {} {}", jarFile.getPath(), jarFile.getTotalSpace());
    }

}
