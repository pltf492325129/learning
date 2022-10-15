package jianzhiOffer;

import java.util.HashMap;

public class _53search {
    public int search(int[] nums, int target) {
        return 0;
    }


    public int search2(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i], map.getOrDefault(nums[i], 0) + 1);
        }

        return map.containsKey(target) ? map.get(target) : 0;
    }
}
