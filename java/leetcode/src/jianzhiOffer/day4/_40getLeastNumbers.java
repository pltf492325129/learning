package jianzhiOffer.day4;

public class _40getLeastNumbers {
    public int[] getLeastNumbers(int[] arr, int k) {
        quickSort(arr,0, arr.length - 1);
        int[] arrNew = new int[k];
        for (int j = 0; j < k; j++) {
            arrNew[j] = arr[j];
        }
        return arrNew;
    }

    public void quickSort(int[] nums, int left, int right) {
        if (left < right) {
            int mid = get_mid(nums, left, right);
            quickSort(nums, left, mid - 1);
            quickSort(nums, mid + 1, right);
        }
    }

    private int get_mid(int[] nums, int left, int right) {
        int pivot = nums[left];
        while (left < right) {
            while ( left < right && pivot <= nums[right]) right--;
            nums[left] = nums[right];
            while (left < right && nums[left] <= pivot) left++;
            nums[right] = nums[left];
        }
        nums[left] = pivot;
        return left;
    }

    public static void main(String[] args) {
        int[] test2 = {3, 2, 1};
        int[] test = {0,1, 2, 1};
        _40getLeastNumbers getLeastNumbers = new _40getLeastNumbers();

        int[] leastNumbers = getLeastNumbers.getLeastNumbers(test, 3);
        for (int leastNumber : leastNumbers) {
            System.out.println(leastNumber);
        }
    }
}
