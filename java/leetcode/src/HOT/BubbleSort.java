package HOT;

public class BubbleSort {
    public static void main(String[] args) {

        int[] arr = {1,1,2,0,9,3,12,7,8,3,4,65,22};
        bubbleSort(arr);
        for (int i : arr) {
            System.out.println(i);
        }
    }
    public static void bubbleSort(int[] arr) {
        int temp;
        for (int j = arr.length - 1; j > 0; j--) {
            for (int i = 0; i < j; i++) {
                if (arr[i + 1] < arr[i]) {
                    temp = arr[i];
                    arr[i] = arr[i+1];
                    arr[i+1] = temp;
                }
            }
        }
    }

}
