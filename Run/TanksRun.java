package Run;

import java.util.Scanner;

import Control.PrihlaseniKl;
import Control.HraLogika;
import Control.LogikaPrihlaseni;
import Control.SitLogika;
import Control.KomukiaceServeru;
import Interfaces.ITCP;
import Network.TCPComm;

public class TanksRun{

	/**
	 * Metoda main Zavola launch a spusti program
	 * 
	 * @param args
	 */

	/** Atributy tridy **/
	private LogikaPrihlaseni logLogics;
	private TCPComm comm;
	private ITCP tcp;
	private KomukiaceServeru m_commObserver;
	private SitLogika netLog;
	private boolean isServer = true;
	private Scanner sc;
	private PrihlaseniKl actions;
	private HraLogika gameControl;
	
	public TanksRun() {
		
		   Runtime.getRuntime().addShutdownHook(new Thread()
	        {
	            @Override
	            public void run()
	            {
	                System.out.format("\nUkonceni programu.");
	                if (logLogics.isLog()) {
						netLog.signOutUser("LogOut,end\n");
					}
	                comm.endThread();
	            }	            
	        });
		
		this.logLogics = new LogikaPrihlaseni(this);
		this.netLog = new SitLogika(this, logLogics);
		this.sc = new Scanner(System.in);
		this.gameControl = new HraLogika(this, logLogics, netLog);
		this.actions = new PrihlaseniKl(this, logLogics, netLog, gameControl);
		prihlaseni();
		createConnect();
	}
	
	
	
	public void regOrLog() {
		
		System.out.println("Pro registraci zadej R, pro prihlaseni P.");
		String volba = sc.nextLine();
		
		if(volba.equals("R")) {
			actions.registrace();
		}else if(volba.equals("P")) {
			actions.prihlaseni();
		}else {
			regOrLog();
			return;
		}
			
		
	}

	public void prihlaseni() {
		
		System.out.println("Zadej adresu serveru:");
		String addr = sc.nextLine();
//		String addr = "127.0.0.1";
		System.out.println("Zadej port serveru:");
		String port = sc.nextLine();
//		String port = "2222";
		logLogics.confirmDataInServerForm(addr, port);
		
	}


	
	

	/**
	 * createConnect() Vytvori spojeni se serverem
	 * 
	 */
	public void createConnect() {
		try {

			comm = new TCPComm(logLogics.getServerAddres(), logLogics.getServerPort());

			m_commObserver = new KomukiaceServeru(this, logLogics, netLog, actions, gameControl);
			comm.registerObserver(m_commObserver);

			logLogics.setComm(comm);
			netLog.setComm(comm);
		} catch (NumberFormatException e) {

			e.printStackTrace();
			System.out.println("Spatne.");
		}
		comm.start();
	}

	
	/*** Setrs and Getrs ***/


	public LogikaPrihlaseni getLogLogics() {
		return logLogics;
	}

	public void setLogLogics(LogikaPrihlaseni logLogics) {
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

	public KomukiaceServeru getM_commObserver() {
		return m_commObserver;
	}

	public void setM_commObserver(KomukiaceServeru m_commObserver) {
		this.m_commObserver = m_commObserver;
	}

	public SitLogika getNetLog() {
		return netLog;
	}

	public void setNetLog(SitLogika netLog) {
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
