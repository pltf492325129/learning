package hotPractice;

import java.util.ArrayList;
import java.util.Arrays;

public class _406reconstructQueue {
    public int[][] reconstructQueue(int[][] people) {
        Arrays.sort(people, (o1, o2) -> {
            if (o1[0] != o2[0]) {
                return o2[0] - o1[0];
            } else {
                return o1[1] - o2[1];
            }
        });
        ArrayList<int[]> list = new ArrayList<>();
        for (int[] person : people) {
            if (list.size() < person[1]) {
                list.add(person);
            } else if (list.size() >= person[1]) {
                list.add(person[1], person);
            }
        }
        return list.toArray(new int[list.size()][]);
        
    }

    public static void main(String[] args) {
        int[][] people = {{7, 0}, {4, 4}, {7, 1}, {5, 0}, {6, 1}, {5, 2}};
        int[][] ints = new _406reconstructQueue().reconstructQueue(people);
        for (int[] anInt : ints) {
            for (int i : anInt) {
                System.out.print(i);
                System.out.print(" ");
            }
            System.out.println();
        }

    }
}
