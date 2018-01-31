package Control;

import Game.TypyTanku;

public class KontrolaZprav {

	private HraLogika gameControl;

	public KontrolaZprav(HraLogika gameControl) {

		this.gameControl = gameControl;

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
				if (pomData.length > 3)
					return false;
			} else {
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

	public boolean gameControl(String[] pomData) {

		if (pomData.length < 1)
			return false;

		switch (pomData[1]) {
		case "poleReady":

			if (pomData.length > 2) {
				return false;
			}
			break;

		case "pripravena":

			if (pomData.length > 2) {
				return false;
			}
			break;
		case "hraj":

			if (pomData.length > 2) {
				return false;
			}
			break;

		case "miss":

			if (pomData.length > 2) {
				return false;
			}
			break;
			
		case "tah":
			if (pomData.length > 3) {
				return false;
			}

			int x = 0;
			int y = 0;

			String[] umisteni2 = gameControl.zpracujUmisteni(pomData[2]);

			try {

				x = Integer.parseInt(umisteni2[0]) - 1;
				y = gameControl.prevedPismenoNaInt(umisteni2[1]);
			} catch (NumberFormatException e) {
				return false;
			}

			if (x > 5 || x < 0) {
				return false;
			}

			if (y > 5 || y < 0) {
				return false;
			}
			break;
			
		case "trefa":
			if (pomData.length > 3)
				return false;

			int odecet;
			try {

				odecet = Integer.parseInt(pomData[2]);
			} catch (NumberFormatException e) {
				return false;
			}

			if (odecet == 75 || odecet == 50 || odecet == 25 || odecet == 0) {
				return true;
			}

			return false;
			
		case "zniceno":
			if (pomData.length > 3) {
				return false;
			}
			
			if (pomData[2].equals(TypyTanku.HT.toString())) {
				return true;
			}
			if( pomData[2].equals(TypyTanku.LT.toString())) {
				return true;
			}
			if(pomData[2].equals(TypyTanku.MT.toString())) {
				return true;
			} 
			
			if( pomData[2].equals(TypyTanku.TD.toString())){
				return true;
			} 
			
			return false;
			
		default:
			return false;
		}

		return true;
	}
}
