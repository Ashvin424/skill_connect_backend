package com.skillconnect.backend.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudnaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dzb4ynmed",
                "api_key", "752147612166756",
                "api_secret", "xGekWdfxBTgjw7JMrM1PYxv4dRU"
        ));
    }
}
