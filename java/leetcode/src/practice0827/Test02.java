package practice0827;

public class Test02 {
    public static void main(String[] args) {
        String data = "1,2,#,4,#,#,3,#,#,";
        String[] nodes = data.split(",");
        System.out.println(nodes[1]);


        int a = 1;
        int b = a;

        System.out.println(a);
        System.out.println(b);

        a = 3;

        System.out.println(a);
        System.out.println(b);
    }
}
