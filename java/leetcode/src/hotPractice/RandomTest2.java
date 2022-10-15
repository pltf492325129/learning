package hotPractice;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RandomTest2 {
    public static void main(String[] args) {
        List<List<Integer>> res = new ArrayList<>();


        LinkedList<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(3);
        list.add(5);


        res.add(new ArrayList<>(list));
        System.out.println(list);
        System.out.println(res);

        list.add(0);
        list.add(6);
        list.add(7);
        res.add(new ArrayList<>(list));

        System.out.println(list);
        System.out.println(res);
    }
}
