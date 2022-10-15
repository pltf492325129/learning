//输入一个非负整数数组，把数组里所有数字拼接起来排成一个数，打印能拼接出的所有数字中最小的一个。 
//
// 
//
// 示例 1: 
//
// 输入: [10,2]
//输出: "102" 
//
// 示例 2: 
//
// 输入: [3,30,34,5,9]
//输出: "3033459" 
//
// 
//
// 提示: 
//
// 
// 0 < nums.length <= 100 
// 
//
// 说明: 
//
// 
// 输出结果可能非常大，所以你需要返回一个字符串而不是整数 
// 拼接起来的数字可能会有前导 0，最后结果不需要去掉前导 0 
// 
// Related Topics 贪心 字符串 排序 
// 👍 257 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public String minNumber(int[] nums) {
        // x + y > y + x 则 x 大于 y
        String[] strs = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            strs[i] = String.valueOf(nums[i]);
        }
        quickSort(strs, 0, nums.length-1);
        StringBuilder res = new StringBuilder();
        for (String s : strs) {
            res.append(s);
        }
        return res.toString();
    }

    private void quickSort(String[] strs, int left, int right) {
        if (left < right) {
            int mid = get_mid(strs, left, right);
            quickSort(strs, left, mid  - 1);
            quickSort(strs, mid + 1, right);

        }
    }

    private int get_mid(String[] strs, int left, int right) {
        String pivot = strs[left];
        while (left < right) {
            //注意这里使用的是pivot，从后向前找比基准数小的数
            //while ((strs[right] + strs[left]).compareTo(strs[left] + strs[right])  >= 0 && left < right) right--;
            while ((strs[right] + pivot).compareTo(pivot+ strs[right])  >= 0 && left < right) right--;
            strs[left] = strs[right];
            //从前往后找比基准数大的数。
            while ((strs[left] + pivot).compareTo(pivot + strs[left])  <= 0 && left < right) left++;
            strs[right] = strs[left];
        }
        strs[left] = pivot;
        return left;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
