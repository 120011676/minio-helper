package com.github.qq120011676.minio.server.controller;

import com.github.qq120011676.ladybird.web.conntroller.ControllerHelper;
import com.github.qq120011676.ladybird.web.exception.RestfulExceptionHelper;
import com.github.qq120011676.minio.properties.MinIOProperties;
import com.github.qq120011676.minio.server.config.AppConfig;
import com.github.qq120011676.minio.server.entity.DownloadViewEntity;
import com.github.qq120011676.minio.server.entity.UploadViewEntity;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;

/**
 * 上传、下载、查看Controller类
 */
@RestController
@RequestMapping("file")
public class MinioController {
    @Resource
    private RestfulExceptionHelper restfulExceptionHelper;
    @Resource
    private MinioClient minioClient;
    @Resource
    private MinIOProperties minIOProperties;
    @Resource
    private AppConfig appConfig;

    /**
     * 上传接口（单个）
     *
     * @param file 上传文件
     * @return 响应
     * @throws IOException                异常
     * @throws InvalidResponseException   异常
     * @throws InvalidKeyException        异常
     * @throws NoSuchAlgorithmException   异常
     * @throws ServerException            异常
     * @throws ErrorResponseException     异常
     * @throws XmlParserException         异常
     * @throws InvalidBucketNameException 异常
     * @throws InsufficientDataException  异常
     * @throws InternalException          异常
     */
    @PostMapping("upload")
    public UploadViewEntity upload(MultipartFile file) throws IOException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, ServerException, ErrorResponseException, XmlParserException, InvalidBucketNameException, InsufficientDataException, InternalException {
        return this.minioUpload(file, "/file/upload");
    }

    /**
     * 上传接口（多个）
     *
     * @param files 上传文件
     * @return 响应
     * @throws IOException                异常
     * @throws InvalidResponseException   异常
     * @throws InvalidKeyException        异常
     * @throws NoSuchAlgorithmException   异常
     * @throws ServerException            异常
     * @throws ErrorResponseException     异常
     * @throws XmlParserException         异常
     * @throws InvalidBucketNameException 异常
     * @throws InsufficientDataException  异常
     * @throws InternalException          异常
     */
    @PostMapping("uploads")
    public List<UploadViewEntity> uploads(MultipartFile[] files) throws IOException, InvalidResponseException, InvalidKeyException, NoSuchAlgorithmException, ServerException, ErrorResponseException, XmlParserException, InvalidBucketNameException, InsufficientDataException, InternalException {
        if (files == null) {
            throw this.restfulExceptionHelper.getRestfulRuntimeException("upload_no_file");
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw this.restfulExceptionHelper.getRestfulRuntimeException("upload_no_file");
            }
        }
        List<UploadViewEntity> uploadViews = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadViews.add(this.minioUpload(file, "/file/uploads"));
        }
        return uploadViews;
    }

    private UploadViewEntity minioUpload(MultipartFile file, String uri) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        if (file.isEmpty()) {
            throw this.restfulExceptionHelper.getRestfulRuntimeException("upload_no_file");
        }
        Map<String, String> map = new HashMap<>();
        map.put("filename", URLEncoder.encode(Objects.requireNonNull(file.getOriginalFilename()), StandardCharsets.UTF_8));
        String objectName = MessageFormat.format("{0}_{1}", UUID.randomUUID().toString(), file.getOriginalFilename());
        var builder = PutObjectArgs.builder();
        if (StringUtils.hasText(this.minIOProperties.getBucket())) {
            builder.bucket(this.minIOProperties.getBucket());
        }
        try (InputStream in = file.getInputStream()) {
            this.minioClient.putObject(builder.contentType(file.getContentType()).extraHeaders(map).object(objectName).stream(in, file.getSize(), -1).build());
        }
        UploadViewEntity uploadView = new UploadViewEntity();
        objectName = URLEncoder.encode(objectName, StandardCharsets.UTF_8);
        uploadView.setFilename(objectName);
        HttpServletRequest request = ControllerHelper.getHttpServletRequest();
        StringBuffer url = request.getRequestURL();
        String baseUrl = url.substring(0, url.length() - uri.length());
        if (StringUtils.hasText(this.appConfig.getBaseUrl())) {
            baseUrl = this.appConfig.getBaseUrl();
        }
        uploadView.setViewUrl(MessageFormat.format("{0}/file/view/{1}", baseUrl, objectName));
        uploadView.setDownloadUrl(MessageFormat.format("{0}/file/download/{1}", baseUrl, objectName));
        return uploadView;
    }

    private ObjectStat statObject(String filename) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        filename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
        var builder = StatObjectArgs.builder();
        if (StringUtils.hasText(this.minIOProperties.getBucket())) {
            builder.bucket(this.minIOProperties.getBucket());
        }
        return this.minioClient.statObject(builder.object(filename).build());
    }

    /**
     * 查看图片接口
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
    @RequestMapping("view/{filename}")
    public ResponseEntity<InputStreamResource> view(@PathVariable String filename) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        filename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
        var objectStat = this.statObject(filename);
        var builderGet = GetObjectArgs.builder();
        if (StringUtils.hasText(this.minIOProperties.getBucket())) {
            builderGet.bucket(this.minIOProperties.getBucket());
        }
        return ResponseEntity
                .ok()
                .contentLength(objectStat.length())
                .contentType(MediaType.valueOf(objectStat.contentType()))
                .body(new InputStreamResource(this.minioClient.getObject(builderGet.object(filename).build())));
    }

    /**
     * 预下载
     *
     * @param filename 文件名称
     * @return 响应
     * @throws IOException                  异常
     * @throws InvalidKeyException          异常
     * @throws InvalidResponseException     异常
     * @throws InsufficientDataException    异常
     * @throws InvalidExpiresRangeException 异常
     * @throws ServerException              异常
     * @throws InternalException            异常
     * @throws NoSuchAlgorithmException     异常
     * @throws XmlParserException           异常
     * @throws InvalidBucketNameException   异常
     * @throws ErrorResponseException       异常
     */
    @RequestMapping("downloadByUrl/{filename}")
    public DownloadViewEntity downloadByUrl(@PathVariable String filename) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, InvalidExpiresRangeException, ServerException, InternalException, NoSuchAlgorithmException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        filename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
        var builder = GetPresignedObjectUrlArgs.builder();
        if (StringUtils.hasText(this.minIOProperties.getBucket())) {
            builder.bucket(this.minIOProperties.getBucket());
        }
        String url = this.minioClient.getPresignedObjectUrl(builder.object(filename).build());
        DownloadViewEntity downloadView = new DownloadViewEntity();
        downloadView.setUrl(url);
        return downloadView;
    }

    /**
     * 下载
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
    @GetMapping("download/{filename}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String filename) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        filename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
        var objectStat = this.statObject(filename);
        String saveFilename = objectStat.name();
        String[] filenameArray = filename.split("_", 2);
        if (filenameArray.length > 1) {
            saveFilename = filenameArray[1];
        }
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
