#ifndef USER_H_
#define USER_H_


/*
 * Definovani struktury Knobs
 * Datova struktura pro uchovani informaci tahu
 */
typedef struct Knobs{
	int free;
	int identifikace;
	int colors[4];


}Knobs;

/*
 * Struktura Good_color
 * Datova struktura pro uchovani informaci o uhodnutych barvach
 */
typedef struct Good_color{
	int free;
	int good_color;
	int identifikace;

}Good_color;

/*
 * Struktura Great_color
 * Datova struktura pro uchovani informaci o uhodnutych pozicich barev
 */
typedef struct Great_color{
	int free;
	int great_color;
	int identifikace;

}Great_color;

/*
 * Struktura Game
 * Datova struktura pro uchovani informaci o hre
 */
typedef struct Game{
	int free; // volna hra
	int id; // id hry
	int tah_vyzivatel;
	int gamer1; // vyzivatel
	int gamer2; // protihrac
	char chellanger[30]; // jmeno vyzivatele
	char player[30];// jmeno protihrace
	int goodColor;
	Knobs knobs[10]; // pole struktur pro uchovani informaci o tahu
	Great_color great_color[10]; // pole struktur pro uchovani uhadnutych barev
	Good_color good_color[10]; // pole struktur pro uchovani uhadnutych pozic barev
	Knobs result; // vysleek
}Game;

/*
 * Struktura User_database
 * Datova struktura pro uchovani registrovanych uzivatelu
 */
typedef struct User_database{
	char nickname[30];
	char passwd[32];
}User_database;


/*
 * Struktura User_conected
 * Datova struktura pro uchovani pripjenych uzivatelu
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
