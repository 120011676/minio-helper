package com.github.qq120011676.minio.server.client.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 自动装配配置类
 */
@Data
@ConfigurationProperties(prefix = "minio.server")
public class MinIOServerClientProperties {
    /**
     * minio server 基础url
     */
    private String baseUrl;
    /**
     * minio server 查看uri
     */
    private String viewUri = "/file/view";
    /**
     * minio server 下载uri
     */
    private String downloadUri = "/file/download";
    /**
     * minio server 预下载uri
     */
    private String downloadByUri = "/file/downloadByUrl";
}
