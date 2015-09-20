/*
 * The ARDUINO board is connected to the metawear board on the PIN 10 port.
 * As the metawear receives a signal (via bluetooth from the mobile app) it sends a Haptic motor signal to the arduino which converts the message to a servo command.
 *
 */
#include <Servo.h>

Servo myServo;  // create a servo object

//int const potPin = A0; // analog pin used to connect the potentiometer
int potVal;  // variable to read the value from the analog pin
int pos = 120;   // variable to hold the angle for the servo motor

void setup() {
    pinMode(10, INPUT);
    myServo.attach(9); // attaches the servo on pin 9 to the servo object
    Serial.begin(9600); // open a serial connection to your computer
}

int last = 0;
int val = 0;

void loop() {
    last = digitalRead(10);
    if (last == 1) {
        val++;
    } else if (val > 0) {
        pos = (val * 180) / 100;
        myServo.write(pos);
        Serial.println(val);
        Serial.println(pos);
        val = 0;
    }
    delay(1);
}

void _loop() {
    for (pos = 0; pos < 180; pos += 1)  // goes from 0 degrees to 180 degrees
    {                                  // in steps of 1 degree
        myServo.write(pos);              // tell servo to go to position in variable 'pos'
        Serial.println(analogRead(10));
        delay(15);                       // waits 15ms for the servo to reach the position
    }
    for (pos = 180; pos >= 1; pos -= 1)     // goes from 180 degrees to 0 degrees
    {
        myServo.write(pos);              // tell servo to go to position in variable 'pos'
        delay(15);                       // waits 15ms for the servo to reach the position
    }
    myServo.write(pos);              // tell servo to go to position in variable 'pos'

    delay(5000);
}

