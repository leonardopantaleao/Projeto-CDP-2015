package attm.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MessagesFile {
	public static Properties getLanguageFile(String language){
		Properties properties = new Properties();
		try {
			File file;
			if (language.equals("Português-BR")){
				file = new File("languages" + File.separator + "portugues.properties");
			}
			else if (language.equals("English")){
				file = new File("languages" + File.separator + "english.properties");
			}
			else
			{
				file = new File("languages" + File.separator + "portugues.properties");
			}
			FileInputStream fileInput = new FileInputStream(file);
			properties.load(fileInput);
			fileInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}
}
