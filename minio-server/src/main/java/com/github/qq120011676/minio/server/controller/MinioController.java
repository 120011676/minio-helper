package com.github.qq120011676.minio.server.controller;

import com.github.qq120011676.ladybird.web.exception.RestfulExceptionHelper;
import com.github.qq120011676.minio.properties.MinIOProperties;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.errors.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("file")
public class MinioController {
    @Resource
    private RestfulExceptionHelper restfulExceptionHelper;
    @Resource
    private MinioClient minioClient;
    @Resource
    private MinIOProperties minIOProperties;

    @PostMapping("upload")
    public JsonObject upload(MultipartFile file) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidArgumentException, InvalidResponseException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException {
        if (file.isEmpty()) {
            throw this.restfulExceptionHelper.getRestfulRuntimeException("upload_no_file");
        }
        return minioUplod(file);
    }

    @PostMapping("uploads")
    public JsonArray uploads(MultipartFile[] files) throws IOException, XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, InvalidArgumentException, InvalidResponseException, InternalException, NoResponseException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException {
        if (files == null) {
            throw this.restfulExceptionHelper.getRestfulRuntimeException("upload_no_file");
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw this.restfulExceptionHelper.getRestfulRuntimeException("upload_no_file");
            }
        }
        JsonArray jsonArray = new JsonArray();
        for (MultipartFile file : files) {
            jsonArray.add(minioUplod(file));
        }
        return jsonArray;
    }

    private JsonObject minioUplod(MultipartFile file) throws InvalidBucketNameException, NoSuchAlgorithmException, IOException, InvalidKeyException, NoResponseException, XmlPullParserException, ErrorResponseException, InternalException, InvalidArgumentException, InsufficientDataException, InvalidResponseException {
        Map<String, String> map = new HashMap<>();
        map.put("filename", file.getOriginalFilename());
        String objectName = UUID.randomUUID().toString();
        this.minioClient.putObject(this.minIOProperties.getBucket(), objectName, file.getInputStream(), file.getSize(), map, null, file.getContentType());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", objectName);
        return jsonObject;
    }

    @GetMapping("downloadByUrl/{filename}")
    public JsonObject uploads(@PathVariable String filename) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidExpiresRangeException, InvalidResponseException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        String url = this.minioClient.presignedGetObject(this.minIOProperties.getBucket(), filename);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", url);
        return jsonObject;
    }

    @GetMapping("download/{filename}")
    public ResponseEntity<InputStream> download(@PathVariable String filename) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidResponseException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidArgumentException {
        ObjectStat objectStat = this.minioClient.statObject(this.minIOProperties.getBucket(), filename);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", MessageFormat.format("attachment; filename=\"{0}\"", objectStat.httpHeaders().get("filename").get(0)));
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(this.minioClient.getObject(this.minIOProperties.getBucket(), filename));
    }
}
