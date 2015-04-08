package attm;

import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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
	private static JLabel horasJLabel;
	private static JLabel minutosJLabel;
	private static JButton salvarJButton;
	private static JButton fecharJButton;
	
	private static HashMap<String, Object> dados;
	private static ConfigFile arquivo;
	private static AppSingleton singleton;
	
	private static JPanel painelPrincipal;
	
	public JanelaConfigurarAlarme(){
		initComponents();
	}
	
	public static void main(String [] args){
		
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
	
	private void initComponents(){
		configurarArquivo();
		
		this.setTitle("Configurar Alarme");
		
		String horasEmArquivo = dados.get("Horas") != null ? (String) dados.get("Horas") : "18";
		horas = Integer.parseInt(horasEmArquivo);
		String minutosEmArquivo = dados.get("Minutos") != null ? (String) dados.get("Minutos") : "25";
		minutos = Integer.parseInt(minutosEmArquivo);
		
		horasJLabel = new JLabel("Horas");
		horasJTextField = new JTextField();
		horasJTextField.setColumns(2);
		horasJTextField.setText(horas + "");
		minutosJLabel = new JLabel("Minutos");
		minutosJTextField = new JTextField();
		minutosJTextField.setColumns(2);
		minutosJTextField.setText(minutos + "");
		
//		Properties messagesProperties = AccentureTaskTimeManager.messagesProperties;
//		salvarJButton = new JButton(messagesProperties.getProperty("geral.salvar"));
//		fecharJButton = new JButton(messagesProperties.getProperty("geral.fechar"));
		
		salvarJButton = new JButton("Salvar");
		salvarJButton.setVisible(true);
		fecharJButton = new JButton("Fechar");
		
		painelPrincipal = new JPanel();
		painelPrincipal.setBorder(BorderFactory.createEtchedBorder());
		
		JLabel vazio = new JLabel("");
		JLabel intervalo = new JLabel("");
		vazio.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel doisPontos = new JLabel(":");
		doisPontos.setHorizontalAlignment(SwingConstants.CENTER);
		
		GridLayout grid = new GridLayout();
		JPanel painelSetarHoras = new JPanel();
		painelSetarHoras.setLayout(grid);
		grid.setColumns(3);
		grid.setRows(2);
		horasJLabel.setHorizontalAlignment(SwingConstants.CENTER);
		minutosJLabel.setHorizontalAlignment(SwingConstants.CENTER);
		horasJTextField.setHorizontalAlignment(SwingConstants.CENTER);
		minutosJTextField.setHorizontalAlignment(SwingConstants.CENTER);
		
		painelSetarHoras.add(horasJLabel);
		painelSetarHoras.add(vazio);
		painelSetarHoras.add(minutosJLabel);
		
		painelSetarHoras.add(horasJTextField);
		painelSetarHoras.add(doisPontos);
		painelSetarHoras.add(minutosJTextField);
		
//		painelSetarHoras.add(salvarJButton);
//		painelSetarHoras.add(intervalo);
//		painelSetarHoras.add(fecharJButton);
		
		painelSetarHoras.setBorder(new EmptyBorder(10, 80, 10, 80));
        
		this.add(painelPrincipal);
		this.add(painelSetarHoras);
//		this.add(painelTextAreas);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
        pack();
		
	}
	
	private static void configurarArquivo() {
		singleton = AppSingleton.getInstance();
		arquivo = singleton.getFile();
		dados = singleton.getConfig();
	}
}
