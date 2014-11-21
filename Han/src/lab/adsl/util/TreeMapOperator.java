package lab.adsl.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lab.adsl.object.PointDistance;

/**
 * use tree map to handle the orderSeeds take distance as key, and store ids by
 * list
 * 
 * @author uktar
 * 
 */

public class TreeMapOperator {
	// Map<Long,List<Long>> orderSeeds = new TreeMap<Long,List<Long>>();
	public static void create(TreeMap<Double, List<Long>> orderSeeds,
			Double dist, Long id) {
		List<Long> ids = new ArrayList<Long>();
		ids.add(id);
		orderSeeds.put(dist, ids);
	}

	public static void add(List<Long> ids, Long id) {
		ids.add(id);
	}

	/**
	 * 
	 * @param orderSeeds
	 * @param dist
	 *            reach distance
	 * @param id
	 *            point id
	 */
	public static void put(TreeMap<Double, List<Long>> orderSeeds, Double dist,
			Long id) {
		List<Long> ids = orderSeeds.get(dist);
		if (ids == null) {
			create(orderSeeds, dist, id);
		} else {
			add(ids, id);
		}
	}

	/**
	 * pop in order. if no data exist, return -1.
	 * 
	 * @param orderSeeds
	 * @return
	 */
	public static long pop(TreeMap<Double, List<Long>> orderSeeds) {
		long result = -1;
		Iterator<Double> ir = orderSeeds.keySet().iterator();
		if (ir.hasNext()) {
			Double dist = ir.next();
			List<Long> ids = orderSeeds.get(dist);
			result = ids.get(0);
			ids.remove(0);
			if (ids.size() == 0) {
				// System.out.println("ids empty");
				orderSeeds.remove(dist);
			}
		}
		return result;
	}

	public static void remove(TreeMap<Double, List<Long>> orderSeeds,
			Double dist, Long id) {
		List<Long> ids = orderSeeds.get(dist);
		ids.remove(id);
		if (ids.size() == 0) {
			orderSeeds.remove(dist);
		}
	}

	public static void main(String args[]) {

		TreeMap<Double, List<Long>> orderSeeds = new TreeMap<Double, List<Long>>();
		Long dist = 0L;
		for (int i = 0; i < 10; i++) {

			Long reachabilityDist = (long) (Math.random() * 10);
			dist = reachabilityDist;
			List<Long> p = orderSeeds.get(reachabilityDist);
			if (p == null) {
				TreeMapOperator.create(orderSeeds, (double) reachabilityDist,
						(long) i);
			} else {
				TreeMapOperator.add(p, (long) i);
			}

		}
		Iterator<Double> ir = orderSeeds.keySet().iterator();
		while (ir.hasNext()) {
			Double p = ir.next();
			System.out.print(p + ":");
			List<Long> sp = orderSeeds.get(p);
			for (int i = 0; i < sp.size(); i++) {
				System.out.print(sp.get(i) + ",");
			}
			System.out.println();

		}
		System.out.println("-------remove" + dist + "," + 9);
		TreeMapOperator.remove(orderSeeds, (double) dist, (long) 9);

		ir = orderSeeds.keySet().iterator();
		while (ir.hasNext()) {
			Double p = ir.next();
			System.out.print(p + ":");
			List<Long> sp = orderSeeds.get(p);
			for (int i = 0; i < sp.size(); i++) {
				System.out.print(sp.get(i) + ",");
			}
			System.out.println();

		}

		List<Long> l = new ArrayList<Long>();

		long id = TreeMapOperator.pop(orderSeeds);

		while (id != -1) {
			System.out.println(id);
			l.add(id);
			id = TreeMapOperator.pop(orderSeeds);
		}
		System.out.println("no data");
		id = TreeMapOperator.pop(orderSeeds);
		System.out.println(id);
		id = TreeMapOperator.pop(orderSeeds);
		System.out.println(id);

		orderSeeds.clear();
		for (int i = 0; i < l.size(); i++) {
			long a = l.get(i);
			System.out.print(a + ",");
		}

	}
}
