package Control;


import Interfaces.ICommObserver;
import Run.MasterMindRun;

public class UiCommObserver implements ICommObserver {

	/** Globani promenne tridy **/
	
	private MasterMindRun mMR;
	private LogginLogics lLog;
	
	private NetworkLogics netLog;
	private MessageControl messageCon;
	private Actions action; 
	/**
	 * Inicializace objektu mMR, lLog, netLog
	 * 
	 * @param mMR
	 * @param lLog
	 * @param netLog
	 */
	public UiCommObserver(MasterMindRun mMR, LogginLogics lLog, NetworkLogics netLog, Actions action) {
		this.mMR = mMR;
		this.lLog = lLog;
		this.netLog = netLog;
		this.messageCon = new MessageControl();
		this.action = action;
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
				//	serW.getWaitLB().setVisible(true);
				//	serW.getConfirmBT().setDisable(true);
					break;
				case "NoServer":
				//	mMR.showNoServer();
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
					//mMR.showReloadGameMesage(Integer.parseInt(pomData[1]), pomData[2], challenger);
					break;
				case "Registrace":
					receiveRegistrace(pomData);
					break;
				case "Log":

						if (pomData[1].contains("yes")) {
							lLog.setLog(true);
							netLog.setName(action.getName());
							System.out.println("Vitej ve hre hraci " + action.getName());
							System.out.println("Ceka se na pripojeni dalsiho hrace.");
							
							//mMR.setWellcomeWindow();
							// Dalsi krok po loginu 
							
							
							
						} else if (pomData[1].contains("no") && pomData[2].contains("badLog")) {
							System.out.println("This nickname is not using");
							action.prihlaseni();
						} else {
							System.out.println("Bad password");
							action.prihlaseni();
						}

						
					

					break;
				case "PlayerList":
					if (pomData.length > 1) {
					//	freePlayerL.getListLV().setItems(netLog.creatPlayersList(pomData[1]));
					} else {
					//	freePlayerL.getListLV().setItems(netLog.creatPlayersList(""));
					}

					break;
				case "Logout":

					//mMR.showLogoutMessage(netLog.getPlayerName(), Integer.parseInt(pomData[1]));

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
	
		
	/*	if (pomData[1].contains("leave")) {
			mMR.showLeaveMessage(pomData[2], Integer.parseInt(pomData[3]));

		} else if (pomData[1].contains("colorResult")) {

			if (pomData[2].contains("R") && netLog.isChallenger()) {
				netLog.setResultR(pomData[3]);
				multiM.getObserText().inc("Find color combination");

			} else if (pomData[2].contains("R") && !netLog.isChallenger()) {

				netLog.setResult(pomData[3]);
				multiM.getObserText().inc("Challenger find combination");

			} else {
				netLog.setResult(pomData[2]);
				multiM.getObserText().inc("Find color combination");
			}

		} else if (pomData[1].contains("goodColors")) {

			netLog.setGoodColor(Integer.parseInt(pomData[3]), Integer.parseInt(pomData[2]));

		} else if (pomData[1].contains("greatColors")) {

			netLog.setGreatColor(Integer.parseInt(pomData[3]), Integer.parseInt(pomData[2]));

		} else if (pomData[1].contains("knobPanel")) {

			netLog.setKnobPanel(Integer.parseInt(pomData[2]), pomData[3]);

		} else if (pomData[1].contains("gameOver")) {

			multiM.getObserText().inc("Challenger had failed");

		} else if (pomData[1].contains("gameDone")) {

			multiM.getObserText().inc("Challenger had succeeded");

		} else if (pomData[1].contains("player")) {

			netLog.setPlayerName(pomData[3]);

		} else if (pomData[1].contains("colorAccept")) {

			netLog.messageAccepted();

		}
*/
		}


	/**
	 * Pomocna metoda pro zpracovani zprava o vyzve
	 * 
	 * @param pomData
	 */
	private void receiveChallenge(String[] pomData) {
	/*	if (pomData[2].contains("invite")) {

			netLog.createChallengeMesagge(pomData[1]);

		} else if (pomData[2].contains("refuse")) {

			mMR.showRefusetMessage(pomData[1]);

		}else if(pomData[1].contains("messageAccept")){
			netLog.invateMessageAccept();
		}else {

			mMR.showAcceptMessage(pomData[1]);
			netLog.setChallenger(true);

			mMR.setGameWindowMultiMode();
			multiM.getObserText().inc("Wait for color combination");

		}
*/
	}

	/*************** Getrs and Setrs ******************/



}
