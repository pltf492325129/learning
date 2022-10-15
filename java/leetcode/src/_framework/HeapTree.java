package _framework;

public class HeapTree {
    //把树调整为堆 heapify
    //i表示第i个节点
    void heapify(int[] tree, int n, int i) {
        //递归终止
        if (i >= n) return;
        int c1 = 2 * i + 1;
        int c2 = 2 * i + 2;
        int max = i;
        if (c1 < n && tree[c1] > tree[max]) {
            max = c1;
        }
        if (c2 < n && tree[c2] > tree[max]) {
            max = c2;
        }
        //这里是 最大值不是i的时候就进行交换，最大值=i就不需要进行交换
        if (max != i) {
            swap(tree, max, i);
            heapify(tree, n, max);
        }
    }

    private void swap(int[] tree, int max, int i) {
        int temp = tree[max];
        tree[max] = tree[i];
        tree[i] = temp;
    }

    //堆 的子节点
    //左子节点 2i * 1
    //又子节点 2i * 2
    //父节点 （i - 1）/ 2  向下取整

    //该方法在乱序下，排序是一个接一个排着的，直到顶点
    public void build_heap(int[] tree, int n) {
        int last_child = n - 1;
        int lc = (last_child - 1) / 2;
        for (int i = lc; i >= 0; i--) {
            heapify(tree, n, i);
        }
    }

    void heap_sort(int[] tree, int n) {
        build_heap(tree, n);
        for (int i = n - 1; i >= 0; i--) {
            swap(tree, i, 0);
            //注意这里 第二个参数为i,
            heapify(tree, i, 0);
        }
    }


    public static void main(String[] args) {
        int[] arr = {10, 5, 8, 3, 4, 6, 7, 1, 2};
        int[] tree = {4, 10, 3, 5, 1, 2};
        int[] tree2 = {10, 3, 4, 1, 5, 2};
        int n = 6;
        HeapTree heapTree = new HeapTree();
        //heapTree.heapify(tree, n, 0);
        //heapTree.build_heap(tree2, 6);
        heapTree.heap_sort(tree2, 6);
        for (int i = 0; i < n; i++) {
            System.out.println(tree2[i]);
        }
    }

}
