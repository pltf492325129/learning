package jianzhiOffer.last;

import java.util.*;

public class _59maxSlidingWindow {

    public int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> queue = new LinkedList<>();
        int left = 0, right= -1;
        int n = nums.length;
        if (n == 0 || k == 0) return new int[0];
        int[] result = new int[n - k + 1];
        int j = 0;
        for (int i = 0; i < n && right < n; i++) {
            right++;
            queue.add(nums[right]);
            //如果窗口的大小 = k 并且right < n,向右遍历
            while ((right - left) == (k - 1) && right < n) {
                int value = maxValue(queue);
                //放结果集
                result[j++] = value;
                queue.removeFirst();
                right++;
                if (right < n) {
                    queue.add(nums[right]);
                }
                left++;
            }
        }
        return result;
    }
    //获取窗口中的最大值
    public int maxValue(Deque<Integer> queue) {
        int max = Integer.MIN_VALUE;
        for (Integer integer : queue) {
            max = Math.max(integer, max);
        }
        return max;
    }


    public static void main(String[] args) {
        int[] a = {1, 3, -1, -3, 5, 3, 6, 7};
        _59maxSlidingWindow maxSlidingWindow = new _59maxSlidingWindow();
        int[] ints = maxSlidingWindow.maxSlidingWindow(a, 3);
        for (int anInt : ints) {
            System.out.print(anInt + " ");
        }
    }
}
