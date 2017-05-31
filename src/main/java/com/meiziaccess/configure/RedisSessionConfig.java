package com.meiziaccess.configure;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Created by user-u1 on 2017/1/6.
 */
@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig {
}
