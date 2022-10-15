package hotPractice;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {

    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        int[] res;
        for (int i = 0; i < nums.length; i++) {
            if (map.containsValue(target - nums[i])) {
                //返回两个值的下标
                return res = new int[]{i, map.get(target - nums[i])};
            }
            map.put(i, nums[i]);
        }
        return new int[0];
    }


    public static void main(String[] args) {

    }

    public int singleNumber(int[] nums) {
        //    使用hashtable存储数字，和出现的次数
        Map<Integer, Integer> map = new HashMap<>();
        for (int i : nums) {
            Integer count = map.get(i);
            count = count == null ? 1 : ++count;
            map.put(i, count);
        }
        for (Integer i : map.keySet()) {
            Integer count = map.get(i);
            if (count == 1) {
                return i;
            }
        }
/*        for (int i = 0; i < nums.length; i++) {
            Integer count = map.get(nums[i]);
            count = count == null ? 1 : ++count;
            map.put(nums[i], count);
        }
        for (int j = 0; j < nums.length; j++) {
            Integer count = map.get(nums[j]);
            if (count == 1) {
                return nums[j];
            }
        }*/
        return -1;
        //    遍历表，找出只出现一次的数字



    }

}



