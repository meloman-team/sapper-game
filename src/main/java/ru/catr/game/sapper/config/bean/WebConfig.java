package ru.catr.game.sapper.config.bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://minesweeper-test.studiotg.ru/") // TODO вынести в конфиг
                .allowedMethods("POST")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}