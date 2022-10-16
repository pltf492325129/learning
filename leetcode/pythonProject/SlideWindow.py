import sys
from msilib.schema import Property
from typing import List


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
            if need.get(c):  # 1 先判断need窗口中是否有有效值，无则不更新
                window[c] = window.setdefault(c, 0) + 1
                if window[c] <= need[c]:
                    valid += 1

            # / ** *debug
            # 输出的位置 ** * /
            print(f"window: ({l}, {r})\n");
            # / ** ** ** ** ** ** ** ** ** ** /

            # 缩小窗口
            while valid == len(t):
                if r - l < lenStr:  # 2 更新子字符串的长度
                    lenStr = r - l
                    start = l
                d = s[l]
                l += 1
                if need.get(d):  # 先判断need窗口中是否有有效值，无则不更新
                    if window[d] == need[d]:
                        valid -= 1
                    window[d] = window.get(d) - 1
        end = start + lenStr
        print(lenStr)
        return "" if lenStr == sys.maxsize else s[start:end]

    def checkInclusion(self, s1: str, s2: str) -> bool:
        '''
        _567字符串的排列
        '''
        need, window = {}, {}
        for s in s1:
            need[s] = need.setdefault(s, 0) + 1
        left, right = 0, 0
        valid = 0
        while right < len(s2):
            c = s2[right]
            right += 1
            if need.get(c):  # ****** 这一步重要
                if need.get(c) == window.get(c):
                    valid += 1
                window[c] = window.setdefault(c, 0) + 1
            # 判断左窗口是否要收缩
            while right - left >= len(s1):
                # 判断是否找到了合法的字符串
                if valid == len(need):
                    return True
                d = s2[left]
                left += 1
                # 进行窗口的一些列更新
                if need.get(d):
                    if need.get(d) == window.get(d):
                        valid -= 1
                    window[d] = window.get(d) - 1
        return False

    def findAnagrams(self, s: str, p: str) -> List[int]:
        result = []
        window, need = {}, {}
        for i in p:
            need[i] = need.setdefault(i, 0) + 1
        left, right = 0, 0
        valid = 0
        while right < len(s):
            c = s[right]
            right += 1
            if need.get(c):
                window[c] = window.setdefault(c, 0) + 1
                if need.get(c) == window.get(c):
                    valid += 1
            print(f'window l: {left}, r: {right}')
            while right - left >= len(p):
                if valid == len(need):
                    result.append(left)
                d = s[left]
                left += 1
                if need.get(d):
                    if need.get(d) == window.get(d):
                        valid -= 1
                    window[d] = window.get(d) - 1
        return result

    def lengthOfLongestSubstring(self, s: str) -> int:
        window = {}
        left, right = 0, 0
        lenStr = 0
        while right < len(s):
            c = s[right]
            right += 1
            window[c] = window.setdefault(c, 0) + 1

            # 缩小窗口
            while window.get(c) > 1:
                d = s[left]
                left += 1
                window[d] = window.get(d) - 1
            lenStr = max(lenStr, right - left)  # 这里来存储最大值
        return lenStr


if __name__ == '__main__':
    s = "pwwkew"
    slou = Sloution()
    res = slou.lengthOfLongestSubstring(s)
    print(res)
