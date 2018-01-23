package Control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Network.TCPComm;
import Run.MasterMindRun;

public class NetworkLogics {

	/** Globalni promenne tridy **/
	private TCPComm comm;
	private MasterMindRun mMR;
	private String playerName;
	private String name;
	private boolean challenger;

	private int[] colorsIndex;
	private int checkIndex;
	private int greatColors = 0;
	private int catchColors = 0;
	private int goodColors = 0;
	private int indexButton;
	private LogginLogics lLog;
	private int countBackPanel = 0;

	/**
	 * Inicializace objektu mMR a lLog
	 * 
	 * @param mMR
	 * @param lLog
	 */
	public NetworkLogics(MasterMindRun mMR, LogginLogics lLog) {

		this.mMR = mMR;
		this.setChallenger(true);
		this.setlLog(lLog);

		
	}

	/**
	 * Odesle zadost o ziskani uzivatelu pro dalsi hru
	 */
	public void getFreePlayerList() {

		comm.send("PlayerList,get,\n");

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
		comm.send("Challenge,invite," + playerName + ",\n");
	}

	/**
	 * Zprava o prijeti vyzvy
	 * 
	 * @param player
	 */
	public void challengeAccepted(String player) {

		comm.send("Challenge,accept," + player + ",\n");
	}

	/**
	 * Zprava o odmituti vyzvy
	 * 
	 * @param player
	 */
	public void challengeRefuse(String player) {

		comm.send("Challenge,refuse," + player + ",\n");

	}

	/**
	 * Zprava o opusteni hry
	 */
	public void leaveGame() {

		comm.send("Game,leave," + name + ",\n");

	}

	/**
	 * Zprava o prohre vyzivatele
	 */
	public void sendGameOver() {

		comm.send("Game,gameOver,\n");

	}

	/**
	 * Zprava o vyhre vyzivatele
	 */
	public void sendGameDone() {

		comm.send("Game,gameDone,\n");
	}

	public void sendHraciPoleReady() {

		comm.send("Game,poleReady,\n");
	}

	public void sendTah(String umisteni) {

		comm.send("Game,tah," + umisteni + ",\n");
	}

	public void sendHraPripravena() {

		comm.send("Game,pripravena,\n");

	}
	
	public void sendTrefa(int odecet) {
		comm.send("Game,trefa," + odecet + ",\n");
		
	}

	public void sendZniceno(String tank) {
		comm.send("Game,zniceno," + tank + ",\n");
		
	}
	
	public void sendHraj() {
		comm.send("Game,hraj,\n");
		
	}
		
	public void sendMiss() {
		comm.send("Game,miss,\n");
		
	}

	/**
	 * Odesle zpravu s informaci o smazani hry ze serveru
	 * 
	 * @param game
	 */
	public void deleteGame(int game) {
		comm.send("DeleteGame," + game + ",\n");

	}

	/**
	 * Odesle zpravu s informaci o smazani hry ze serveru
	 * 
	 * @param game
	 */
	public void deleteGameLeave(int game) {
		comm.send("DeleteGame," + game + ",both\n");

	}

	public void sendSingForm(String nickname, String passwd) {
		comm.send(lLog.createLogMessage(nickname, mMR.getLogLogics().hashPassword(passwd)));

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

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
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

	public int getIndexButton() {
		return indexButton;
	}

	public void setIndexButton(int indexButton) {
		this.indexButton = indexButton;
	}

	public LogginLogics getlLog() {
		return lLog;
	}

	public void setlLog(LogginLogics lLog) {
		this.lLog = lLog;
	}

	

	

	

}
