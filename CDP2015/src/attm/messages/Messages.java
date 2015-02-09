package attm.messages;

public abstract class Messages {
	String TEXTO_SOBRE; 
	String NOME_APLICACAO;
	String MENSAGEM_ABERTURA;
	String MENU_ITEM_SOBRE;
	String MENU_ITEM_SAIR;
	String MENU_ITEM_TAREFAS;
	String MENU_ITEM_IDIOMA;
	String MENU_ITEM_PORTUGUES;
	String MENU_ITEM_INGLES;
	String MENU_ITEM_INSERIR_TAREFAS;
	String JANELA_INSERIR_TAREFAS_TITULO;
	String JANELA_INSERIR_TAREFAS_INSERIR_TEXTO;
	String CANCELAR;
	
	
	public String getMessage(MessagesEnum msg) {
		switch (msg){
		case TEXTO_SOBRE:
			return this.TEXTO_SOBRE;
		case NOME_APLICACAO:
			return this.NOME_APLICACAO;
		case MENSAGEM_ABERTURA:
			return this.MENSAGEM_ABERTURA;
		case MENU_ITEM_SOBRE:
			return this.MENU_ITEM_SOBRE;
		case MENU_ITEM_SAIR:
			return this.MENU_ITEM_SAIR;
		case MENU_ITEM_TAREFAS:
			return this.MENU_ITEM_TAREFAS;
		case MENU_ITEM_IDIOMA:
			return this.MENU_ITEM_IDIOMA;
		case MENU_ITEM_PORTUGUES:
			return this.MENU_ITEM_PORTUGUES;
		case MENU_ITEM_INGLES:
			return this.MENU_ITEM_INGLES;
		case MENU_ITEM_INSERIR_TAREFAS:
			return this.MENU_ITEM_INSERIR_TAREFAS;
		case JANELA_INSERIR_TAREFAS_TITULO:
			return this.JANELA_INSERIR_TAREFAS_TITULO;
		case JANELA_INSERIR_TAREFAS_INSERIR_TEXTO:
			return this.JANELA_INSERIR_TAREFAS_INSERIR_TEXTO;
		case CANCELAR:
			return this.CANCELAR;
		default:
			return null;
		}
	}
}
