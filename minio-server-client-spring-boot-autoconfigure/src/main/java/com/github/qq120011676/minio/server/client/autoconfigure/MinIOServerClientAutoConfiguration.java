package com.github.qq120011676.minio.server.client.autoconfigure;

import com.github.qq120011676.minio.server.client.MinIOServerClient;
import com.github.qq120011676.minio.server.client.autoconfigure.properties.MinIOServerClientProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@ConditionalOnClass(MinIOServerClient.class)
@EnableConfigurationProperties(MinIOServerClientProperties.class)
public class MinIOServerClientAutoConfiguration {
    @Resource
    private MinIOServerClientProperties minIOServerClientProperties;

    @Bean
    @ConditionalOnMissingBean(MinIOServerClient.class)
    public MinIOServerClient minIOServerClient() {
        return new MinIOServerClient(this.minIOServerClientProperties.getBaseUrl(), this.minIOServerClientProperties.getViewUri(), this.minIOServerClientProperties.getDownloadUri(), this.minIOServerClientProperties.getDownloadByUri());
    }
}
