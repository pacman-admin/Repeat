package utilities;

import java.util.List;
import java.util.ListIterator;

public final class IterableUtility {

    /**
     * Private constructor so that no instance is created
     */
    private IterableUtility() {
    }

    public static int[] toIntegerArray(List<Integer> list) {
        int[] output = new int[list.size()];

        for (ListIterator<Integer> iterator = list.listIterator(); iterator.hasNext(); ) {
            int i = iterator.nextIndex();
            Integer o = iterator.next();
            output[i] = o;
        }
        return output;
    }

}
