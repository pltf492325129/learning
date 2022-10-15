package hotPractice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class _554leastBricks {
    public int leastBricks(List<List<Integer>> wall) {
        HashMap<Integer, Integer> map = new HashMap<>();

        for (List<Integer> widths: wall) {
            int sum = 0;
            int n = widths.size();
            for (int j = 0; j < n - 1; j++) {
                sum += widths.get(j);
                map.put(sum, map.getOrDefault(sum, 0) + 1);
            }
        }
        int max = 0;
        for (Map.Entry<Integer, Integer> val : map.entrySet()) {
            max = Math.max(max, val.getValue());
        }
        return wall.size() - max;

    }

    public static void main(String[] args) {

    }
}
