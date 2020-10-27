package com.github.qq120011676.minio.autoconfigure;

import com.github.qq120011676.minio.properties.MinIOProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 自动装配类
 */
@Configuration
@ConditionalOnClass(MinioClient.class)
@EnableConfigurationProperties(MinIOProperties.class)
public class MinIOAutoConfiguration {
    @Resource
    private MinIOProperties minIOProperties;

    /**
     * 自动装配生成对象方法
     *
     * @return MinioClient对象
     * @throws IOException                异常
     * @throws InvalidKeyException        异常
     * @throws InvalidResponseException   异常
     * @throws InsufficientDataException  异常
     * @throws NoSuchAlgorithmException   异常
     * @throws ServerException            异常
     * @throws InternalException          异常
     * @throws XmlParserException         异常
     * @throws InvalidBucketNameException 异常
     * @throws ErrorResponseException     异常
     * @throws RegionConflictException    异常
     */
    @Bean
    @ConditionalOnMissingBean(MinioClient.class)
    public MinioClient minioClient() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException, RegionConflictException {
        var build = MinioClient.builder();
        build.endpoint(this.minIOProperties.getEndpoint()).credentials(this.minIOProperties.getAccessKey(), this.minIOProperties.getSecretKey());
        if (StringUtils.hasText(this.minIOProperties.getRegion())) {
            build.region(this.minIOProperties.getRegion());
        }
        MinioClient minioClient = build.build();
        if (StringUtils.hasText(this.minIOProperties.getBucket())) {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(this.minIOProperties.getBucket()).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(this.minIOProperties.getBucket()).build());
            }
        }
        return minioClient;
    }
}
