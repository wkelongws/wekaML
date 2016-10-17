import amten.ml.NNParams;
import amten.ml.matrix.Matrix;
import amten.ml.matrix.MatrixUtils;

public class test {

	public static void runtest() throws Exception {
       
		boolean useConvolution = true;
		
        // Read data from CSV-file
        int headerRows = 1;
        char separator = ',';
        Matrix data = MatrixUtils.readCSV("C:/Users/shuowang/Desktop/test.csv", separator, headerRows);
        System.out.println(data);
        // Split data into training set and crossvalidation set.
        float crossValidationPercent = 20;
        float testPercent = 10;
        Matrix[] split = MatrixUtils.split(data, crossValidationPercent, testPercent);
        System.out.println("\n");
        Matrix dataTrain = split[0];
        System.out.println(dataTrain);
        Matrix dataCV = split[1];
        //System.out.println("\n");
        //System.out.println(dataCV);
        Matrix dataTest = split[2];
        //System.out.println("\n");
        //System.out.println(dataTest);

        // First column contains the classification label. The rest are the indata.
        Matrix xTrain = dataTrain.getColumns(1, -1);
        System.out.println(xTrain);
        Matrix yTrain = dataTrain.getColumns(0, 0);
        System.out.println(yTrain);
        Matrix xCV = dataCV.getColumns(1, -1);
        Matrix yCV = dataCV.getColumns(0, 0);
        
        NNParams params = new NNParams();
        
        params.numClasses = 10; // 10 digits to classify
        params.hiddenLayerParams = useConvolution ? new NNParams.NNLayerParams[]{ new NNParams.NNLayerParams(20, 5, 5, 2, 2) , new NNParams.NNLayerParams(100, 5, 5, 2, 2) } :
                                                    new NNParams.NNLayerParams[] { new NNParams.NNLayerParams(100) };
        
        params.maxIterations = useConvolution ? 10 : 200;
        params.learningRate = useConvolution ? 1E-2 : 0;

        long startTime = System.currentTimeMillis();
        amten.ml.NeuralNetwork nn = new amten.ml.NeuralNetwork(params);
        nn.train(xTrain, yTrain);
        System.out.println("\nTraining time: " + String.format("%.3g", (System.currentTimeMillis() - startTime) / 1000.0) + "s");

        int[] predictedClasses = nn.getPredictedClasses(xTrain);
        int correct = 0;
        for (int i = 0; i < predictedClasses.length; i++) {
            if (predictedClasses[i] == yTrain.get(i, 0)) {
                correct++;
            }
        }
        System.out.println("Training set accuracy: " + String.format("%.3g", (double) correct/predictedClasses.length*100) + "%");

        predictedClasses = nn.getPredictedClasses(xCV);
        correct = 0;
        for (int i = 0; i < predictedClasses.length; i++) {
            if (predictedClasses[i] == yCV.get(i, 0)) {
                correct++;
            }
        }
        System.out.println("Crossvalidation set accuracy: " + String.format("%.3g", (double) correct/predictedClasses.length*100) + "%");
        
        

    }


    public static void main(String[] args) throws Exception {
        runtest();
    }
	
}
