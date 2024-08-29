package core.keyChain.mouseGestureRecognition;

import core.keyChain.MouseGesture;

import java.awt.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mouse gesture classification class. This class classifies a list of points as a mouse gesture.
 *
 * @author HP Truong
 */
public final class MouseGestureClassifier {

    private static final Logger LOGGER = Logger.getLogger(MouseGestureClassifier.class.getName());

    private final LogisticRegressionModel logisticRegressionModel;

    public MouseGestureClassifier() {
        logisticRegressionModel = new LogisticRegressionModel();
        logisticRegressionModel.load();
    }

    /**
     * Classify the mouse gesture given an {@link Queue} of points.
     * At the end of the method, the number of points considered during classification
     * will be removed from the queue.
     *
     * @param points a {@link Queue} of points ordered chronologically.
     * @param size   the number of points to be considered in the queue.
     * @return the mouse gesture classified by the model.
     */
    public MouseGesture classifyGesture(Queue<Point> points, int size) {
        if (size < DataNormalizer.POINT_COUNT) {
            LOGGER.log(Level.FINE, "Not enough points for classification. "
                            + "Required at least {0} points but provided {1} points.",
                    new Object[]{DataNormalizer.POINT_COUNT, size});
            return MouseGesture.RANDOM;
        }

        ArrayList<Point> input = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            input.add(points.poll());
        }

        ArrayList<Float> normalized = new DataNormalizer().normalize(input);

        double[] normalizedArray = new double[normalized.size()];
        int index = 0;
        for (Float f : normalized) {
            normalizedArray[index++] = f.doubleValue();
        }

        String prediction = logisticRegressionModel.predict(normalizedArray);
        if (prediction == null) {
            return MouseGesture.RANDOM;
        }

        MouseGesture result = MouseGesture.find(prediction);
        return result == null ? MouseGesture.RANDOM : result;
    }
}
