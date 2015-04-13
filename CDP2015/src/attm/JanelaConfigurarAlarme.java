package attm;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
	
	private static Properties messagesProperties;
	
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
		messagesProperties = AccentureTaskTimeManager.messagesProperties;
		
		this.setTitle(messagesProperties.getProperty("geral.configurar.alarme"));
		
		String horasEmArquivo = dados.get("Horas") != null ? (String) dados.get("Horas") : "18";
		horas = Integer.parseInt(horasEmArquivo);
		String minutosEmArquivo = dados.get("Minutos") != null ? (String) dados.get("Minutos") : "25";
		minutos = Integer.parseInt(minutosEmArquivo);
		
		horasJLabel = new JLabel(messagesProperties.getProperty("janela.alarme.horas"));
		horasJTextField = new JTextField();
		horasJTextField.setColumns(2);
		horasJTextField.setText(horas + "");
		minutosJLabel = new JLabel(messagesProperties.getProperty("janela.alarme.minutos"));
		minutosJTextField = new JTextField();
		minutosJTextField.setColumns(2);
		minutosJTextField.setText(minutos + "");
		
		salvarJButton = new JButton(messagesProperties.getProperty("geral.salvar"));
		fecharJButton = new JButton(messagesProperties.getProperty("geral.fechar"));
		
		painelPrincipal = new JPanel();
		painelPrincipal.setBorder(BorderFactory.createEtchedBorder());
		
		GridLayout gridPrincipal = new GridLayout();
		painelPrincipal.setLayout(gridPrincipal);
		gridPrincipal.setVgap(10);
		gridPrincipal.setColumns(1);
		gridPrincipal.setRows(2);
		
		JLabel vazio = new JLabel("");
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
		
		JPanel painelBotoesInferiores = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(SwingConstants.CENTER);
		painelBotoesInferiores.setLayout(flowLayout);
		
		salvarJButton.setHorizontalAlignment(SwingConstants.CENTER);
		fecharJButton.setHorizontalAlignment(SwingConstants.CENTER);
		painelBotoesInferiores.add(salvarJButton);
		painelBotoesInferiores.add(fecharJButton);
		flowLayout.setAlignment(SwingConstants.BOTTOM);
		
		painelPrincipal.setBorder(new EmptyBorder(0, 45, 0, 45));
        
		painelPrincipal.add(painelSetarHoras);
		painelPrincipal.add(painelBotoesInferiores);
		this.add(painelPrincipal);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		fecharJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fechar();
			}
		});
		
		salvarJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					salvarHorasMinutosNoArquivo();
					AccentureTaskTimeManager.configurarTimer();
					JOptionPane.showMessageDialog(getContentPane(), messagesProperties.getProperty("janela.alarme.salvar.sucesso"), messagesProperties.getProperty("janela.alarme.salvar.sucesso.titulo")
							, JOptionPane.INFORMATION_MESSAGE);
				}catch(IOException io){
					JOptionPane.showMessageDialog(getContentPane(), messagesProperties.getProperty("janela.alarme.salvar.falha"), messagesProperties.getProperty("janela.alarme.salvar.falha.titulo")
							, JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
        pack();
		
	}
	
	private void fechar(){
		this.setVisible(false);
	}
	
	private static void configurarArquivo() {
		singleton = AppSingleton.getInstance();
		arquivo = singleton.getFile();
		dados = singleton.getConfig();
	}
	
	private void salvarHorasMinutosNoArquivo() throws IOException{
		dados.put("Horas", horasJTextField.getText().toString());
		dados.put("Minutos", minutosJTextField.getText().toString());
		arquivo.gravarObjetoNoArquivo(dados);
	}
	
}
