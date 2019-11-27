package com.github.qq120011676.minio.autoconfigure;

import com.github.qq120011676.minio.properties.MinIOProperties;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(MinIOProperties.class)
public class MinIOAutoConfiguration {
    @Resource
    private MinIOProperties minIOProperties;

    @Bean
    @ConditionalOnMissingBean(MinioClient.class)
    public MinioClient minioClient() throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidResponseException, ErrorResponseException, NoResponseException, InvalidBucketNameException, XmlPullParserException, InternalException, RegionConflictException {
        MinioClient minioClient = new MinioClient(this.minIOProperties.getEndpoint(), 0, this.minIOProperties.getAccessKey(), this.minIOProperties.getSecretKey(), this.minIOProperties.getRegion(), !(this.minIOProperties.getEndpoint() != null && this.minIOProperties.getEndpoint().startsWith("http://")));
        if (StringUtils.hasText(this.minIOProperties.getBucket())){
            if(!minioClient.bucketExists(this.minIOProperties.getBucket())) {
                minioClient.makeBucket(this.minIOProperties.getBucket());
            }
        }
        return minioClient;
    }
}
