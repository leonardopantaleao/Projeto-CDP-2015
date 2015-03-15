package attm.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class ConfigFile {
	private File config;
	private HashMap<String, Object> fileContent;



	@SuppressWarnings("unchecked")
	public ConfigFile() {
		config = new File("config" + File.separator + "config.txt");
		fileContent = new HashMap<String, Object>();

		try{
			if(!config.exists()){
				config.createNewFile();
			}
			else
			{
				FileInputStream f = new FileInputStream(config);
			    ObjectInputStream s = new ObjectInputStream(f);
				this.fileContent = (HashMap<String, Object>) s.readObject();
			    s.close();
			}

		} catch(IOException e) {
			System.out.println("Erro!");
		} catch (ClassNotFoundException e) {
			System.out.println("Erro!");
		}
	}


	public File getConfig() {
		return config;
	}

	public void setConfig(File config) {
		this.config = config;
	}

	public HashMap<String, Object> getFileContent() {
		return fileContent;
	}


	public void setFileContent(HashMap<String, Object> fileContent) {
		this.fileContent = fileContent;
	}
	
	public void gravarObjetoNoArquivo(HashMap<String, Object> map){
		try {
			FileOutputStream f = new FileOutputStream(config);
	        ObjectOutputStream s = new ObjectOutputStream(f);
	        s.writeObject(map);
	        s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    
		
	}
}
