package HOT;

public class FastSorting {
    public static void main(String[] args) {
        int[] nums = {5,1,1,2,0,0};
        FastSorting fastSorting = new FastSorting();
        fastSorting.fastSorting(nums, 0, 5);
        for (int num : nums) {
            System.out.print(num);
            System.out.println(" ");
        }
    }
    public int[] fastSorting(int[] nums, int l, int r) {
        //这里是 if
        if (l < r) {
            int mid = getMid(nums, l, r);
            fastSorting(nums, l, mid-1);
            fastSorting(nums, mid + 1, r);
        }
        return nums;
    }

    public static int getMid(int[] nums, int l, int r) {
        int pivot = nums[l];
        // l < r 没有等号
        while (l < r) {
            while (nums[r] >= pivot && l < r) {
                r--;
            }
            //这里是 nums[l] = nums[r]
            nums[l] = nums[r];
            while (nums[l] <= pivot && l < r) {
                l++;
            }
            nums[r] = nums[l];
        }
        nums[l] = pivot;
        return l;
    }
}
