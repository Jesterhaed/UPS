package Control;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;


import Network.TCPComm;
import Run.MasterMindRun;


public class LogginLogics {

	/** Globalni promenne tridy **/
	private boolean isLog = false;
	private String userNick;
	
	private String name;
	private String surname;
	private String nickname;
	private String passwd;
	private String passwd2;

	private String serverAddres = "192.168.40.131";
	private int serverPort = 1111;

	private MasterMindRun mMR;
	private TCPComm comm;

	
	public LogginLogics(MasterMindRun mMR) {
		this.mMR = mMR;
	}

	/**
	 * Kontrola udaju zadanych do formulare pro registraci uzivatele
	 * 
	 * @param name
	 * @param surname
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
		
		return "Registrace," + jmeno + "," + hashPassword(heslo) + "\n" ; 
		
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
			/*Alert alert = new Alert(javafx.scene.control.Alert.AlertType.WARNING);
			alert.setTitle("Sign error");
			alert.setHeaderText("No IP address!");
			alert.setContentText("You must fill IP address of server !");
			alert.showAndWait();*/
			 
			 // vypsat text chyby

		}else if(!addres.contains("localhost")){
			
			if (pom.length < 4 || confirmAddressBlock(pom)) {
			/*	Alert alert = new Alert(javafx.scene.control.Alert.AlertType.WARNING);
				alert.setTitle("Sign error");
				alert.setHeaderText("Bad IP address!");
				alert.setContentText("You must fill IP address in this style \" 127.0.0.1\"  !");
				alert.showAndWait(); */
				 // vypsat text chyby

			}else{
				setServerAddres(addres);
				return true;
			}

		}else {
			//convertIPAddress(addres.split("."));
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
			 System.out.println("Bad ip address format");
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
			//Dopsat vypis

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
			System.out.println("Bad type of port");
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
		System.out.println(" Nezadane jmeno");

		}else if(nickname.length() > 30){
		System.out.println("Prilis dlouhe heslo");
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
		if (passwd.equals(Constants.nullConstat)) {
		/*	Alert alert = new Alert(javafx.scene.control.Alert.AlertType.WARNING);
			alert.setTitle("Sign error");
			alert.setHeaderText("No password!");
			alert.setContentText("You must fill password !");
			alert.showAndWait();
*/
			 // vypsat text chyby

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

		if (passwd2.equals(Constants.nullConstat)) {
		/*	Alert alert = new Alert(javafx.scene.control.Alert.AlertType.WARNING);
			alert.setTitle("Sign error");
			alert.setHeaderText("No password!");
			alert.setContentText("You must fill confirm password !");
			alert.showAndWait();
*/
			 // vypsat text chyby

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

				/*Alert alert = new Alert(javafx.scene.control.Alert.AlertType.WARNING);
				alert.setTitle("Sign error");
				alert.setHeaderText("Bad passwords!");
				alert.setContentText("Passwords is not same!");
				alert.showAndWait();
				sUW.getPasswdTF().setText("");
				sUW.getPasswd2TF().setText("");
*/
				 // vypsat text chyby

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
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

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public TCPComm getComm() {
		return comm;
	}

	public void setComm(TCPComm comm) {
		this.comm = comm;
	}
	

}