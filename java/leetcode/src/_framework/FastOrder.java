package _framework;

public class FastOrder {

    public void fastOrder(int nums[], int left, int right) {
        if (left < right) {
            int mid = get_mid(nums, left, right);
            fastOrder(nums, left, mid - 1);
            fastOrder(nums, mid + 1, right);
        }
    }

    private int get_mid(int[] nums, int left, int right) {
        int pivot = nums[left];
        while (left < right) {
            //注意有 = 号
            while (nums[right] >= pivot && left < right) right--;
            nums[left] = nums[right];
            while (nums[left] <= pivot && left < right) left++;
            nums[right] = nums[left];
        }
        //这里不要弄错方向， 最后把pivot值赋给最中间的值
        nums[left] = pivot;
        //此时返回的是中间的pivot下标，进行下一轮循环
        return left;
    }

    public static void main(String[] args) {
        FastOrder fastOrder = new FastOrder();
        int[] nums = {5, 4, 7, 2, 9, 1, 77};
        fastOrder.fastOrder(nums, 0 , 6);
        for (int i = 0; i < nums.length; i++) {
            System.out.println(nums[i]);
        }
    }
}
