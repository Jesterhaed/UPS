package Control;

import java.util.ArrayList;

import Interfaces.IKomunikaceServeru;
import Run.TanksRun;

public class KomukiaceServeru implements IKomunikaceServeru {

	/** Globani promenne tridy **/

	private TanksRun tR;
	private LogikaPrihlaseni logLogic;

	private SitLogika netLogic;
	private KontrolaZprav messageCon;
	private PrihlaseniKl action;
	private HraLogika gameControl;

	/**
	 * Inicializace objektu tR, lLog, netLog
	 * 
	 * @param tR
	 * @param logLogic
	 * @param netLogic
	 */
	public KomukiaceServeru(TanksRun tR, LogikaPrihlaseni logLogic, SitLogika netLogic, PrihlaseniKl action,
			HraLogika gameConrol) {
		this.tR = tR;
		this.logLogic = logLogic;
		this.netLogic = netLogic;
		this.action = action;
		this.gameControl = gameConrol;
		this.messageCon = new KontrolaZprav(gameConrol);
	}

	/**
	 * Metoda pro zpracovani prijatych zprav ze serveru
	 */
	public void processData(String data) {

//		System.out.println("Data: " + data + " : " + netLog.getName());

		if (!messageCon.is_valid(data)) {

			System.out.println("Nevalidni vstup!");
			return;
		}

		String[] pomData = data.split(",");

		switch (pomData[0]) {

		case "Nevalidni vstup":
			System.out.println("Odeslana nevalidni zprava.");
			break;
		case "NoServer":
			System.out.println("Spojeni se serverem ztraceno.");
			break;
		case "CheckConnect":
			netLogic.checkConnect();
			break;
		case "Connect":
			System.out.println("Navazano spojeni se serverem.");
			tR.regOrLog();
			break;
		case "Registrace":
			receiveRegistrace(pomData);
			break;
		case "Log":

			if (pomData[1].contains("yes")) {
				logLogic.setLog(true);
				netLogic.setName(action.getName());
				System.out.println("Vitej ve hre hraci " + action.getName());
				
				netLogic.getFreePlayerList();
				
				
			} else if (pomData[1].contains("no") && pomData[2].contains("badLog")) {
				System.out.println("Tato prezdivka neni zaregistrovana.");
				tR.regOrLog();
			} else {
				System.out.println("Spatne heslo.");
				tR.regOrLog();
			}

			break;
		case "PlayerList":

			if (pomData.length > 1) {
				vypisHrace(pomData);				
			} else {
				System.out.println("Musite pockat na volneho hrace...");
			}

			break;
		case "Logout":
			System.out.println("Hrac" + netLogic.getName() +  "opustil hru");			
			netLogic.deleteGameLeave(Integer.parseInt(pomData[1]));
			
			System.out.println("Budete presmerovan na vyber jineho hrace.");
			System.out.println("Pro ukonceni stisknete Ctrl + C.");
			
			netLogic.getFreePlayerList();
			
			break;
		case "Challenge":
			receiveChallenge(pomData);

			break;
		case "Game":
			receive_game(pomData);

			break;
		default:
			break;
		}
	}

	private void vypisHrace(String[] pomData) {
		System.out.println("Pro volbu hrace zvolte cislo.");
		ArrayList<String> hraci = netLogic.creatPlayersList(pomData[1]);

		for (int i = 0; i < hraci.size(); i++) {
			System.out.println(i + " pro hrace: " + hraci.get(i));

		}

		int pom = 0;
		String volba = tR.getSc().next();
		try {
			pom = 	Integer.parseInt(volba);
		} catch (NumberFormatException e) {
			
			System.out.println("Pro volbu hrace musite stisknout cislo!");
			vypisHrace(pomData);
			return;
		}
		
		if (pom > (hraci.size()-1)) {
			vypisHrace(pomData);
			return;
		}
		
		netLogic.createGame(hraci.get(pom));

		
	}

	/**
	 * Pomocna metoda pro zpracovani zprav o registraci
	 * 
	 * @param pomData
	 */
	private void receiveRegistrace(String[] pomData) {

		if (pomData[1].contains("bad")) {
			System.out.println("Tato prezdivka je pouzita.");
			System.out.println("Vyberte si jiny nick!");
			tR.regOrLog();
		} else if (pomData[1].contains("bad2")) {
			System.out.println("Tato prezdivka je prilis dlouha!");
			System.out.println("Zvolte nick do triceti znaku.");
			tR.regOrLog();
		} else {

			System.out.println("Jste registrovan");
			tR.regOrLog();
		}

	}

	/**
	 * Pomocna metoda pro zpracovani zprav o hre
	 * 
	 * @param pomData
	 */
	private void receive_game(String[] pomData) {

	
		if (pomData[1].contains("poleReady")) {
			gameControl.setPoleReady(false);
			
		} else if (pomData[1].contains("pripravena")) {

			System.out.println("Hraje vyzivatel, pockejte na jeho tah.");
		
		} else if (pomData[1].contains("tah")) {

			gameControl.vyhodnotTah(pomData[2]);

		} else if (pomData[1].contains("trefa")) {
		
			System.out.println("Trefen tank, ubrano " + pomData[2] + " HP.");
			System.out.println("Na tahu je souper.");
			netLogic.sendHraj();
		
		} else if (pomData[1].contains("zniceno")) {
			
			System.out.println("Znicen tank: " + pomData[2]);
		
			gameControl.setZasazenychTanku(gameControl.getZasazenychTanku()+1);
			
			if (gameControl.getZasazenychTanku() == 4) {
				System.out.println("Gratuluju vyhral jste!!!");
				
				netLogic.sendEndGame();
				gameControl.vynuluj_hru();
				System.out.println("Vyberete protihrace.");
				netLogic.getFreePlayerList();
			}else {
				System.out.println("Hraje protihrac...");			
				netLogic.sendHraj();				
			}
			

		
		} else if (pomData[1].contains("miss")) {
			
			System.out.println("Bohuzel nebylo nic trefeno :(");
			System.out.println("Na tahu je souper.");
			netLogic.sendHraj();
		
		} else if (pomData[1].contains("hraj")) {
	
			gameControl.udelejTah();
			
		}
	}

	/**
	 * Pomocna metoda pro zpracovani zprava o vyzve
	 * 
	 * @param pomData
	 */
	private void receiveChallenge(String[] pomData) {
		if (pomData[2].contains("invite")) {

			action.provedAkciNaPozvani(pomData[1]);

		} else if (pomData[2].contains("refuse")) {

			System.out.println("Proti hrac odmitl hru");
			netLogic.getFreePlayerList();

		} else if (pomData[1].contains("messageAccept")) {
			// System.out.println("Protihrac prijal hru.");

		} else {

			System.out.println("Zacina hra s hracem " + pomData[1]);
			netLogic.setChallenger(true);
			gameControl.nastaveniHracihoPole(action.getName(), true);

		}

	}


}
