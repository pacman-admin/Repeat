package core.keyChain.mouseGestureRecognition;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("unused")
public final class DataCutReverseTrimmer extends AbstractDataTrimmer {

    /**
     * Remove points from the end of the list.
     */
    @Override
    public ArrayList<Point> internalTrim(ArrayList<Point> input) {
        ArrayList<Point> output = new ArrayList<>(DataNormalizer.POINT_COUNT);

        Iterator<Point> it = input.iterator();
        for (int i = 0; i < DataNormalizer.POINT_COUNT; i++) {
            output.add(it.next());
        }

        return output;
    }

}
