#include <SoftwareSerial.h>
#include <Servo.h>

//  the speed of the BT module
#define BLUETOOTH_SPEED 57600

const int ESC_PIN = 9;
const int BUTTON_PIN = 13;

//  the BT and Arduino communication port [RX, TX]
SoftwareSerial btSerial(10, 11);

//  ESC Servo communication on port ESC_PIN
Servo boardEsc;

// the current status of the servo
bool servoAttached = false;

//  the name of the BT module
String _btName;

//  the current servo value
int servoMaxThrottle = 0;
int servoCurrentThrottle = 0;

int buttonState = 0;

void setup() {
  //  open the console
  Serial.begin(9600);

  //  init the button
  pinMode(BUTTON_PIN, INPUT);

  //  start the BT module communication
  btSerial.begin(BLUETOOTH_SPEED);
  delay(1000);

  //  send some basic command to the BT module
  _btName = sendCommand("AT+VERSION");
  if (_btName.length() > 0) {
    Serial.println("BT module named '" + _btName + "' is ready to use.");
  } else {
    Serial.println("Failed to initialize BT module.");
  }
}

void modeBoardControl() {
  buttonState = digitalRead(BUTTON_PIN);

  if (btSerial.available() > 0) {
    int val = btSerial.read();
    Serial.println(val);
    if (val == 0) {
      boardEsc.detach();
      servoAttached = false;
      Serial.println("Servo DETACH command received.");
    } else if (val == 1) {
      servoMaxThrottle = 1500;
      boardEsc.attach(ESC_PIN, 700, 2000);
      servoAttached = true;
      Serial.println("Servo ATTACH command received.");
    } else {
      servoMaxThrottle = map(val, 0, 100, 700, 2000);
    }
  }

  //  write the servo value
  if (servoAttached) {
    if (buttonState == 1) {
      servoCurrentThrottle = 1550;
    } else {
      stepThrottle();
    }
    boardEsc.writeMicroseconds(servoCurrentThrottle);
  }
}

void loop() {
  modeBoardControl();
}

int stepCycle = 6;
int cycleCnt = 0;
void stepThrottle() {
  if (servoCurrentThrottle != servoMaxThrottle) {
    cycleCnt++;
    if (cycleCnt == stepCycle) {
      cycleCnt = 0;
      if (servoCurrentThrottle < servoMaxThrottle) {
        servoCurrentThrottle++;
      } else {
        servoCurrentThrottle--;
      }
    }
    Serial.println(servoCurrentThrottle);
  }
}

/**
 * Sends a command to the bluetooth module and returns the response.
 *
 * @param String cmd - the command which will be sent to the BT module
 * return the response from the module
 */
String sendCommand(String cmd) {
  btSerial.print(cmd);
  delay(1000);
  if (btSerial.available()) {
    return btSerial.readString();
  }
  return "";
}

int convertEscCmd(float val) {
  val = val * ((2000 - 700) / 100);
  val += 700;
  return val;
}

