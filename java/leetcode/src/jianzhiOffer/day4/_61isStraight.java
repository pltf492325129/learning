package jianzhiOffer.day4;

import java.util.Arrays;
import java.util.HashSet;

public class _61isStraight {
    public boolean isStraight(int[] nums) {
        HashSet<Object> repeat = new HashSet<>();
        int max = 0, min = 14;
        for (int num : nums) {
            if (num == 0) continue;
            max = Math.max(max, num);
            min = Math.min(min, num);
            if (repeat.contains(num)) return  false;
            repeat.add(num);
        }
        return max - min  < 5;
    }
    public boolean isStraight3(int[] nums) {
        Arrays.sort(nums);
        int joker = 0;
        for (int i = 0; i < 5; i++) {
            if (nums[i] == 0) joker++;
            if (nums[i+1] == nums[i]) return  false;
        }
        return nums[4] - nums[joker] < 5;
    }
    //没考虑全情况，1 首先不能有重复元素，除大小王， 2 最大值和最小值不能小于5
    public boolean isStraight2(int[] nums) {
        int m = 0, n = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0){
                m++;
            }else if ((i + 1) < nums.length && nums[i+1] == nums[i] + 1){
                continue;
            }else {
                m--;
            }
        }
        return m < 0 ? false : true;
    }

    public static void main(String[] args) {
        _61isStraight isStraight = new _61isStraight();
        int[] nums = {0, 0, 1, 2, 6};
        boolean straight = isStraight.isStraight(nums);
        System.out.println(straight);
    }
}
