package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.MultiGaussianSampler;
import utils.RandUtils;
import utils.ZigguratGaussianSampler;

/**
 * Read in User file and Geo file
 * Mapping users and geographic locations together via some different strategies.
 * @author yinxusen
 *
 */
public class MappingUserGeo {
	Set<String> users;
	Set<String> geos;
	Map<String, List<Integer>> mixin;

	public MappingUserGeo() {
		users = new HashSet<String>();
		geos = new HashSet<String>();
	}

	public void readdata(String f, Set<String> container) throws IOException {
		BufferedReader reader = null;
		FileInputStream file = new FileInputStream(new File(f));
		System.out.println("read " + f);
		reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
		String tempString = null;

		int cnt = 0;
		while ((tempString = reader.readLine()) != null) {
			cnt++;
			if (cnt % 1000 == 0) {
				System.out.println("read");
			}
			container.add(tempString.trim());
		}
		reader.close();
		file.close();
	}

	public void init(String fuser, String fgeo) throws IOException {
		readdata(fuser, users);
		readdata(fgeo, geos);
	}

	/**
	 * Mixin users with locations
	 * @param repetition repetition rate of geographic locations for users
	 * @param meanng mean of number of geographical location for users
	 */
	public void MixIn(double repetition, int meanng, int nummodal) {
		mixin = new HashMap<String, List<Integer>>();
		MultiGaussianSampler gsampler = new MultiGaussianSampler(nummodal);

		int numgeo = 0;
		double[] meanOfGeo = new double[nummodal];
		double[] varOfGeo = new double[nummodal];
		for (int i = 0; i < nummodal; i++) {
			meanOfGeo[i] = Math.random() * geos.size();
			varOfGeo[i] = Math.random() * repetition;
		}
		for (String user : users) {
			numgeo = (int) RandUtils.RandGamma(meanng);
			for (int i = 0; i < numgeo; i++) {
				if (mixin.containsKey(user)) {
					double tmp = gsampler.SampleOne(meanOfGeo, varOfGeo);
					mixin.get(user).add((int) Math.floor(tmp));
				} else {
					mixin.put(user, new ArrayList<Integer>());
				}
			}
		}
	}

	public void output(String fmapping) throws IOException {
		BufferedWriter output = new BufferedWriter(new FileWriter(fmapping));
		String[] geolist = geos.toArray(new String[geos.size()]);
		int length = geolist.length;
		for (String user : mixin.keySet()) {
			for (int index : mixin.get(user)) {
				if (index < 0 || index > length - 1) {
					index = (int) Math.floor(Math.random() * length);
				}
				output.write(user + "," + geolist[index] + "\n");
			}
		}
		output.close();
	}

	public static void main(String[] args) throws IOException {
		long begin = System.currentTimeMillis();
		MappingUserGeo test = new MappingUserGeo();
		test.init("weibo_users", "msra_geo");
		test.MixIn(10000, 500, 100);
		long end = System.currentTimeMillis();
		System.out.println(1.0 * (end - begin) / 1000 + "s");
		test.output("user_geo_mapping");
	}

}
