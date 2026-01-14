package core.keyChain.mouseGestureRecognition;


import Jama.Matrix;
import staticResources.MouseGestureModelResources;

/**
 * Logistic regression model raw implementation. This only implements the predict function.
 *
 * @author HP Truong
 */
final class LogisticRegressionModel {

    private String[] labels; // classifying classes
    private Matrix w; // coefficients
    private Matrix c; // intercepts

    /**
     * Load model data from static resources.
     */
    void load() {
        labels = MouseGestureModelResources.getLabels();

        double[] intercepts = MouseGestureModelResources.getIntercepts();
        double[][] coefficients = MouseGestureModelResources.getCoefficients(labels.length);

        assert coefficients != null;
        w = new Matrix(coefficients);

        assert intercepts != null;
        double[][] twoDimensionalIntercept = new double[1][intercepts.length];
        twoDimensionalIntercept[0] = intercepts;
        c = new Matrix(twoDimensionalIntercept);

    }

    String predict(double[] featureValues) {
        if (!isLoaded()) {
            return null;
        }

        double[][] values = new double[1][featureValues.length];
        values[0] = featureValues;
        Matrix x = new Matrix(values);
        Matrix result = x.times(w.transpose()).plus(c);

        double[] probabilities = result.getArray()[0];
        int maxIndex = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < probabilities.length; i++) {
            double p = probabilities[i];
            if (p > maxValue) {
                maxValue = p;
                maxIndex = i;
            }
        }

        return labels[maxIndex];
    }

    /**
     * @return whether model was loaded successfully.
     */
    private boolean isLoaded() {
        return labels != null && w != null && c != null;
    }
}
