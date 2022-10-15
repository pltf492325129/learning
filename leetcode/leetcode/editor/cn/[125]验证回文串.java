//给定一个字符串，验证它是否是回文串，只考虑字母和数字字符，可以忽略字母的大小写。 
//
// 说明：本题中，我们将空字符串定义为有效的回文串。 
//
// 示例 1: 
//
// 输入: "A man, a plan, a canal: Panama"
//输出: true
// 
//
// 示例 2: 
//
// 输入: "race a car"
//输出: false
// 
// Related Topics 双指针 字符串 
// 👍 373 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public boolean isPalindrome(String s) {
        //自顶向下编程 高层次 (主干) 逻辑
        // 1 去除除字母和数字的无关字符 filter out number & char; 2 所有字符倒过来 reverse and compare
        String filteredS = _filterNonNumberAndChar(s);
        String reversedS = _reverseString(filteredS);
        return reversedS.equalsIgnoreCase(reversedS);
    }
    private String _reverseString(String s) {
        return new StringBuffer(s).reverse().toString();
    }
    private String _filterNonNumberAndChar(String s) {
        return s.replaceAll("[^a-zA-Z0-9]"," ")
    }
}


//leetcode submit region end(Prohibit modification and deletion)
