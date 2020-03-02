package learntree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class CsvReader {
	
	// returns null if error
	public static ArrayList<Integer[]> readCsv(String path) {
		ArrayList<Integer[]> ret = new ArrayList<Integer[]>();
		ret.ensureCapacity(1 << 16);
		
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		        String [] sVals = line.split(",");
		        ret.add(new Integer[sVals.length]);
		        
		        for(int j = 0; j < sVals.length; j++)
		        	ret.get(ret.size() - 1)[j] = Integer.parseInt(sVals[j]);
		    }
		} catch (Exception e) {
			return null;
		}
		
		return ret;
	}
}
