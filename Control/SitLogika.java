package Control;

import java.util.ArrayList;

import Network.TCPComm;
import Run.TanksRun;

public class SitLogika {

	/** Globalni promenne tridy **/
	private TCPComm comm;
	private TanksRun tR;
	private String name;
	private boolean challenger;

	private LogikaPrihlaseni lLog;

	/**
	 * Inicializace objektu tR a lLog
	 * 
	 * @param tR
	 * @param lLog
	 */
	public SitLogika(TanksRun tR, LogikaPrihlaseni lLog) {

		this.tR = tR;
		this.setChallenger(true);
		this.setlLog(lLog);

		
	}

	/**
	 * Odesle zadost o ziskani uzivatelu pro dalsi hru
	 */
	public void getFreePlayerList() {

		comm.send("PlayerList,get\n");

	}
	
	
	public void ukonciSpojeniSChybnymServerem() {
		
		System.out.println(KomukiaceServeru.failCount + " chybnych zprav od serveru, ukoncuji klienta.");
		//comm.endConection();
		System.exit(0);
		//tR.prihlaseni();
	}

	/**
	 * Odesle zpravu s informaci o odhlaseni uzivatel
	 * 
	 * @param message
	 */
	public void signOutUser(String message) {

		lLog.setLog(false);
		comm.send(message);		
	}

	/**
	 * Vytvori seznam prihlasenych uzivatelu se kterymi je mozne hrat
	 * 
	 * @param mesagge
	 * @return
	 */
	public ArrayList<String> creatPlayersList(String mesagge) {

		String[] players = mesagge.split(";");

		ArrayList<String> data = new ArrayList<>();

		
		for (int i = 0; i < players.length; i++) {
			data.add(players[i]);
		}

		return data;
	}

	/**
	 * Odesle zpravu s informaci o zadost pro pripojeni daneho hrace do hry
	 * 
	 * @param playerName
	 */
	public void createGame(String playerName) {
		comm.send("Challenge,invite," + playerName + "\n");
	}

	/**
	 * Zprava o prijeti vyzvy
	 * 
	 * @param player
	 */
	public void challengeAccepted(String player) {

		comm.send("Challenge,accept," + player + "\n");
	}

	/**
	 * Zprava o odmituti vyzvy
	 * 
	 * @param player
	 */
	public void challengeRefuse(String player) {

		comm.send("Challenge,refuse," + player + "\n");

	}

	public void sendHraciPoleReady() {
		comm.send("Game,opponent,ready\n");
	}

	public void sendTah(String umisteni) {
		comm.send("Game,shot," + umisteni + "\n");
	}

	public void sendHraPripravena() {
		comm.send("Game,challenger,ready\n");
	}
	
	public void sendTrefa(int odecet) {
		comm.send("Game,hit," + odecet + "\n");
	}

	public void sendZniceno(String tank) {
		comm.send("Game,destroyed," + tank + "\n");
	}
	
	public void sendHraj() {
		comm.send("Game,play,game\n");
	}
		
	public void sendMiss() {
		comm.send("Game,miss,tank\n");
	}
	
	public void sendEndGame() {
		comm.send("Game,end,game\n");
	}

	/**
	 * Odesle zpravu s informaci o smazani hry ze serveru
	 * 
	 * @param game
	 */
	public void deleteGameLeave(int game) {
		comm.send("DeleteGame," + game + "\n");

	}

	public void sendSingForm(String nickname, String passwd) {
		comm.send(lLog.createLogMessage(nickname, tR.getLogLogics().hashPassword(passwd)));

	}

	public void sendRegForm(String jmeno, String heslo1) {
		comm.send(lLog.createRegMessage(jmeno, heslo1));
	}

	/**
	 * Vynulovani pocitadla pro timeouty
	 */
	public void checkConnect() {

		comm.setCounterTimeOUt(0);

	}

	/*****************************************
	 * Getrs and Setrs
	 *******************/

	public TCPComm getComm() {
		return comm;
	}

	public void setComm(TCPComm comm) {
		this.comm = comm;
	}
	
	public boolean isChallenger() {
		return challenger;
	}

	public void setChallenger(boolean challenger) {
		this.challenger = challenger;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public LogikaPrihlaseni getlLog() {
		return lLog;
	}

	public void setlLog(LogikaPrihlaseni lLog) {
		this.lLog = lLog;
	}
}
