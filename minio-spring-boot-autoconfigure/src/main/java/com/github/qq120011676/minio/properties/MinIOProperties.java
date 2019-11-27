package com.github.qq120011676.minio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinIOProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
}
