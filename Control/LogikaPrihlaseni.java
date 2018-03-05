package Control;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;


import Network.TCPComm;
import Run.TanksRun;


public class LogikaPrihlaseni {

	/** Globalni promenne tridy **/
	
	public static final String nullConstat = "d41d8cd98f00b204e9800998ecf8427e";
	
	private boolean isLog = false;

	private String nickname;
	private String passwd;
	private String passwd2;

	private String serverAddres = "127.0.0.1";
	private int serverPort = 2222;
	

	private TanksRun tR;
	private TCPComm comm;

	
	public LogikaPrihlaseni(TanksRun tR) {
		this.tR = tR;
	}

	/**
	 * Kontrola udaju zadanych do formulare pro registraci uzivatele
	 * @param nickname
	 * @param passwd
	 * @param passwd2
	 * @return
	 */
	public boolean confirmDataInForm(String nickname, String passwd, String passwd2) {

		if (nicknameConfirm(nickname)
				&& passwdsConfirm(passwd, passwd2)) {
			return true;
		}

		return false;

	}

	/**
	 * Kontrola udaju zadanych do formulare pro pripojeni na server
	 * @param addres
	 * @param port
	 * @return
	 */
	public boolean confirmDataInServerForm(String addres, String port) {

		if (serverAddresConfirm(addres) && serverPortConfirm(port)) {
			return true;
		}
	
		return false;

	}

	/**
	 * Vytvoreni zpravy s registracnimi udaji
	 * @return
	 */
	public String createRegMessage(String jmeno, String heslo){
		
		return "Registration," + jmeno + "," + hashPassword(heslo) + "\n" ; 
		
	}
	
	
	/**
	 * Vytvoreni zpravy s prihlasujicimi udaji
	 * @return
	 */
	public String createLogMessage(String nick, String passwd){
		
		return "Log," + nick + "," + passwd + "\n" ; 
		
	}

	/**
	 * Kontrola udaju zadanych do formulare pro prihlaseni uzivatele
	 * @param nickname
	 * @param passwd
	 * @return
	 */
	public boolean confirmDataInForm(String nickname, String passwd) {

		if (nicknameConfirm(nickname) && passwdConfirm(passwd)){
			return true;
		}
		
		return false;

	}

	/**
	 * Kontrola zadane adresy serveru
	 * @param addres
	 * @return
	 */
	 public boolean serverAddresConfirm(String addres) {
		
		 String[] pom = addres.split(Pattern.quote("."));
		 if (addres.length() == 0) {
			 
			System.out.println("Chyba prihlaseni.");
			System.out.println("Neni zadana IP adresa!");
			System.out.println("Zadej spravnou IP adresu serveru!");
			tR.prihlaseni();
		}else if(!addres.contains("localhost")){
			
			if (pom.length < 4 || confirmAddressBlock(pom)) {
				
				System.out.println("Chyba prihlaseni.");
				System.out.println("Zadana spatna IP adresa!");
				System.out.println("Zadej spravnou IP adresu serveru ve tvaru \"127.0.0.1\" !");
				tR.prihlaseni();
			}else{
				setServerAddres(addres);
				return true;
			}

		}else {
			setServerAddres(addres);
			return true;
		}

		return false;
	}
	 
	 private boolean confirmAddressBlock(String[] pom) {
		 int number;
		 try{
			 for (int i = 0; i < pom.length; i++) {				 
				 number = Integer.parseInt(pom[i]);
				 if(number > 255 ){
					 return true;
				 }
			 }
		 }catch(NumberFormatException e){
			 System.out.println("Chybna IPv4 adresa.");
			 return true;
		 }
		 
		return false;
	}

	/**
	  * Kontrola zadaneho portu serveru
	  * @param port
	  * @return
	  */
	public boolean serverPortConfirm(String port) {
		if (port.length() == 0 || confirmPortNumber(port)) {
			System.out.println("Spatne zadany port serveru!");
			System.out.println("Zadej spravny port.");
			tR.prihlaseni();
		}else {
			this.serverPort = Integer.parseInt(port);
			return true;
		}

		return false;
	}

	private boolean confirmPortNumber(String port) {
		int number;
		try{
			
			number = Integer.parseInt(port);
			
			if (number > 65536 || number < 1024) {
				return true;
			}
			
		}catch (NumberFormatException e) {
			System.out.println("Spatny typ portu!");
			return true;
		}
		
		
		return false;
	}

	/**
	 * Kontrola policka se jmenem
	 * @param nickname
	 * @return
	 */
	public boolean nicknameConfirm(String nickname) {
		if (nickname.length() == 0) {
		System.out.println(" Nezadane jmeno.");

		}else if(nickname.length() > 30){
			System.out.println("Tato prezdivka je prilis dlouha!");
			System.out.println("Zvolte nick do triceti znaku.");
		}else {
			this.nickname = nickname;
			return true;
		}

		return false;
	}

	

	/**
	 * Kontrola policka s heslem
	 * @param passwd
	 * @return
	 */
	public boolean passwdConfirm(String passwd) {
		if (passwd.equals(nullConstat)) {
			System.out.println("Chyba prihlaseni.");
			System.out.println("Zadne heslo!");
			System.out.println("Zadejte heslo!");

		} else {
			this.passwd = passwd;
			return true;
		}

		return false;
	}

	/**
	 * Kontrola policka s kontrolnim heslem
	 * @param passwd2
	 * @return
	 */
	public boolean passwd2Confirm(String passwd2) {

		if (passwd2.equals(nullConstat)) {
			System.out.println("Chyba prihlaseni.");
			System.out.println("Zadne heslo!");
			System.out.println("Zadejte potvrzeni hesla!");
		} else {
			this.passwd2 = passwd2;

			return true;
		}

		return false;
	}

	/**
	 * Kontrola shody hesel
	 * @param passwd
	 * @param passwd2
	 * @return
	 */
	public boolean passwdsConfirm(String passwd, String passwd2) {

		if (passwdConfirm(passwd) && passwd2Confirm(passwd2)) {
			if (!passwd.equals(passwd2)) {

				System.out.println("Chyba prihlaseni.");
				System.out.println("Hesla se neschoduji!");

			} else {
				return true;
			}
		}

		return false;

	}

	/**
	 * Metoda pro vytvoreni hashe z hesla uzivatele.
	 * @param original
	 * @return
	 */
	public String hashPassword(String original) {

		StringBuffer sb = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			md.update(original.getBytes());

			byte[] digest = md.digest();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();

		}
	
		return sb.toString();

	}

	/*** Getrs and Setrs ***/

	public boolean isLog() {
		return isLog;
	}

	public void setLog(boolean isLog) {
		this.isLog = isLog;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getPasswd2() {
		return passwd2;
	}

	public void setPasswd2(String passwd2) {
		this.passwd2 = passwd2;
	}

	public String getServerAddres() {
		return serverAddres;
	}

	public void setServerAddres(String serverAddres) {
		this.serverAddres = serverAddres;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public TCPComm getComm() {
		return comm;
	}

	public void setComm(TCPComm comm) {
		this.comm = comm;
	}
	

}
