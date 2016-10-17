import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class RealTimeIdentPred80CB {

	public static void main(String[] args) throws Exception {
		// load pretrained weka model
		Classifier pred = (Classifier) weka.core.SerializationHelper.read("/Users/Shuo/Downloads/pred.model");
		Classifier ident = (Classifier) weka.core.SerializationHelper.read("/Users/Shuo/Downloads/ident.model");
		
		// read detector list and mile marker in
		HashMap<String, Double> EB80 = new HashMap<String, Double>();
		List<Double> EB80milemarker = new ArrayList<Double>();
		HashMap<String, Double> WB80 = new HashMap<String, Double>();
		List<Double> WB80milemarker = new ArrayList<Double>();
        BufferedReader br0 = new BufferedReader(new FileReader("/Users/Shuo/Documents/Wavetronix/milemarker.csv"));
        String line0;
        while ((line0 = br0.readLine()) != null) 
        {
        	String[] columns0 = line0.split(",");
        	if (!columns0[0].equals("Direction"))
        	{
        		if (columns0[0].equals("EB"))
            	{
            		EB80.put(columns0[5], 0.0);
            		EB80milemarker.add(Double.parseDouble(columns0[10]));
            	}
            	else
            	{
            		WB80.put(columns0[5], 0.0);
            		WB80milemarker.add(Double.parseDouble(columns0[10]));
            	}
        	}
        	
        }
        br0.close();
        double[] EB80mm = new double[EB80milemarker.size()];
        for (int i = 0; i < EB80mm.length; i++) {
        	EB80mm[i] = EB80milemarker.get(i);     
        }
        double[] WB80mm = new double[WB80milemarker.size()];
        for (int i = 0; i < WB80mm.length; i++) {
        	WB80mm[i] = WB80milemarker.get(i);     
        }
        
        List<Double> interpmilemarker = new ArrayList<Double>();
        for (double i=5.0;i<89.0;i++)
        {
        	interpmilemarker.add(i/10);
        }
        double[] interpmm = new double[interpmilemarker.size()];
        for (int i = 0; i < interpmm.length; i++) {
        	interpmm[i] = interpmilemarker.get(i);     
        }
    	// initialize observations container
 		TreeMap<Integer, List<Double>> observationsEB = new TreeMap<Integer, List<Double>>(); 		
 		TreeMap<Integer, List<Double>> observationsWB = new TreeMap<Integer, List<Double>>();

		// data download
		int counter=0;
		int timecounter=1;
		do{
			timecounter++;
			
			long timeBefore = System.currentTimeMillis();    		// 1444766579486
			
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
		Date today = Calendar.getInstance().getTime();        		// Tue Oct 13 15:02:59 CDT 2015
		String reportDate = dateFormat.format(today);				// 10132015
		
		String Folderpath = "/Users/Shuo/Documents/Wavetronix/waveXML/"+ reportDate +"/";
		File filex=new File(Folderpath);
		filex.mkdir();
		//URL link = new URL ("http://205.221.97.102/Iowa.Sims.AllSites.C2C/IADOT_SIMS_AllSites_C2C.asmx/OP_ShareTrafficDetectorData?MSG_TrafficDetectorDataRequest=string HTTP/1.1");
		URL link = new URL ("http://205.221.97.102/Iowa.Sims.AllSites.C2C.Geofenced/IADOT_SIMS_AllSites_C2C.asmx/OP_ShareTrafficDetectorData?MSG_TrafficDetectorDataRequest=string%20HTTP/1.1");
		String output="/Users/Shuo/Documents/Wavetronix/waveCSV/";
		
		//PrintStream outxx2 = new PrintStream(new FileOutputStream(output+  reportDate + ".csv", true));
		//System.setOut(outxx2);
				
		try{
			DateFormat dateFormat2 = new SimpleDateFormat("HH-mm-ss");
			Date today2 = Calendar.getInstance().getTime();        
			String reportDate2 = dateFormat2.format(today2);		// 16-01-01
			
			InputStream in = new BufferedInputStream(link.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n=0;
		
			while(-1!=(n=in.read(buf)))
			{
				out.write(buf,0,n);
			}
			out.close();
			in.close();
			byte[] response = out.toByteArray();
			
			FileOutputStream fos = new FileOutputStream(Folderpath + reportDate2 + ".xml/");
			fos.write(response);
			fos.close();
						
			DocumentBuilderFactory factory =DocumentBuilderFactory.newInstance();
			DocumentBuilder builder =factory.newDocumentBuilder();	
			Document document = builder.parse(new File(Folderpath + reportDate2 + ".xml/"));
			//C:\Users\Shuo\Desktop\WavetronixData\waveXML\10142015\10-15-01.xml
			
			// xml -> document -(getElementsByTagName)-> NodeList -(item)-> Node -(getTextContent)-> string
			//Node -(element)-> element -(getElementsByTagName)-> NodeList -(item)-> Node -(getTextContent)-> string
			document.getDocumentElement().normalize();
			NodeList timestamps = document.getElementsByTagName("detection-time-stamp");
			Element timestamp = (Element) timestamps.item(0);
			String date = timestamp.getElementsByTagName("local-date").item(0).getTextContent();
			//System.out.println();
			
			NodeList starttime = document.getElementsByTagName("start-time");
			Node stNode = starttime.item(0);
			String starttimer = stNode.getTextContent();
			
			NodeList endtime = document.getElementsByTagName("end-time");
			Node enNode = endtime.item(0);
			String endtimer = enNode.getTextContent();
			
			NodeList detectorReports = document.getElementsByTagName("detector-report");
			for (int ixa=0; ixa<detectorReports.getLength(); ixa++){
				Element detectorReport = (Element) detectorReports.item(ixa);
				String detectorID = detectorReport.getElementsByTagName("detector-id").item(0).getTextContent().trim();
				String status = detectorReport.getElementsByTagName("status").item(0).getTextContent();
				
				
				NodeList Lanes = detectorReport.getElementsByTagName("lanes");
				Element Ln = (Element) Lanes.item(0);
				int tl = Ln.getElementsByTagName("lane").getLength();
				String numOfLanes = Integer.toString(tl);

				//System.out.print(detectorID +","+date+","+starttimer+","+endtimer+","+status +","+ numOfLanes +",");
				
				NodeList curLanes = Ln.getElementsByTagName("lane");
				String laneId = "null";String count = "null";String volume = "null";String occupancy = "null";String speed = "null";
				double weightedSUMspeed = 0.0;
				double totalcount = 0.0;
				
				for (int ixb=0; ixb<tl; ++ixb){ // loop through all lanes
					
					Element Lns = (Element) curLanes.item(ixb);
					int claneid =Lns.getElementsByTagName("lane-id").getLength();
					if (claneid>0){ laneId = Lns.getElementsByTagName("lane-id").item(0).getTextContent();}else{ laneId = "null";}
					
					int ccount =Lns.getElementsByTagName("count").getLength();
					if (ccount>0){ count = Lns.getElementsByTagName("count").item(0).getTextContent();}else{ count = "null";}
					
					int cvolume =Lns.getElementsByTagName("volume").getLength();
					if (cvolume>0){ volume = Lns.getElementsByTagName("volume").item(0).getTextContent();}else{ volume = "null";}
					
					int coccupancy =Lns.getElementsByTagName("occupancy").getLength();
					if (coccupancy>0){ occupancy = Lns.getElementsByTagName("occupancy").item(0).getTextContent();}else{ occupancy = "null";}
					
					int cspeed =Lns.getElementsByTagName("speed").getLength();
					if (cspeed>0){ speed = Lns.getElementsByTagName("speed").item(0).getTextContent();}else{ speed = "null";}

					//System.out.print(laneId +","+count +","+volume +","+occupancy +","+speed +",");
					totalcount += Double.parseDouble(count);
					weightedSUMspeed += Double.parseDouble(count)*Double.parseDouble(speed);
					
					}
				double weightedSpeed = 60.0*1.6;
				if(totalcount>0.0 & weightedSUMspeed>0.0)
				{
					weightedSpeed = weightedSUMspeed/totalcount;
				}
				
				if (EB80.containsKey(detectorID))
				{
					EB80.put(detectorID, weightedSpeed);
				}
				if (WB80.containsKey(detectorID))
				{
					WB80.put(detectorID, weightedSpeed);
				}
				
				//System.out.println();
			}
			
			
			
			}catch(Exception exception)
			{
			}
		

		//add to observation container
		
		List<Double> speedEB80 = new ArrayList<Double>();
        for (String i:EB80.keySet())
        {
        	speedEB80.add(EB80.get(i));
        }
        observationsEB.put(timecounter, speedEB80);
        // action when reach 6 observations
        if (observationsEB.size() == 6) 
        {
        	
        	FileWriter pw = new FileWriter(output+  reportDate + ".csv",true);
        	for (int k :observationsEB.keySet()) 
        	{
        		
        		double[] speedEB = new double[observationsEB.get(k).size()];
        		for (int i = 0; i < speedEB.length; i++) {
        			speedEB[i] = observationsEB.get(k).get(i)/1.6;     
        		}
//        		for(int j=0;j<speedEB.length;j++)
//        		{
//        			System.out.print(speedEB[j]+",");
//        		}
        		double[] interpspeedEB = interpLinear(EB80mm, speedEB, interpmm);
        		for(int j=0;j<interpspeedEB.length;j++)
        		{
        			
        			//System.out.print(interpspeedEB[j]+",");
        			pw.append(Double.toString(interpspeedEB[j])+","); //write vectorized training samples to local file 
                     
                    
        		}
            }
        	//System.out.println(timecounter%2);
        	pw.append(Integer.toString(timecounter%2));
        	pw.append("\n");
        	pw.flush();
            pw.close();
        	
            // read the vectorized test sample in weka format and apply the pretrained models.
        	CSVLoader loader = new CSVLoader();
			loader.setOptions(weka.core.Utils.splitOptions("-H"));
		    loader.setSource(new File(output+  reportDate + ".csv"));
		    Instances data = loader.getDataSet();
		    data.setClassIndex(data.numAttributes() - 1);
		    NumericToNominal filter = new NumericToNominal();                         
		    filter.setOptions(weka.core.Utils.splitOptions("-R last"));                      
		    filter.setInputFormat(data);                         
		    Instances Data = Filter.useFilter(data, filter);
		    double value_pred = pred.classifyInstance(Data.instance(Data.numInstances()-1));			
			double[] percentage_pred=pred.distributionForInstance(Data.instance(Data.numInstances()-1));
			double value_ident = ident.classifyInstance(Data.instance(Data.numInstances()-1));			
			double[] percentage_ident=ident.distributionForInstance(Data.instance(Data.numInstances()-1));
			
//			FileWriter pw = new FileWriter("/Users/Shuo/Documents/Wavetronix/monitor.txt",true);
//			pw.append(Double.toString(value)+","+Double.toString(percentage[0])+","+Double.toString(percentage[1]));
//            pw.append("\n"); 
//            pw.flush();
//            pw.close();
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			Date dateobj = new Date();
			System.out.println("For WorkZone on I80EB in Council Bluffs at " + df.format(dateobj) + ":");
			System.out.println("System observed: "+percentage_ident[0]+" likelihood of having a crash in the PAST 2min");
			System.out.println("                 "+percentage_pred[1]+" likelihood of having a crash in the NEXT 5min");
			System.out.println(" ");
			
        	observationsEB.remove(observationsEB.firstKey());		  
        }
        
        
        // seprate process for westbound (didn't finish)
		List<Double> speedWB80 = new ArrayList<Double>();
        for (String i:WB80.keySet())
        {
        	speedWB80.add(WB80.get(i));
        }
        observationsWB.put(timecounter, speedWB80);
        
		
		
		
		
		long timeAfter = System.currentTimeMillis();
		long elapsedtime = timeAfter - timeBefore;
		//System.out.println(elapsedtime);
		if (elapsedtime<20000){
		try {
			Thread.sleep(20000-elapsedtime);
		} catch (InterruptedException e) {
		}
		}
		}while(counter<2);

	}
		
	// linear interpolation function	
	public static final double[] interpLinear(double[] x, double[] y, double[] xi) throws IllegalArgumentException {

	    if (x.length != y.length) {
	        throw new IllegalArgumentException("X and Y must be the same length");
	    }
	    if (x.length == 1) {
	        throw new IllegalArgumentException("X must contain more than one value");
	    }
	    double[] dx = new double[x.length - 1];
	    double[] dy = new double[x.length - 1];
	    double[] slope = new double[x.length - 1];
	    double[] intercept = new double[x.length - 1];

	    // Calculate the line equation (i.e. slope and intercept) between each point
	    for (int i = 0; i < x.length - 1; i++) {
	        dx[i] = x[i + 1] - x[i];
	        if (dx[i] == 0) {
	            throw new IllegalArgumentException("X must be montotonic. A duplicate " + "x-value was found");
	        }
	        if (dx[i] < 0) {
	            throw new IllegalArgumentException("X must be sorted");
	        }
	        dy[i] = y[i + 1] - y[i];
	        slope[i] = dy[i] / dx[i];
	        intercept[i] = y[i] - x[i] * slope[i];
	    }

	    // Perform the interpolation here
	    double[] yi = new double[xi.length];
	    for (int i = 0; i < xi.length; i++) {
	        if ((xi[i] > x[x.length - 1]) || (xi[i] < x[0])) {
	            yi[i] = Double.NaN;
	        }
	        else {
	            int loc = Arrays.binarySearch(x, xi[i]);
	            if (loc < -1) {
	                loc = -loc - 2;
	                yi[i] = slope[loc] * xi[i] + intercept[loc];
	            }
	            else {
	                yi[i] = y[loc];
	            }
	        }
	    }

	    return yi;
	}

}
