package lab.adsl.optics;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import lab.adsl.object.Point;
import lab.adsl.object.Model;

public class LSH {
	private static Logger logger = Logger.getLogger(LSH.class.getName());
	public static final String QUERY_FILE = "LSH_query_"; 
	public static final String POINTSET_FILE = "LSH_pointset_";
	
	/*
	 * 1. generate the point file
	 * 2. system call
	 * 3. read the result
	 * 4. use the point result as the neighbors
	 * 
	 * */
	
	public static Set<Long> getKNN(Point pt, double r, List<Long> featureList) {
		long start = Calendar.getInstance().getTimeInMillis();
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		String line = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String[] tmp;
		String cmd = "";
		
		Set<Long> result = new HashSet<Long>();
		String fileQuery = LSH.QUERY_FILE + pt.id;
		String filePointset = LSH.POINTSET_FILE;
		Map<Long, Double> probs;
		double p;
		
		int skip = 0;
		
		long startProcess;
		long endProcess;
		long startIO;
		long endIO;

		try {
			
			for (Map.Entry<Long, Model> seg : pt.mPatterns.segmentResults.entrySet()) {
				filePointset = LSH.POINTSET_FILE + seg.getKey();
				probs = seg.getValue().regionProb;
				// write the query point file
				BufferedWriter bw = new BufferedWriter(new FileWriter(fileQuery));
				for (int i = 0; i < featureList.size(); i++){
					if (probs.get(featureList.get(i)) == null) {
						p = 0.0;
					} else {
						p = probs.get(featureList.get(i));
					}
					p = Math.sqrt(p);
					if(i == 0) {
						bw.write(Double.toString(p));
					} else {
						bw.write(" " + Double.toString(p));
					}
				}
				bw.newLine();
				bw.close();
				
				// system call
				cmd = "bin/lsh "+ r + " " + filePointset + " " + fileQuery;
				startProcess = Calendar.getInstance().getTimeInMillis();
				process = runtime.exec(cmd);
				endProcess = Calendar.getInstance().getTimeInMillis();
				System.out.println("process, runtime:" + (endProcess - startProcess) );
				is = process.getInputStream();
				isr = new InputStreamReader(is);
				br = new BufferedReader(isr);
				skip = 0;
				startIO = Calendar.getInstance().getTimeInMillis();
				while ((line = br.readLine()) != null) {

					if (line.matches("^0.*")) {
						//System.out.println(line);
						//System.out.flush();
						tmp = line.split("\t");
						if(tmp.length > 0) {
							//System.out.println("node:" + tmp[0]);
							// read the result
							result.add(Long.parseLong(tmp[0]));
						}
					}
				}
				endIO = Calendar.getInstance().getTimeInMillis();
				System.out.println("io, runtime:" + (endIO - startIO) );
				is.close();
				isr.close();
				br.close();
				//System.out.println("OK");
				
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = Calendar.getInstance().getTimeInMillis();
		System.out.println("lsh, runtime:" + (end - start) );
		LSH.logger.info("LSH: neightbor size:" + result.size() + ", r:" + r);
		return result;
	}
	

	public static void main(String[] args) {
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		String line = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String[] tmp;

		try {
			process = runtime.exec("bin/lsh 3 data_set query_set");
			is = process.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				System.out.flush();
				tmp = line.split("\t");
				if(tmp.length > 0) {
					System.out.println("node:" + tmp[0]);
				}
			}
			is.close();
			isr.close();
			br.close();
			System.out.println("OK");
		} catch (IOException e) {
			System.out.println(e);
			runtime.exit(0);
		}
	}// end main method
	
	
	
	

}
