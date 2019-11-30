import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

public class MediaTypeTest {
    public static void main(String[] args) {
        String type = "image/png";
        System.out.println(MediaType.parseMediaType(type));
        System.out.println(MediaType.asMediaType(MimeType.valueOf(type)));
        System.out.println(MediaType.valueOf(type));
        System.out.println(MediaType.parseMediaType("application/force-download"));
    }
}
