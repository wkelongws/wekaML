import java.awt.Point;
import java.io.BufferedReader;
import java.lang.Object.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.random.CorrelatedRandomVectorGenerator;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import java.io.Serializable;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.RandomSource;

public class simpletest {

	public static void main(String[] args) throws NumberFormatException, IOException {
		
		
		double[][] data = {{10.3,10.4},{3.4,4.5},{1.4,6.7},{13.2,5.0},{4.6,7.3}};
		
		List<DoublePoint> clusterInput = new ArrayList<DoublePoint>(data.length);
		for(int i=0;i<data.length;i++)
		{
			clusterInput.add(new DoublePoint(data[i]));
		}
		
		System.out.println(clusterInput);
		
		KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<DoublePoint>(2, 10000);
		List<CentroidCluster<DoublePoint>> clusterResults = clusterer.cluster(clusterInput);

		// output the clusters
		for (int i=0; i<clusterResults.size(); i++) {
		    System.out.println("K-mean Cluster " + i);
		    System.out.print("controid " + i + ":");
		    System.out.println(clusterResults.get(i).getCenter());
		    
		    for (DoublePoint a : clusterResults.get(i).getPoints())
		        System.out.println(a);
		    System.out.println();
		}
		
		DBSCANClusterer<DoublePoint> dbcluster = new DBSCANClusterer<DoublePoint>(6.0,1);
		List<Cluster<DoublePoint>> dbclusterResults = dbcluster.cluster(clusterInput);
		// output the clusters
		System.out.println(dbclusterResults.size() + "DB Clusters");
				for (int i=0; i<dbclusterResults.size(); i++) {
				    System.out.println("DB Cluster " + i);
				    for (DoublePoint a : dbclusterResults.get(i).getPoints())
				        System.out.println(a);
				    System.out.println();
				}

		
	}

}
