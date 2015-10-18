#include <SoftwareSerial.h>

//  the speed of the BT module
#define BLUETOOTH_SPEED 57600

//  the BT and Arduino communication port [RX, TX]
SoftwareSerial mySerial(10, 11);

//  the name of the BT module
String _btName;

//  Arduino LED pin
int ledPin = 13;

//  the current readed state
int state = 0;

//  the current status of the LED
int flag = 0;

void setup() {

  //  init the LED
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);

  //  open the console
  Serial.begin(9600);

  //  start the BT module communication
  mySerial.begin(BLUETOOTH_SPEED);
  delay(1000);

  //  send some basic command to the BT module
  _btName = sendCommand("AT+VERSION");
  if (_btName.length() > 0) {
    Serial.println("BT module named '" + _btName + "' is ready to use.");
  } else {
    Serial.println("Failed to initialize BT module.");    
  }
}
void loop() {
  //if some data is sent, read it and save it in the state variable
  if (mySerial.available() > 0) {
    state = mySerial.read();
    flag = 0;
  }
  // if the state is 0 the led will turn off
  if (state == '0') {
    digitalWrite(ledPin, LOW);
    if (flag == 0) {
      Serial.println("LED: off");
      flag = 1;
    }
  }
  // if the state is 1 the led will turn on
  else if (state == '1') {
    digitalWrite(ledPin, HIGH);
    if (flag == 0) {
      Serial.println("LED: on");
      flag = 1;
    }
  }
}

/**
 * Sends a command to the bluetooth module and returns the response.
 *
 * @param String cmd - the command which will be sent to the BT module
 * return the response from the module
 */
String sendCommand(String cmd) {
  mySerial.print(cmd);
  delay(1000);
  if (mySerial.available()) {
    return mySerial.readString();
  }
  return "";
}
