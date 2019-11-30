import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MinioClientTest {
    public static void main(String[] args) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidResponseException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, RegionConflictException, InvalidArgumentException, InvalidExpiresRangeException {
        String url = "http://111.9.116.135:9000/";
        String ak = "minio";
        String sk = "=[;._PL<0okm";
        String bucket = "industry";
        String filepath = "/Users/say/Downloads/";
        String filename = "7704D1657FAA63310DA126475EE320AF.png";
        MinioClient minioClient = new MinioClient(url, ak, sk);
        if (!minioClient.bucketExists(bucket)) {
            minioClient.makeBucket(bucket);
        }
        String objectName = UUID.randomUUID().toString();
        Map<String, String> map = new HashMap<>();
        map.put("filename", filename);
        minioClient.putObject(bucket, objectName, filepath + filename, null, map, null, null);
        String presignedUrl = minioClient.presignedGetObject(bucket, objectName);
        System.out.println(presignedUrl);
        System.out.println(minioClient.getObjectUrl(bucket, objectName));
        ObjectStat objectStat = minioClient.statObject(bucket, objectName);
        System.out.println(objectStat);
        System.out.println(objectStat.httpHeaders());
        System.out.println(objectStat.httpHeaders().get("filename"));
    }
}
