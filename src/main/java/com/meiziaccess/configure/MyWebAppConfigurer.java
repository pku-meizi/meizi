package com.meiziaccess.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by user-u1 on 2016/4/12.
 */
@Configuration
public class MyWebAppConfigurer extends WebMvcConfigurerAdapter {

    //设置静态资源方式1
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/myresource/**").addResourceLocations("classpath:/myresource/");
//        registry.addResourceHandler("/myresource/**").addResourceLocations("file:E:/myresource/");
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/myresource/", "file:E:/myresource");
        super.addResourceHandlers(registry);
    }

    //设置映射
    @Override
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/index").setViewName("index");
    }

    //默认是在template文件夹下面扫描对应的.html文件，这里也可以设置自定义的路径
}

