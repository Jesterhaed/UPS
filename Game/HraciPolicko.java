package Game;

import Control.HraLogika;

public class HraciPolicko {
	
	private boolean trefeno = false;
	private boolean obsazeno = false;
	
	private Tank tank;
	private int odecet;
	
	private IdPolicka id;
	
	private HraLogika gameControl;
	
	public HraciPolicko(HraLogika gameControl, IdPolicka id){
		
		this.id = id;
		this.gameControl = gameControl;
		
	}
	
	
	public void addTank(Tank tank) {
		
		this.tank = tank;
		obsazeno = true;
		
	}

	@Override
	public String toString() {
		
		String vypis = " ";
		if(trefeno) {
		 
			vypis = "#";
			
		}else if (obsazeno) {
			
			vypis = tank.toString();
		}
		
		return vypis;
	}
	
	
	
	/** Getrs and Setrs  **/
	public boolean isTrefeno() {
		return trefeno;
	}


	public void setTrefeno(boolean trefeno) {
		this.trefeno = trefeno;
	}


	public boolean isObsazeno() {
		return obsazeno;
	}


	public void setObsazeno(boolean obsazeno) {
		this.obsazeno = obsazeno;
	}


	public Tank getTank() {
		return tank;
	}


	public void setTank(Tank tank) {
		this.tank = tank;
	}


	public IdPolicka getId() {
		return id;
	}


	public void setId(IdPolicka id) {
		this.id = id;
	}


	public int getOdecet() {
		return odecet;
	}


	public void setOdecet(int odecet) {
		this.odecet = odecet;
	}
	
}
