package persistence;

import java.io.Serializable;

public class DataAccessObject  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int id = -1;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

}
