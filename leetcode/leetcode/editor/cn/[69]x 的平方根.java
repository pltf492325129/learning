//实现 int sqrt(int x) 函数。 
//
// 计算并返回 x 的平方根，其中 x 是非负整数。 
//
// 由于返回类型是整数，结果只保留整数的部分，小数部分将被舍去。 
//
// 示例 1: 
//
// 输入: 4
//输出: 2
// 
//
// 示例 2: 
//
// 输入: 8
//输出: 2
//说明: 8 的平方根是 2.82842..., 
//     由于返回类型是整数，小数部分将被舍去。
// 
// Related Topics 数学 二分查找 
// 👍 703 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int mySqrt(int x) {
        int left = 0;
        int right = x;
        int res = -1;
        while (left <= right) {
            int mid = left + (right-left) / 2;
            if ((long)mid * mid <= x) {
                /**
                 * res放置结果，遍历一遍，最大的值，就是平方根。
                 */
                res = mid;
                left = mid + 1;
            } else{
                right = mid - 1;
            }
        }
        return res;

    }
}
//leetcode submit region end(Prohibit modification and deletion)
