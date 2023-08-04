package pl.allegro.tech.eden.performancefaultyapp.list;

import java.util.LinkedList;

public class SlowListIterator implements ListIterator {

    private final LinkedList<Integer> integers = new LinkedList<>();

    @Override
    public int findMax() {
        int max = 0;
        for (Integer i : integers) {
            max = Math.max(i, max);
        }
        return max;
    }

    @Override
    public void append(int val) {
        integers.add(val);
    }
}
