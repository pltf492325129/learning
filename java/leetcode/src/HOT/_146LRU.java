package HOT;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class _146LRU {
    LinkedHashMap<Integer, Integer> cache = new LinkedHashMap<>();
    int cap;
    public _146LRU(int capacity) {
        cap = capacity;
    }

    public int get(int key) {
        if (!cache.containsKey(key)) return -1;
        makeRecently(key);
        return cache.get(key);
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            cache.put(key,value);
            makeRecently(key);
            //注意这里直接有个返回
            return;
        }
        if (cache.size() >= cap) {
            /**
             * 这里的迭代器，返回的是最久未使用的那个元素嘛？？
             */
            int oldKey = cache.keySet().iterator().next();
            cache.remove(oldKey);
        }
        cache.put(key, value);
    }
    private void makeRecently(int key) {
        int val = cache.get(key);
        cache.remove(key);
        cache.put(key, val);
    }
}

class LRUCache {
    //key -> Node<key, val>
    private HashMap<Integer, Node> map;
    private DoubleList cache;
    //最大容量
    private int cap;

    public LRUCache(int capacity) {
        this.cap = capacity;
        map = new HashMap<>();
        cache = new DoubleList();
    }
    //为了防止漏掉一些操作，删除某个key时，删除了对应的Node中，却忘记了删除对应的某个key

    //将某个key提升为最近使用的
    public void makeRecently(int key) {
        Node x = map.get(key);
        cache.remove(x);
        cache.addLast(x);
    }
    // 添加最近使用的元素
    //这里传入的是值
    public void addRecently(int key, int val) {
        Node x = new Node(key, val);
        map.put(key, x);
        cache.addLast(x);
    }

    // 删除某一个key
    public void deleteKey(int key) {
        Node x = map.get(key);
        cache.remove(x);
        map.remove(key);
    }
    // 删除最久未使用的元素
    public void deleteLeastRencently() {
        Node deleteNode = cache.removeFirst();
        int deleteKey = deleteNode.key;
        map.remove(deleteKey);
    }

    //把该值提升为最近使用的，并返回该值
    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        makeRecently(key);
        return map.get(key).val;
    }

    public void put(int key, int val) {
        if (map.containsKey(key)) {
            deleteKey(key);
            addRecently(key, val);
        }
        if (cap == cache.size) {
            deleteLeastRencently();
        }
        addRecently(key, val);
    }
}


class DoubleList {
    public Node head, tail;
    public int size;
    //链表的元素数目

    public DoubleList() {
        /**
         * 注意这里的头节点和尾节点都是虚拟节点
         */
        head = new Node(0, 0);
        head = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    //在链表尾部添加节点x，时间复杂度为1
    public void addLast(Node node) {
        node.next = tail;
        tail.prev.next = node;
        node.prev = tail.prev;
    }

    //删除链表中的 x 节点（x 一定存在）
    public void remove(Node x) {
        x.prev.next = x.next;
        x.next.prev = x.prev;
        size--;
    }

    //删除链表中的第一个节点
    public Node removeFirst() {
        if (head.next == tail) return null;
        //注意这里移除的是第二个节点，头节点之后的那个节点
        Node first = head.next;
        remove(first);
        return first;
    }

    //返回链表长度
    public int size() {
        return size;
    }
}

class Node {
    public int key, val;
    public Node next,prev;

    public Node(int k, int v) {
        this.key = k;
        this.val = v;
    }
}
