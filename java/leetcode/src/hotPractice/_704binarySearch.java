package hotPractice;

public class _704binarySearch {
    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        int res = -1;
        while (left < right) {
            int mid = left + right;
            if (nums[mid] > target) {
                right = mid - 1;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else if (nums[mid] == target) {
                res = mid;
                break;
            }
        }

        return res;
    }

    public static void main(String[] args) {
        //System.out.println(42 == 42.0);
        //int[] nums = {-1, 0, 3, 5, 9, 12};
        int[] nums = {-1,0,3,5,9,12};
        int target = 2;

        int res = new _704binarySearch().search(nums, target);
        System.out.println(res);
    }
}
//nums = [-1,0,3,5,9,12], target = 9
//class TreeNode{
//    TreeNode left;
//    TreeNode right;
//    int val;
//    public TreeNode(int x ) {
//        this.val = x;
//    }
//
//    public TreeNode(TreeNode left, TreeNode right,int x) {
//        this.left = left;
//        this.right = right;
//        this.val = x;
//    }
//}
