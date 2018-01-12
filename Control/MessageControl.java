package Control;

import java.util.regex.Pattern;

public class MessageControl {

	
	public MessageControl() {
	
	}

	public boolean is_valid(String input) {

		if (input == null || input.equals(" ")) {
			return false;
		}

		String[] pomData = input.split(",");

		if (pomData.length == 0)
			return false;

		for (int i = 0; i < pomData.length; i++) {

			if (pomData[i].equals(""))
				return false;
		}
		if (!messageBlockControl(pomData))
			return false;

		return true;
	}

	private boolean messageBlockControl(String[] pomData) {
		switch (pomData[0]) {
		case "Nevalidni vstup":
			if (pomData.length > 1)
				return false;

			break;
		case "Wait":
			if (pomData.length > 1)
				return false;

			break;
		case "NoServer":
			if (pomData.length > 1)
				return false;

			break;
		case "CheckConnect":

			if (pomData.length > 1)
				return false;
			break;
		case "Connect":
			if (pomData.length > 1)
				return false;
			break;
		case "Reload":

			if (pomData.length < 4 || pomData.length > 4)
				return false;

			try {
				int pom = Integer.parseInt(pomData[1]);
			} catch (NumberFormatException e) {

				return false;
			}
			break;
		case "Registrace":

			if (pomData.length < 3 || pomData.length > 3) {
				return false;
			}
			break;
		case "Log":
			if (pomData.length < 3 || pomData.length > 3)
				return false;
			break;
		case "PlayerList":
			if (pomData.length > 2)
				return false;
			break;
		case "Logout":
			if (pomData.length > 2)
				return false;
			try {
				int pom = Integer.parseInt(pomData[1]);
			} catch (NumberFormatException e) {

				return false;
			}
			break;
		case "Challenge":

			if (pomData[2].equals("invite") || pomData[2].equals("refuse") || pomData[2].equals("accept")) {
				if (pomData.length > 3)
					return false;
			} else if (pomData[1].equals("messageAccept")) {
				if (pomData.length >3 )
					return false;
			}else{
				return false;
			}
			break;
		case "Game":

			if (!gameControl(pomData))
				return false;
			break;

		default:

			return false;

		}
		return true;

	}

	public boolean gameControl(String[] pomData){
		
		if(pomData.length < 2) return false;
		
		switch (pomData[1]) {
		case "colorResult":
			
			if (pomData[2].equals("R")) {
				
				if (pomData.length > 4)
					return false;				
				
				if(!confirmResultColors(pomData[3])) return false;

			} else {
				if (pomData.length > 3)
					return false;

				if(!confirmResultColors(pomData[2])) return false;
			}
			
			
			break;
		case "knobPanel":
			if (pomData.length > 4)
				return false;
			if(!confirmColors(pomData[2], pomData[3])) return false;
			break;
		case "goodColors":
			if (pomData.length > 4)
				return false;
			try {
				int pom = Integer.parseInt(pomData[2]);
				int pom1 = Integer.parseInt(pomData[3]);
			} catch (NumberFormatException e) {
				return false;
			}

			break;
		case "greatColors":
			if (pomData.length > 4)
				return false;
			try {
				int pom = Integer.parseInt(pomData[2]);
				int pom1 = Integer.parseInt(pomData[3]);
			} catch (NumberFormatException e) {
				return false;
			}
			break;
		case "leave":
			if (pomData.length > 4)
				return false;
			try {
				int pom1 = Integer.parseInt(pomData[3]);
			} catch (NumberFormatException e) {
				return false;
			}
			break;
		case "gameOver":
			if (pomData.length > 2)
				return false;
			break;
		case "gameDone":
			if (pomData.length > 2)
				return false;
			break;
		case "player":
			if (pomData.length > 4)
				return false;
			break;
			
		case "colorAccept":
			if (pomData.length > 2)
				return false;
			break;

		default:
			return false;
		}
		
		return true;
	}

	public boolean confirmResultColors(String colors) {
		String[] pom = colors.split(";");
		
		if (pom.length < 4 || pom.length > 4)
			return false;
		
		
		for (int i = 0; i < pom.length; i++) {

			try {
				int pom1 = Integer.parseInt(pom[i]);
			} catch (NumberFormatException e) {

				return false;
			}
		}
		
		return true;
	}

	public boolean confirmColors(String id, String colors) {

		try {
			int pom1 = Integer.parseInt(id);

			String[] pom = colors.split(";");
			if (pom.length < 4 || pom.length > 4)
				return false;

			for (int i = 0; i < pom.length; i++) {
				pom1 = Integer.parseInt(pom[i]);
			}
		} catch (NumberFormatException e) {

			return false;
		}

		return true;
	}

}
