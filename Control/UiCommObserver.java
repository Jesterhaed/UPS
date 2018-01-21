package Control;

import java.util.ArrayList;

import Interfaces.ICommObserver;
import Run.MasterMindRun;

public class UiCommObserver implements ICommObserver {

	/** Globani promenne tridy **/

	private MasterMindRun mMR;
	private LogginLogics lLog;

	private NetworkLogics netLog;
	private MessageControl messageCon;
	private Actions action;
	private GameControl gameControl;

	/**
	 * Inicializace objektu mMR, lLog, netLog
	 * 
	 * @param mMR
	 * @param lLog
	 * @param netLog
	 */
	public UiCommObserver(MasterMindRun mMR, LogginLogics lLog, NetworkLogics netLog, Actions action,
			GameControl gameConrol) {
		this.mMR = mMR;
		this.lLog = lLog;
		this.netLog = netLog;
		this.messageCon = new MessageControl();
		this.action = action;
		this.gameControl = gameConrol;
	}

	/**
	 * Metoda pro zpracovani prijatych zprav ze serveru
	 */
	public void processData(String data) {

		System.out.println("Data " + data + " " + netLog.getName());

		if (!messageCon.is_valid(data)) {

			System.out.println("Invalid input ");
			return;
		}

		String[] pomData = data.split(",");

		switch (pomData[0]) {

		case "Nevalidni vstup":
			System.out.println("Odeslana nevalidni zprava");
			break;
		case "Wait":
			// serW.getWaitLB().setVisible(true);
			// serW.getConfirmBT().setDisable(true);
			break;
		case "NoServer":
			// mMR.showNoServer();
			break;
		case "CheckConnect":
			netLog.checkConnect();
			break;
		case "Connect":
			System.out.println("Navazano spojeni se serverem.");
			break;

		case "Reload":
			boolean challenger;
			if (pomData[3].equals("0")) {
				challenger = false;
			} else {
				challenger = true;
			}
			// mMR.showReloadGameMesage(Integer.parseInt(pomData[1]), pomData[2],
			// challenger);
			break;
		case "Registrace":
			receiveRegistrace(pomData);
			break;
		case "Log":

			if (pomData[1].contains("yes")) {
				lLog.setLog(true);
				netLog.setName(action.getName());
				System.out.println("Vitej ve hre hraci " + action.getName());
				netLog.getFreePlayerList();
			} else if (pomData[1].contains("no") && pomData[2].contains("badLog")) {
				System.out.println("This nickname is not using");
				action.prihlaseni();
			} else {
				System.out.println("Bad password");
				action.prihlaseni();
			}

			break;
		case "PlayerList":

			System.out.println(pomData.toString() + "Pom data");

			if (pomData.length > 1) {
				System.out.println("Pro volbu hrace zvolte cislo");
				ArrayList<String> hraci = netLog.creatPlayersList(pomData[1]);

				for (int i = 0; i < hraci.size(); i++) {
					System.out.println(i + " pro " + hraci.get(i));

				}

				int pom = mMR.getSc().nextInt();

				netLog.createGame(hraci.get(pom));

			} else {
				System.out.println("Musite pockat na volneho hrace...");
			}

			break;
		case "Logout":

			// mMR.showLogoutMessage(netLog.getPlayerName(), Integer.parseInt(pomData[1]));

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

	/**
	 * Pomocna metoda pro zpracovani zprav o registraci
	 * 
	 * @param pomData
	 */
	private void receiveRegistrace(String[] pomData) {

		if (pomData[1].contains("bad")) {
			System.out.println("This nickname is using");

		} else if (pomData[1].contains("bad2")) {

			System.out.println("This nickname is long, length must be less 30 and must not be 0");

		} else {

			System.out.println("Jste registrovan");
			mMR.RegOrLog();
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

			System.out.println("Hraje vyzivatel, pockejte na jeho tah");
		
		} else if (pomData[1].contains("tah")) {

			gameControl.vyhodnotTah(pomData[2]);

		} else if (pomData[1].contains("trefa")) {
		
			System.out.println("Trefen tank odecet " + pomData[2]);
			System.out.println("Hraje protihrac...");
			netLog.sendHraj();
		
		} else if (pomData[1].contains("zniceno")) {
			System.out.println("Znicen tank " + pomData[2]);
			System.out.println(gameControl.getHraciPole().getTankuVeHre() + "Tanku");
			gameControl.setZasazenychTanku(gameControl.getZasazenychTanku()+1);
			
			if (gameControl.getZasazenychTanku() == 4) {
				System.out.println("Gratuluju vyhral jste!!!");
			}else {
				System.out.println("Hraje protihrac...");			
				netLog.sendHraj();				
			}
			

		
		} else if (pomData[1].contains("miss")) {
			
			System.out.println("Bohuzel nebylo nic trefeno");
			System.out.println("Hraje protihrac...");
			netLog.sendHraj();
		
		} else if (pomData[1].contains("hraj")) {
	
			gameControl.udelejTah();
		}else if (pomData[1].contains("gameDone")) {
			
			System.out.println("Gratuluju vyhral jste!!!");
			netLog.sendGameOver();
		
		}else if (pomData[1].contains("gemeOver")) {
	
		System.out.println("Jenam lito protivnik vyhral");
		
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
			netLog.getFreePlayerList();

		} else if (pomData[1].contains("messageAccept")) {
			// System.out.println("Proti hrac prijal hru");

		} else {

			System.out.println("Zacina hra s hracem " + pomData[1]);
			netLog.setChallenger(true);
			gameControl.nastaveniHracihoPole(action.getName(), true);

		}

	}

	/*************** Getrs and Setrs ******************/

}
