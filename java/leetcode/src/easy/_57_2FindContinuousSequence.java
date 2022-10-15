package easy;

import java.util.ArrayList;
import java.util.List;

public class _57_2FindContinuousSequence {
    public int[][] findContinuousSequence(int target) {
        List<int[]> res = new ArrayList();
        int left = 1, right = 1;
        int sum = 0;
        //这里为啥是 target/2, 至少是两位数，至少是一半
        while (left <= target/2) {
            if (sum < target) {
                //窗口增大
                sum += right;
                right++;
            } else if (sum > target) {
                //窗口缩小
                sum -= left;
                left++;
            } else {
                int[] arr = new int[right - left];
                for (int i = left; i < right; i++) {
                    arr[i-left] = i;
                }
                res.add(arr);
                sum -= left;
                left++;
            }
        }
        return res.toArray(new int[res.size()][]);
    }
}
