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
	
	private static Cronometro cronometro;
	private static JanelaConfigurarAlarme janelaAlarme;

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

	private static void criarExibirGUI() {
		cronometro = new Cronometro();
		cronometro.setVisible(false);
		janelaAlarme = new JanelaConfigurarAlarme();
		janelaAlarme.setVisible(false);
		
		UIManager.put("OptionPane.cancelButtonText", messagesProperties.getProperty("geral.cancelar"));

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
		tarefasMenu = criarAdicionarMenuAoMenu(popup, messagesProperties.getProperty("menu.item.tarefas"));
		
		if(existemTarefasAdicionadas()){
			criarAdicionarTarefaItemAoMenu(tarefasMenu, (String) dados.get("Tarefa"));
			tarefasMenu.setEnabled(true);
		}
		else{
			tarefasMenu.setEnabled(false);
		}

//		MenuItem errorItem = new MenuItem("Error");
//		MenuItem warningItem = new MenuItem("Warning");
//		MenuItem infoItem = new MenuItem("Info");
//		MenuItem noneItem = new MenuItem("None");

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

		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"This dialog box is run from System Tray");
			}
		});

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

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MenuItem item = (MenuItem)e.getSource();
				//TrayIcon.MessageType type = null;
				System.out.println(item.getLabel());
				if ("Error".equals(item.getLabel())) {
					//type = TrayIcon.MessageType.ERROR;
					trayIcon.displayMessage(messagesProperties.getProperty("nome.aplicacao"),
							"This is an error message", TrayIcon.MessageType.ERROR);

				} else if ("Warning".equals(item.getLabel())) {
					//type = TrayIcon.MessageType.WARNING;
					trayIcon.displayMessage(messagesProperties.getProperty("nome.aplicacao"),
							"This is a warning message", TrayIcon.MessageType.WARNING);

				} else if ("Info".equals(item.getLabel())) {
					//type = TrayIcon.MessageType.INFO;
					trayIcon.displayMessage(messagesProperties.getProperty("nome.aplicacao"),
							"This is an info message", TrayIcon.MessageType.INFO);

				} else if ("None".equals(item.getLabel())) {
					//type = TrayIcon.MessageType.NONE;
					trayIcon.displayMessage(messagesProperties.getProperty("nome.aplicacao"),
							"This is an ordinary message", TrayIcon.MessageType.NONE);
				}
			}
		};
		
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
				selecionaLingua("Português-BR");
				dados.put("Lingua", "Português-BR");
				arquivo.gravarObjetoNoArquivo(dados);
				reiniciaAplicacao();
				//				popup.removeAll();d
				//				}
			}
		});

		inglesItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selecionaLingua("English");
				dados.put("Lingua", "English");
				arquivo.gravarObjetoNoArquivo(dados);
				reiniciaAplicacao();
				//				popup.removeAll();d
				//				}
			}
		});

		tarefasItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object nomeTarefa = JOptionPane.showInputDialog(null, messagesProperties.getProperty("janela.inserir.tarefas.inserir.texto"), messagesProperties.getProperty("janela.inserir.tarefas.titulo"), JOptionPane.PLAIN_MESSAGE, null, null, null);
				final String caixaNomeTarefaText = nomeTarefa != null ? nomeTarefa.toString(): "";
				dados.put("Tarefa", caixaNomeTarefaText);
				arquivo.gravarObjetoNoArquivo(dados);
				reiniciaAplicacao();
			}
		});
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
				cronometro.setTitle(nomeLabelItem);
				cronometro.setLocationRelativeTo(null);
				cronometro.setVisible(true);
			}
		});
		return item;
	}

	private static void reiniciaAplicacao(){
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
		if(dados.containsKey("Tarefa")){
			return true;
		}
		else{
			return false;
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
}
