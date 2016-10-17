import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.core.SerializationHelper;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import amten.ml.NNParams;
import amten.ml.matrix.Matrix;
import amten.ml.matrix.MatrixUtils;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class simpleModelTrain {

	public static void main(String[] args) throws Exception {
		CSVLoader loader = new CSVLoader();
		loader.setOptions(weka.core.Utils.splitOptions("-H"));
	    loader.setSource(new File("/Users/Shuo/Downloads/identification_Dur6Ratio1.csv"));
	    Instances data = loader.getDataSet();
	    data.setClassIndex(data.numAttributes() - 1);
	    //System.out.println(data.attribute(504));
	    NumericToNominal filter = new NumericToNominal();                         
	    filter.setOptions(weka.core.Utils.splitOptions("-R last"));                      
	    filter.setInputFormat(data);                         
	    Instances Data = Filter.useFilter(data, filter); 
	    System.out.println(Data.attribute(504));
	    
	    NaiveBayes NB = new NaiveBayes();
	    NB.setOptions(weka.core.Utils.splitOptions("")); 
	    NB.buildClassifier(Data); 
	    Evaluation eval_NB = new Evaluation(Data);
	    eval_NB.crossValidateModel(NB, Data, 5, new Random(1));
	    double AUC_NB=eval_NB.areaUnderROC(0);
	    System.out.println(AUC_NB);
	    
	    weka.core.SerializationHelper.write("/Users/Shuo/Downloads/ident.model", NB);
	}

}
