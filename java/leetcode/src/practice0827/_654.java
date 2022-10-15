package practice0827;

public class _654 {
    public TreeNode constructMaximumBinaryTree(int[] nums) {
        TreeNode root = build(nums, 0, nums.length-1);
        return root;
    }


    public TreeNode build(int[] nums, int left, int right) {
        if (left > right) return null;
        //寻找数组中最大的值，并找出索引
        int maxVal = Integer.MIN_VALUE;
        int index = 0;
        for (int i = left; i <= right; i++) {
            if (maxVal < nums[i]) {
                maxVal = nums[i];
                index = i;
            }
        }
        //1、创建根节点
        TreeNode root = new TreeNode(maxVal);
        //2 递归的构建左子树
        //root.left = build(nums[起始--终止]);
        root.left = build(nums, left, index - 1);
        //3 递归的构建右子树
        root.right = build(nums, index + 1, right);
        return root;
    }
}
