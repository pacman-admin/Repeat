package core.keyChain.mouseGestureRecognition;

import java.awt.*;
import java.util.ArrayList;

/**
 * Remove points from the list using a certain strategy.
 */
abstract class AbstractDataTrimmer {
    public final ArrayList<Point> trim(ArrayList<Point> input) {
        if (input.size() <= DataNormalizer.POINT_COUNT) {
            return input;
        }

        return internalTrim(input);
    }

    protected abstract ArrayList<Point> internalTrim(ArrayList<Point> input);
}
