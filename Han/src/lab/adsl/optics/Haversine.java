package lab.adsl.optics;

public class Haversine {
	private final static double earthRadius = 6371;

	/**
	 * return distance in meter(double)
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */

	public static double getDistanceDouble(double lat1, double lng1,
			double lat2, double lng2) {
		double dist = -1;

		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);

		dist = 2 * earthRadius * 1000 * Math.asin(Math.sqrt(a));
		return dist;

	}

	/**
	 * return distance in meter
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static long getDistanceM(double lat1, double lng1, double lat2,
			double lng2) {
		long dist = -1;

		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);

		dist = Math.round(2 * earthRadius * 1000 * Math.asin(Math.sqrt(a)));
		return dist;

	}

	public static void main(String args[]) {
		System.out.println(getDistanceM(24.98228, 121.5562, 35.591, 139.4194));
		System.out
				.println(getDistanceM(24.98228, 121.5562, 35.69874, 139.7079));
		System.out.println(getDistanceM(24, 120, 24, 120.001));
		System.out.println(getDistanceM(23.7875, 120.954, 151.615, -5.502));
		System.out.println(getDistanceM(90, 0, -90, 0));
		System.out.println(getDistanceM(90, 180, -90, 180));
		System.out.println(getDistanceM(0, 0, 0, -180));
		System.out.println(getDistanceM(0, 0, 0, -360));
		System.out.println(getDistanceM(90, 10, 90, 10));

	}

}
