package attm;

import java.awt.Dimension;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JPanel;  
import javax.swing.Timer;  
  
public class Chronometer extends JPanel {  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel label;  
    private Timer timer;  
    private int current = 0;  
  
    public Chronometer(int tempo) {  
        this.current = tempo;
        this.add(this.getLabel());  
        this.go();  
    }  
  
    public JLabel getLabel() {  
        if (this.label == null) {  
            this.label = new JLabel(this.current + "");  
            this.label.setPreferredSize(new Dimension(100, 22));  
        }  
        return this.label;  
    }  
  
    public void go() {  
        ActionListener action = new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
                label.setText(++current + "");
                if(current == 11){
                  timer.stop();
                  System.exit(0);
                }  
            }  
        };
        this.timer = new Timer(1000, action);  
        this.timer.start();          
    }  
  
    public static void main(String[] args) {  
        JFrame frame = new JFrame();  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setContentPane(new Chronometer(0));  
        frame.setSize(200, 75);  
        frame.setVisible(true);  
    }  
} 
