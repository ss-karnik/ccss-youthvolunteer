package com.ccss.youthvolunteer.util;

import java.util.List;

public class CommonUtils {

    public static int sum(List<Integer> list, int start) {
        if (start == list.size()) return 0;
        return list.get(start) + sum(list, start + 1);
    }
}
