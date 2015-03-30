package kenny.ml.nn.rbm.learn;


import cern.colt.function.tdouble.DoubleFunction;
import kenny.ml.nn.rbm.math.DenseMatrix;
import kenny.ml.nn.rbm.math.Matrix;
import kenny.ml.nn.rbm.math.functions.Round;
import kenny.ml.nn.rbm.RBM;
import kenny.ml.nn.rbm.factory.RandomRBMFactory;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kenny on 5/23/14.
 */
public class TestBackErrorPropagation {

    private static final Logger LOGGER = Logger.getLogger(TestBackErrorPropagation.class);

    private static final DoubleFunction ROUND = new Round();

    @Test
    public void backErrorPropagationOr() {
        // or
        final List<Matrix> trainData = new ArrayList<>();
        trainData.add(DenseMatrix.make(new double[][]{{0, 0}}));
        trainData.add(DenseMatrix.make(new double[][] {{0,1}}));
        trainData.add(DenseMatrix.make(new double[][] {{1,1}}));
        trainData.add(DenseMatrix.make(new double[][] {{1,1}}));

        final List<Matrix> teacherSignals = new ArrayList<>();
        teacherSignals.add(DenseMatrix.make(new double[][] {{0}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1}}));

        final RBM rbm = new RandomRBMFactory().build(2, 1);

        final LearningParameters learningParameters = new LearningParameters().setEpochs(10000).setLearningRate(0.1);
        final BackErrorPropagation backErrorPropagation = new BackErrorPropagation(learningParameters);

        LOGGER.info(rbm.getWeights());
        backErrorPropagation.learn(rbm, trainData, teacherSignals);
        LOGGER.info(rbm.getWeights());
        for(Matrix input : trainData) {
            final Matrix output = backErrorPropagation.feedFoward(rbm, input);
            LOGGER.info(input + " -> " + output.apply(ROUND));
        }
    }

    @Test
    public void backErrorPropagationOr2() {
        // or
        final List<Matrix> trainData = new ArrayList<>();
        trainData.add(DenseMatrix.make(new double[][] {{0,0,0}}));
        trainData.add(DenseMatrix.make(new double[][] {{0,0,1}}));
        trainData.add(DenseMatrix.make(new double[][] {{0,1,0}}));
        trainData.add(DenseMatrix.make(new double[][] {{0,1,1}}));
        trainData.add(DenseMatrix.make(new double[][] {{1,0,0}}));
        trainData.add(DenseMatrix.make(new double[][] {{1,0,1}}));
        trainData.add(DenseMatrix.make(new double[][] {{1,1,0}}));
        trainData.add(DenseMatrix.make(new double[][] {{1,1,1}}));

        final List<Matrix> teacherSignals = new ArrayList<>();
        teacherSignals.add(DenseMatrix.make(new double[][] {{0}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1}}));

        final RBM rbm = new RandomRBMFactory().build(3, 1);

        final LearningParameters learningParameters = new LearningParameters().setEpochs(10000).setLearningRate(0.1);
        final BackErrorPropagation backErrorPropagation = new BackErrorPropagation(learningParameters);

        LOGGER.info(rbm.getWeights());
        backErrorPropagation.learn(rbm, trainData, teacherSignals);
        LOGGER.info(rbm.getWeights());
        for(Matrix input : trainData) {
            final Matrix output = backErrorPropagation.feedFoward(rbm, input);
            LOGGER.info(input + " -> " + output.apply(ROUND));
        }
    }

    @Test
    public void backErrorPropagationOrLeftAndRight() {
        // or
        final List<Matrix> trainData = new ArrayList<>();
        trainData.add(DenseMatrix.make(new double[][] {{0,0,0}}));
        trainData.add(DenseMatrix.make(new double[][] {{0,0,1}}));
        trainData.add(DenseMatrix.make(new double[][] {{0,1,0}}));
        trainData.add(DenseMatrix.make(new double[][] {{0,1,1}}));
        trainData.add(DenseMatrix.make(new double[][] {{1,0,0}}));
        trainData.add(DenseMatrix.make(new double[][] {{1,0,1}}));
        trainData.add(DenseMatrix.make(new double[][] {{1,1,0}}));
        trainData.add(DenseMatrix.make(new double[][] {{1,1,1}}));

        final List<Matrix> teacherSignals = new ArrayList<>();
        teacherSignals.add(DenseMatrix.make(new double[][] {{0,0}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{0,1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1,1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1,1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1,0}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1,1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1,1}}));
        teacherSignals.add(DenseMatrix.make(new double[][] {{1,1}}));

        final RBM rbm = new RandomRBMFactory().build(3, 2);

        final LearningParameters learningParameters = new LearningParameters().setEpochs(10000).setLearningRate(0.1);
        final BackErrorPropagation backErrorPropagation = new BackErrorPropagation(learningParameters);

        LOGGER.info(rbm.getWeights());
        backErrorPropagation.learn(rbm, trainData, teacherSignals);
        LOGGER.info(rbm.getWeights());
        for(Matrix input : trainData) {
            final Matrix output = backErrorPropagation.feedFoward(rbm, input);
            LOGGER.info(input + " -> " + output.apply(ROUND));
        }
    }

}
