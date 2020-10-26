import io.minio.*;
import io.minio.errors.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MinioClientTest {
    public static void main(String[] args) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException, RegionConflictException, InvalidExpiresRangeException {
        String url = "http://10.4.1.166:9000/";
        String ak = "minio";
        String sk = "=[;._PL<0okm";
        String bucket = "industry";
        String filepath = "/Users/say/Downloads/";
        String filename = "7704D1657FAA63310DA126475EE320AF.png";
        MinioClient minioClient = MinioClient.builder().endpoint(url).credentials(ak, sk).build();
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        String objectName = UUID.randomUUID().toString();
        Map<String, String> map = new HashMap<>();
        map.put("filename", filename);
        File file = new File(filepath + filename);
        try (FileInputStream fin = new FileInputStream(file)) {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName).extraHeaders(map).stream(fin, file.length(), -1).build());
        }
        String presignedUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucket).object(objectName).build());
        System.out.println(presignedUrl);
        System.out.println(minioClient.getObjectUrl(bucket, objectName));
        ObjectStat objectStat = minioClient.statObject(bucket, objectName);
        System.out.println(objectStat);
        System.out.println(objectStat.httpHeaders());
        System.out.println(objectStat.httpHeaders().get("filename"));
    }
}
