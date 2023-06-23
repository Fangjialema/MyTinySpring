package com.fangjialema.bean;

import com.fangjialema.annotation.Bean;
import com.fangjialema.annotation.Di;
import com.fangjialema.annotation.MySpringConfig;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class AnnotationApplicationContext extends MyTinyBeanFactory {
    public AnnotationApplicationContext(Class<?> config) throws Exception {
        Stack<File> stk=new Stack<>();
        HashMap<String,Class<?>> dic=new HashMap<>();
        if (config.isAnnotationPresent(MySpringConfig.class)){
            MySpringConfig configAnnotation = config.getAnnotation(MySpringConfig.class);
            String[] basePackages = configAnnotation.value();
            for (String basePackage : basePackages) {
                var url = ClassLoader.getSystemClassLoader().getResource(basePackage.replace('.', '/'));
                if (url == null) {
                    throw new Exception("无法获取扫描路径");
                }
                var fullPath = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
                File folder = new File(fullPath);
                if (!folder.exists() || !folder.isDirectory()) {
                    throw new Exception("扫描路径无效");
                }
                var rootPath=fullPath.substring(0,fullPath.length()-basePackage.length()).replace(File.separator,"/");
                stk.clear();stk.push(folder);
                while (!stk.empty()){
                    var nowFile=stk.pop();
                    if (nowFile.isDirectory()){
                        var files=nowFile.listFiles();
                        if (files !=null){
                            for (File file:files) {
                                stk.push(file);
                            }
                        }
                    }
                    else{
                        var fName=nowFile.getName();
                        int dotIndex=fName.lastIndexOf('.');
                        if (dotIndex>0 && dotIndex <fName.length()-1){
                            var ext=fName.substring(dotIndex+1).toLowerCase();
                            if (ext.equals("class")){
                                var fPath= nowFile.getAbsolutePath().substring(rootPath.length()-1);
                                String className = fPath.substring(0, fPath.lastIndexOf('.')).replace(File.separatorChar, '.');
                                Class<?> clazz=Class.forName(className);
                                if (!clazz.isInterface()){
                                    Bean anno= clazz.getAnnotation(Bean.class);
                                    if (anno!=null){
                                        registerBeanDefinition(clazz.getSimpleName(),new MyTinyBeanDefinition(clazz));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else
            throw new Exception("参数 config 不存在 MySpringConfig 注解");
        preInstantiateSingletons();
    }
}
