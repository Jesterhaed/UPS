#ifndef USER_H_
#define USER_H_

/*
 * Struktura Game
 * Struktura pro informaci o rozehrane hre
 */
typedef struct Game{
	int free; // volna hra
	int id; // id hry
	int tah_vyzivatel;
	int gamer1; // vyzivatel
	int gamer2; // protihrac
	char chellanger[30]; // jmeno vyzivatele
	char player[30];// jmeno protihrace
}Game;

/*
 * Struktura User_database
 * Struktura pro registrovane uzivatele
 */
typedef struct User_database{
	char nickname[30];
	char passwd[32];
}User_database;


/*
 * Struktura User_conected
 * Struktura pro pripojene uzivatele
 */
typedef struct User_conected{

	char nickname[30];
	int protihracLogOut;
	int socket;
	int id;
	int isLog;
	int play;
	Game* game;
}User_conected;


#endif

