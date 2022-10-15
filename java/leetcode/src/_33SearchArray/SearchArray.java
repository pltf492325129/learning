package _33SearchArray;

public class SearchArray {
    public int search(int[] nums, int target) {
        int n = nums.length;
        if (n == 0) return -1;
        if (n == 1) return nums[0] == target ? 0 : -1;
        int l = 0, r = n - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if (nums[m] == target) return m;
            //这里是<=
            if (nums[0] <= nums[m] ) {
                /**
                 * 注意这里的target > nums[0]
                 * 注意这里的l是0
                 */
                //if (nums[l] > target && target < nums[m]) {
                if (nums[0] <= target && target < nums[m]) {
                    r = m - 1;
                } else {
                    l = m + 1;
                }
            } else if (nums[0] > nums[m]) {
                //if (target > nums[l] && target < nums[r]) {
                if (target > nums[m] && target <= nums[n-1]) {
                    l = m + 1;
                } else {
                    r = m  - 1;
                }
            }
        }
        return -1;
    }
}
