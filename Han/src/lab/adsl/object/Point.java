package lab.adsl.object;

import java.util.Date;
import java.util.Map;

import com.ckenken.storage.NewPoint;

import lab.adsl.optics.OPTICS;

public class Point {
	public int processed = OPTICS.UNPROCESSED;
	public double coreDist = OPTICS.UNDEFINED;
	public double reachabilityDist = OPTICS.UNDEFINED;
	// in feature region is check-in id, in pattern cluster is user's id
	public long id = -1; 

	public long clusterId = OPTICS.OUTLIER;
	// Haversine, distance in degree
	public double lat = -1; 
	public double lng = -1;
	// distance from pId
	public double distFromPId = -1; 
	// pId point
	public long pId = -1; 
	public Date ckTime;

	// for pattern cluster
	public MobilityPattern mPatterns;
	
	// ckenken
	public int Gid;
	public int same;
	public String cate;

	public Point() {
		super();
	}

	public Point(int processed, double coreDist, double reachabilityDist,
			long id, long clusterId, double lat, double lng,
			double distFromPId, long pId, Date ckTime) {
		super();
		this.processed = processed;
		this.coreDist = coreDist;
		this.reachabilityDist = reachabilityDist;
		this.id = id;
		this.clusterId = clusterId;
		this.lat = lat;
		this.lng = lng;
		this.distFromPId = distFromPId;
		this.pId = pId;
		this.ckTime = ckTime;
	}

	public Point(long id, double lat, double lng) {
		super();
		this.id = id;
		this.lat = lat;
		this.lng = lng;

	}

	public Point(long id, Date ckTime, long clusterId) {
		super();
		this.id = id;
		this.ckTime = ckTime;
		this.clusterId = clusterId;

	}

	public Point(long id, NewPoint np) {
		this.id = id;
		this.ckTime = np.getTime();
		this.lat = np.getLat();
		this.lng = np.getLng();
		this.cate = np.getCate();
	}
	
	public void show()
	{
		System.out.println(id + ": " + lat + "," + lng  + " same=" + same + ", cate = " + cate);
		
	}
	
	@Override
	public String toString() {
		return "Point [processed=" + processed + ", coreDist=" + coreDist
				+ ", reachabilityDist=" + reachabilityDist + ", id=" + id
				+ ", clusterId=" + clusterId + ", lat=" + lat + ", lng=" + lng
				+ ", distFromPId=" + distFromPId + ", pId=" + pId + "]";
	}

}
