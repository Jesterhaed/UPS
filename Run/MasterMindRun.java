package Run;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Scanner;

import Control.Actions;
import Control.LogginLogics;
import Control.NetworkLogics;
import Control.UiCommObserver;
import Interfaces.ITCP;
import Network.TCPComm;

public class MasterMindRun{

	/**
	 * Metoda main Zavola launch a spusti program
	 * 
	 * @param args
	 */

	/** Atributy tridy **/
	private LogginLogics logLogics;
	private TCPComm comm;
	private ITCP tcp;
	private UiCommObserver m_commObserver;
	private NetworkLogics netLog;
	private boolean isServer = true;
	private Scanner sc;
	private Actions actions;
	
	public MasterMindRun() {
		
		this.logLogics = new LogginLogics(this);
		this.netLog = new NetworkLogics(this, logLogics);
		this.sc = new Scanner(System.in);
		this.actions = new Actions(this, logLogics, netLog);
		
		prihlaseni();
		createConnect();
		RegOrLog();
		
	}
	
	public void RegOrLog() {
		
		System.out.println("'Pro registraci zadej R, pro prihlaseni P");
		String volba = sc.nextLine();
		
		if(volba.equals("R")) {
			actions.registrace();
		}else if(volba.equals("P")) {
			actions.prihlaseni();
		}else {
			RegOrLog();
		}
			
		
	}

	public void prihlaseni() {
		
		System.out.println("'Zadej adresu serveru");
//		String addr = sc.nextLine();
		String addr = "127.0.0.1";
		System.out.println("'Zadej port serveru");
//		String port = sc.nextLine();
		String port = "22343";
		logLogics.confirmDataInServerForm(addr, port);
		
	}


	
	

	/**
	 * createConnect() Vytvori spojeni se serverem
	 * 
	 */
	public void createConnect() {
		try {

			comm = new TCPComm(logLogics.getServerAddres(), logLogics.getServerPort());

			m_commObserver = new UiCommObserver(this, logLogics, netLog, actions);
			comm.registerObserver(m_commObserver);

			logLogics.setComm(comm);
			netLog.setComm(comm);
		} catch (NumberFormatException e) {

			e.printStackTrace();
			System.out.println("Spatne");
		}
		comm.start();

	}

	
	/*** Setrs and Getrs ***/


	public LogginLogics getLogLogics() {
		return logLogics;
	}

	public void setLogLogics(LogginLogics logLogics) {
		this.logLogics = logLogics;
	}

	public ITCP getTcp() {
		return tcp;
	}

	public void setTcp(ITCP tcp) {
		this.tcp = tcp;
	}

	public TCPComm getComm() {
		return comm;
	}

	public void setComm(TCPComm comm) {
		this.comm = comm;
	}

	public UiCommObserver getM_commObserver() {
		return m_commObserver;
	}

	public void setM_commObserver(UiCommObserver m_commObserver) {
		this.m_commObserver = m_commObserver;
	}

	public NetworkLogics getNetLog() {
		return netLog;
	}

	public void setNetLog(NetworkLogics netLog) {
		this.netLog = netLog;
	}
	public boolean isServer() {
		return isServer;
	}

	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}

	public Scanner getSc() {
		return sc;
	}

	public void setSc(Scanner sc) {
		this.sc = sc;
	}
	

}
