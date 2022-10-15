//写一个函数，求两个整数之和，要求在函数体内不得使用 “+”、“-”、“*”、“/” 四则运算符号。 
//
// 
//
// 示例: 
//
// 输入: a = 1, b = 1
//输出: 2 
//
// 
//
// 提示： 
//
// 
// a, b 均可能是负数或 0 
// 结果不会溢出 32 位整数 
// 
// Related Topics 位运算 数学 
// 👍 202 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public int add(int a, int b) {
        int sum = a ^ b;
        int carry = (a & b) << 1;
        while (carry != 0) {
            int oralSum = sum;
            sum = sum ^ carry;
            carry = (oralSum & carry) << 1;
        }
        return sum;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
