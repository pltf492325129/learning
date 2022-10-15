package hotPractice;

import java.util.HashMap;
import java.util.Map;

public class _169majorityElement {
    public int majorityElement(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>();
        int ret = 0;
        for (int num : nums) {
            if (!map.containsKey(num)) {
                map.put(num, 1);
            }else {
                map.put(num, map.get(num) + 1);
            }
            if (map.get(num) > nums.length / 2) {
                ret = num;
                break;
            }
        }
/*        for (int i = 0; i < nums.length; i++) {
            int count = map.get(nums[i]) == 0 ? 1 : map.get(nums[i]) + 1;
            map.put(nums[i], count);
        }
        for (int j = 0; j < nums.length; j++) {
            if (map.get(nums[j]) > (nums.length / 2)) {
                ret = nums[j];
            }
        }*/
        return ret;
    }

    public static void main(String[] args) {
        _169majorityElement _169majorityElement = new _169majorityElement();
        int[] nums = {2,2,1,1,1,2,2};
        int res = _169majorityElement.majorityElement(nums);
        System.out.println(res);

    }
}
