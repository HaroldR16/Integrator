//librerias
#include <NewPing.h> //libreria para el sensor ultrasonico 
                     //https://bitbucket.org/teckel12/arduino-new-ping/wiki/Home

//Variables a utilizar
#define luzPin A0 //Sensor detector de luz
int M1_Izq = 12; //Direccion
int M1_Derecha = 11;
int M2_Izq = 8;//Direccion 2
int M2_Derecha = 9;
const int Trigger = 6;
const int Echo = 5;
const int MaxDistancia = 200;

//Constructor del metodo NewPing incluido en la libreria
NewPing sonar(Trigger, Echo, MaxDistancia);

//Setup
void setup()
{
  //Comienzo del detector de luz analogico
  Serial.begin(9600);
  pinMode(luzPin, INPUT);
  delay(1000);
  
  //Pines de los motores
  pinMode(M1_Izq, OUTPUT);
  pinMode(M1_Derecha, OUTPUT);
  pinMode(M2_Izq, OUTPUT);
  pinMode(M2_Derecha, OUTPUT);
}

void loop(){
  //Metodos para avanzar los motores
  avanzar ();
  delay(250); 
}

//Apartado metodos grandes

//Metodo avanzar
void avanzar()
{ 
  //Aun en testeo, no se si es necesario
  /*Variables boolean de inputs
  boolean inPin1 = LOW;
  boolean inPin2 = HIGH;*/
  
  //variable de la intensidad de la luz
  int intensidad;
  intensidad = analogRead(luzPin);
  Serial.println(intensidad);
  
 //Comienzo del if
  if(intensidad > 800){
    Serial.println("Encendido");
  
    //If de distancia si es mayor a 10 cm e igual a 0
    //0 representa que no se detecto objeto
    if(sonar.ping_cm(MaxDistancia) > 10 || sonar.ping_cm(MaxDistancia) == 0 ){
      //Ir hacia adelante
      adelante ();
      Serial.println(sonar.ping_cm(MaxDistancia));
    }else{
        stop();
        delay(2000);
        //Darse de reversa
        reversa ();
      }
    /*Testeo
    inPin1 = HIGH;
    inPin2 = LOW;*/
  }else{
    stop();
   }
    
}


//Apartado de metodos pequeños

//Metodo Stop
void stop(){
    digitalWrite(M1_Izq, LOW);
    digitalWrite(M1_Derecha, LOW);
    digitalWrite(M2_Izq, LOW);
    digitalWrite(M2_Derecha, LOW);
}

//Metodo Reversa
void reversa(){
    
    digitalWrite(M1_Izq, LOW);
    digitalWrite(M1_Derecha, HIGH);
    digitalWrite(M2_Izq, LOW);
    digitalWrite(M2_Derecha, HIGH);
}

//metodo adelante
void adelante(){

    digitalWrite(M1_Izq, HIGH);
    digitalWrite(M1_Derecha, LOW);
    digitalWrite(M2_Izq, HIGH);
    digitalWrite(M2_Derecha, LOW);
}
