package jianzhiOffer;

class _57twotarget {
    public int[] twoTarget(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[l] + nums[r] > target) {
                r--;
            }else if (nums[l] + nums[r] < target) {
                l++;
            }
            if (nums[l] + nums[r] == target) {
                return new int[]{ nums[l], nums[r] };
            }
        }
        return new int[0];
    }
}
