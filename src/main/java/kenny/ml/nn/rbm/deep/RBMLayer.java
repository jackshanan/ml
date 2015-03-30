package kenny.ml.nn.rbm.deep;


import kenny.ml.nn.rbm.RBM;

import java.util.Arrays;

/**
 * Created by kenny on 5/16/14.
 *
 * A layer can have multiple RBMs, this allows convolution-like networks when configuring a deep rbm
 */
public class RBMLayer {

    public final RBM[] rbms;

    public RBMLayer(RBM[] rbms) {
        this.rbms = rbms;
    }

    public RBM getRBM(int r) {
        return rbms[r];
    }

    public int size() {
        return rbms.length;
    }

    @Override
    public String toString() {
        return "RBMLayer{" +
                "rbms=" + Arrays.toString(rbms) +
                '}';
    }
}

