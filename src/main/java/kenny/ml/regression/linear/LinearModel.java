package kenny.ml.regression.linear;

import kenny.ml.math.Line;

/**
 * Created by kenny
 */
public class LinearModel {
    public final Line line;
    public final double coefficientOfDetermination;

    public LinearModel(final Line line) {
        this(line, 0.0);
    }

    public LinearModel(final Line line, final double coefficientOfDetermination) {
        this.line = line;
        this.coefficientOfDetermination = coefficientOfDetermination;
    }

    public Line getLine() {
        return line;
    }

    public double distance(final double x, final double y) {
        return Math.abs(y - f(x));
    }

    public boolean isAbove(final double x, final double y) {
        return y > f(x);
    }

    public double predict(final double x) {
        return f(x);
    }

    private double f(final double x) {
        return line.f(x);
    }

    @Override
    public String toString() {
        return "LinearModel{" +
                "line=" + line +
                ", coefficientOfDetermination=" + coefficientOfDetermination +
                '}';
    }

}
