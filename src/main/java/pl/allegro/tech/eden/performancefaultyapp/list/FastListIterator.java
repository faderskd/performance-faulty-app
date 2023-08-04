package pl.allegro.tech.eden.performancefaultyapp.list;

import java.util.ArrayList;

public class FastListIterator implements ListIterator {

    private final ArrayList<Integer> integers = new ArrayList<>();

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
