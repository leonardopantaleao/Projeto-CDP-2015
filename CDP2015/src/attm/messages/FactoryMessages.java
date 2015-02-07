package attm.messages;

public class FactoryMessages {
	public static Messages getInstance(String language){
		if (language.equals("Portugu�s-BR")){
			return new MessagesPTBR();
		}
		if (language.equals("English")){
			return new MessagesENG();
		}
		else
		{
			return new MessagesPTBR();
		}
	}
}
