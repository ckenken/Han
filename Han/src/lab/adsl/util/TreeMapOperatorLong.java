package lab.adsl.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lab.adsl.object.PointDistance;

/**
 * use tree map to handle the orderSeeds take distance as key, and store ids by
 * list data structure: key -> list example: k1 -> (v1,v2,v3) ; k2 ->
 * (v2,v5,v10)
 * 
 * @author uktar
 * 
 */

public class TreeMapOperatorLong {
	// Map<Long,List<Long>> orderSeeds = new TreeMap<Long,List<Long>>();
	public static void create(TreeMap<Long, List<Long>> order, Long key, Long id) {
		List<Long> ids = new ArrayList<Long>();
		ids.add(id);
		order.put(key, ids);
	}

	public static void add(List<Long> ids, Long id) {
		ids.add(id);
	}

	/**
	 * 
	 * @param order
	 * @param key
	 * @param id
	 */
	public static void put(TreeMap<Long, List<Long>> order, Long key, Long id) {
		List<Long> ids = order.get(key);
		if (ids == null) {
			create(order, key, id);
		} else {
			add(ids, id);
		}
	}

	/**
	 * pop in order. if no data exist, return -1.
	 * 
	 * @param order
	 * @return
	 */
	public static long pop(TreeMap<Long, List<Long>> order) {
		long result = -1;
		Iterator<Long> ir = order.keySet().iterator();
		if (ir.hasNext()) {
			Long key = ir.next();
			List<Long> ids = order.get(key);
			result = ids.get(0);
			ids.remove(0);
			if (ids.size() == 0) {
				// System.out.println("ids empty");
				order.remove(key);
			}
		}
		return result;
	}

	public static void remove(TreeMap<Long, List<Long>> order, Long key, Long id) {
		List<Long> ids = order.get(key);
		ids.remove(id);
		if (ids.size() == 0) {
			order.remove(key);
		}
	}

	public static void main(String args[]) {

		TreeMap<Long, List<Long>> order = new TreeMap<Long, List<Long>>();
		Long dist = 0L;
		for (int i = 0; i < 10; i++) {

			Long reachabilityDist = (long) (Math.random() * 10);
			dist = reachabilityDist;
			List<Long> p = order.get(reachabilityDist);
			if (p == null) {
				TreeMapOperatorLong.create(order, reachabilityDist, (long) i);
			} else {
				TreeMapOperatorLong.add(p, (long) i);
			}

		}
		Iterator<Long> ir = order.keySet().iterator();
		while (ir.hasNext()) {
			Long p = ir.next();
			System.out.print(p + ":");
			List<Long> sp = order.get(p);
			for (int i = 0; i < sp.size(); i++) {
				System.out.print(sp.get(i) + ",");
			}
			System.out.println();

		}
		System.out.println("-------remove" + dist + "," + 9);
		TreeMapOperatorLong.remove(order, dist, (long) 9);

		ir = order.keySet().iterator();
		while (ir.hasNext()) {
			Long p = ir.next();
			System.out.print(p + ":");
			List<Long> sp = order.get(p);
			for (int i = 0; i < sp.size(); i++) {
				System.out.print(sp.get(i) + ",");
			}
			System.out.println();

		}

		List<Long> l = new ArrayList<Long>();

		long id = TreeMapOperatorLong.pop(order);

		while (id != -1) {
			System.out.println(id);
			l.add(id);
			id = TreeMapOperatorLong.pop(order);
		}
		System.out.println("no data");
		id = TreeMapOperatorLong.pop(order);
		System.out.println(id);
		id = TreeMapOperatorLong.pop(order);
		System.out.println(id);

		order.clear();
		for (int i = 0; i < l.size(); i++) {
			long a = l.get(i);
			System.out.print(a + ",");
		}

	}
}
