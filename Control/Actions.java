package Control;

import java.util.Scanner;

import Run.MasterMindRun;

public class Actions {

	private MasterMindRun mMR;
	private LogginLogics logLogic;
	private Scanner sc;
	private NetworkLogics netLogic;
	
	private String name;
	
	
	public Actions(MasterMindRun mMR, LogginLogics logLogic, NetworkLogics netLogic) {
		this.mMR = mMR;
		this.logLogic = logLogic;
		this.sc = mMR.getSc();
		this.netLogic = netLogic;
		
	}
	
	
	public void registrace() {
		
		System.out.println("Zadej jmeno");
		String jmeno = sc.nextLine();
		System.out.println("Zadej heslo");
		String heslo1 = sc.nextLine();
		System.out.println("Zadej heslo znovu");
		String heslo2 = sc.nextLine();
		
		if(!logLogic.confirmDataInForm(jmeno, heslo1, heslo2)) {
			registrace();
		}
		
		netLogic.sendRegForm(jmeno, heslo1);
		
	}
	
public void prihlaseni() {
		
		System.out.println("Zadej jmeno");
		String jmeno = sc.nextLine();
		System.out.println("Zadej heslo");
		String heslo1 = sc.nextLine();	
		if(!logLogic.confirmDataInForm(jmeno, heslo1)) {
			prihlaseni();
		}
		name = jmeno;
		netLogic.sendSingForm(jmeno, heslo1);
		
	}


public String getName() {
	return name;
}


public void setName(String name) {
	this.name = name;
}





}
