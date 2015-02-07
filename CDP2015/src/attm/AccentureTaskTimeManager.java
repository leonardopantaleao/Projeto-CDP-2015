/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package attm;  
/*
 * TrayIconDemo.java
 */

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
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import attm.messages.FactoryMessages;
import attm.messages.Messages;
import attm.messages.MessagesEnum;

public class AccentureTaskTimeManager {
	
	private static Messages messages;
	private static TrayIcon trayIcon;
	private static SystemTray tray;
	private static boolean exibeMensagemAbertura = true;
	private static PopupMenu popup;
	private static Preferences preferences;
	
    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        preferences = Preferences.userRoot().node(getUserDataDirectory());
        
        messages = FactoryMessages.getInstance("Português-BR"); 
        
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    private static void createAndShowGUI() {
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
        Menu tarefasMenu = criarAdicionarMenuAoMenu(popup, messages.getMessage(MessagesEnum.MENU_ITEM_TAREFAS));
        
        MenuItem errorItem = new MenuItem("Error");
        MenuItem warningItem = new MenuItem("Warning");
        MenuItem infoItem = new MenuItem("Info");
        MenuItem noneItem = new MenuItem("None");
        
        //Add components to popup menu
        popup.addSeparator();
//        popup.add(cb1);
//        popup.add(cb2);
//        popup.addSeparator();
        Menu idiomaMenu = criarAdicionarMenuAoMenu(popup, messages.getMessage(MessagesEnum.MENU_ITEM_IDIOMA));
        MenuItem portuguesItem = criarAdicionarItemAoMenu(idiomaMenu, messages.getMessage(MessagesEnum.MENU_ITEM_PORTUGUES));
        MenuItem inglesItem = criarAdicionarItemAoMenu(idiomaMenu, messages.getMessage(MessagesEnum.MENU_ITEM_INGLES));
        popup.addSeparator();
        MenuItem aboutItem = criarAdicionarItemAoMenuPopup(popup, messages.getMessage(MessagesEnum.MENU_ITEM_SOBRE));
        MenuItem exitItem = criarAdicionarItemAoMenuPopup(popup, messages.getMessage(MessagesEnum.MENU_ITEM_SAIR));
        
        tarefasMenu.add(errorItem);
        tarefasMenu.add(warningItem);
        tarefasMenu.add(infoItem);
        tarefasMenu.add(noneItem);
        
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
                		messages.getMessage(MessagesEnum.TEXTO_SOBRE), messages.getMessage(MessagesEnum.NOME_APLICACAO), JOptionPane.PLAIN_MESSAGE);
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
                    trayIcon.displayMessage(messages.getMessage(MessagesEnum.NOME_APLICACAO),
                            "This is an error message", TrayIcon.MessageType.ERROR);
                    
                } else if ("Warning".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.WARNING;
                    trayIcon.displayMessage(messages.getMessage(MessagesEnum.NOME_APLICACAO),
                            "This is a warning message", TrayIcon.MessageType.WARNING);
                    
                } else if ("Info".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.INFO;
                    trayIcon.displayMessage(messages.getMessage(MessagesEnum.NOME_APLICACAO),
                            "This is an info message", TrayIcon.MessageType.INFO);
                    
                } else if ("None".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.NONE;
                    trayIcon.displayMessage(messages.getMessage(MessagesEnum.NOME_APLICACAO),
                            "This is an ordinary message", TrayIcon.MessageType.NONE);
                }
            }
        };
        
        errorItem.addActionListener(listener);
        warningItem.addActionListener(listener);
        infoItem.addActionListener(listener);
        noneItem.addActionListener(listener);
        
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
        
        if(exibeMensagemAbertura){
	        trayIcon.displayMessage(messages.getMessage(MessagesEnum.NOME_APLICACAO),
	        		messages.getMessage(MessagesEnum.MENSAGEM_ABERTURA), TrayIcon.MessageType.INFO);
        }
        
        portuguesItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selecionaLingua("Português-BR");
				reiniciaAplicacao();
//				popup.removeAll();d
//				}
			}
		});
        
        inglesItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selecionaLingua("English");
				reiniciaAplicacao();
//				popup.removeAll();d
//				}
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
    
    public static MenuItem criarAdicionarItemAoMenu(Menu menu, String nomeLabelItem){
    	MenuItem item = new MenuItem(nomeLabelItem);
    	menu.add(item);
    	return item;
    }
    
    private static void reiniciaAplicacao(){
    	exibeMensagemAbertura = false;
		popup.removeAll();
		tray.remove(trayIcon);
		createAndShowGUI();
    }
    
    private static void selecionaLingua(String lingua){
    	messages = FactoryMessages.getInstance(lingua);
		preferences.put("linguaEscolhida", lingua);
    }
    
    public static String getUserDataDirectory() {
        return System.getProperty("user.home") + File.separator + ".jstock" + File.separator;
    }
}
