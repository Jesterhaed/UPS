package Control;

import java.util.Scanner;

import Run.TanksRun;

public class PrihlaseniKl {

	private TanksRun tR;
	private LogikaPrihlaseni logLogic;
	private Scanner sc;
	private SitLogika netLogic;
	
	private String name;
	private HraLogika gameControl;
	
	public PrihlaseniKl(TanksRun tR, LogikaPrihlaseni logLogic, SitLogika netLogic, HraLogika gameControl) {
		this.tR = tR;
		this.logLogic = logLogic;
		this.sc = tR.getSc();
		this.netLogic = netLogic;
		this.gameControl = gameControl;
		
	}
	
	
	public void registrace() {
		
		System.out.println("Zadej jmeno");
		String jmeno = sc.nextLine();
		System.out.println("Zadej heslo");
		String heslo1 = sc.nextLine();
		System.out.println("Zadej heslo znovu");
		String heslo2 = sc.nextLine();
		
		if(!logLogic.confirmDataInForm(jmeno, heslo1, heslo2)) {
			tR.regOrLog();
			return;
		}
		
		netLogic.sendRegForm(jmeno, heslo1);
	}
	
public void prihlaseni() {
		
		System.out.println("Zadej jmeno");
		String jmeno = sc.nextLine();
		System.out.println("Zadej heslo");
		String heslo1 = sc.nextLine();	
		if(!logLogic.confirmDataInForm(jmeno, heslo1)) {
			tR.regOrLog();
			return;
		}
		name = jmeno;
		netLogic.sendSingForm(jmeno, heslo1);
		
	}

public void provedAkciNaPozvani(String jmeno) {
	
	System.out.println("Hrac " + jmeno + " vas pozval do hry.");
	System.out.println("Pro prijeti zmacknete 1, pro odmitnuti 2.");

	int volba = 0;

	
	try {
		volba = sc.nextInt();
		
	} catch (Exception e) {
		System.out.println("Pro potvrzeni volby musite stisknout cislo.");
		sc.nextLine();
		provedAkciNaPozvani(jmeno);
		return;
	}
	
	if (volba == 1) {
		netLogic.challengeAccepted(jmeno);
		gameControl.nastaveniHracihoPole(jmeno, false);
		
	}else if(volba == 2) {
		netLogic.challengeRefuse(jmeno);
	}else {
		provedAkciNaPozvani(jmeno);
		return;
	}
	
}


public String getName() {
	return name;
}


public void setName(String name) {
	this.name = name;
}

}
