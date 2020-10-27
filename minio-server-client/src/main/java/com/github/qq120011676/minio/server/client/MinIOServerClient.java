package com.github.qq120011676.minio.server.client;

import java.text.MessageFormat;

/**
 * sdk住类
 */
public class MinIOServerClient {
    private final String baseUrl;
    private final String viewUri;
    private final String downloadUri;
    private final String downloadByUri;

    /**
     * 构造方法
     *
     * @param baseUrl 基础server url
     */
    public MinIOServerClient(String baseUrl) {
        this(baseUrl, null, null, null);
    }

    /**
     * 构造方法
     *
     * @param baseUrl       基础server url
     * @param viewUri       查看uri
     * @param downloadUri   下载uri
     * @param downloadByUri 预下载uri
     */
    public MinIOServerClient(String baseUrl, String viewUri, String downloadUri, String downloadByUri) {
        this.baseUrl = baseUrl;
        if (viewUri == null || viewUri.length() <= 0) {
            viewUri = "/file/view";
        }
        this.viewUri = viewUri;
        if (downloadUri == null || downloadUri.length() <= 0) {
            downloadUri = "/file/download";
        }
        this.downloadUri = downloadUri;
        if (downloadByUri != null && downloadByUri.length() <= 0) {
            downloadByUri = "/file/downloadByUrl";
        }
        this.downloadByUri = downloadByUri;
    }

    /**
     * 获取查看url
     *
     * @param filename 文件名称
     * @return 返回查看url
     */
    public String viewUrl(String filename) {
        return MessageFormat.format("{0}{1}/{2}", this.baseUrl, this.viewUri, filename);
    }

    /**
     * 获取下载url
     *
     * @param filename 文件名称
     * @return 返回下载url
     */
    public String downloadUrl(String filename) {
        return MessageFormat.format("{0}{1}/{2}", this.baseUrl, this.downloadUri, filename);
    }

    /**
     * 获取预下载url
     *
     * @param filename 文件名称
     * @return 返回预下载url
     */
    public String downloadByUrl(String filename) {
        return MessageFormat.format("{0}{1}/{2}", this.baseUrl, this.downloadByUri, filename);
    }
}
