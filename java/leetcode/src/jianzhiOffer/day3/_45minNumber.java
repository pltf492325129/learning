package jianzhiOffer.day3;

public class _45minNumber {
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

    public static void main(String[] args) {
        _45minNumber minNumber = new _45minNumber();
        int[] nums = {3, 30, 34, 5, 9};
        int[] nums2 = {10, 2};
        int[] nums3 = {1,2,3,1};

        String s = minNumber.minNumber(nums3);
        System.out.println(s);
    }


}
