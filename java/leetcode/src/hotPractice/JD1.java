package hotPractice;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JD1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        int x = sc.nextInt();
        int y = sc.nextInt();
        int z = sc.nextInt();
        Map<Character, int[]> map = new HashMap<>();
        for (int i = 0; i < n; i++) {
            String s = sc.next();
            for (int j = 0; j < m; j++) {
                map.put(s.charAt(j), new int[] {i,j});
            }
        }
        String string = sc.next();

        int res = 0;
        int curHeng = 0;
        int curZong = 0;
        for (int i = 0; i < string.length(); i++) {
            int[] ints = map.get(string.charAt(i));
            int heng = ints[0];
            int zong = ints[1];
            //当前坐标和坐标对比
            if (curHeng == heng) {
                if (zong == curZong) {
                    res += z;
                }else {
                    res += Math.abs(curZong - zong)*x + z;
                }
            } else if (curHeng != heng) {
                if (zong != curZong) {
                    res += Math.abs(curZong - zong)*x + Math.abs(curHeng - heng)*x + y + z;
                }else if (zong == curZong) {
                    res += Math.abs(curHeng - heng)*x + z;
                }
            }
            curHeng = heng;
            curZong = zong;
        }
        System.out.println(res);
    }
}
//2 2 1 1 1
//.E
//:F
//EE:F.: