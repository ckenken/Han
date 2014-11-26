/*
 * 
 * http://en.wikipedia.org/wiki/OPTICS_algorithm
 * 
 *  OPTICS(DB, eps, MinPts)
 for each point p of DB
 p.reachability-distance = UNDEFINED
 for each unprocessed point p of DB
 N = getNeighbors(p, eps)
 mark p as processed
 output p to the ordered list
 Seeds = empty priority queue
 if (core-distance(p, eps, Minpts) != UNDEFINED)
 update(N, p, Seeds, eps, Minpts)
 for each next q in Seeds
 N' = getNeighbors(q, eps)
 mark q as processed
 output q to the ordered list
 if (core-distance(q, eps, Minpts) != UNDEFINED)
 update(N', q, Seeds, eps, Minpts)
 * 
 *  update(N, p, Seeds, eps, Minpts)
 coredist = core-distance(p, eps, MinPts)
 for each o in N
 if (o is not processed)
 new-reach-dist = max(coredist, dist(p,o))
 if (o.reachability-distance == UNDEFINED) // o is not in Seeds
 o.reachability-distance = new-reach-dist
 Seeds.insert(o, new-reach-dist)
 else               // o in Seeds, check for improvement
 if (new-reach-dist < o.reachability-distance)
 o.reachability-distance = new-reach-dist
 Seeds.move-up(o, new-reach-dist)
 * 
 * 
 * execution step
 * 1. setup environment
 * 2. load data
 * 3. execute OPTICS 
 * 4. extract optics cluster
 * 5. output
 * 
 */

package lab.adsl.optics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;




import lab.adsl.object.MobilityPattern;
import lab.adsl.object.Model;
import lab.adsl.object.Point;
import lab.adsl.object.PointDistance;
import lab.adsl.util.TreeMapOperator;

public class OPTICS extends Thread {
	private static Logger logger = Logger.getLogger(OPTICS.class.getName());

	// the static variable
	public static final double UNDEFINED = -1;
	public static final int PROCESSED = 1;
	public static final int UNPROCESSED = 0;
	public static final long OUTLIER = -1;

	// distance function of the Euclidean Distance
	public static final int EU_DISTANCE = 0;
	// distance function of the Hellinger Distance
	// Here is a custom distance function for segments
	public static final int HE_DISTANCE = 1;

	// global data
	public Map<Long, Point> pts;
	public ArrayList<Point> clusterOrder = new ArrayList<Point>();
	public TreeMap<Double, List<Long>> orderSeeds = new TreeMap<Double, List<Long>>();
	public List<Long> featureList;
	// lsh id => user id
	public Map<Long, Long> lshMapping = new HashMap<Long, Long>();

	// permanent connection statement
//	private DBConnection conn;
	private PreparedStatement stmt;

	// OPTICS parameter
	// default 0
	private long paramId = 0;
	// default 1000M
	private double eps = 1000;
	// default 5 points
	private int minPts = 5;

	// segment setting.  used in pattern cluster.
	private long settingId = 0;

	// checki cluster param id. used in pattern cluster.
	private long checkinParamId = 0;
	
	// distance function
	private int distFunction = OPTICS.EU_DISTANCE;
	
	// using LSH
	private boolean useLSH = false;
	private double rLSH = 10;

	/**
	 * connect to database and set statement for reuse. for query neighbor
	 * points
	 */
	/*
	public void connect() {
		this.conn = new DBConnection();
		String sql = "select point2, distance from distance_ny where point1 = ? and distance < ? ";
		this.stmt = conn.getPreparedStatementReadOnly(sql);
	}
*/
	/**
	 * close statement and connection
	 */
//	public void disconnect() {
//		try {
//			this.stmt.close();
//			this.conn.disconnect();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * get neighbors from pre-computed distance data in database
	 * 
	 * @param id
	 * @param eps
	 * @return
	 */
	@Deprecated
	public List<PointDistance> getNeighbors(long id, double eps) {
		List<PointDistance> result = null;
		ResultSet rs;
		try {
			this.stmt.setLong(1, id);
			this.stmt.setDouble(2, eps);
			rs = this.stmt.executeQuery();
			if (rs != null) {
				result = new ArrayList<PointDistance>();
				while (rs.next()) {
					PointDistance p = new PointDistance();
					p.point1 = id;
					p.point2 = rs.getLong("point2");
					p.distance = rs.getDouble("distance");
					result.add(p);
				}
			}
			if (rs != null)
				rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * get neighbors from a built TreeMap<Double,List<Long>>
	 * 
	 * @param pt
	 * @param eps
	 * @return
	 */
	public List<PointDistance> getNeighborsV2(Point pt, double eps) {
		// step1 build tree map from these points

		// a order tree for sorting the distance
		TreeMap<Double, List<Long>> neighborsDist = new TreeMap<Double, List<Long>>();
		double dist = -1;
		// neighbor id
		long nId = 0; 
		Point n = null;

		Iterator<Long> ir = null;
		List<Long> lshResult = new ArrayList<Long>();
		
		if (this.useLSH == true){
			// lsh neighbors
			Set<Long> lshNeighborSet = LSH.getKNN(pt, this.rLSH, this.featureList);
			Iterator<Long> sir = lshNeighborSet.iterator();
			while (sir.hasNext()) {
				lshResult.add(this.lshMapping.get(sir.next()));
			}
			System.out.println("RNN lshResult size:" + lshResult.size());
			ir = lshResult.iterator();
			
		}else{
			// all neighbors
			ir = this.pts.keySet().iterator();
			//System.out.println("RNN pts size:" + this.pts.keySet().size());  ///////////
		}
		long start = Calendar.getInstance().getTimeInMillis();
		while (ir.hasNext()) {
			nId = ir.next();
			n = this.pts.get(nId);
			// p2 is pt's neighbor
			if (n.id != pt.id) {

				if (this.distFunction == OPTICS.HE_DISTANCE) {
					dist = PatternDistance.getPatternDistance(pt.mPatterns,
							n.mPatterns);
				} else {
					dist = Haversine.getDistanceDouble(pt.lat, pt.lng, n.lat,
							n.lng);
				}
				// logger.debug("p1:"+pt.id+","+lat1+","+lng1);
				// logger.debug("p2:"+n.id+","+lat2+","+lng2);
				logger.debug("(" + pt.id + "," + n.id + ")'s " + "distance:"
						+ dist + ", eps:" + eps);
				//System.out.println("("+ pt.id + ", " + n.id +") " + "MEPDdistance: "
				//		+ dist);
				if (dist <= eps) { // distance is smaller than eps
					n.pId = pt.id; // save the id and distance to point set
					n.distFromPId = dist;
					TreeMapOperator.put(neighborsDist, dist, nId);
				}
			}
		}
		long end = Calendar.getInstance().getTimeInMillis();
		//System.out.println("(" + this.settingId + "," + this.checkinParamId + ")" + "dist, runtime:" + (end - start) );  ///////

		// step2 return the elements which distance is smaller than the eps.
		List<PointDistance> neighborList = new ArrayList<PointDistance>();
		long id = TreeMapOperator.pop(neighborsDist);
		while (id != -1) {
			PointDistance p = new PointDistance();
			Point tmp = this.pts.get(id);
			p.point1 = tmp.pId;
			p.point2 = tmp.id;
			p.distance = tmp.distFromPId;
			neighborList.add(p);
			id = TreeMapOperator.pop(neighborsDist);
		}

		String t = "";
		for (int i = 0; i < neighborList.size(); i++) {
			PointDistance d = neighborList.get(i);
			if (i == 0) {
				t = t + "" + d.point2;
			} else {
				t = t + "," + d.point2;
			}
		}
		logger.debug(pt.id + "'s neighbors within eps:" + t);

		return neighborList;
	}

	public double coreDistance(Point pt, int minPts,
			List<PointDistance> neighbors) {
		if (neighbors.size() < minPts) {
			return UNDEFINED;
		} else {
			double coreDist = neighbors.get(minPts - 1).distance;
			logger.debug("coreDistance at position " + (minPts - 1) + " is "
					+ coreDist + " for pid:" + pt.id);
			// set core-distance
			pt.coreDist = coreDist;
			return coreDist;
		}
	}

	/**
	 * (double) max function
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public double max(double a, double b) {
		if (a > b)
			return a;

		return b;
	}

	/**
	 * 
	 * @param neighbors
	 * @param p
	 * @param coreDist
	 */
	public void update(List<PointDistance> neighbors, Long p, double coreDist) {

		// neighbor id
		long oId = 0;
		// distance between p and o
		double distPO = 0;
		// point of oid
		Point o = null;

		for (int i = 0; i < neighbors.size(); i++) {
			oId = neighbors.get(i).point2;
			distPO = neighbors.get(i).distance;
			o = this.pts.get(oId);
			logger.debug("neighbor:" + o.id + ",distance:" + distPO
					+ ",o.processed:" + o.processed);
			if (o.processed == OPTICS.UNPROCESSED) {
				// max(coreDist,distPO)
				double newReachDist = this.max(coreDist, distPO);
				logger.debug("neighbor:" + oId + ",currentDist"
						+ o.reachabilityDist + ",newReachDist:" + newReachDist);
				if (o.reachabilityDist == OPTICS.UNDEFINED) {
					o.reachabilityDist = newReachDist;
					TreeMapOperator.put(orderSeeds, o.reachabilityDist, o.id);
				} else {
					if (newReachDist < o.reachabilityDist) {
						TreeMapOperator.remove(orderSeeds, o.reachabilityDist,
								o.id);
						o.reachabilityDist = newReachDist;
						TreeMapOperator.put(orderSeeds, o.reachabilityDist,
								o.id);
					}
				}
			}
		}
	}

	// 1. setup environment
	/**
	 * set OPTICS parameter
	 * 
	 * @param eps
	 * @param minPts
	 */
	public void setParameter(long paramId, double eps, int minPts) {
		this.paramId = paramId;
		this.eps = eps;
		this.minPts = minPts;
	}

	/**
	 * set OPTICS parameter from database by optics param id
	 * 
	 * @param paramId
	 */
//	void setParameterfromDB(long paramId) {
//		OpticsSetting oSetting = DB.getOpticsSettingById(paramId);
//		System.out.println(oSetting.toString());
//		this.paramId = oSetting.getParamId();
//		this.eps = oSetting.getEps();
//		this.minPts = oSetting.getMinPts();
//	};

	/**
	 * for pattern cluster use set OPTICS parameter from database by optics
	 * param id
	 * 
	 * @param paramId
	 */
//	void setParameterfromDBPattern(long paramId) {
//		OpticsSetting oSetting = DB.getOpticsSettingPatternById(paramId);
//		System.out.println(oSetting.toString());
//		this.paramId = oSetting.getParamId();
//		this.eps = oSetting.getEps();
//		this.minPts = oSetting.getMinPts();
//	};

	/**
	 * choose the distance function
	 * 
	 * @param distFunction
	 */
	public void setDistFunction(int distFunction) {
		this.distFunction = distFunction;
		logger.info("useing distance function: " + this.distFunction);
	}
	
	/**
	 * turn on/off LSH
	 * set e2lsh k
	 * @param use default false
	 * @param k  default 10
	 */
	public void setLSH(boolean use, double r) {
		this.useLSH = use;
		this.rLSH = r;
		logger.info("useing LSH: " + this.useLSH + ", r: " + this.rLSH);
	}

	/**
	 * choose the the segmentation setting and checkin cluster parameter for loading data
	 * used in pattern cluster
	 * @param settingId
	 */
	public void setPatternClusterInputSource(long settingId, long checkinParamId) {
		this.settingId = settingId;
		this.checkinParamId = checkinParamId;
	}

	// 2. load data
	/**
	 * load testing data check-ins
	 */
	public void loadSyntheticData() {
		this.pts = this.getSyntheticData();
		// set the distance function for this data set;
		this.setDistFunction(OPTICS.EU_DISTANCE);
	}

	/**
	 * load testing data mobility pattern (segments and models)
	 */
	public void loadSyntheticDataPattern() {
		this.pts = this.getSyntheticDataPattern();
		// set the distance function for this data set;
		this.setDistFunction(OPTICS.HE_DISTANCE);
	}

	/**
	 * load gowalla data from database
	 */
//	public void loadGowallaData() {
//		this.pts = DB.getGowallaDataHash();
//		// set the distance function for this data set;
//		this.setDistFunction(OPTICS.EU_DISTANCE);
//	}

	/**
	 * local mobility pattern from database
	 */
//	public void loadPatternData() {
//		// NOTE: using checkinParamId for load pattern data, do not use paramId
//		this.pts = DB.getPatternHash(this.settingId, this.checkinParamId);
//		// set the distance function for this data set;
//		this.setDistFunction(OPTICS.HE_DISTANCE);
//	}
	/**
	 * pattern with initial model
	 */
//	public void loadPatternIModelData() {
//		// NOTE: using checkinParamId for load pattern data, do not use paramId
//		this.pts = DB.getPatternInitialModelHash(this.settingId, this.checkinParamId);
//		// set the distance function for this data set;
//		this.setDistFunction(OPTICS.HE_DISTANCE);
//	}

//	public void loadPatternDataCompressed() {
//		// NOTE: using checkinParamId for load pattern data, do not use paramId
//		this.pts = DB.getPatternHashCompressed(this.settingId, this.checkinParamId);
//		// set the distance function for this data set;
//		this.setDistFunction(OPTICS.HE_DISTANCE);
//	}

//	public void loadPatternIModelDataCompressed() {
//		// NOTE: using checkinParamId for load pattern data, do not use paramId
//		this.pts = DB.getPatternInitialModelHashCompressed(this.settingId, this.checkinParamId);
//		// set the distance function for this data set;
//		this.setDistFunction(OPTICS.HE_DISTANCE);
//	}	
	
	// 3. execute OPTICS

	/**
	 * run OPTICS cluster
	 */
	public void runOptics() {

		logger.debug("start runOptics");
		// neighbor pointer
		List<PointDistance> neighbors = null;
		// neighbor pointer
		List<PointDistance> n2 = null;
		// point id
		long pId = 0;
		// point of pid
		Point p = null;
		// core distant
		double coreDist = 0;
		// seed id
		long qId = 0;
		// point of qid
		Point q = null;
		// core distant for seeds set
		double coreDist2 = 0;
		
		int count = 0;

		if (this.pts == null) {
			logger.error("get points from data base failed!");
			return;
		}
		logger.info("points size: " + this.pts.size());

		// default value was set in data structure
		// for(int i=0; i<ptsList.size();i++){
		// }
		Iterator<Long> ir = this.pts.keySet().iterator();
		while (ir.hasNext()) {
			// current id of point
			pId = ir.next();
			// current
			p = pts.get(pId);
			if (p.processed == OPTICS.PROCESSED)
				continue;

			if (neighbors != null)
				neighbors.clear();

			neighbors = this.getNeighborsV2(p, this.eps);
			// mark as processed
			p.processed = OPTICS.PROCESSED;
			count++;
			if (count % 1000 == 0)
				System.out.println("COUNT: " + count);
			// output to ordered list
			this.clusterOrder.add(p);
			logger.debug("clusterOrder add:" + p.id);
			// empty priority queue
			orderSeeds.clear();
			// calculate p's core distance and set p's core distance
			coreDist = this.coreDistance(p, this.minPts, neighbors);
			logger.debug("pId:" + pId + ",coreDist:" + coreDist);
			if (coreDist != OPTICS.UNDEFINED) {
				// update neighbors' reach distance
				this.update(neighbors, pId, coreDist);
				qId = TreeMapOperator.pop(orderSeeds);
				// not empty
				while (qId != -1) {
					q = pts.get(qId);
					if (q.processed == OPTICS.PROCESSED) {
						// next node
						qId = TreeMapOperator.pop(orderSeeds);
						continue;
					}
					if (n2 != null)
						n2.clear();

					n2 = this.getNeighborsV2(q, this.eps);
					q.processed = OPTICS.PROCESSED;
					count++;
					if (count % 1000 == 0)
						System.out.println("COUNT: " + count);
					// output to ordered list
					this.clusterOrder.add(q);
					logger.debug("clusterOrder add:" + q.id);
					// calculate q's core distance and set q's core distance
					coreDist2 = this.coreDistance(q, this.minPts, n2);
					logger.debug("qId:" + qId + ",coreDist:" + coreDist2);
					if (coreDist2 != OPTICS.UNDEFINED) {
						// update neighbors' reach distance
						this.update(n2, qId, coreDist2);
					}
					// next node
					qId = TreeMapOperator.pop(orderSeeds);
				}
			}
		}

	}

	// 4. extract optics cluster
	/**
	 * extract optics cluster
	 */
	public void extractCluster() {
		long clusterId = -1;

		for (int i = 0; i < this.clusterOrder.size(); i++) {
			Point o = this.clusterOrder.get(i);

			if (o.reachabilityDist == OPTICS.UNDEFINED
					|| o.reachabilityDist > this.eps) {
				if (o.coreDist != OPTICS.UNDEFINED) {
					clusterId++;
					o.clusterId = clusterId;
					// System.out.println(clusterId);
				} else {
					o.clusterId = OPTICS.OUTLIER;
					// System.out.println(clusterId);
				}
			} else {
				o.clusterId = clusterId;
				// System.out.println(clusterId);
			}
		}
	}

	// 5. output
	/**
	 * write result to database
	 */
//	public void outputToDatabase() {
//		//DB.deleteCluser(paramId);
//		DB.writeCluser(this.clusterOrder, this.paramId);
//	}
//
//	public void outputPatternClusterToDatabase() {
//		//DB.deletePatternCluser(this.paramId, this.settingId, this.checkinParamId);
//		DB.writePatternCluser(this.clusterOrder, this.paramId, this.settingId, this.checkinParamId);
//	}
	
	
//	public void outputPatternClusterIModelToDatabase() {
//		//DB.deletePatternCluserIModel(this.paramId, this.settingId, this.checkinParamId);
//		DB.writePatternCluserIModel(this.clusterOrder, this.paramId, this.settingId, this.checkinParamId);
//	}
//	
//	public void outputPatternClusterLSHToDatabase() {
//		DB.deletePatternCluserLSH(this.paramId, this.settingId, this.checkinParamId);
//		DB.writePatternCluserLSH(this.clusterOrder, this.paramId, this.settingId, this.checkinParamId);
//	}
//	

	/**
	 * display in console
	 * @throws IOException 
	 */
	public void displayCluster() throws IOException {
		File f = new File("sameG.txt");
		FileWriter FW = new FileWriter(f);
		
		for (int i = 0; i < this.clusterOrder.size(); i++) {
			Point o = this.clusterOrder.get(i);
			System.out.println("output:" + o.id + "," + o.clusterId + ","
					+ o.coreDist + "," + o.reachabilityDist);
			FW.append("output:" + o.id + "," + o.clusterId + ","
					+ o.coreDist + "," + o.reachabilityDist + "\n");
		}
		FW.close();
	}

	/**
	 * display input of segment results in console
	 */
	public void displayPatternPonits() {
		for (Map.Entry<Long, Point> pt : this.pts.entrySet()) {
			System.out.println("id:" + pt.getKey());
			System.out.println("userId:" + pt.getValue().mPatterns.userId);
			for (Map.Entry<Long, Model> sResult : pt.getValue().mPatterns.segmentResults
					.entrySet()) {
				System.out.println("segment:" + sResult.getKey());
				System.out.println("model:" + sResult.getValue().modelId);
				for (Map.Entry<Long, Double> region : sResult.getValue().regionProb
						.entrySet()) {
					System.out.print("probs:" + region.getKey() + ":"
							+ region.getValue() + ",");
				}
				System.out.println();
			}
			System.out.println("___________");
		}
	}

	/**
	 * display input of check-in records in console
	 */
	public void displayCheckinPonits() {
		for (Map.Entry<Long, Point> pt : this.pts.entrySet()) {
			System.out.println("id:" + pt.getKey() + ", lat:"
					+ pt.getValue().lat + ", lng:" + pt.getValue().lng);
		}
	}

	
//	public void outputLSHFile() {
//		// use checkinParamId not paramId because we want to get the feature regions
//		this.featureList = DB.getOpticsClusterId(this.checkinParamId);
//		//this.featureList = this.getfeatureTest();
//		long count = 0;
//		String filePointset = LSH.POINTSET_FILE;
//		Map<Long, Double> probs;
//		double p;
//
//		try {
//			for (Map.Entry<Long, Point> u : this.pts.entrySet()) {
//				this.lshMapping.put(count, u.getKey());
//				count++;
//
//				for (Map.Entry<Long, Model> seg : u.getValue().mPatterns.segmentResults.entrySet()) {
//					filePointset = LSH.POINTSET_FILE + seg.getKey();
//					probs = seg.getValue().regionProb;
//					// write point set to file
//					BufferedWriter bw = new BufferedWriter(new FileWriter(filePointset, true));
//					for (int i = 0; i < this.featureList.size(); i++){
//						//System.out.println(this.featureList.get(i));
//						//System.out.println(probs.get(this.featureList.get(i)));
//						if (probs.get(this.featureList.get(i)) == null) {
//							p = 0.0;
//						} else {
//							p = probs.get(this.featureList.get(i));
//						}
//						p = Math.sqrt(p);
//						if(i == 0) {
//							bw.write(Double.toString(p));
//						} else {
//							bw.write(" " + Double.toString(p));
//						}
//					}
//					bw.newLine();
//					bw.close();
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//			OPTICS.logger.error("write lsh file error: " + e.toString());
//		}		
//		
//	}
	
	
	
	
	// unit test data

	/**
	 * generate synthetic data
	 * 
	 * @return
	 */
	public Map<Long, Point> getSyntheticData() {
		Map<Long, Point> result = new HashMap<Long, Point>();
		Point p0 = new Point(0, 53.7456517542, -0.4875719547);
		Point p1 = new Point(1, 51.7456517542, -0.4875719547);
		Point p2 = new Point(2, 51.746003821, -0.4861021042);
		Point p3 = new Point(3, 51.7456587833, -0.48136945);
		Point p4 = new Point(4, 51.7459706073, -0.4852330685);
		Point p5 = new Point(5, 51.7429879141, -0.4880654812);
		Point p6 = new Point(6, 51.7556517542, -0.4975719547);
		Point p7 = new Point(7, 51.756003821, -0.4961021042);
		Point p8 = new Point(8, 51.7556587833, -0.49136945);
		Point p9 = new Point(9, 51.7559706073, -0.4952330685);
		Point p10 = new Point(10, 51.7529879141, -0.4980654812);
		Point p11 = new Point(11, 52.7529879141, -0.4980654812);

		result.put(0L, p0);
		result.put(1L, p1);
		result.put(2L, p2);
		result.put(3L, p3);
		result.put(4L, p4);
		result.put(5L, p5);
		result.put(6L, p6);
		result.put(7L, p7);
		result.put(8L, p8);
		result.put(9L, p9);
		result.put(10L, p10);
		result.put(11L, p11);

		List<Long> pList = new ArrayList<Long>(result.keySet());

		for (int i = 0; i < pList.size() - 1; i++) {
			double lat1 = result.get(pList.get(i)).lat;
			double lng1 = result.get(pList.get(i)).lng;
			for (int j = i + 1; j < pList.size(); j++) {
				double lat2 = result.get(pList.get(j)).lat;
				double lng2 = result.get(pList.get(j)).lng;
				System.out.println("(" + pList.get(i) + "," + pList.get(j)
						+ ")="
						+ Haversine.getDistanceDouble(lat1, lng1, lat2, lng2));
			}
		}
		return result;
	}

	public Map<Long, Point> getSyntheticDataPattern() {

		Map<Long, Point> result = new HashMap<Long, Point>();
		long rId;
		double prob;

		// mobility pattern 1

		// point
		Point pt1 = new Point();
		pt1.id = 1;
		pt1.mPatterns = new MobilityPattern();
		// segment result
		pt1.mPatterns.userId = 1;
		pt1.mPatterns.segmentResults = new HashMap<Long, Model>();
		// probs
		Model m1 = new Model();
		m1.modelId = 0;
		m1.regionProb = new HashMap<Long, Double>();
		rId = 0;
		prob = 3.0 / 6.0;
		m1.regionProb.put(rId, prob);
		rId = 1;
		prob = 2.0 / 6.0;
		m1.regionProb.put(rId, prob);
		rId = 2;
		prob = 1.0 / 6.0;
		m1.regionProb.put(rId, prob);

		Model m2 = new Model();
		m2.modelId = 1;
		m2.regionProb = new HashMap<Long, Double>();
		rId = 0;
		prob = 2.0 / 6.0;
		m2.regionProb.put(rId, prob);
		rId = 1;
		prob = 1.0 / 6.0;
		m2.regionProb.put(rId, prob);
		rId = 2;
		prob = 3.0 / 6.0;
		m2.regionProb.put(rId, prob);

		Model m3 = new Model();
		m3.modelId = 2;
		m3.regionProb = new HashMap<Long, Double>();
		rId = 0;
		prob = 1.0 / 6.0;
		m3.regionProb.put(rId, prob);
		rId = 1;
		prob = 4.0 / 6.0;
		m3.regionProb.put(rId, prob);
		rId = 2;
		prob = 1.0 / 6.0;
		m3.regionProb.put(rId, prob);

		pt1.mPatterns.segmentResults.put(19L, m1);
		pt1.mPatterns.segmentResults.put(20L, m2);
		pt1.mPatterns.segmentResults.put(21L, m3);

		Point pt2 = new Point();
		pt2.id = 2;
		pt2.mPatterns = new MobilityPattern();
		// segment result
		pt2.mPatterns.userId = 2;
		pt2.mPatterns.segmentResults = new HashMap<Long, Model>();
		// probs
		Model m4 = new Model();
		m4.modelId = 1;
		m4.regionProb = new HashMap<Long, Double>();
		rId = 0;
		prob = 2.0 / 6.0;
		m4.regionProb.put(rId, prob);
		rId = 1;
		prob = 1.0 / 6.0;
		m4.regionProb.put(rId, prob);
		rId = 3;
		prob = 3.0 / 6.0;
		m4.regionProb.put(rId, prob);

		Model m5 = new Model();
		m5.modelId = 2;
		m5.regionProb = new HashMap<Long, Double>();
		rId = 0;
		prob = 1.0 / 6.0;
		m5.regionProb.put(rId, prob);
		rId = 1;
		prob = 4.0 / 6.0;
		m5.regionProb.put(rId, prob);
		rId = 2;
		prob = 1.0 / 6.0;
		m5.regionProb.put(rId, prob);

		Model m6 = new Model();
		m6.modelId = 3;
		m6.regionProb = new HashMap<Long, Double>();
		rId = 0;
		prob = 3.0 / 6.0;
		m6.regionProb.put(rId, prob);
		rId = 1;
		prob = 2.0 / 6.0;
		m6.regionProb.put(rId, prob);
		rId = 2;
		prob = 1.0 / 6.0;
		m6.regionProb.put(rId, prob);

		pt2.mPatterns.segmentResults.put(20L, m4);
		pt2.mPatterns.segmentResults.put(21L, m5);
		pt2.mPatterns.segmentResults.put(22L, m6);

		result.put(pt1.id, pt1);
		result.put(pt2.id, pt2);

		return result;

	}
	
	public List<Long> getfeatureTest(){
		List<Long> result = new ArrayList<Long>();
		result.add(0L);
		result.add(1L);
		result.add(2L);
		result.add(3L);
		return result;
		
	}
	
}
