import sys


class Sloution:
    """
    滑动窗口题目
    """

    def minWindow(self, s: str, t: str) -> str:
        """
        _76最小覆盖子串
        :param s:
        :param t:
        :return:
        """
        need, window = {}, {}
        for i in t:
            need[i] = need.setdefault(i, 0) + 1
        l, r = 0, 0
        # 扩大窗口
        valid, lenStr = 0, sys.maxsize
        start = 0
        print(len(s))
        while (r < len(s)):
            c = s[r]
            r += 1
            if need.get(c):              #1 先判断need窗口中是否有有效值，无则不更新
                window[c] = window.setdefault(c, 0) + 1
                if window[c] <= need[c]:
                    valid += 1

            # / ** *debug
            # 输出的位置 ** * /
            print(f"window: ({l}, {r})\n");
            # / ** ** ** ** ** ** ** ** ** ** /

            # 缩小窗口
            while valid == len(t):
                if r - l < lenStr:      #2 更新子字符串的长度
                    lenStr = r - l
                    start = l
                d = s[l]
                l += 1
                if need.get(d):        #先判断need窗口中是否有有效值，无则不更新
                    if window[d] == need[d]:
                        valid -= 1
                    window[d] = window.get(d) - 1
        end = start + lenStr
        print(lenStr)
        return "" if lenStr == sys.maxsize else s[start:end]


if __name__ == '__main__':
    s = Sloution()
    a = s.minWindow

    a = {'sss': 'zhagnsan', 2: 'lisi'}
    a['sss'] = 'zhang3'
    print(a)
    a[3] = 'wnagwu'
    a.pop('sss')
    a['z6'] = 'zhaoneng'
    print(a)
    a.setdefault('s7', 'foldphone')
    print(a)
    print(a.setdefault('s7', 'defaultvalue'))
    print(a.get('s7'))

    print(a.items())
    print(type(str(a.keys())))
    print(a.values())

    print(sys.maxsize)

    a = 'abc'
    for i in a:
        print(i)
    print(len(a))
    print(len("gsfdgljf"))
    a = "kkk"
    print(a[0])
    need = {}
    need['k'] = need.setdefault('k', 0) + 1

    print("=================")
    s = "ADOBECODEBANC"
    t = "ABC"
    s = "aa"
    t = "aa"
    slou = Sloution()
    result = slou.minWindow(s, t)
    print(result)