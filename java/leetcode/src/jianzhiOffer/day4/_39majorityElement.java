package jianzhiOffer.day4;

import java.util.Arrays;

public class _39majorityElement {
    //还有哈希表
    public int majorityElement(int[] nums) {
        Arrays.sort(nums);
        int n = nums.length;
        return nums[n / 2];
    }
}
