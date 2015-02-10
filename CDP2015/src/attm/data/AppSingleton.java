package attm.data;

import java.util.HashMap;

public class AppSingleton {
	private static AppSingleton singleton = null;
	private static HashMap<String, Object> config;
	private static ConfigFile file;

	public AppSingleton() {
	}
	public static AppSingleton getInstance() {
		if(singleton == null) {
			synchronized(AppSingleton.class) {
				if(singleton == null) {
					singleton = new AppSingleton();
					file = new ConfigFile();
					config = file.getFileContent();
				}
			}
		}
		return singleton;
	}
	public HashMap<String, Object> getConfig() {
		return config;
	}
	public ConfigFile getFile() {
		return file;
	}
}
