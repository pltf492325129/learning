package practice0827;

import java.util.*;

public class Test01 {
    public String string;

    public static void main(String[] args) {
        int[] a = {2, 2, 1};
        int[] a2 = {4, 1, 2, 1, 2};
        Test01 test01 = new Test01();
        int i = test01.singleNumber3(a2);

        int[] b1 = {2, 2, 1, 1, 1, 2, 2};
        int[] b2 = {3,2,3};
        int i1 = test01.majorityElement(b1);
        System.out.println(i1);
    }

    public int singleNumber2(int[] nums) {
        int res = nums[0];
        HashMap hashMap = new HashMap();
        for (int i = 0; i < nums.length; i++) {
            if (hashMap.containsKey(nums[i])) {
                hashMap.put(nums[i], (Integer) hashMap.get(nums[i]) + 1);
            } else {
                hashMap.put(nums[i], 1);
            }
        }
        for (int num : nums) {
            if ((Integer) hashMap.get(num) == 1) {
                res = num;
            }
        }
        return res;
    }

    public int singleNumber(int[] nums) {
        HashSet objects = new HashSet();
        for (int num : nums) {
            if (objects.contains(num)) {
                objects.remove(num);
            } else {
                objects.add(num);
            }
        }
        Iterator iterator = objects.iterator();
        Object next = iterator.next();
        return (Integer) next;
    }

    public int singleNumber3(int[] nums) {
        Set objects = new HashSet<>();
        for (int num : nums) {
            if (!objects.add(num)) {
                objects.remove(num);
            }
        }

        return (int) objects.toArray()[0];

    }

    public int majorityElement(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>();
        int res = nums[0];
        int len = nums.length;
        for (int i = 0; i < len; i++) {
            if (map.containsKey(nums[i])) {
                map.put(nums[i], map.get(nums[i])+1);
                if (map.get(nums[i]) > len / 2) {
                    return nums[i];
                }
            }else {
                map.put(nums[i], 1);
            }
        }
        return res;
        //for (int i = 0; i < len; i++) {
        //    if (map.get(nums[i]) > len / 2) {
        //        res = nums[i];
        //    }
        //}
    }
}
