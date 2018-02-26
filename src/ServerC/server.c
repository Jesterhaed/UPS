#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <pthread.h>
#include <unistd.h>
#include <signal.h>
#include <time.h>
#include "Users.h"

#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

//==========================================================================

#define MAX_CONECTED 10
#define COUNT_Game 10
#define FILE_NAME "logy.log"

//--------------------------------------------------------------------------
struct tm *loctime;
/* Globalni promenne tridy*/
time_t log_time;
User_conected* conected_users[MAX_CONECTED];
User_database* database_users[MAX_CONECTED];
pthread_t threads[MAX_CONECTED];
FILE *file = NULL;
int lastInt = -1;
int srv_port = 2222;
char address[20];
int is_address = 1;

Game* game[MAX_CONECTED];
pthread_mutex_t lock;

/*
* void help();
*
* Vypise jednoduchy navod jak spustit program z prikazove radky
*/
void help() {
	printf(
		"Pouziti:\n   port \"<port>\", kde \"<port>\" je cislo portu serveru.\n"
		"Adresa \"<address>\", kde \"<address>\" je adresa serveru, napr. ve tavru 10.17.17.1 \n "
		"nebo -a pro INADDR_ANY\n");

}

/*
* void sigint_handler(int sig);
*
* Nastavi handler pro zachyceni ukonceni procesu
*/
void sigint_handler(int sig) {

	printf("Ukonceni procesu %d\n", getpid());
	pthread_mutex_destroy(&lock);
	exit(0);
}

/*
* void nacti_soubor();
*
*Nacte logovaci soubor
*/
void nacti_soubor() {

	file = fopen(FILE_NAME, "a+");

	if (file == NULL) {
		printf("ERR: Soubor neexistuje!");
	}

}

void write_log(char* message) {
	nacti_soubor();
	char buffer[255];
	/* Get the current time. */
	log_time = time(NULL);

	/* Convert it to local time representation. */
	loctime = localtime(&log_time);

	/* Print out the date and time in the standard format. */
	strftime(buffer, 255, "%A, %B %d. %I:%M %p \n", loctime);
	fprintf(file, "%s \n %s\n", buffer, message);
	fclose(file);
}

/*
* nacti_port(int argc, char **argv)
*
* argc pocet zadanych parametru
* argv pole se zadanymi parametry
*
* Nacte port pokud byl zadan z cmd
*/
int nacti_Port(int argc, char **argv) {

	char* pom = (char*)malloc(MAX_CONECTED * 10);
	long val;
	char *next;

	if (argc == 3) {
		val = strtol(argv[1], &next, 10);

		if (val > 65536 || val < 0 || (next == argv[1]) || (*next != '\0')) {

			sprintf(pom, "Spatny port: %s\n", argv[1]);
			write_log(pom);
			return 1;
		}
		else {
			srv_port = atoi(argv[1]);
		}

	}
	sprintf(pom, "Server bezi na portu: %d\n", srv_port);
	write_log(pom);

	free(pom);

	return 0;
}

int control_address(char* address) {
	char *ret = strchr(address, '.');
	int i;
	char *ret1;
	long val;
	char *next;

	if (strcmp(address, "-a") == 0 || strcmp(address, "a") == 0) {

		return 2;
	}

	if (ret != NULL) {
		*ret = '\0';
		ret++;
	}
	else {
		return 1;
	}

	val = strtol(address, &next, 10);

	if (val > 255 || (next == address) || (*next != '\0')) {
		return 1;

	}

	int counter = 0;
	for (i = 0; i < 3; ++i) {

		ret1 = strchr(ret, '.');

		if (ret1 != NULL) {
			*ret1 = '\0';
			ret1++;
		}
		else {
			if (counter < 2) {
				return 1;
			}
		}

		val = strtol(ret, &next, 10);
		if (val > 255 || (next == address) || (*next != '\0') || val < 0
			|| strlen(ret) < 1) {
			return 1;
		}

		if (ret1 != NULL) {
			strcpy(ret, ret1);
		}
		counter++;

	}

	if (ret1 != NULL) {
		return 1;
	}

	return 0;

}
/*
* read_address(int argc, char **argv)
*
* argc pocet zadanych parametru
* argv pole se zadanymi parametry
*
* Nacte adresu pokud byla zadana z cmd
*/
int read_address(int argc, char **argv) {

	char* pom = (char*)malloc(MAX_CONECTED * 60 + MAX_CONECTED);
	char* s = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	if (argc == 3) {

		strcpy(s, argv[2]);

		int i = control_address(argv[2]);

		if (i != 1) {
			if (i != 2) {
				strcpy(address, s);
				is_address = 0;

			}
			else {
				printf("Server posloucha na vsech adresach.\n");
				write_log("Server posloucha na vsech adresach.\n");
				return 0;
			}
		}
		else {
			sprintf(pom, "Spatne zadana adresa: %s\n", s);
			write_log(pom);
			return 1;
		}
	}

	printf("Server bezi na adrese: %s\n", address);
	sprintf(pom, "Server bezi na adrese: %s\n", address);
	write_log(pom);

	free(pom);
	free(s);
	return 0;
}

/*
*      Remove given section from string. Negative len means remove
*      everything up to the end.
*/
int str_cut(char *str, int begin, int len) {
	int l = strlen(str);

	if (len < 0)
		len = l - begin;
	if (begin + len > l)
		len = l - begin;
	memmove(str + begin, str + begin + len, l - len + 1);

	return len;
}

/*
* sgetline(int fd, char **out)
*
* argc pocet zadanych parametru
* argv pole se zadanymi parametry
*
* Nacte zpravu
*/
int sgetline(int fd, char ** out) {
	int buf_size = 128;
	int bytesloaded = 0;
	int ret;
	int i = 0;
	char buf;
	char * buffer = malloc(buf_size);
	char * newbuf;

	if (NULL == buffer)
		return -1;

	while (i != 127) {
		// read a single byte
		ret = read(fd, &buf, 1);

		if (ret < 1) {
			// error or disconnect
			return -1;
		}

		// has end of line been reached?
		if (buf == '\n') {
			break; // yes
		}

		buffer[bytesloaded] = buf;
		bytesloaded++;
		// is more memory needed?
		if (bytesloaded >= buf_size) {
			buf_size += 128;
			newbuf = realloc(buffer, buf_size);

			if (NULL == newbuf) {
				return -1;
			}

			buffer = newbuf;
		}
		i++;
	}

	// if the line was terminated by "\r\n", ignore the
	// "\r". the "\n" is not in the buffer
	if ((bytesloaded) && (buffer[bytesloaded - 1] == '\r'))
		bytesloaded--;

	int len = strlen(buffer);
	int distinction = len - bytesloaded;
	str_cut(buffer, bytesloaded, distinction);

	*out = buffer; // complete line

	
	return bytesloaded;
}

/*
* pull_user(User_conected* user)
*Vyjme uzivatele z pole pripojenych uzivatelu
*/
void pull_user(User_conected* user) {

	pthread_mutex_lock(&lock);
	conected_users[user->id] = (User_conected*)NULL;
	pthread_mutex_unlock(&lock);

	free(user);

}

/*
* put_user(int socket)
*
*Vlozi uzivatele z pole pripojenych uzivatelu pomoci prijateho socketu
*
*/
User_conected* put_user(int socket) {
	User_conected* user = malloc(sizeof(User_conected));

	memset(user, 0, sizeof(User_conected));

	user->socket = socket;

	pthread_mutex_lock(&lock);
	int i;
	for (i = 0; i < MAX_CONECTED; ++i) {
		if (conected_users[i] == NULL) {
			conected_users[i] = user;
			user->id = i;

			break;
		}
	}

	pthread_mutex_unlock(&lock);

	return user;

}

/*
* send_message(User_conected* user, char* message)
*
* Odesle zadanou zpravu zadanemu uzivateli na zaklade socketu uzivatele
*
*/
void send_message(User_conected* user, char* message) {

	send(user->socket, message, strlen(message), 0);

}
void invalid_input(User_conected* user) {

	printf("Nevalidni vstup\n");
	send_message(user, "Nevalidni vstup\n");
	printf("Ukoncuji spojeni \n");
	
}
/*
* nickname_control(char* nickname)
*
* Kontrola velikosti uzivatelskeho jmena
*
*/
int nickname_control(char* nickname, User_conected* user) {

	if (strlen(nickname) > 30 || strlen(nickname) == 0) {
		send_message(user, "Registrace,bad2,invalid_nickname\n");
		return 0;
	}
	return 1;
}
/*
* passwd_control(char* nickname)
*
* Kontrola velikosti hesla
*
*/
int passwd_control(char* passwd, User_conected* user) {
	if (strlen(passwd) > 32 || strlen(passwd) < 32 || strlen(passwd) == 0) {
		send_message(user, "Registrace,bad2,invalid_passwd\n");
		return 0;
	}
	return 1;
}

/*
* reg_user(char* buffer, User_conected* user)
*
* Registrace uzivatele na zaklade prijatych dat
* Kontrola existence jmena v databazi
* Odpovedi s informacinimy hlaskami
*
*/
void reg_user(char* buffer, User_conected* user) {

	if (buffer == NULL) {
		invalid_input(user);
		return;
	}

	char *ret = strchr(buffer, ',');

	if (ret != NULL) {
		*ret = '\0';
		ret++;
	}
	else {
		invalid_input(user);
		return;
	}

	char *ret2 = strchr(ret, ',');

	if (ret2 != NULL) {
		invalid_input(user);
	}

	pthread_mutex_lock(&lock);
	int i;
	for (i = 0; i < MAX_CONECTED; ++i) {

		if (database_users[i] != NULL
			&& strcmp(database_users[i]->nickname, buffer) == 0) {

			send_message(user, "Registrace,bad1,This nickname is exist,\n");
			pthread_mutex_unlock(&lock);
			return;

		}

	}

	User_database* reg_user = malloc(sizeof(User_database));
	memset(reg_user, 0, sizeof(User_database));
	char* pom = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	if (nickname_control(buffer, user) == 1 && passwd_control(ret, user) == 1) {
		strcpy(reg_user->nickname, buffer);
		strcpy(reg_user->passwd, ret);

		for (i = 0; i < MAX_CONECTED; ++i) {

			if (database_users[i] == NULL) {

				database_users[i] = reg_user;
				break;
			}
		}

		send_message(user, "Registrace,yes,succes,\n");
		sprintf(pom, "Registrace uzivatele: %s\n", reg_user->nickname);
		write_log(pom);
	}
	pthread_mutex_unlock(&lock);
	free(pom);
}
/*
* is_bad_passwd(char* passwd)
*
* Pomocna funkce pro kontrolu spravnosti hesla uzivatele
*
*/
int is_bad_passwd(char* passwd) {

	int i;

	for (i = 0; i < MAX_CONECTED; ++i) {

		if (database_users[i] != NULL) {

			if (strcmp(database_users[i]->passwd, passwd) == 0) {

				return 1;
			}
		}
	}

	return 0;

}
/*
* is_bad_loggin(char* passwd)
*
* Pomocna funkce pro kontrolu spravnosti logginu uzivatele
*
*/
int is_bad_loggin(char* log) {

	int i = 0;

	for (i = 0; i < MAX_CONECTED; ++i) {

		if (database_users[i] != NULL) {
			if (strcmp(database_users[i]->nickname, log) == 0) {

				return 1;
			}
		}
	}

	return 0;

}

/*
* send_player(Game* game, User_conected* user)
*
* Posle jmeno hrace se kterym ma uzivatel aktualne rozehranou hru
*
*/
void send_player(Game* game, User_conected* user) {

	char* player = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	strcpy(player, "Game,player,");

	if (strcmp(conected_users[game->gamer1]->nickname, user->nickname) == 0) {

		strcat(player, "challenger,");
		strcat(player, conected_users[game->gamer1]->nickname);
		strcat(player, "\n");

	}
	else {

		strcat(player, "challenger,");
		strcat(player, conected_users[game->gamer2]->nickname);
		strcat(player, "\n");

	}

	send_message(user, player);

	sleep(1);
}


/*
* log_control(User_conected* user, char* buffer)
*
* Provede kontrolu prihlasujiciho se uzivatele
* Pokud jsou prihlasovaci udaje v poradku tak ho prihlasi
*
*/
void log_control(User_conected* user, char* buffer) {

	if (buffer == NULL) {
		invalid_input(user);
		return;
	}

	char *ret = strchr(buffer, ',');
	char* pom = (char*)malloc(MAX_CONECTED * 60 + MAX_CONECTED);

	if (ret != NULL) {
		*ret = '\0';
		ret++;
	}
	else {
		invalid_input(user);
		return;
	}
	char *ret2 = strchr(ret, ',');

	if (ret2 != NULL) {
		invalid_input(user);
		return;
	}

	int i = 0;
	for (i = 0; i < MAX_CONECTED; ++i) {

		if (database_users[i] != NULL) {
			if (strcmp(database_users[i]->nickname, buffer) == 0
				&& strcmp(database_users[i]->passwd, ret) == 0) {

				user->isLog = 1;
				strcpy(user->nickname, buffer);
				send_message(user, "Log,yes,succes\n");

				sprintf(pom, "Prihlaseny uzivatel: %s\n", user->nickname);
				write_log(pom);

				break;
			}
		}
	}

	if (user->isLog == 0) {
		if (is_bad_loggin(buffer) == 0) {
			send_message(user, "Log,no,badLog\n");
		}
		else {
			send_message(user, "Log,no,badPasswd\n");
		}
	}

	free(pom);

}
/*
* send_free_players(User_conected* user)
*
* Odesle vyzivateli seznam volnych prihlasenych uzivatelu
*
*/
void send_free_players(User_conected* user) {

	char* message = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);
	char* finish = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	int i;
	char strednik[2];

	strcpy(message, "PlayerList,");
	strcpy(strednik, ";");

	for (i = 0; i < MAX_CONECTED; ++i) {

		if (conected_users[i] != NULL && conected_users[i]->play != 1) {
			if (conected_users[i]->isLog == 1
				&& strcmp(conected_users[i]->nickname, user->nickname)
				!= 0) {
				finish = strcat(message, conected_users[i]->nickname);
				message = strcat(finish, strednik);

			}
		}
	}

	finish = strcat(message, ",\n");
	send_message(user, finish);
	sleep(1);

	//free(message);
	//free(finish);

}

int control_chellange(char* player) {

	char *ret1 = strchr(player, ',');

	if (ret1 != NULL) {
		*ret1 = '\0';
		ret1++;
	}
	else {
		return 1;
	}

	if (strlen(ret1) > 0) {
		return 1;
	}
	return 0;
}

/*
* send_invitation(User_conected* user, char* player)
*
* Odesle pozvani do hry druhemu uzivateli
*
*/
void send_invitation(User_conected* user, char* player) {

	if (control_chellange(player) == 1) {
		invalid_input(user);
		return;
	}


	int i;
	char* message = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);
	char* finish = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	strcpy(message, "Challenge,");

	char *ret1 = strchr(player, ',');
	if (ret1 != NULL) {
		*ret1 = '\0';
		ret1++;
	}

	for (i = 0; i < MAX_CONECTED; ++i) {

		if (conected_users[i] != NULL) {
			if (strcmp(conected_users[i]->nickname, player) == 0) {

				if (conected_users[i]->play != 1) {

					finish = strcat(message, user->nickname);
					message = strcat(finish, ",invite,\n");
					send_message(conected_users[i], message);

				}
				else {
					char* message1 = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);
					strcpy(message1, "Challenge,refuse,protihrac,\n");
					send_message(user, message1);
					free(message1);
				}

			}


		}

	}

	//free(message);
	//free(finish);

}
/*
* find_free_game(User_conected* player, User_conected* chellanger)
*
* Najde volnou pozici pro vytvoreni hry
* Priradi vytvorenou hru danym hracum
*
*/
void find_free_game(User_conected* player, User_conected* chellanger) {

	pthread_mutex_lock(&lock);

	int i;
	for (i = 0; i < MAX_CONECTED; i++) {
		if (game[i] == NULL) {
			game[i] = malloc(sizeof(Game));
			memset(game[i], 0, sizeof(Game));
			game[i]->gamer1 = player->id;
			game[i]->gamer2 = chellanger->id;

			strcpy(game[i]->chellanger, player->nickname);
			strcpy(game[i]->player, chellanger->nickname);

			player->game = game[i];
			chellanger->game = game[i];
			player->play = 1;
			chellanger->play = 1;
			game[i]->free = 1;
			game[i]->id = i;
			break;
		}
	}

	pthread_mutex_unlock(&lock);

}

/*
* send_accept_chellange(User_conected* user, char* player)
*
* Odesle vyzivateli informaci o prijeti jeho vyzvy druhym hracem
*
*/
void send_accept_chellange(User_conected* user, char* player) {
	if (control_chellange(player) == 1) {
		invalid_input(user);
		return;
	}
	int i;
	char* message = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	char* finish = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);
	char *ret1 = strchr(player, ',');
	if (ret1 != NULL) {
		*ret1 = '\0';
		ret1++;
	}
	strcpy(message, "Challenge,");
	for (i = 0; i < MAX_CONECTED; ++i) {

		if (conected_users[i] != NULL) {
			if (strcmp(conected_users[i]->nickname, player) == 0) {
				find_free_game(conected_users[i], user);
				finish = strcat(message, user->nickname);
				message = strcat(finish, ",accept,\n");
				send_message(conected_users[i], message);
			}

		}

	}

	//free(message);
	//free(finish);

}

/*
* send_refuse_chellange(User_conected* user, char* player)
*
* Odesle vyzivateli informaci o odmitnuti jeho vyzvy druhym hracem
*
*/
void send_refuse_chellange(User_conected* user, char* player) {
	if (control_chellange(player) == 1) {
		invalid_input(user);
		return;
	}
	int i;
	char* message = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);
	char* finish = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	strcpy(message, "Challenge,");

	char *ret2 = strchr(player, ',');
	if (ret2 != NULL) {
		*ret2 = '\0';
		ret2++;
	}
	for (i = 0; i < MAX_CONECTED; ++i) {

		if (conected_users[i] != NULL) {
			if (strcmp(conected_users[i]->nickname, player) == 0) {

				finish = strcat(message, user->nickname);
				message = strcat(finish, ",refuse,\n");

				send_message(conected_users[i], message);

			}

		}

	}

}

/*
* result_to_game(User_conected* user, char* message1)
*
* Odesle vyzivateli informaci o hledanem vysledku
*
*/
void pole_ready(User_conected* user) {


	if (user->game != NULL) {
		Game* game1 = user->game;
		send_message(conected_users[game1->gamer1], "Game,poleReady,\n");

	}

}
/*
* good_color_to_game(User_conected* user, char* message1)
*
* Odesle druhemu hraci informaci o uhodnutych barvach v tahu
*
*/
void spust_souboj(User_conected* user) {

	char* message = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	strcpy(message, "Game,pripravena,\n");



	if (user->game != NULL) {

		Game* game1 = user->game;
		game1->tah_vyzivatel = 1;

		send_message(conected_users[game1->gamer2], message);

	}
}

void preposli_tah(User_conected* user, char* message1) {

	char *ret3 = strchr(message1, ',');

	char* pom = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);


	if (ret3 != NULL) {
		*ret3 = '\0';
		ret3++;
	}
	else {
		invalid_input(user);
		sprintf(pom, "Prijata zprava :%s nevalidni vstup \n", message1);
		write_log(pom);
	}

	if (user->game != NULL) {

		Game* game1 = user->game;
		char* message = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

		strcpy(message, "Game,tah,");
		message = strcat(message, message1);

		message = strcat(message, ",\n");

		if (game1->tah_vyzivatel != 1) {
			send_message(conected_users[game1->gamer1], message);
		}
		else {
			send_message(conected_users[game1->gamer2], message);

		}
	}
}


/*
* good_color_to_game(User_conected* user, char* message1)
*
* Odesle druhemu hraci informaci o uhodnutych pozicich barev v tahu
*
*/
void tank_trefen(User_conected* user, char* message1) {

	char *ret3 = strchr(message1, ',');

	char* pom = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);


	if (ret3 != NULL) {
		*ret3 = '\0';
		ret3++;
	}
	else {
		invalid_input(user);
		sprintf(pom, "Prijata zprava :%s nevalidni vstup \n", message1);
		write_log(pom);
	}

	if (user->game != NULL) {


		Game* game1 = user->game;
		char* message = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

		strcpy(message, "Game,trefa,");
		message = strcat(message, message1);

		message = strcat(message, ",\n");

		if (game1->tah_vyzivatel == 1) {
			send_message(conected_users[game1->gamer1], message);
		}
		else {
			send_message(conected_users[game1->gamer2], message);

		}
	}
}


void tank_znicen(User_conected* user, char* message1) {
	char* pom = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	char *ret3 = strchr(message1, ',');

	if (ret3 != NULL) {
		*ret3 = '\0';
		ret3++;
	}
	else {
		invalid_input(user);
		sprintf(pom, "Prijata zprava :%s nevalidni vstup \n", message1);
		write_log(pom);
	}

	if (user->game != NULL) {


		Game* game1 = user->game;
		char* message = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

		strcpy(message, "Game,zniceno,");
		message = strcat(message, message1);

		message = strcat(message, "\n");

		if (game1->tah_vyzivatel == 1) {
			send_message(conected_users[game1->gamer1], message);

		}
		else {
			send_message(conected_users[game1->gamer2], message);

		}
	}
}


void pole_netrefeno(User_conected* user) {
	char* message = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	strcpy(message, "Game,miss,\n");

	if (user->game != NULL) {
		Game* game1 = user->game;

		if (game1->tah_vyzivatel == 1) {
			send_message(conected_users[game1->gamer1], message);

		}
		else {
			send_message(conected_users[game1->gamer2], message);

		}
	}
}



void zmena_hrace(User_conected* user) {
	char* message = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	strcpy(message, "Game,hraj,\n");
	if (user->game != NULL) {
		Game* game1 = user->game;

		if (game1->tah_vyzivatel == 1) {
			send_message(conected_users[game1->gamer2], message);
			game1->tah_vyzivatel = 0;
		}
		else {
			send_message(conected_users[game1->gamer1], message);
			game1->tah_vyzivatel = 1;
		}
	}
}



/*
* leave_game(User_conected* user)
*
* Odesle druhemu hraci informaci o opusteni hry po odhlaseni
*
*/
void logout_user(User_conected* user, char* ret2) {
	char* message = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);
	char* pom = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	char* pom1 = (char*)malloc(MAX_CONECTED * 60 + MAX_CONECTED);
	if (user->game != NULL) {
		if (user->game->free == 1) {
			sprintf(pom, "%d", user->game->id);
			strcpy(message, "Logout,");
			strcat(message, pom);
			strcat(message, ",\n");
			user->isLog = 0;
			int index;

			if (strcmp(user->game->chellanger, user->nickname) == 0) {
				index = user->game->gamer2;
			}
			else {
				index = user->game->gamer1;
			}
			sprintf(pom1, "Odhlasen uzivatel: %s\n)", user->nickname);
			write_log(pom1);
			conected_users[index]->protihracLogOut = 1;
			send_message(conected_users[index], message);
		}
		else {
			printf("user %s  ", user->nickname);
			user->isLog = 0;

		}

	}
	else {
		printf("user %s  ", user->nickname);
		user->isLog = 0;
	}
	char *ret3 = strchr(ret2, ',');
	if (ret3 != NULL) {
		*ret3 = '\0';
		ret3++;
	}

	if (strcmp(ret2, "end") == 0) {
	free(pom);
	free(pom1);

		write_log("Server ukoncil vlakno. \n");
		pthread_join(pthread_self(), PTHREAD_CANCELED);
	}

}

/*
* receive_challenge(User_conected* user, char* message)
*
* Pomocna fuknce pro rozdeleni zpravy obsahujici challenge a naslednou obsluhu
*
*/
void receive_challenge(User_conected* user, char* message) {

	char* pom = (char*)malloc(MAX_CONECTED * 60 + MAX_CONECTED);
	char *ret3 = strchr(message, ',');

	if (ret3 != NULL) {
		*ret3 = '\0';
		ret3++;
	}
	else {
		invalid_input(user);
		return;
	}

	if (strcmp(message, "accept") == 0) {
		send_accept_chellange(user, ret3);
	}
	else if (strcmp(message, "refuse") == 0) {
		send_refuse_chellange(user, ret3);
	}
	else if (strcmp(message, "invite") == 0) {

		send_invitation(user, ret3);
	}
	else {
		invalid_input(user);
		sprintf(pom, "Prijata zprava :%s nevalidni vstup \n", message);
		write_log(pom);
	}

	free(pom);
}

void delete_game_end(User_conected* user) {

	int id;


	if (user->game != NULL) {

		Game* game1 = user->game;
		id = game1->id;

		int id1 = game[id]->gamer1;
		int id2 = game[id]->gamer2;

		if (conected_users[id1] != NULL) {
			conected_users[id1]->play = 0;
			conected_users[id2]->play = 0;
		}
		if (conected_users[id2] != NULL) {
			conected_users[id2]->game = NULL;
			conected_users[id2]->game = NULL;
		}

	}

	free(game[id]);
	game[id] = NULL;
}


/*
* receive_game(User_conected* user, char* message)
*
* Pomocna fuknce pro rozdeleni zpravy obsahujici game a naslednou obsluhu
*
*/
void receive_game(User_conected* user, char* message) {

	char* pom = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	char *ret3 = strchr(message, ',');

	if (ret3 != NULL) {
		*ret3 = '\0';
		ret3++;
	}
	else {
		invalid_input(user);
		sprintf(pom, "Prijata zprava :%s nevalidni vstup \n", message);
		write_log(pom);
	}
	if (strcmp(message, "poleReady") == 0) {
		pole_ready(user);

	}
	else if (strcmp(message, "pripravena") == 0) {
		spust_souboj(user);

	}
	else if (strcmp(message, "tah") == 0) {
		preposli_tah(user, ret3);

	}
	else if (strcmp(message, "trefa") == 0) {

		tank_trefen(user, ret3);

	}
	else if (strcmp(message, "zniceno") == 0) {

		tank_znicen(user, ret3);

	}
	else if (strcmp(message, "miss") == 0) {
		pole_netrefeno(user);

	}
	else if (strcmp(message, "hraj") == 0) {

		zmena_hrace(user);
	}
	else if (strcmp(message, "endGame") == 0) {

		delete_game_end(user);
	}
	else {
		invalid_input(user);
		sprintf(pom, "Prijata zprava :%s nevalidni vstup \n", message);
		write_log(pom);
	}

	free(pom);
}

int control_player_list(char* message) {
	char *ret = strchr(message, ',');

	if (ret != NULL) {
		*ret = '\0';
		ret++;
	}
	else {
		return 1;
	}
	if (strcmp(message, "get") != 0)
		return 1;

	char* ret1 = strchr(ret, ',');
	if (ret1 != NULL) {
		return 1;
	}

	return 0;
}

void delete_game(User_conected* user, char* message) {

	printf("Delete game");

	long val;
	char *next;
	char *ret = strchr(message, ',');

	if (ret != NULL) {
		*ret = '\0';
		ret++;
	}
	val = strtol(message, &next, 10);
	if (val > COUNT_Game || (next == address) || (*next != '\0') || val < 0) {
		invalid_input(user);
		return;
	}

	if (game[val] == NULL) {
		invalid_input(user);
		return;
	}

	int i = val;
	int id1 = game[i]->gamer1;
	int id2 = game[i]->gamer2;


	if (conected_users[id1] != NULL) {
		conected_users[id1]->play = 0;
	}

	if (conected_users[id2] != NULL) {
		conected_users[id2]->play = 0;
	}

	if (conected_users[id2] != NULL) {
		conected_users[id2]->game = NULL;
	}

	if (conected_users[id1] != NULL) {
		conected_users[id1]->game = NULL;
	}


	free(game[i]);
	game[i] = NULL;
}



/*
* *createThread(void *incoming_socket)
*
* Funkce pro rizeni vlakna
* Vytvori pripojeni s klientem
* Prijima a rozdeluje prihozi zpravy
*
*/
void *createThread(void *incoming_socket) {

	char* buffer = malloc(1024);
	char* pom = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	int socket = *(int *)incoming_socket;
	User_conected* user = put_user(socket);

	user->protihracLogOut = 0;
	while (1) {

		int ret = sgetline(socket, &buffer);
		if (ret < 0)
			break;

		if (ret == 0) {
			printf("End of Headers detected.\n");
			break;
		}

		printf("obsah  %s \n", buffer);
		char *ret2 = strchr(buffer, ',');

		if (ret2 != NULL) {
			*ret2 = '\0';
			ret2++;
		}
		else {
			invalid_input(user);
			break;
		}

		if (strcmp(buffer, "Registrace") == 0) {

			reg_user(ret2, user);


		}
		else if (strcmp(buffer, "CheckConnect") == 0) {

			send_message(user, "CheckConnect,\n");
			sleep(1);

		}
		else if (strcmp(buffer, "DeleteGame") == 0) {

			delete_game(user, ret2);
		}
		else if (strcmp(buffer, "Log") == 0) {
			log_control(user, ret2);
		}
		else if (strcmp(buffer, "LogOut") == 0) {

			if (user->protihracLogOut != 1) {
				logout_user(user, ret2);
			}

		}
		else if (strcmp(buffer, "PlayerList") == 0) {
			if (control_player_list(ret2) == 1) {
				invalid_input(user);
				break;
			}
			send_free_players(user);

		}
		else if (strcmp(buffer, "Challenge") == 0) {
			receive_challenge(user, ret2);

		}
		else if (strcmp(buffer, "Game") == 0) {
			if (user->protihracLogOut != 1) {

				receive_game(user, ret2);

			}

		}
		else {
			invalid_input(user);
			sprintf(pom, "Prijata zprava :%s nevalidni vstup \n", buffer);
			write_log(pom);
			break;

		}

	}

	free(pom);
	free(buffer);
	pull_user(user);
	close(socket);
	return NULL;
}

/*
* find_free_index()
*
* Pomocna funkce pro nalezeni volne pozice pro prihlaseni uzivatele
*
*/
int find_free_index() {

	pthread_mutex_lock(&lock);
	int i;
	for (i = 0; i < MAX_CONECTED; ++i) {
		if (conected_users[i] == NULL) {

			return i;

		}
	}

	pthread_mutex_unlock(&lock);

	return -1;

}

void print_err(char* msg) {
	printf("error (%i, %s): %s\n", errno, strerror(errno), msg);
}

//--------------------------------------------------------------------------

//--------------------------------------------------------------------------

/*
*  start_server(int argc, char** argv)
*
* Funkce pro kontrolu argumentu prijatych z prikazove radky
* a nasledne spusteni serveru se zadanymi argumenty
*
*/
void start_server(int argc, char** argv) {

	if (argc == 1 || argc == 3) {
		printf("Start server\n");
		if ((nacti_Port(argc, argv) == 0) && (read_address(argc, argv) == 0)) {
			time(&log_time);
			signal(SIGINT, sigint_handler);
			;
			printf("Server bezi na portu %d\n", srv_port);
		}
		else {

			printf("Stop server\n\n");

			help();
			exit(1);
		}

	}
	else {

		help();
		exit(1);
	}
}

int main(int argc, char** argv) {

	start_server(argc, argv);
	char* pom = (char*)malloc(MAX_CONECTED * 30 + MAX_CONECTED);

	memset(conected_users, 0, sizeof(User_conected*) * MAX_CONECTED);

	int sock = -1, incoming_sock = -1;
	int optval = -1;
	struct sockaddr_in addr, incoming_addr;
	unsigned int incoming_addr_len = 0;

	if (pthread_mutex_init(&lock, NULL) != 0) {
		printf("\n Chyba inicializace mutexu.\n");
		write_log("Chyba serveru \"Chyba inicializace mutexu.\":\n");
		return 1;
	}
	/* create socket */
	sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

	if (sock < 0) {
		print_err("socket()");
		write_log("Chyba serveru \"socket\":\n");
		return 1;
	}

	/* set reusable flag */
	optval = 1;
	setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval));

	/* prepare inet address */
	memset(&addr, 0, sizeof(addr));
	addr.sin_family = AF_INET;
	addr.sin_port = htons(srv_port);

	if (is_address) {
		addr.sin_addr.s_addr = htonl(INADDR_ANY); /* listen on all interfaces */
		sprintf(pom, "Server bezi na vsech interfaces: \n");
		write_log(pom);
	}
	else {
		addr.sin_addr.s_addr = inet_addr(address);
		sprintf(pom, "Server bezi na adrese %s: \n", address);
		write_log(pom);
	}

	if (bind(sock, (struct sockaddr*) &addr, sizeof(addr)) < 0) {
		print_err("bind");
		write_log("Chyba serveru \"bind\":\n");
		return 1;
	}

	if (listen(sock, 10) < 0) {
		print_err("listen");
		write_log("Chyba serveru \"listen\":\n");
		return 1;
	}

	for (;;) {
		incoming_addr_len = sizeof(incoming_addr);
		incoming_sock = accept(sock, (struct sockaddr*) &incoming_addr,
			&incoming_addr_len);
		if (incoming_sock < 0) {
			print_err("accept");
			write_log("Chyba serveru accept\n");
			close(sock);
			continue;
		}

		char* message = "Connect\n";
		printf("Spojeni od %s:%i\n", inet_ntoa(incoming_addr.sin_addr),
			ntohs(incoming_addr.sin_port));
		sprintf(pom, "Spojeni od %s:%i\n", inet_ntoa(incoming_addr.sin_addr), ntohs(incoming_addr.sin_port));

		write_log(pom);

		pthread_t thread;

		/* create a second thread which executes inc_x(&x) */
		if (pthread_create(&thread, NULL, createThread, &incoming_sock)) {

			fprintf(stderr, "Chyba pri vytvareni vlakna.\n");
			write_log("Chyba serveru \"creating thread\" \n ");
			return 1;

		}

	}
	pthread_mutex_destroy(&lock);
	fclose(file);
	return 0;
}

