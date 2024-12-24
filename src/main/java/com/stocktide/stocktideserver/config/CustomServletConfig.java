package com.stocktide.stocktideserver.config;

import com.stocktide.stocktideserver.formatter.LocalDateFormatter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Log4j2
public class CustomServletConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new LocalDateFormatter());
//        registry.addFormatter(new LocalDateTimeFormatter());
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        //addMapping : 허용 파일
//        //addMapping : 허용 파일
//        //allowedOrigins : 허용 url
//        //maxAge : 허용 시간
//        //OPTIONS 미리 한번 찔러볼 떄
//        registry.addMapping("/**")
//                .maxAge(500)
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
//                .allowedOrigins("*");
//
//    }
}

