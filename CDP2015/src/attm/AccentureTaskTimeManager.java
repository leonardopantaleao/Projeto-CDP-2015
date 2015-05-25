package attm;  

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import attm.chronometer.Cronometro;
import attm.data.AppSingleton;
import attm.data.ConfigFile;
import attm.data.Tarefa;
import attm.messages.MessagesFile;

public class AccentureTaskTimeManager {
	private static Timer timer;
	public static Properties messagesProperties;
	private static TrayIcon trayIcon;
	private static SystemTray tray;
	private static boolean exibeMensagemAbertura = true;
	private static PopupMenu popup;

	private static HashMap<String, Object> dados;
	private static ConfigFile arquivo;
	private static AppSingleton singleton;

	private static Menu tarefasMenu;

	private static LinkedList<Cronometro> listaJanelasCronometro;
	private static JanelaConfigurarAlarme janelaAlarme;

	private static LinkedList<Tarefa> tarefasAdicionadas;
	
	private static boolean fromRemove = false;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		
		configurarArquivo();

		configurarTimer();

		obterLinguaSalvaArquivo();

		UIManager.put("swing.boldMetal", Boolean.FALSE);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				criarExibirGUI();
			}
		});
	}

	private static void obterLinguaSalvaArquivo() {
		try{
			messagesProperties = MessagesFile.getLanguageFile((String)dados.get("Lingua"));
		}catch(Exception e){
			messagesProperties = MessagesFile.getLanguageFile("Português-BR");
		}
	}

	private static void configurarArquivo() {
		singleton = AppSingleton.getInstance();
		arquivo = singleton.getFile();
		dados = singleton.getConfig();
	}

	@SuppressWarnings("unchecked")
	private static void criarExibirGUI() {
		janelaAlarme = new JanelaConfigurarAlarme();
		janelaAlarme.setVisible(false);
		
		tarefasAdicionadas = (LinkedList<Tarefa>) dados.get("Tarefas");
		if(tarefasAdicionadas == null){
			tarefasAdicionadas = new LinkedList<Tarefa>();
		}

		if(!fromRemove){
			listaJanelasCronometro = new LinkedList<Cronometro>();
			iniciarJanelasCronometro();
		}


		UIManager.put("OptionPane.cancelButtonText", messagesProperties.getProperty("geral.cancelar"));
		UIManager.put("OptionPane.yesButtonText", messagesProperties.getProperty("geral.sim"));
		UIManager.put("OptionPane.noButtonText", messagesProperties.getProperty("geral.nao"));


		//Check the SystemTray support
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		popup = new PopupMenu();
		trayIcon =
				new TrayIcon(createImage("../images/attm.gif", "tray icon"));
		trayIcon.setImageAutoSize(true);
		tray = SystemTray.getSystemTray();

		//        CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
		//        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
		MenuItem tarefasItem = criarAdicionarItemAoMenu(popup, messagesProperties.getProperty("menu.item.inserir.tarefas"));
		MenuItem removerTarefaItem = criarAdicionarItemAoMenu(popup, messagesProperties.getProperty("menu.item.remover.tarefa"));
		
		removerTarefaItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tarefasAdicionadas != null && tarefasAdicionadas.size() > 0){
					AccentureTaskTimeManager.removerTarefas();
				}
				else{
					JOptionPane.showMessageDialog(null, messagesProperties.getProperty("menu.item.remover.tarefa.vazio"));
				}
				
			}
		});
		
		tarefasMenu = criarAdicionarMenuAoMenu(popup, messagesProperties.getProperty("menu.item.tarefas"));

		if(existemTarefasAdicionadas()){
			for(Tarefa tarefa: tarefasAdicionadas){
				criarAdicionarTarefaItemAoMenu(tarefasMenu, tarefa.getNomeTarefa());
			}
			//			criarAdicionarTarefaItemAoMenu(tarefasMenu, (String) dados.get("Tarefa"));
		}
		else{
			tarefasMenu.setEnabled(false);
		}

		//		MenuItem errorItem = new MenuItem("Error");
		//		MenuItem warningItem = new MenuItem("Warning");
		//		MenuItem infoItem = new MenuItem("Info");
		//		MenuItem noneItem = new MenuItem("None");
		
		MenuItem relatorio = new MenuItem(messagesProperties.getProperty("menu.item.relatorio"));
		relatorio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exibirRelatorioHoras();
			}
		});
		popup.add(relatorio);
		

		//Add components to popup menu
		popup.addSeparator();
		//        popup.add(cb1);
		//        popup.add(cb2);
		//        popup.addSeparator();
		Menu idiomaMenu = criarAdicionarMenuAoMenu(popup, messagesProperties.getProperty("menu.item.idioma"));
		MenuItem configurarAlarmeMenuItem = criarAdicionarItemAoMenu(popup, messagesProperties.getProperty("menu.item.configurar.alarme"));
		MenuItem portuguesItem = criarAdicionarItemAoMenu(idiomaMenu, messagesProperties.getProperty("menu.item.portugues"));
		MenuItem inglesItem = criarAdicionarItemAoMenu(idiomaMenu, messagesProperties.getProperty("menu.item.ingles"));
		popup.addSeparator();
		MenuItem aboutItem = criarAdicionarItemAoMenuPopup(popup, messagesProperties.getProperty("menu.item.sobre"));
		MenuItem exitItem = criarAdicionarItemAoMenuPopup(popup, messagesProperties.getProperty("menu.item.sair"));

		//		tarefasMenu.add(errorItem);
		//		tarefasMenu.add(warningItem);
		//		tarefasMenu.add(infoItem);
		//		tarefasMenu.add(noneItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			return;
		}

		//		trayIcon.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				JOptionPane.showMessageDialog(null,
		//						"This dialog box is run from System Tray");
		//			}
		//		});

		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						messagesProperties.getProperty("texto.sobre"), messagesProperties.getProperty("nome.aplicacao"), JOptionPane.PLAIN_MESSAGE);
			}
		});

		//        cb1.addItemListener(new ItemListener() {
		//            public void itemStateChanged(ItemEvent e) {
		//                int cb1Id = e.getStateChange();
		//                if (cb1Id == ItemEvent.SELECTED){
		//                    trayIcon.setImageAutoSize(true);
		//                } else {
		//                    trayIcon.setImageAutoSize(false);
		//                }
		//            }
		//        });
		//        
		//        cb2.addItemListener(new ItemListener() {
		//            public void itemStateChanged(ItemEvent e) {
		//                int cb2Id = e.getStateChange();
		//                if (cb2Id == ItemEvent.SELECTED){
		//                    trayIcon.setToolTip("Sun TrayIcon");
		//                } else {
		//                    trayIcon.setToolTip(null);
		//                }
		//            }
		//        });

		//		ActionListener listener = new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				MenuItem item = (MenuItem)e.getSource();
		//				//TrayIcon.MessageType type = null;
		//				System.out.println(item.getLabel());
		//				if ("Error".equals(item.getLabel())) {
		//					//type = TrayIcon.MessageType.ERROR;
		//					trayIcon.displayMessage(messagesProperties.getProperty("nome.aplicacao"),
		//							"This is an error message", TrayIcon.MessageType.ERROR);
		//
		//				} else if ("Warning".equals(item.getLabel())) {
		//					//type = TrayIcon.MessageType.WARNING;
		//					trayIcon.displayMessage(messagesProperties.getProperty("nome.aplicacao"),
		//							"This is a warning message", TrayIcon.MessageType.WARNING);
		//
		//				} else if ("Info".equals(item.getLabel())) {
		//					//type = TrayIcon.MessageType.INFO;
		//					trayIcon.displayMessage(messagesProperties.getProperty("nome.aplicacao"),
		//							"This is an info message", TrayIcon.MessageType.INFO);
		//
		//				} else if ("None".equals(item.getLabel())) {
		//					//type = TrayIcon.MessageType.NONE;
		//					trayIcon.displayMessage(messagesProperties.getProperty("nome.aplicacao"),
		//							"This is an ordinary message", TrayIcon.MessageType.NONE);
		//				}
		//			}
		//		};

		configurarAlarmeMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				janelaAlarme.setVisible(true);
			}
		});

		//		errorItem.addActionListener(listener);
		//		warningItem.addActionListener(listener);
		//		infoItem.addActionListener(listener);
		//		noneItem.addActionListener(listener);

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tray.remove(trayIcon);
				System.exit(0);
			}
		});

		if(exibeMensagemAbertura){
			trayIcon.displayMessage(messagesProperties.getProperty("nome.aplicacao"),
					messagesProperties.getProperty("mensagem.abertura"), TrayIcon.MessageType.INFO);
		}

		portuguesItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int resposta = JOptionPane.showConfirmDialog(null, messagesProperties.getProperty("janela.mudar.lingua.pergunta"), messagesProperties.getProperty("janela.mudar.lingua"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (resposta == JOptionPane.NO_OPTION) {
					//Clicou em Não.
				} else if (resposta == JOptionPane.YES_OPTION) {
					selecionaLingua("Português-BR");
					dados.put("Lingua", "Português-BR");
					arquivo.gravarObjetoNoArquivo(dados);
					destivarJanelas();
					reiniciarAplicacao();
				} else if (resposta == JOptionPane.CLOSED_OPTION) {
					//Clicou em fechar.
				}

			}
		});

		inglesItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int resposta = JOptionPane.showConfirmDialog(null, messagesProperties.getProperty("janela.mudar.lingua.pergunta"), messagesProperties.getProperty("janela.mudar.lingua"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (resposta == JOptionPane.NO_OPTION) {
					//Clicou em Não.
				} else if (resposta == JOptionPane.YES_OPTION) {
					selecionaLingua("English");
					dados.put("Lingua", "English");
					arquivo.gravarObjetoNoArquivo(dados);
					destivarJanelas();
					reiniciarAplicacao();
				} else if (resposta == JOptionPane.CLOSED_OPTION) {
					//Clicou em fechar.
				}
			}
		});

		tarefasItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object nomeTarefa = JOptionPane.showInputDialog(null, messagesProperties.getProperty("janela.inserir.tarefas.inserir.texto"), messagesProperties.getProperty("janela.inserir.tarefas.titulo"), JOptionPane.PLAIN_MESSAGE, null, null, null);
				final String caixaNomeTarefaText = nomeTarefa != null ? nomeTarefa.toString(): "";
				dados.put("Tarefa", caixaNomeTarefaText);
				//Adicao da lista de tarefas
				int indiceAtual = tarefasAdicionadas.size();
				Tarefa tarefa = new Tarefa(indiceAtual, caixaNomeTarefaText.toString(), 0, 0);
				tarefasAdicionadas.add(tarefa);
				dados.put("Tarefas", tarefasAdicionadas);
				arquivo.gravarObjetoNoArquivo(dados);
				criarAdicionarTarefaItemAoMenu(tarefasMenu, caixaNomeTarefaText);
				tarefasMenu.setEnabled(true);


				Cronometro janelaCronometro = new Cronometro();
				janelaCronometro.setVisible(false);
				janelaCronometro.setLocationRelativeTo(null);
				listaJanelasCronometro.add(janelaCronometro);
				janelaCronometro.setTitle(caixaNomeTarefaText);
			}
		});
	}

	private static void iniciarJanelasCronometro() {
		for(int i = 0; i < tarefasAdicionadas.size(); i++){
			Cronometro janelaCronometro = new Cronometro();
			janelaCronometro.setVisible(false);
			janelaCronometro.setLocationRelativeTo(null);
			listaJanelasCronometro.add(janelaCronometro);
		}

		int indiceJanelaCronometro = 0;

		//Atribui a cada janela o título da tarefa
		for(Cronometro janelaCronometro: listaJanelasCronometro){
			janelaCronometro.setTitle(tarefasAdicionadas.get(indiceJanelaCronometro).getNomeTarefa());
			indiceJanelaCronometro++;
		}
	}

	//Obtain the image URL
	protected static Image createImage(String path, String description) {
		URL imageURL = AccentureTaskTimeManager.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}

	public static MenuItem criarAdicionarItemAoMenuPopup(PopupMenu menu, String nomeLabelItem){
		MenuItem item = new MenuItem(nomeLabelItem);
		menu.add(item);
		return item;
	}

	public static Menu criarAdicionarMenuAoMenu(PopupMenu menuPopup, String nomeLabelItem){
		Menu menu = new Menu(nomeLabelItem);
		menuPopup.add(menu);
		return menu;
	}

	public static MenuItem criarAdicionarItemAoMenu(final Menu menu, final String nomeLabelItem){
		MenuItem item = new MenuItem(nomeLabelItem);
		menu.add(item);
		return item;
	}

	public static MenuItem criarAdicionarTarefaItemAoMenu(final Menu menu, final String nomeLabelItem){
		MenuItem item = new MenuItem(nomeLabelItem);
		menu.add(item);
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				listaJanelasCronometro.get(buscarIndiceTarefaClicada(nomeLabelItem)).setVisible(true);
			}

			private int buscarIndiceTarefaClicada(String nomeLabelItem){
				int indiceTarefaClicada = 0;

				for(Tarefa tarefaClicada: tarefasAdicionadas){
					if(!tarefaClicada.getNomeTarefa().equals(nomeLabelItem)){
						indiceTarefaClicada++;
					}
					else{
						break;
					}
				}

				return indiceTarefaClicada;
			}
		});
		return item;
	}


	private static void reiniciarAplicacao(){
		exibeMensagemAbertura = false;
		popup.removeAll();
		tray.remove(trayIcon);
		criarExibirGUI();
	}

	private static void selecionaLingua(String lingua){
		messagesProperties = MessagesFile.getLanguageFile(lingua);
	}

	public static String getUserDataDirectory() {
		return System.getProperty("user.home") + File.separator + ".jstock" + File.separator;
	}

	public static boolean existemTarefasAdicionadas(){
//		if(dados.containsKey("Tarefas")){
//			return true;
//		}
//		else{
//			return false;
//		}
		
		if(tarefasAdicionadas.size() == 0){
			return false;
		}
		else{
			return true;
		}
	}

	public static void configurarTimer() {
		Calendar calendar = Calendar.getInstance();
		String horasEmArquivo = dados.get("Horas") != null ? (String) dados.get("Horas") : "18";
		String minutosEmArquivo = dados.get("Minutos") != null ? (String) dados.get("Minutos") : "25";
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horasEmArquivo));
		calendar.set(Calendar.MINUTE, Integer.parseInt(minutosEmArquivo));
		calendar.set(Calendar.SECOND, 0);
		Date time = calendar.getTime();

		if(time.before(new Date())){
			calendar.add(Calendar.DATE, 1);
			time = calendar.getTime();
		}

		timer = new Timer();
		timer.schedule(new RemindTask(), time);
	}

	static class RemindTask extends TimerTask {
		public void run() {
			trayIcon.displayMessage(messagesProperties.getProperty("nome.aplicacao"),
					messagesProperties.getProperty("cronometro.lancar.horas"), TrayIcon.MessageType.INFO);
			timer.cancel();
		}
	}


	public static boolean existeJanelaAtivada(){
		for(Cronometro janela: listaJanelasCronometro){
			if(janela.isAtivada()){
				return true;
			}
		}
		return false;
	}

	public static void destivarJanelas(){
		for(Cronometro janela: listaJanelasCronometro){
			janela.setVisible(false);
			janela = null;
		}
	}
	
	public static void removerTarefas(){
		String[] tarefasAdicionadasArray = new String[tarefasAdicionadas.size()];
		for(int i = 0; i < tarefasAdicionadasArray.length; i++){
			tarefasAdicionadasArray[i] = tarefasAdicionadas.get(i).getNomeTarefa();
		}
		
		String tarefaSelecionada = (String) JOptionPane.showInputDialog(null, messagesProperties.getProperty("menu.item.remover.tarefa.mensagem"), messagesProperties.getProperty("menu.item.remover.tarefa.titulo"),
				JOptionPane.PLAIN_MESSAGE, null, tarefasAdicionadasArray, tarefasAdicionadasArray[0]);
		
		for(int i = 0; i < tarefasAdicionadas.size(); i++){
			if(tarefasAdicionadas.get(i).getNomeTarefa().equals(tarefaSelecionada)){
				tarefasAdicionadas.remove(i);
				listaJanelasCronometro.get(i).setRemovida(true);
			}
		}
		
		for(int i = 0; i < tarefasMenu.getItemCount(); i++){
			if(tarefasMenu.getItem(i).getName().equals(tarefaSelecionada)){
				tarefasMenu.remove(i);
			}
		}
		dados.remove("Tarefas");
		dados.put("Tarefas", tarefasAdicionadas);
		arquivo.gravarObjetoNoArquivo(dados);
		
		fromRemove = true;
		reiniciarAplicacao();
	}
	
	public static void exibirRelatorioHoras(){
		if(existemTarefasAdicionadas()){
			String relatorioFinal = messagesProperties.getProperty("janela.relatorio.relatorio");
			for(Cronometro janela: listaJanelasCronometro){
				if(!janela.isRemovida()){
					relatorioFinal += janela.getTitle() + ": " + janela.getCurrentHora() + "h " + janela.getCurrentMinuto() + "m\n";
				}
			}
			JOptionPane.showMessageDialog(null, relatorioFinal);
		}
		else{
			JOptionPane.showMessageDialog(null, messagesProperties.get("janela.relatorio.sem.tarefa"));
		}
	}
}
