package jianzhiOffer.day4;

public class _21exchangeNumber {
    public int[] exchange(int[] nums) {
        int i = 0, j = 0;
        while (i < j) {
            while (i < j && (nums[i] & 1) == 1) i++;
            while (i < j && (nums[j] & 1) == 0) j--;
            //swap(nums[i], nums[j]);
        }
        return nums;
    }
    public int[] exchange2(int[] nums) {
        int low = 0, fast = 0;
        while (fast < nums.length) {
            if ((nums[fast] & 1) == 1) {
                //int tmp = nums[low];
                //nums[low] = nums[fast];
                //nums[fast] = tmp;
                swap(nums, low, fast);
                low++;
            }
            fast++;
        }
        return nums;
    }

    /**
     * 注意这里涉及到值引用，如果不传数组，只给两个数赋值，并没有改变数组的引用，所以导致出错
     */

    public static void swap(int[] a, int low, int fast) {
        int temp = a[low];
        a[low] = a[fast];
        a[fast] = temp;
    }

    private static int[] swap2(int a, int b){
        int temp = a;
        a = b;
        b = temp;
        return new int[]{a,b};
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4};
        _21exchangeNumber exchangeNumber = new _21exchangeNumber();
        exchangeNumber.exchange2(nums);
        for (int num : nums) {
            System.out.println(num);
        }

    }
}
