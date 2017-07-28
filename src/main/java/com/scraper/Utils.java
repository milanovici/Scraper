package com.scraper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor Milanovic on 4/5/17.
 */
public class Utils {

    static <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }
}
