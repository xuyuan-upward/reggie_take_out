package com.xuyuan.config;

import com.xuyuan.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
      registry.addResourceHandler("/backend/**")
              .addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**")
              .addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展mvc框架的消息转换器（就是后端反应前端时候把Java对象转成了json数据）
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展的消息转换器....");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter JSONMessageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器（设置底层将Java对象转化为json数据）
        JSONMessageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面设置的消息转换器追加到mvc框架去
        converters.add(0,JSONMessageConverter);
    }
}
