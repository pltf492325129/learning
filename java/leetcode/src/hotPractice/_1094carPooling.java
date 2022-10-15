package hotPractice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class _1094carPooling {
    public boolean carPooling(int[][] trips, int capacity) {
        PriorityQueue<int[]> heap = new PriorityQueue<>(Comparator.comparingInt(o -> o[2]));
        //上车的顺序
        Arrays.sort(trips, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[1];
            }
        });
        for (int[] trip : trips) {
            capacity -= trip[0];
            if (capacity < 0) {
                while (!heap.isEmpty() && heap.peek()[2] <= trip[1]) {
                    capacity += heap.poll()[2];
                }
            }
            if (capacity < 0) return false;
            heap.offer(trip);
        }
        return true;
    }

    public static void main(String[] args) {
        int[][] trips = {{2, 1, 5}, {3, 3, 7}};
        Arrays.sort(trips, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[1] - o2[1];
            }
        });
        for (int[] trip : trips) {
            for (int i : trip) {
                System.out.print(i);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}
