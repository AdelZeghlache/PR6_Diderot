#include <stdio.h>
#include <unistd.h>
#include <string.h> /* for strncpy */

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <net/if.h>
#include <arpa/inet.h>

#include <netdb.h>
#include <ifaddrs.h>
#include <stdlib.h>

#include <pthread.h>

void * envoi_UDP(void *ptr);
void * ecoute_UDP(void *ptr);
long long int concatenate(long long int x, long long int y);
long long int ip2lli(char * ip);
char * makeUniqueID();

struct arg_struct {
    int sock;
    struct sockaddr* saddr;
}args;

int sock_s;
int port_udp=7272;
int port_tcp=5558;	
char *port_suiv= NULL;
char * port_multCast= NULL;
char * ip_suiv= NULL;
char * ipMultCast= NULL;
char * ip_connect = "192.168.0.40";
char * idsmsgs[1000];
int nb_msg = 0;
char *my_ip;

int main() {
	port_suiv= malloc(10*sizeof(char));
	port_multCast= malloc(10*sizeof(char));
	ip_suiv= malloc(11*sizeof(char));
	ipMultCast= malloc(11*sizeof(char));
	ip_connect = "127.0.0.1";


	int i=0;
	for(i=0;i<1000;i++){
		idsmsgs[i]=malloc(8*sizeof(char));
		strcpy(idsmsgs[i],"vide");
	}
	long long int test = ip2lli("255.0.0.1");
	printf("%lld--\n", test);
	struct ifaddrs *myaddrs, *ifa;
	struct sockaddr_in *s4;
	int status;
	my_ip=(char *)malloc(11*sizeof(char));
	status = getifaddrs(&myaddrs);
	if (status != 0){
		perror("Probleme de recuperation d'adresse IP");
		exit(1);
	}
	for (ifa = myaddrs; ifa != NULL; ifa = ifa->ifa_next){
		if (ifa->ifa_addr == NULL) continue;
		if ((ifa->ifa_flags & IFF_UP) == 0) continue;
		if ((ifa->ifa_flags & IFF_LOOPBACK) != 0) continue;
		if (ifa->ifa_addr->sa_family == AF_INET){
			s4 = (struct sockaddr_in *)(ifa->ifa_addr);
			if (inet_ntop(ifa->ifa_addr->sa_family, (void *)&(s4->sin_addr),
			my_ip, 64*sizeof(char)) != NULL){
				printf("Adresse IP :%s\n",my_ip);
			}
		}
	}
	freeifaddrs(myaddrs);	

	struct sockaddr_in adress_sock;
	adress_sock.sin_family = AF_INET;
	adress_sock.sin_port = htons(5555);
	inet_aton(ip_connect,&adress_sock.sin_addr);

	int descr=socket(PF_INET,SOCK_STREAM,0);
	int r=connect(descr,(struct sockaddr *)&adress_sock,
	sizeof(struct sockaddr_in));
	if(r!=-1){
		char buff[512];
		int size_rec=read(descr,buff,511*sizeof(char));
		buff[size_rec] = '\0';
		printf("Message : %s\n",buff);   //  ex: WELC 192.168.70.149 6969 235.255.255.255 9998
		
		//On parse la chaine
		char * pch;
		pch = strtok (buff," ");
		int i = 0;
		while (pch != NULL)
		{
			switch(i) {
				case 0:
					 break;
				case 1:
					 strcpy(ip_suiv,pch);
				case 2:
					 strcpy(port_suiv,pch);
					 break;
				case 3:
					 strcpy(ipMultCast,pch);
					 break;
				case 4 :
					 strcpy(port_multCast,pch);
					 break;
				default :
					break;
			}
			
			pch = strtok (NULL, " ");
			i++;
		}		

		//PROTOCOLE D'INSERTION
		char *mess = malloc(511*sizeof(char));

		strcpy(mess,"NEWC ");
		strcat(mess, my_ip);
		strcat(mess," ");
		strcat(mess,"7272");
		strcat(mess,"\n");
		
		printf("%s",mess);
		write(descr,mess,strlen(mess));
		
		size_rec=read(descr,buff,511*sizeof(char));
		buff[size_rec] = '\0';

		printf("Message : %s\n",buff);

	//FERMETURE DE LA CONNEXION TCP

	}
	
	pthread_t th1, th2;	

	//Envoie UDP
	
	
	pthread_create(&th1,NULL,envoi_UDP, NULL);
	//RECEPTION UDP
	int sock_l=socket(PF_INET,SOCK_DGRAM,0);
	sock_l=socket(PF_INET,SOCK_DGRAM,0);
	struct sockaddr_in address_sock;
	address_sock.sin_family=AF_INET;
	address_sock.sin_port=htons(7272);
	address_sock.sin_addr.s_addr=htonl(INADDR_ANY);
	int r_l=bind(sock_l,(struct sockaddr *)&address_sock,sizeof(struct sockaddr_in));
	if(r_l==0){
		struct arg_struct *args_l = malloc(sizeof(struct arg_struct));

		args_l->sock = sock_l;
		pthread_create(&th2,NULL,ecoute_UDP, (void *)args_l);

	}
	pthread_join(th1,NULL);
	pthread_join(th2,NULL);
	return 0; 
}

void *envoi_UDP(void *ptr){
	char tampon[512];
	char * test;
	int i=0;

	while(1){
		char a[100];
		char *pos;
		fgets(a, sizeof(a)-1, stdin);
		if((pos=strchr(a, '\n')) != NULL)
			*pos = '\0';
		strtok(a,"\n");		
		//printf("FLAG 1\n");
		int sock_s=socket(PF_INET,SOCK_DGRAM,0);
		struct addrinfo *first_info;
		struct addrinfo hints;
		memset(&hints, 0, sizeof(struct addrinfo));
		hints.ai_family = AF_INET;
		hints.ai_socktype=SOCK_DGRAM;

		int r_s=getaddrinfo(ip_suiv,port_suiv,&hints,&first_info);
		if(r_s==0){
			if(first_info!=NULL){
				struct sockaddr *saddr=first_info->ai_addr;

				test = makeUniqueID();
				//printf("flag 2\n");
				//printf("TEST : %s \n", test);
				if(strncmp(a,"WHOS",4)==0){
					strcat(a," ");
					strcat(a, test);
					//on ajoute test au tableau d'IDs
					store_id(idsmsgs, 1000, test);
					printf("Envoi de : WHOS : %s \n", a);
					
					sendto(sock_s,a,strlen(a),0,saddr,(socklen_t)sizeof(struct sockaddr_in));
				}
				else if(strncmp(a,"GBYE",4)==0){
					strcat(a," ");
					strcat(a,test);
					strcat(a," ");
					strcat(a,my_ip);
					strcat(a," ");
					strcat(a,"7272");
					strcat(a," ");
					strcat(a,ip_suiv);
					strcat(a," ");
					char * port = port_suiv + '0';
					strcat(a,(char *) port_suiv);
					printf("Envoi de : GBYE %s \n", test);
					//On construit le message
					
					store_id(idsmsgs, 1000, test);
					// on ajoute test au tableau d'IDs
					sendto(sock_s,a,strlen(a),0,saddr,(socklen_t)sizeof(struct sockaddr_in));
				}
				else{
					strcat(a," ");
					strcat(a,test);
					printf("Syntaxe error: Impossible d'envoyer le message \n");
				}	
			
			
			}
		}
	}
	return NULL;
}

void *ecoute_UDP(void *ptr){
	struct arg_struct *args = (struct arg_struct *)ptr;
	int sock_l = args->sock;
	
	char tampon[512];
	while(1){

		// Je traite
		int rec=recv(sock_l,tampon,512,0);
		tampon[rec]='\0';
		
		printf("Message recu : %s\n",tampon);
		
		if(strncmp(tampon,"WHOS",4)==0){
			char * test = makeUniqueID();
			char MAE[512];
			strcat(MAE,"MEMB ");
			strcat(MAE,test);
			strcat(MAE," ");
			strcat(MAE,"cccccccc");
			strcat(MAE," ");
			strcat(MAE,my_ip);
			strcat(MAE," ");
			strcat(MAE,"7272");

			store_id(idsmsgs, 1000, test);								
			
			int sock_s=socket(PF_INET,SOCK_DGRAM,0);
			struct addrinfo *first_info;
			struct addrinfo hints;
			memset(&hints, 0, sizeof(struct addrinfo));
			hints.ai_family = AF_INET;
			hints.ai_socktype=SOCK_DGRAM;

			int r_s=getaddrinfo(ip_suiv,port_suiv,&hints,&first_info);
			if(r_s==0){
				if(first_info!=NULL){
					struct sockaddr *saddr=first_info->ai_addr;
					sendto(sock_s,MAE,strlen(MAE),0,saddr,(socklen_t)sizeof(struct sockaddr_in));
					printf("On envoie MEMB");
				}
			}
			
		}
		else if(strncmp(tampon,"EYBG",4)==0){
			exit(0);
		}
		else if(strncmp(tampon,"GBYE",4)==0){
			
		}

		int sock_s=socket(PF_INET,SOCK_DGRAM,0);
		struct addrinfo *first_info;
		struct addrinfo hints;
		memset(&hints, 0, sizeof(struct addrinfo));
		hints.ai_family = AF_INET;
		hints.ai_socktype=SOCK_DGRAM;

		int r_s=getaddrinfo(ip_suiv,port_suiv,&hints,&first_info);
		if(r_s==0){
			if(first_info!=NULL){
				struct sockaddr *saddr=first_info->ai_addr;
				sendto(sock_s,tampon,strlen(tampon),0,saddr,(socklen_t)sizeof(struct sockaddr_in));
	
			}
		}
		

	
	}
	return NULL;
}

void *ecoute_TCP(void *ptr){
	char tampon[100];
	
	return NULL;
}


int store_id(char ** a, int size, char * value){
    int i;
    for (i=0; i<size; i++)
    {
	 if (strcmp(a[i],"vide")==0)
	 {
         strcpy(a[i],value);
	    return 1;  /* it was found */
	 }
    }
    return -1;
}

int delete_id(char ** a, int size, char * value){
    int i;
    for (i=0; i<size; i++)
    {
	 if (strcmp(a[i],value)==0)
	 {
         strcpy(a[i],"vide");
	    return 1;  /* it was found */
	 }
    }
    return -1;
}

int contains(char ** a, int size, char * value){
   int i;
   for (i=0; i<size; i++)
   {
	if (strcmp(a[i],value)==0)
	{
	   return 1;  /* it was found */
	}
   }
   return -1;  /* if it was not found */
}

long long int concatenate(long long int x, long long int y) {
    int pow = 10;
    while(y >= pow)
        pow *= 10;
    return x * pow + y;
}

long long int ip2lli(char * ip){
	long long int res;
	char * pch;
	char * pres = malloc(64*sizeof(char));
	
	char str[64]; 
	strcpy(str,ip);
	   const char s[2] = ".";
	   char *token;
	   
	   /* get the first token */
	   token = strtok(str, s);
	   /* walk through other tokens */
	   while( token != NULL ) 
	   {
	      strcat(pres, token);
	      token = strtok(NULL, s);
	   }
	res = atoll(pres);
	//free(pres);
	return atoll(pres);
}

char * makeUniqueID(){
	char * res = malloc(sizeof(char));
//VERROU
	nb_msg++;
//FERMER VERROUR
	long long int ip = ip2lli(my_ip);
	long long int ipPort = concatenate(ip, port_udp);
	long long int idUnique = concatenate(ipPort, nb_msg);
	//printf("ID message =  %lld \n",idUnique);

	unsigned char toto[8*sizeof(char)];
	memcpy(toto, &idUnique, 8*sizeof(char));
	
	strcpy(res,toto);
	return res;

}
