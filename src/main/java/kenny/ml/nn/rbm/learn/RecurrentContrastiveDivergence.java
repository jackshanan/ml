package kenny.ml.nn.rbm.learn;


import cern.colt.function.tdouble.DoubleDoubleFunction;
import cern.colt.function.tdouble.DoubleFunction;
import kenny.ml.nn.rbm.math.DenseMatrix;
import kenny.ml.nn.rbm.math.Matrix;
import kenny.ml.nn.rbm.math.functions.Sigmoid;
import kenny.ml.nn.rbm.math.functions.doubledouble.rbm.ActivationState;
import kenny.ml.nn.rbm.RBM;
import kenny.ml.utils.Clock;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by kenny on 5/15/14.
 *
 */
public class RecurrentContrastiveDivergence {

    private static final Logger LOGGER = Logger.getLogger(RecurrentContrastiveDivergence.class);

    private static final DoubleDoubleFunction ACTIVATION_STATE = new ActivationState();

    private static final DoubleFunction SIGMOID = new Sigmoid();

    private static final Clock CLOCK = new Clock();

    private final LearningParameters learningParameters;

    private final int memory;

    public RecurrentContrastiveDivergence(final LearningParameters learningParameters) {
        this.learningParameters = learningParameters;
        this.memory = this.learningParameters.getMemory();
    }

    /**
     *
     * input memory will be of the form v_t-m, v_t-m-1, ..., v_t-1, v_t(current)
     * learn a sequence of events
     * @param rbm
     * @param events
     */
    public void learn(final RBM rbm, final List<Matrix> events) {
        checkRBMConfigurations(rbm, events);

        LOGGER.info("Start Learning single recurrent event of (" + events.size() + " sequences)");
        CLOCK.start();
        for(int epoch = 0; epoch < learningParameters.getEpochs(); epoch++) {
            final double error = trainEvents(rbm, events);

            if(epoch % 10 == 0) {
                LOGGER.info("Epoch: " + epoch + "/" + learningParameters.getEpochs() + ", error: " + error + ", time: " + CLOCK.elapsedMillis() + "ms");
                CLOCK.reset();
            }
        }
    }

    /**
     * learn many independent temporal events
     * @param rbm
     * @param allEvents
     */
    public void learnMany(final RBM rbm, final List<List<Matrix>> allEvents) {
        checkRBMConfigurations(rbm, allEvents.get(0));

        for(int epoch = 0; epoch < learningParameters.getEpochs(); epoch++) {
            double error = 0.0;
            for(List<Matrix> events : allEvents) {
                error += trainEvents(rbm, events);
            }

            if(epoch % 1 == 0) {
                LOGGER.info("Epoch: " + epoch + "/" + learningParameters.getEpochs() + ", error: " + (error / allEvents.size()) + ", time: " + CLOCK.elapsedMillis() + "ms");
                CLOCK.reset();
            }
        }
    }

    private double trainEvents(final RBM rbm, final List<Matrix> events) {
        final int numberEvents = events.size();
        final Matrix weights = rbm.getWeights();

        double error = 0.0;
        for(int event = 0; event < events.size() - memory; event++) {

            final Matrix currentAndNextEvent = createTemporalInput(event, events);

            // Read training data and sample from the hidden later, positive CD phase, (reality phase)
            final Matrix positiveHiddenActivations = currentAndNextEvent.dot(weights);
            final Matrix positiveHiddenProbabilities = positiveHiddenActivations.apply(SIGMOID);
            final Matrix positiveHiddenStates = positiveHiddenProbabilities.copy().apply(DenseMatrix.random(currentAndNextEvent.rows(), rbm.getHiddenSize()), ACTIVATION_STATE);

            // Note that we're using the activation *probabilities* of the hidden states, not the hidden states themselves, when computing associations.
            // We could also use the states; see section 3 of Hinton's A Practical Guide to Training Restricted Boltzmann Machines" for more.
            final Matrix positiveAssociations = currentAndNextEvent.transpose().dot(positiveHiddenProbabilities);

            // Reconstruct the visible units and sample again from the hidden units. negative CD phase, aka the daydreaming phase.
            final Matrix negativeVisibleActivations = positiveHiddenStates.dot(weights.transpose());
            final Matrix negativeVisibleProbabilities = negativeVisibleActivations.apply(SIGMOID);
            final Matrix negativeHiddenActivations = negativeVisibleProbabilities.dot(weights);
            final Matrix negativeHiddenProbabilities = negativeHiddenActivations.apply(SIGMOID);

            // Note, again, that we're using the activation *probabilities* when computing associations, not the states themselves.
            final Matrix negativeAssociations = negativeVisibleProbabilities.transpose().dot(negativeHiddenProbabilities);

            // Update weights.
            weights.add(positiveAssociations.subtract(negativeAssociations).divide(numberEvents).multiply(learningParameters.getLearningRate()));

            error += currentAndNextEvent.subtract(negativeVisibleProbabilities).pow(2).sum();
        }
        return error / events.size();
    }

    private void checkRBMConfigurations(RBM rbm, List<Matrix> events) {
        final int requiredSize = events.get(0).columns() + (events.get(0).columns() * memory);
        if(rbm.getVisibleSize() != requiredSize) {
            throw new IllegalArgumentException("RBM Input size must equal event.columns() + event.columns() * memory, required = " + requiredSize + ", actual = " + rbm.getVisibleSize());
        }
    }


    private Matrix createTemporalInput(int event, List<Matrix> events) {
        final Matrix currentEvent = events.get(event);

        Matrix temporalEvent = currentEvent;
        for(int i = event + 1, t = 0; i < events.size() && t < memory; i++, t++) {
            temporalEvent = temporalEvent.addColumns(events.get(i));
        }

        final int temporalEventColumns = currentEvent.columns() + currentEvent.columns() * memory;
        if(temporalEvent.columns() < temporalEventColumns) { // fill in blanks if there is not enough temporal data to train
            temporalEvent = temporalEvent.addColumns(DenseMatrix.make(currentEvent.rows(), temporalEventColumns - temporalEvent.columns()));
        }
        //LOGGER.info("Dataset\n" + PrettyPrint.toPixelBox(temporalEvent.row(0).toArray(), 28, 0.5));
        return temporalEvent;
    }

    /*
        Assuming the RBM has been trained, run the network on a set of visible units to get a sample of the hidden units.
        Parameters, A matrix where each row consists of the states of the visible units.
        hidden_states, A matrix where each row consists of the hidden units activated from the visible
        units in the data matrix passed in.

        Recurrent version pass in an empty t-1 visible layer
     */
    public Matrix runVisible(final RBM rbm, final Matrix event) {
        final Matrix weights = rbm.getWeights();

        final Matrix currentAndNoNextEvent = event.addColumns(DenseMatrix.make(event.rows(), event.columns() * memory)); // append an empty visible layer for next guess

        // Calculate the activations of the hidden units.
        final Matrix hiddenActivations = currentAndNoNextEvent.dot(weights);
        // Calculate the probabilities of turning the hidden units on.
        final Matrix hiddenProbabilities = hiddenActivations.apply(SIGMOID);
        // Turn the hidden units on with their specified probabilities.
        final Matrix hiddenStates = hiddenProbabilities.apply(DenseMatrix.random(event.rows(), rbm.getHiddenSize()), ACTIVATION_STATE);

        return hiddenStates;
    }


    public Matrix visualizeEvents(final RBM rbm, final List<Matrix> events) {
        final Matrix weights = rbm.getWeights();

        Matrix lastVisibleStates;

        int event = 0;
        do {
            final Matrix currentAndNextEvent = createTemporalInput(event, events);

            // run visible
            // Calculate the activations of the hidden units.
            final Matrix hiddenActivations = currentAndNextEvent.dot(weights);
            // Calculate the probabilities of turning the hidden units on.
            final Matrix hiddenProbabilities = hiddenActivations.apply(SIGMOID);
            // Turn the hidden units on with their specified probabilities.
            final Matrix hiddenStates = hiddenProbabilities.apply(DenseMatrix.random(currentAndNextEvent.rows(), rbm.getHiddenSize()), ACTIVATION_STATE);

            // run hidden
            // Calculate the activations of the hidden units.
            final Matrix visibleActivations = hiddenStates.dot(weights.transpose());
            // Calculate the probabilities of turning the visible units on.
            final Matrix visibleProbabilities = visibleActivations.apply(SIGMOID);
            // Turn the visible units on with their specified probabilities.
            final Matrix visibleStates = visibleProbabilities.apply(DenseMatrix.random(hiddenStates.rows(), rbm.getVisibleSize()), ACTIVATION_STATE);

            lastVisibleStates = visibleStates;

            event++;
        } while(event < events.size() - memory);

        return lastVisibleStates;
    }

    /*
        Assuming the RBM has been trained, run the network on a set of hidden units to get a sample of the visible units.
        Parameters, A matrix where each row consists of the states of the hidden units.
        visible_states, A matrix where each row consists of the visible units activated from the hidden
        units in the data matrix passed in.
     */
    public Matrix runHidden(final RBM rbm, final Matrix hidden) {

        final Matrix weights = rbm.getWeights();

        // Calculate the activations of the hidden units.
        final Matrix visibleActivations = hidden.dot(weights.transpose());
        // Calculate the probabilities of turning the visible units on.
        final Matrix visibleProbabilities = visibleActivations.apply(SIGMOID);
        // Turn the visible units on with their specified probabilities.
        final Matrix visibleStates = visibleProbabilities.apply(DenseMatrix.random(hidden.rows(), rbm.getVisibleSize()), ACTIVATION_STATE);

        return visibleStates;
    }

}
