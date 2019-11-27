package com.github.qq120011676.minio.controller;

import com.github.qq120011676.ladybird.web.exception.RestfulExceptionHelper;
import com.github.qq120011676.minio.entity.UploadEntity;
import com.github.qq120011676.minio.properties.MinIOProperties;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("file")
public class FileController {
    @Resource
    private MinioClient minioClient;
    @Resource
    private MinIOProperties minIOProperties;
    @Resource
    private RestfulExceptionHelper restfulExceptionHelper;

    @PostMapping("upload")
    public UploadEntity upload(MultipartFile file) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidArgumentException, InvalidResponseException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException {
        if (file.isEmpty()) {
            throw this.restfulExceptionHelper.getRestfulRuntimeException("upload_no_file");
        }
        String objectName = UUID.randomUUID().toString();
        Map<String, String> map = new HashMap<>();
        map.put("filename", file.getOriginalFilename());
        this.minioClient.putObject(this.minIOProperties.getBucket(), objectName, file.getInputStream(), file.getSize(), map, null, file.getContentType());
        return new UploadEntity(objectName);
    }

    @GetMapping(value = "download/{filename}")
    public UploadEntity download(@PathVariable String filename) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidResponseException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        return new UploadEntity(this.minioClient.getObjectUrl(this.minIOProperties.getBucket(), filename));
    }
}
