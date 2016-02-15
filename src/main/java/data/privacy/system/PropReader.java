package data.privacy.system;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropReader {

	private static PropReader singlePropReader = null;
	private static Properties prop = null;
	
	static{
		if (singlePropReader == null) {
			singlePropReader = new PropReader();
		}
	}
	
	private PropReader() {
		prop = new Properties();
		InputStream input = null;
		try {

			String filename = "privbayes.properties";
			input = PropReader.class.getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				System.out.println("Sorry, unable to find " + filename);
				return;
			}

			// load a properties file from class path, inside static method
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getPropStr(String property){
		return prop.getProperty(property);
	}
}
