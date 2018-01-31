package Game;

//import com.sun.org.apache.xml.internal.security.encryption.AgreementMethod;

import Control.HraLogika;

public class HraciPole {
	
	
	private HraciPolicko hraciPole[][];	
	private Hrac vyzivatel;
	private Hrac protihrac;
	private int tankuVeHre = 4;
	
	private HraLogika gameControl;
	
	public HraciPole(HraLogika gameControl, boolean vyzivatelovo){
		
		this.hraciPole = new HraciPolicko[HraLogika.velikostPole][HraLogika.velikostPole];
		
		this.gameControl = gameControl;
		this.vyzivatel = vyzivatel;
		this.protihrac = protihrac;
		
		initPole();
	}
	
	
	private void initPole() {
		
		for (int i = 0; i < gameControl.velikostPole; i++) {
		
			for (int j = 0; j < gameControl.velikostPole; j++) {
				
				hraciPole[i][j] = new HraciPolicko(gameControl, new IdPolicka(j, i));
				
			}
			
		}
	}
	
	
	
	public void vypisPole() {
		
		for (int i = 0; i < gameControl.velikostPole; i++) {
			System.out.println("---------------------");	
			System.out.print((i+1));
			for (int j = 0; j < gameControl.velikostPole; j++) {
				
				System.out.print(" |" + hraciPole[i][j] );
				
			}
			System.out.print(" |");
			System.out.println();
	
		}
		System.out.println("    A| B| C| D| E| F ");
	}
	
	
	
	

	public HraciPolicko[][] getHraciPole() {
		return hraciPole;
	}

	public void setHraciPole(HraciPolicko hraciPole[][]) {
		this.hraciPole = hraciPole;
	}


	public int getTankuVeHre() {
		return tankuVeHre;
	}


	public void setTankuVeHre(int tankuVeHre) {
		this.tankuVeHre = tankuVeHre;
	}
	
	

}
