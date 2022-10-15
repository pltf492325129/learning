package dailyExercise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class _1200min {
    public List<List<Integer>> minimumAbsDifference(int[] arr) {
        //1 排序
        List<List<Integer>> ans = new ArrayList<List<Integer>>();
        int best = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int n = arr.length;
        Arrays.sort(arr);
        for (int i : arr) {
            System.out.print(" " + i);
        }
        System.out.println();
        List<List<Integer>> com = new ArrayList<List<Integer>>();
        //2 比较绝对差
        for (int i = 0; i < n-1; i++) {
            //计算绝对差
            int temp = arr[i+1] - arr[i];
            //3 使用一个新数组进行存储

            //若绝对差大于最小差
            if (temp < best){
                best = temp;
                com = ans;
                ans.clear();
                ArrayList<Integer> t = new ArrayList<>();
                t.add(arr[i]);
                t.add(arr[i + 1]);
                ans.add(t);
            } else if (temp == best) {
                ArrayList<Integer> t = new ArrayList<>();
                t.add(arr[i]);
                t.add(arr[i + 1]);
                ans.add(t);
            }
        }
        System.out.println(ans);
        return ans;

    }

    public static void main(String[] args) {
        int[] arr = new int[]{40, 11, 26, 27, -20};//1 2 3 5
        //int[] arr = new int[]{1,2,3,5};//1 2 3 5
        _1200min min = new _1200min();

        min.minimumAbsDifference(arr);


    }


}














