package Control;

import java.util.Scanner;

import Game.HraciPole;
import Game.Tank;
import Game.TypyTanku;
import Run.TanksRun;

public class HraLogika {

	public static final int velikostPole = 6;

	private TanksRun tR;
	private LogikaPrihlaseni logLogic;
	private Scanner sc;
	private SitLogika netLogic;
	private HraciPole hraciPole;

	private int zasazenychTanku = 0;

	private boolean vyzivatel = false;
	private boolean vyzivatelPoleReady = false;
	private boolean protivnikPoleReady = false;

	public HraLogika(TanksRun rR, LogikaPrihlaseni logLogic, SitLogika netLogic) {
		this.tR = rR;
		this.logLogic = logLogic;
		this.sc = new Scanner(System.in);
		this.netLogic = netLogic;
	}

	public void vynuluj_hru() {
		zasazenychTanku = 0;

		vyzivatel = false;
		vyzivatelPoleReady = false;
		protivnikPoleReady = false;
	}
	
	public void nastaveniHracihoPole(String jmeno, boolean vyzivatel) {

				
		this.hraciPole = new HraciPole(this, vyzivatel);
		this.setVyzivatel(vyzivatel);

		Tank HT = new Tank(this, TypyTanku.HT);
		Tank MT = new Tank(this, TypyTanku.MT);
		Tank TD = new Tank(this, TypyTanku.TD);
		Tank LT = new Tank(this, TypyTanku.LT);

		vypisUmisteniTanku("Zvolte umisteni pro tank HT na hraci pole, napriklad ve formatu 1;A ", HT);
	
		vypisUmisteniTanku("Zvolte umisteni pro tank MT na hraci pole, napriklad ve formatu 1;A ", MT);
		
		vypisUmisteniTanku("Zvolte umisteni pro tank TD na hraci pole, napriklad ve formatu 1;A ", TD);
		
		vypisUmisteniTanku("Zvolte umisteni pro tank LT na hraci pole, napriklad ve formatu 1;A ", LT);
		
		setPoleReady(vyzivatel);

		if (!vyzivatel) {
			netLogic.sendHraciPoleReady();
		}

	}

	public void setPoleReady(boolean protivnik) {

		if (!protivnik) {
			protivnikPoleReady = true;
//			System.out.println("Nastaveni ready protivnik " + protivnik);
		} else {
			vyzivatelPoleReady = true;

//			System.out.println("Nastaveni ready vyzivatel " + protivnik);
		}

//		System.out.println("Pripravena " + protivnikPoleReady + " " + vyzivatelPoleReady);

		if (protivnikPoleReady && vyzivatelPoleReady) {

//			System.out.println("Pripravena " + protivnik);
			netLogic.sendHraPripravena();
			udelejTah();
		}
		else {
//			System.out.println("Vsechny tanky rozmisteny, pockejte na zapoceti hry.");
		}
	}

	public void udelejTah() {
		System.out.println("Zvol policko pro strelbu:");
		String tah = sc.nextLine();
		
		int x = 0;
		int y = 0;

		String[] umisteni2 = zpracujUmisteni(tah);

		if (umisteni2.length < 2) {
			
			System.out.println("Spatne zadane souradnice.");
			udelejTah();
			return;
		
		}
		
		
		try {

			x = Integer.parseInt(umisteni2[0]) - 1;
			y = prevedPismenoNaInt(umisteni2[1]);
			
		} catch (NumberFormatException e) {
			System.out.println("Spatne zadane souradnice.");
			udelejTah();
			return;
		}

		if (y == -1 || y > 5 || x > 5 || x < 0) {

			System.out.println("Spatne zadane souradnice.");
			udelejTah();
			return;
			
		} else {

			netLogic.sendTah(tah);
		}

	}

	private void vypisUmisteniTanku(String hlaska, Tank tank) {

		System.out.println(hlaska);
		hraciPole.vypisPole();

		String umisteni = sc.nextLine();
		netLogic.checkConnect();
		int x = 0;
		int y = 0;
		String[] umisteni2 = zpracujUmisteni(umisteni);

		if (umisteni2.length < 2) {
			vypisUmisteniTanku(hlaska, tank);
			return;
		}
		
		try {

			x = Integer.parseInt(umisteni2[0]) - 1;
			y = prevedPismenoNaInt(umisteni2[1]);
		} catch (NumberFormatException e) {
			System.out.println("Spatne zadane souradnice.");
			vypisUmisteniTanku(hlaska, tank);
			return;
		}
		
		
		if (y == -1 || y > 5 || x > 5 || x < 0) {

			System.out.println("Spatne zadane souradnice.");
			vypisUmisteniTanku(hlaska, tank);
			return;
		} 
		
		
		if (!zkontrolujUmisteniTanku(tank, x, y)) {
			vypisUmisteniTanku(hlaska, tank);
			return;
		}

		tank.nastavTankNaPole(hraciPole, x, y);


		

	}

	private boolean zkontrolujUmisteniTanku(Tank tank, int x, int y) {
		
		if (tank.toString().equals(TypyTanku.HT.toString())) {
			if ((x-2) < 0 ) {
				System.out.println("Zde dany tank nemuze byt umisten.");
				return false;
			}
						
			}else if(y == -1){
			System.out.println("Spatne zadane souradnice.");
			return false;
		
		}if (tank.toString().equals(TypyTanku.LT.toString())) {
		
			
			if(hraciPole.getHraciPole()[x][y].isObsazeno()) {
				System.out.println("Na danych souradnicich uz je umisten jiny tank.");
				return false;
			}
			
			
		}else if (tank.toString().equals(TypyTanku.MT.toString())) {
			if ((x-1) < 0 ) {
				System.out.println("Zde dany tank nemuze byt umisten.");
				return false;
			}else if(hraciPole.getHraciPole()[x][y].isObsazeno()) {
				System.out.println("Na danych souradnicich uz je umisten jiny tank.");
				return false;
			}else if(hraciPole.getHraciPole()[x-1][y].isObsazeno()) {
				System.out.println("Na danych souradnicich uz je umisten jiny tank");
				return false;
			}
			
		}else if (tank.toString().equals(TypyTanku.TD.toString())) {
			if ((x-1) < 0 ) {
				System.out.println("Zde dany tank nemuze byt umisten.");
				return false;
			}else if(hraciPole.getHraciPole()[x][y].isObsazeno()) {
				System.out.println("Na danych souradnicich uz je umisten jiny tank.");
				return false;
			}else if(hraciPole.getHraciPole()[x-1][y].isObsazeno()) {
				System.out.println("Na danych souradnicich uz je umisten jiny tank.");
				return false;
			}
		}
		
		return true;
	}

	public int prevedPismenoNaInt(String pismeno) {

		switch (pismeno) {
		case "A":
			return 0;
		case "B":
			return 1;
		case "C":
			return 2;
		case "D":
			return 3;
		case "E":
			return 4;
		case "F":
			return 5;
		default:
			break;
		}

		return -1;
	}

	public String[] zpracujUmisteni(String umisteni) {

		return umisteni.split(";");

	}

	public void vyhodnotTah(String tah) {

		int x = 0;
		int y = 0;

		String[] umisteni2 = zpracujUmisteni(tah);
		x = Integer.parseInt(umisteni2[0]) - 1;

		y = prevedPismenoNaInt(umisteni2[1]);

		if (!hraciPole.getHraciPole()[x][y].isObsazeno()) {
			System.out.println("Souper se netrefil.");
			
			hraciPole.getHraciPole()[x][y].setTrefeno(true);
			netLogic.sendMiss();
			
		} else {

			vyhodnotZasah(hraciPole.getHraciPole()[x][y].getOdecet(), hraciPole.getHraciPole()[x][y].getTank());
		}

	}

	private void vyhodnotZasah(int odecet, Tank tank) {

		int zivoty = tank.getHP() - odecet;
		tank.setHP(zivoty);
		if (zivoty > 0) {
			System.out.println("Byl trefen vas " + tank.toString() + ", jeho zbyvajici zivoty: " + zivoty);
		}
		else if (zivoty == 0) {
			System.out.println("Souper znicil vas " + tank.toString());
		}
		else {
			System.out.println("Souper strelil do zniceneho tanku.");
		}
		

		if (odecet == 75 && zivoty < 0) {
			zivoty = 0;
		}
		
		if (zivoty > 0) {
			netLogic.sendTrefa(odecet);
		} else if (zivoty == 0 && !tank.isZnicen()) {
			netLogic.sendZniceno(tank.getTyp().toString());
			tank.setZnicen(true);
			int tanku = hraciPole.getTankuVeHre() - 1;
			hraciPole.setTankuVeHre(tanku);

			if (tanku == 0) {
				System.out.println("Jenam lito, ale prohral jste.");
				vynuluj_hru();
				System.out.println();
				System.out.println("Pockejte na volneho hrace.");
			}

		} else {
			netLogic.sendMiss();
		}

	}

	public HraciPole getHraciPole() {
		return hraciPole;
	}

	public void setHraciPole(HraciPole hraciPole) {
		this.hraciPole = hraciPole;
	}

	public int getZasazenychTanku() {
		return zasazenychTanku;
	}

	public void setZasazenychTanku(int zasazenychTanku) {
		this.zasazenychTanku = zasazenychTanku;
	}

	public boolean isVyzivatel() {
		return vyzivatel;
	}

	public void setVyzivatel(boolean vyzivatel) {
		this.vyzivatel = vyzivatel;
	}

}
