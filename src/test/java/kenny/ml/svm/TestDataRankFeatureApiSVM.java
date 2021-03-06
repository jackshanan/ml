package kenny.ml.svm;

import com.datarank.api.DataRank;
import com.datarank.api.config.DataRankConfiguration;
import com.datarank.api.request.filters.FeatureMode;
import com.datarank.api.request.filters.Limit;
import com.datarank.api.response.envelopes.CommentFeaturesEnvelope;
import kenny.ml.svm.kernels.Gaussian;
import kenny.ml.svm.kernels.IKernel;
import kenny.ml.svm.problem.Problem;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by kenny
 */
public class TestDataRankFeatureApiSVM {

    private final String target = "THEME PURCHASE INTENT";

    /*
     * Loading features for [tide-pods]
     * Loaded [38] features for 2500 comments.
     * ...............................................
     * Support Vector Machine: 96.08%
     */
    @Test
    public void trainSvm() throws IOException {
        // initialize datarank SDK
        final File apiKeyFile = new File(System.getProperty("user.home") + "/.datarank-api-key");
        final DataRank dataRank = new DataRank(new DataRankConfiguration(IOUtils.toString(new FileInputStream(apiKeyFile)).trim()));
        // perform api call to return 1000 categorical features
        final CommentFeaturesEnvelope commentFeatures = dataRank.commentsFeatures("tide-pods", new Limit(1000), FeatureMode.NUMERIC).getBody();

        // perform lightweight transforms to build decision tree features
        final Problem problem = buildModel(commentFeatures);

        final SupportVectorMachine supportVectorMachine = new SupportVectorMachine();

        final IKernel kernel = new Gaussian();
        final KernelParams kernelParams = new KernelParams(kernel, 0.023, 2, 7);
        kernelParams.setC(Math.pow(2, 0));

        final Problem test = problem; // test ability to learn self
        supportVectorMachine.train(problem, kernelParams);
        final int[] predictions = supportVectorMachine.test(test, false);

        final EvalMeasures evalMeasures = new EvalMeasures(test, predictions, 2);
        System.out.println("Support Vector Machine: " + (evalMeasures.accuracy() * 100.0) + "%");
    }

    // build model, remove the target variable from the training features
    private Problem buildModel(final CommentFeaturesEnvelope commentFeaturesEnvelope) {
        final int targetIndex = findTargetIndex(target, commentFeaturesEnvelope.getFeatureNames());
        final Problem problem = new Problem();
        problem.categories.add(1);
        problem.categories.add(-1);
        problem.trainingSize = commentFeaturesEnvelope.getFeatures().size();
        problem.x = new FeatureSpace[commentFeaturesEnvelope.getFeatures().size()];
        problem.y = new int[commentFeaturesEnvelope.getFeatures().size()];

        int i = 0;
        for(List<String> featureSet : commentFeaturesEnvelope.getFeatures()) {
            problem.y[i] = Integer.valueOf(featureSet.get(targetIndex));
            featureSet.remove(targetIndex); // remove the target variable from the data
            int j = 0;
            final FeatureSpace featureSpace = new FeatureSpace();
            for(String feature : featureSet) {
                featureSpace.set(j++, Double.valueOf(feature));
            }
            problem.x[i] = featureSpace;
            i++;
        }
        return problem;
    }

    private int findTargetIndex(final String target, final List<String> featureNames) {
        int i = 0;
        for(String featureName : featureNames) {
            if(target.equals(featureName)) { return i; }
            i++;
        }
        return -1;
    }

}
