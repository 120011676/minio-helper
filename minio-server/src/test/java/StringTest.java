public class StringTest {
    public static void main(String[] args) {
        StringBuilder url = new StringBuilder("http://localhost:8080/file/upload");
        String uri = "/file/upload";
        System.out.println(url.substring(0, url.length() - uri.length()));
    }
}
