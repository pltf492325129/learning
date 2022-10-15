package jianzhiOffer;

public class _26isSubStructure {
    public boolean isSubStructure(TreeNode A, TreeNode B) {
        //注意这里的括号，（recur(A,B) || ）

        //特例处理： 当 树 AA 为空 或 树 BB 为空 时，直接返回 falsefalse ；
        //返回值： 若树 BB 是树 AA 的子结构，则必满足以下三种情况之一，因此用或 || 连接；
        //以 节点 AA 为根节点的子树 包含树 BB ，对应 recur(A, B)；
        //树 BB 是 树 AA 左子树 的子结构，对应 isSubStructure(A.left, B)；
        //树 BB 是 树 AA 右子树 的子结构，对应 isSubStructure(A.right, B)；
        return (A != null && B != null) && (recur(A,B)
                || isSubStructure(A.left, B)
                || isSubStructure(A.right, B));
    }
    boolean recur(TreeNode A, TreeNode B) {
        if (B == null)return true;
        if (A == null || A.val != B.val) return false;
        return recur(A.left, B.left) && recur(A.right, B.right);
    }
}
