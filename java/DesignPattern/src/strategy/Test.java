package strategy;

import java.util.Arrays;
import java.util.Comparator;

public class Test {
    public static void main(String[] args) {
        Integer[] a = {9, 5, 6, 7, 4, 8, 1};
        Integer[] a2 = {19, 15, 16, 17, 16, 14, 18, 1};
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 >= o2) {
                    return -1;
                } else {
                    return 1;
                }
            }

            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };

        Arrays.sort(a, comparator);
        System.out.println(Arrays.toString(a));

        Arrays.sort(a2, (var1, var2) -> {
            if (var1.compareTo(var2) > 0) {
                return -1;
            }else {
                return 1;
            }
        });
        System.out.println(Arrays.toString(a2));


    }
}
