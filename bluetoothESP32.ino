#include "BluetoothSerial.h"
BluetoothSerial Blue;
int LED = 2;
int incoming;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Blue.begin("ESP32_LED_Control");
  Serial.println("Bluetooth device is ready to use");

  pinMode(LED,OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Blue.available()){
      incoming = Blue.read();
      Serial.println("Received");
      Serial.println("incoming");
      if(incoming == 49){
            digitalWrite(LED,HIGH);
            Blue.println("Led turned on");
         }
      if(incoming == 48){
          digitalWrite(LED,LOW);
          Blue.println("Led turned of");
        }
    }
  delay(20);
}
