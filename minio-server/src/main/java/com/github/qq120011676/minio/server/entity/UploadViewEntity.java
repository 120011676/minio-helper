package com.github.qq120011676.minio.server.entity;

import lombok.Data;

@Data
public class UploadViewEntity {
    private String filename;
    private String viewUrl;
    private String downloadUrl;
}
