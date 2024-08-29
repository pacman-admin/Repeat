package core.keyChain.mouseGestureRecognition;

import java.awt.*;
import java.util.ArrayList;

/**
 * Remove points from the list using a certain strategy.
 */
abstract class AbstractDataTrimmer {

    /**
     * Remove points from the list so that the list has at most {@link DataNormalizer.POINT_COUNT} points.
     *
     * @param input input list of points.
     * @return a list of points with at most {@link DataNormalizer.POINT_COUNT} points.
     */
    public final ArrayList<Point> trim(ArrayList<Point> input) {
        return input.size() <= DataNormalizer.POINT_COUNT ? input : internalTrim(input);

    }

    protected abstract ArrayList<Point> internalTrim(ArrayList<Point> input);
}
