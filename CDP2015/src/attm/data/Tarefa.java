package attm.data;

import java.io.Serializable;

public class Tarefa implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3197765293288297653L;
	private int indiceTarefa;
	private String nomeTarefa;
	private int horasTarefa;
	private int minutosTarefa;
	
	public Tarefa(){
		
	}
	
	public Tarefa(int indiceTarefa, String nomeTarefa, int horasTarefa,
			int minutosTarefa) {
		super();
		this.indiceTarefa = indiceTarefa;
		this.nomeTarefa = nomeTarefa;
		this.horasTarefa = horasTarefa;
		this.minutosTarefa = minutosTarefa;
	}

	public int getIndiceTarefa() {
		return indiceTarefa;
	}
	public void setIndiceTarefa(int indiceTarefa) {
		this.indiceTarefa = indiceTarefa;
	}
	public String getNomeTarefa() {
		return nomeTarefa;
	}
	public void setNomeTarefa(String nomeTarefa) {
		this.nomeTarefa = nomeTarefa;
	}
	public int getHorasTarefa() {
		return horasTarefa;
	}
	public void setHorasTarefa(int horasTarefa) {
		this.horasTarefa = horasTarefa;
	}
	public int getMinutosTarefa() {
		return minutosTarefa;
	}
	public void setMinutosTarefa(int minutosTarefa) {
		this.minutosTarefa = minutosTarefa;
	}

}
