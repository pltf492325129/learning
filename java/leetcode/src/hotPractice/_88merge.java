package hotPractice;

public class _88merge {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int left = 0, right = 0;

        for (int i = 0; i < m + n; i++) {
            if (nums1[left] <= nums2[right] && left < m) {
                left++;
            } else if (left >= m){
                nums1[left++] = nums2[right++];
            }if (left < m){
                int temp = nums1[left];
                nums1[left] = nums2[right];
                nums2[right] = temp;
            }
        }
    }
    public void merge2(int[] nums1, int m, int[] nums2, int n) {
        int left = m-1, right = n - 1;
        int tail = m + n - 1;
        while (left >= 0 && right >= 0) {
            nums1[tail--] = (nums1[left] > nums2[right]) ? nums1[left--] : nums2[right--];
        }
        System.arraycopy(nums2,0, nums1,0,right+1);

    }
    public static void main(String[] args) {
        int[] nums1 = {1,2,3,0,0,0};
        int[] nums2 = {2,5,6};
        _88merge merge = new _88merge();
        merge.merge2(nums1, 3, nums2, 3);

        for (int i : nums1) {
            System.out.print(i);
            System.out.print(" ");
        }
    }
}
