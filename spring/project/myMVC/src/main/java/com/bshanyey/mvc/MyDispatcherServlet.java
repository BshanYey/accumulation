package com.bshanyey.mvc;

import com.bshanyey.annotations.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

/**
 * 简单的spring mvc实现
 * 注意：
 *      只有在Controller上有Autowired注解扫描，也就是说只能自动注入controller上的service对象
 *      在注入的时候没有对字段的类型做判断
 */
public class MyDispatcherServlet extends HttpServlet {

    /**
     * key：service注解的 value
     * value：service类的对象
     */
    private static HashMap<String, Object> serviceMapping = new HashMap<>();
    /**
     * key: 类上的RequestMapping的value
     * value：controller对象
     */
    private static HashMap<String, Object> controllerMapping = new HashMap<>();
    /**
     * key: 类上的RequestMapping的value  +  method上的RequestMapping的Value
     * value：Method对象
     */
    private static HashMap<String, Method> controllerMethodMapping = new HashMap<>();
    /**
     * key:     qualifer 的 value
     * value：   ArrayList  0 : field对象   1 :  controllerMapping的key
     */
    private static HashMap<String, ArrayList<Object>> controllerAutowiredMapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        // 1、获取访问链接
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String url = requestURI.replaceAll(contextPath, "");
        System.out.println("访问路径：" + url);
        try {
            if(controllerMethodMapping == null || controllerMethodMapping.size() < 1){
                System.out.println("没有controlle，或者注入失败");
                return;
            }
            Method controllerMethod = controllerMethodMapping.get(url);
            if(controllerMethod == null){
                System.out.println("没有访问链接对应的方法");
                return;
            }
            String result = controllerMethod.invoke(controllerMapping.get("/" + url.split("/")[1])).toString();
            System.out.println("返回:" + result);
            req.getRequestDispatcher("/" + result + ".jsp").forward(req, resp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void init() {
        // 1、扫描包获取对象映射
        getHindlerMapping();
        // 2、依赖注入
        for(String key : controllerAutowiredMapping.keySet()){
            Object serviceObject = serviceMapping.get(key);
            if(serviceObject == null){
                System.out.println(key + "类注入失败");
                continue;
            }
            try {
                // 注意：这里autowired标记的属性是私有属性
                Field field =  (Field)controllerAutowiredMapping.get(key).get(0);
                field.setAccessible(true);
                field.set(controllerMapping.get(controllerAutowiredMapping.get(key).get(1).toString()), serviceObject);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        System.out.println("init end");
    }

    /**
     * 获取映射
     */
    private void getHindlerMapping(){
        String packageName = getPackageName();
        String packageFilePath = this.getClass().getClassLoader().getResource("/") + packageName.replaceAll("\\.", "/");
        initMapping(new File(packageFilePath.replace("file:/", "")));
    }

    private void initMapping(File packagFile){
        if(packagFile == null){
            return;
        }
        if(packagFile.isDirectory()){
            // 文件夹
            File[] files = packagFile.listFiles();
            for (File file : files){
                initMapping(file);
            }
        } else {
            // 文件
            if(packagFile.getName().endsWith(".class")){
                // java类， 判断是不是需要加载
                try {
                    String s = packagFile.getPath().replaceAll("\\.class", "");
                    s = s.substring(s.indexOf("com")).replaceAll("\\\\", "\\.");
                    Class<?> clazz = Class.forName(s);
                    // 扫描注解
                    if(clazz.isAnnotationPresent(Service.class)){
                        // 这里是service
                        String serviceName = clazz.getAnnotation(Service.class).value();
                        serviceMapping.put(serviceName, clazz.newInstance());
                    } else if(clazz.isAnnotationPresent(Controller.class) && clazz.isAnnotationPresent(RequestMapping.class)){
                        // 文件是controller
                        String requestMappingValue = clazz.getAnnotation(RequestMapping.class).value();
                        controllerMapping.put(requestMappingValue, clazz.newInstance());

                        Field[] fields = clazz.getDeclaredFields();
                        Method[] methods = clazz.getMethods();
                        for(Field field : fields){
                            if(field.isAnnotationPresent(Autowired.class) && field.isAnnotationPresent(Qualifier.class)){
                                // 含有autowired 需要注入的字段
                                String qualifer = field.getAnnotation(Qualifier.class).value();
                                controllerAutowiredMapping.put(qualifer, new ArrayList<>(Arrays.asList(field, requestMappingValue)));
                            }
                        }

                        for(Method method : methods){
                            if(method.isAnnotationPresent(RequestMapping.class)){
                                controllerMethodMapping.put(requestMappingValue + method.getAnnotation(RequestMapping.class).value(), method);
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 获取配置文件里边的需要扫描的包的名称
     * @return packageName
     */
    private String getPackageName(){
        String packageName = "";
        try {
            // 读取配置文件 -- 获取需要扫描的包
            String path = this.getClass().getClassLoader().getResource("/application.properties").getPath();
            Properties properties = new Properties();
            properties.load(new FileReader(path));
            packageName = properties.get("package").toString();
            System.out.println(packageName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packageName;
    }
}
