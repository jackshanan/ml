package kenny.ml.nn.bep;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

public class TestBEPNeuralNetwork {

	
	@Test
	public void test() {
		NeuralNetworkConfig config = new NeuralNetworkConfig();
		config.bias = true;
		config.numCenterLayers = 1;
		config.numCenterNodes = 20;
		config.numInputNodes = 2;
		config.numOutputNodes = 1;
		config.learningRate = 0.8;
		NeuralNetwork nn = new NeuralNetwork(config);
		
		int numTrainData = 4;
		double[][] trainData = new double[numTrainData][nn.getInputs().length];
		double[][] teacherSignal = new double[numTrainData][nn.getOutputs().length];
		
		trainData[0] = new double[]{0.0, 0.0};  // 0 & 0
		trainData[1] = new double[]{0.0, 1.0};  // 0 & 1
		trainData[2] = new double[]{1.0, 0.0};  // 1 & 0
		trainData[3] = new double[]{1.0, 1.0};  // 1 & 1

		teacherSignal[0] = new double[]{0.0};
		teacherSignal[1] = new double[]{0.0};
		teacherSignal[2] = new double[]{0.0};
		teacherSignal[3] = new double[]{1.0};
		
		// Train
		double maxError = 0.001;
		double error = Double.MAX_VALUE;
		int count = 0;
		System.out.println("Begin trainings");
		while(error > maxError) {
			error = 0;
			for(int i = 0; i < numTrainData; i++) {
				for(int j = 0; j < config.numInputNodes; j++) {
					nn.setInput(j, trainData[i][j]);
					nn.setInput(j, trainData[i][j]);
				}
				for(int j = 0; j < config.numOutputNodes; j++) {
					nn.setTeacherSignal(j, teacherSignal[i][j]);
				}
				nn.feedForward();
				error += nn.calculateError();
				nn.backPropagate();
				nn.clearAllValues();
			}
			count++;
			error /= numTrainData;
			if(count % 100 == 0) {
				System.out.println("[" + count + "] error = " + error);
			}
		}
		
		// print results
		for(int i = 0; i < numTrainData; i++) {
			nn.clearAllValues();
			System.out.print("[ ");
			for(int j = 0; j < nn.getInputs().length; j++) {
				nn.setInput(j, trainData[i][j]);
				System.out.print(" " + trainData[i][j]);
			}
			System.out.print("] => [ ");
			nn.feedForward();
			for(int j = 0; j < nn.getOutputs().length; j++) {
				nn.setTeacherSignal(j, teacherSignal[i][j]);
				System.out.print(nn.getOutput(j));
			}
			System.out.println("]");
		}
	}
	
	@Ignore
	public void sanityCheck() {
		NeuralNetworkConfig config = new NeuralNetworkConfig();
		config.bias = false;
		config.numCenterLayers = 1;
		config.numCenterNodes = 3;
		config.numInputNodes = 2;
		config.numOutputNodes = 1;
		config.learningRate = 0.2;
		NeuralNetwork nn = new NeuralNetwork(config);
		
		int numTrainData = 1;
		double[][] trainData = new double[numTrainData][nn.getInputs().length];
		double[][] teacherSignal = new double[numTrainData][nn.getOutputs().length];
		
		trainData[0] = new double[]{1.0, 1.0};  // 1 & 1

		teacherSignal[0] = new double[]{1.0};

		for(int i = 0; i < 100; i++) {
			nn.setInput(0, trainData[0][0]);
			nn.setInput(1, trainData[0][1]);
	
			nn.setTeacherSignal(0, teacherSignal[0][0]);
			nn.feedForward();
			System.out.println("FEED FORWARD");
			System.out.println("V: " + nn.getInputLayer().getNeuron(0).getValue());
			System.out.println("W: " + nn.getInputLayer().getNeuron(0).getWeights());
			System.out.println("V: " + nn.getInputLayer().getNeuron(1).getValue());
			System.out.println("W: " + nn.getInputLayer().getNeuron(1).getWeights());
	
			System.out.println("V: " + nn.getCenterLayers()[0].getNeuron(0).getValue());
			System.out.println("W: " + nn.getCenterLayers()[0].getNeuron(0).getWeights());
			System.out.println("V: " + nn.getCenterLayers()[0].getNeuron(0).getValue());
			System.out.println("W: " + nn.getCenterLayers()[0].getNeuron(1).getWeights());
			System.out.println("V: " + nn.getCenterLayers()[0].getNeuron(0).getValue());
			System.out.println("W: " + nn.getCenterLayers()[0].getNeuron(2).getWeights());
			System.out.println(Arrays.toString(nn.getOutputs()));
			nn.calculateError();
			nn.backPropagate();
			
			System.out.println("BACK PROPAGATE");
			System.out.println("V: " + nn.getInputLayer().getNeuron(0).getValue());
			System.out.println("W: " + nn.getInputLayer().getNeuron(0).getWeights());
			System.out.println("V: " + nn.getInputLayer().getNeuron(1).getValue());
			System.out.println("W: " + nn.getInputLayer().getNeuron(1).getWeights());
	
			System.out.println("V: " + nn.getCenterLayers()[0].getNeuron(0).getValue());
			System.out.println("W: " + nn.getCenterLayers()[0].getNeuron(0).getWeights());
			System.out.println("V: " + nn.getCenterLayers()[0].getNeuron(0).getValue());
			System.out.println("W: " + nn.getCenterLayers()[0].getNeuron(1).getWeights());
			System.out.println("V: " + nn.getCenterLayers()[0].getNeuron(0).getValue());
			System.out.println("W: " + nn.getCenterLayers()[0].getNeuron(2).getWeights());
			System.out.println(Arrays.toString(nn.getOutputs()));
			
			nn.clearAllValues();
		}

		// System.out.println(sigmoid(1.096587868));
	}

	
	public double sigmoid(double x) {
		return (1.0 / (1 + Math.exp(-x)));
	}

}
