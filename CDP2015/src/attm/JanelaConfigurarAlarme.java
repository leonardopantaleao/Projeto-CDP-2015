package attm;

import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import attm.chronometer.Cronometro;
import attm.data.AppSingleton;
import attm.data.ConfigFile;

public class JanelaConfigurarAlarme extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3681391810442173414L;
	
	private static int horas;
	private static int minutos;
	private static JTextField horasJTextField;
	private static JTextField minutosJTextField;
	private static JButton salvarJButton;
	private static JButton fecharJButton;
	
	private static HashMap<String, Object> dados;
	private static ConfigFile arquivo;
	private static AppSingleton singleton;
	
	private static JPanel painelPrincipal;
	
	public static void main(String [] args){
		initComponents();
		
		try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cronometro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cronometro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cronometro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cronometro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
		
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JanelaConfigurarAlarme().setVisible(true);
            }
        });
	}
	
	private static void initComponents(){
		configurarArquivo();
		
		String horasEmArquivo = dados.get("Horas") != null ? (String) dados.get("Horas") : "18";
		horas = Integer.parseInt(horasEmArquivo);
		String minutosEmArquivo = dados.get("Minutos") != null ? (String) dados.get("Minutos") : "25";
		minutos = Integer.parseInt(minutosEmArquivo);
		
		horasJTextField = new JTextField();
		minutosJTextField = new JTextField();
		
//		Properties messagesProperties = AccentureTaskTimeManager.messagesProperties;
//		salvarJButton = new JButton(messagesProperties.getProperty("geral.salvar"));
//		fecharJButton = new JButton(messagesProperties.getProperty("geral.fechar"));
		
		salvarJButton = new JButton("Salvar");
		fecharJButton = new JButton("Fechar");
		
		painelPrincipal = new JPanel();
		painelPrincipal.setBorder(BorderFactory.createEtchedBorder());
		
		
		
	}
	
	private static void configurarArquivo() {
		singleton = AppSingleton.getInstance();
		arquivo = singleton.getFile();
		dados = singleton.getConfig();
	}
}
