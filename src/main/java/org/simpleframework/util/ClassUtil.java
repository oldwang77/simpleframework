package org.simpleframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtil {


    public static final String FILE_PROTOCOL = "file";

    /**
     * 获取包下类的集合
     * Class<?>表示任意类的类型
     *
     * @param packageName package的名称：com.imooc
     * @return 类集合
     */
    public static Set<Class<?>> extractPackageClass(String packageName) {
        //1 获取类加载器
        ClassLoader classLoader = getClassLoader();
        // 2 通过类加载器获取到加载到的资源
        // 看源码知道传入com.imooc，但是要求用/作为分割符
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        // 3 根据不同的资源类型，采取不同的方式获取资源的集合
        if (url == null) {
            log.warn("unable to receive message from package : " + packageName);
            return null;
        }
        // 我们需要获取的是本地类型的文件
        Set<Class<?>> classSet = null;
        if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
            classSet = new HashSet<Class<?>>();
            // file:/Users/oldwong/Desktop/Spring/simpleframework/target/classes/com/imooc/entity
            File packageDirectory = new File(url.getPath());
            // 对上面这个目录，提取这个目录下的所有class文件
            extractClassFile(classSet, packageDirectory, packageName);
        }

        // TODO:可以对其他文件类型进行处理
        return classSet;
    }

    /**
     * 递归获取目标package里面所有的class文件（包括子package里面的文件）
     *
     * @param emptyClassSet 装载目标类的集合
     * @param fileSource    文件或者目录
     * @param packageName   包名
     */
    public static void extractClassFile(Set<Class<?>> emptyClassSet, File fileSource, String packageName) {
        if (!fileSource.isDirectory()) {
            return;
        }
        // 如果是一个文件夹，那么调用listFiles方法获取文件夹下的文件或文件夹
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    // 获取文件的绝对路径
                    String absolutePath = file.getAbsolutePath();
                    if (absolutePath.endsWith(".class")) {
                        // 如果是class文件，则直接加载
                        addToClassSet(absolutePath);
                    }
                }
                return false;
            }

            // 根据class文件的绝对值路径，获取并生成class对象，并放入classSet中
            private void addToClassSet(String absoluteFilePath) {
                //1.从class文件的绝对值路径里提取出包含了package的类名
                //如/Users/baidu/imooc/springframework/sampleframework/target/classes/com/imooc/entity/dto/MainPageInfoDTO.class
                //需要弄成com.imooc.entity.dto.MainPageInfoDTO
                // File.separator:根据操作系统判断当前的分割符
                absoluteFilePath = absoluteFilePath.replace(File.separator, ".");
                // className = com.imooc.entity.dto.MainPageInfoDTO
                String className = absoluteFilePath.substring(absoluteFilePath.indexOf(packageName));
                // className = MainPageInfoDTO
                className = className.substring(0, className.lastIndexOf("."));
                // 2 通过反射机制获取对应的Class对象
                Class targetClass = loadClass(className);
                emptyClassSet.add(targetClass);
            }

        });

        if (files != null) {
            for (File f : files) {
                // 递归调用
                extractClassFile(emptyClassSet, f, packageName);
            }
        }
    }

    /**
     * 获取class类名
     *
     * @param className className = package+类名
     * @return
     */
    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error:", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取ClassLoader
     *
     * @return 返回房前的classloader
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static void main(String[] args) {
        extractPackageClass("com.imooc.entity");
    }

}
