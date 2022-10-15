package hotPractice;

public class Test3 {
    public static void main(String[] args) {
        Test3 test3 = new Test3();
        String s = test3.toLowerCase("HeLLdsfsfsdHHo");
        System.out.println(s);
    }
    public String toLowerCase(String s) {
        char[] array = s.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] >= 'A' && array[i] <= 'Z') {
                array[i] += 32;
            }
        }
        return new String(array);
    }
}

