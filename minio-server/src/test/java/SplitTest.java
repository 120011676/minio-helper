public class SplitTest {
    public static void main(String[] args) {
        String filename = "aa_bb_cc.jpg";
        String[] arr = filename.split("_", 2);
        System.out.println(arr.length);
        System.out.println(arr[1]);
    }
}
