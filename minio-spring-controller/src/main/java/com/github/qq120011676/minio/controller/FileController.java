package com.github.qq120011676.minio.controller;

import com.github.qq120011676.ladybird.web.exception.RestfulExceptionHelper;
import com.github.qq120011676.minio.entity.UploadEntity;
import com.github.qq120011676.minio.properties.MinIOProperties;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传Controller
 */
@RestController
@RequestMapping("file")
public class FileController {
    @Resource
    private MinioClient minioClient;
    @Resource
    private MinIOProperties minIOProperties;
    @Resource
    private RestfulExceptionHelper restfulExceptionHelper;

    /**
     * 文件上传（单个）
     *
     * @param file 上传文件
     * @return 响应UploadEntity对象
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
     */
    @PostMapping("upload")
    public UploadEntity upload(MultipartFile file) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        if (file.isEmpty()) {
            throw this.restfulExceptionHelper.getRestfulRuntimeException("upload_no_file");
        }
        String objectName = UUID.randomUUID().toString();
        Map<String, String> map = new HashMap<>();
        map.put("filename", file.getOriginalFilename());
        var builder = PutObjectArgs.builder();
        if (StringUtils.hasText(this.minIOProperties.getBucket())) {
            builder.bucket(this.minIOProperties.getBucket());
        }
        try (InputStream in = file.getInputStream()) {
            this.minioClient.putObject(builder.contentType(file.getContentType()).extraHeaders(map).object(objectName).stream(in, file.getSize(), -1).build());
        }
        return new UploadEntity(objectName);
    }

    /**
     * 下载文件
     *
     * @param filename 文件名称
     * @return 响应
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
     */
    @GetMapping(value = "download/{filename}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String filename) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        var builder = StatObjectArgs.builder();
        if (StringUtils.hasText(this.minIOProperties.getBucket())) {
            builder.bucket(this.minIOProperties.getBucket());
        }
        ObjectStat objectStat = this.minioClient.statObject(builder.object(filename).build());
        String saveFilename = objectStat.name();
        List<String> filenames = objectStat.httpHeaders().get("x-amz-meta-filename");
        if (filenames != null && !filenames.isEmpty()) {
            saveFilename = filenames.get(0);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", MessageFormat.format("attachment; filename=\"{0}\"", saveFilename));
        var builderGet = GetObjectArgs.builder();
        if (StringUtils.hasText(this.minIOProperties.getBucket())) {
            builderGet.bucket(this.minIOProperties.getBucket());
        }
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/force-download"))
                .body(new InputStreamResource(this.minioClient.getObject(builderGet.object(filename).build())));
    }
}
