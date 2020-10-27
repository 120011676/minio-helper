package com.github.qq120011676.minio.server.client;

import java.text.MessageFormat;

public class MinIOServerClient {
    private final String baseUrl;
    private final String viewUri;
    private final String downloadUri;
    private final String downloadByUri;

    public MinIOServerClient(String baseUrl) {
        this(baseUrl, null, null, null);
    }

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

    public String viewUrl(String filename) {
        return MessageFormat.format("{0}{1}/{2}", this.baseUrl, this.viewUri, filename);
    }

    public String downloadUrl(String filename) {
        return MessageFormat.format("{0}{1}/{2}", this.baseUrl, this.downloadUri, filename);
    }

    public String downloadByUrl(String filename) {
        return MessageFormat.format("{0}{1}/{2}", this.baseUrl, this.downloadByUri, filename);
    }
}
