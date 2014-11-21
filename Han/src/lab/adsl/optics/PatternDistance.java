package lab.adsl.optics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import lab.adsl.object.MobilityPattern;
import lab.adsl.object.Model;

public class PatternDistance {

	private static Logger logger = Logger.getLogger(PatternDistance.class
			.getName());

	public static double HellingerDistance(Map<Long, Double> p1,
			Map<Long, Double> p2) {

		Set<Long> regionSet = new HashSet<Long>();
		regionSet.addAll(p1.keySet());
		regionSet.addAll(p2.keySet());

		double pMin = 0.000001;

		double heDist = 0.0;

		long rId = 0;
		double prob1;
		double prob2;

		Iterator<Long> ir = regionSet.iterator();
		while (ir.hasNext()) {
			rId = ir.next();

			if (p1.get(rId) == null) {
				prob1 = pMin;
			} else {
				prob1 = p1.get(rId);
			}
			if (p2.get(rId) == null) {
				prob2 = pMin;
			} else {
				prob2 = p2.get(rId);
			}
			PatternDistance.logger.debug("(p1:" + prob1 + ", p2:" + prob2 +
			") = " + Math.pow(Math.sqrt(prob1) - Math.sqrt(prob2), 2));
			heDist = heDist + Math.pow(Math.sqrt(prob1) - Math.sqrt(prob2), 2);
		}
		heDist = Math.sqrt(heDist) / Math.sqrt(2);

		return heDist;
	}

	public static double getPatternDistance(MobilityPattern mp1,
			MobilityPattern mp2) {

		if (mp1 == null || mp2 == null) {
			logger.error("input null for mobility pattern");
			return -1;
		}

		double dist = 0;

		Model model1 = null;
		Model model2 = null;
		long sId = 0;
		// the Hellinger distance store.
		// modelId:modelId as key
		Map<String, Double> heDistResultHash = new HashMap<String, Double>();
		String key = "";
		Double modelDist = null;

		Set<Long> segmentSet = new HashSet<Long>();
		segmentSet.addAll(mp1.segmentResults.keySet());
		segmentSet.addAll(mp2.segmentResults.keySet());

		if (segmentSet.size() == 0) {
			logger.warn("empty segment, dist = 0");
			return 0;
		}

		Iterator<Long> ir = segmentSet.iterator();
		while (ir.hasNext()) {
			sId = ir.next();
			PatternDistance.logger.debug("sId:" + sId);
			model1 = mp1.segmentResults.get(sId);
			model2 = mp2.segmentResults.get(sId);
			// create an empty model for measuring the helliger distance
			if (model1 == null) {
				PatternDistance.logger.debug("model1 null");
				model1 = new Model();
				model1.regionProb = new HashMap<Long, Double>();
			}
			// create an empty model for measuring the helliger distance
			if (model2 == null) {
				PatternDistance.logger.debug("model2 null");
				model2 = new Model();
				model2.regionProb = new HashMap<Long, Double>();
			}

			// if two segments with the same pair of models, using previous
			// computing result for accelerating executing speed
			key = model1.modelId + ":" + model2.modelId;
			PatternDistance.logger.debug("heDist store key: " + key);
			modelDist = heDistResultHash.get(key);
			PatternDistance.logger.debug("heDistResultHash get modelDist:"
					+ modelDist);
			if (modelDist == null) {
				modelDist = PatternDistance.HellingerDistance(
						model1.regionProb, model2.regionProb);
				PatternDistance.logger.debug("(" + model1.modelId + ","
						+ model2.modelId + ")'s " + "heDist:" + modelDist);
				dist = dist + modelDist;
				heDistResultHash.put(key, modelDist);
			} else {
				dist = dist + modelDist;
			}
		}
		// average
		dist = dist / (double) segmentSet.size();

		return dist;

	}

}
