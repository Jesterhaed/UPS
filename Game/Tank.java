package Game;

import Control.HraLogika;

public class Tank {

	private TypyTanku typ;
	private int HP;
	
	private HraLogika gameControl;
	private IdPolicka idPolicka;
	private boolean znicen = false;
	
	public Tank(HraLogika gameControl, TypyTanku typ) {
		this.typ = typ;
		this.gameControl = gameControl;
		this.HP = 100;
	}
	
	
	
	public void nastavTankNaPole(HraciPole pole, int x, int y) {
		
		if (typ.toString().equals("HT")){
			pole.getHraciPole()[x-2][y].addTank(this);
			pole.getHraciPole()[x-2][y].setOdecet(0);;
			pole.getHraciPole()[x-1][y].addTank(this);
			pole.getHraciPole()[x-1][y].setOdecet(25);;
			pole.getHraciPole()[x][y].addTank(this);
			pole.getHraciPole()[x][y].setOdecet(50);;
		}else if(typ.toString().equals("MT")){
			pole.getHraciPole()[x-1][y].addTank(this);
			pole.getHraciPole()[x][y].addTank(this);
			
			pole.getHraciPole()[x-1][y].setOdecet(25);;
			pole.getHraciPole()[x][y].setOdecet(75);;
			
		}else if(typ.toString().equals("TD")){
			pole.getHraciPole()[x-1][y].addTank(this);
			pole.getHraciPole()[x][y].addTank(this);
			
			pole.getHraciPole()[x-1][y].setOdecet(0);;
			pole.getHraciPole()[x][y].setOdecet(50);;
		
		}else if(typ.toString().equals("LT")){
			pole.getHraciPole()[x][y].addTank(this);
			pole.getHraciPole()[x][y].setOdecet(100);;
		}
	}
	
	
	
	@Override
	public String toString() {
		
		if (znicen) {
			return "X";
		}
		
		return typ.toString();
	}
	

	public TypyTanku getTyp() {
		return typ;
	}

	public void setTyp(TypyTanku typ) {
		this.typ = typ;
	}

	public int getHP() {
		return HP;
	}

	public void setHP(int hP) {
		HP = hP;
	}

	public HraLogika getGameControl() {
		return gameControl;
	}

	public void setGameControl(HraLogika gameControl) {
		this.gameControl = gameControl;
	}


	public IdPolicka getIdPolicka() {
		return idPolicka;
	}


	public void setIdPolicka(IdPolicka idPolicka) {
		this.idPolicka = idPolicka;
	}



	public boolean isZnicen() {
		return znicen;
	}



	public void setZnicen(boolean znicen) {
		this.znicen = znicen;
	}
}
