# elBoard

This is my DIY electric longboard project name elBoard. The project's goal is to create a longboard which will be powered by an electronic motor.

## Vision

My vision is an electric longboard which can be controller with foot and no other in hand controller required, because carrying a controller during the ride is just simple inconvenient.
To replace the controller's functionality a button will be mounted on the board's surface, the button will provide two simple functions:

* when the button has pushed the board will increase the speed until the current maximum
* when the button has released the board will decrease the speed until it stops

The current setup's top speed will be around 25-30 km/h which can be too fast in some circumstances, because of that the board will be able to accept commands via bluetooth to set the:

* current maximum speed
* speed increasing characteristics
* speed decreasing characteristics

These parameters will be controlled by an Androis mobile application.

## Project description

### Hardware parts

* my bustin longboard
* a 270kv electric motor
* a 5800mA LiPo battery
* Hobby King 150A ESC
* an ARDUINO UNO board
* JY-MCU Bluetooth adapter for the mobile and board communication
* Android mobile device

### Software parts

* an Android 4.0 mobile application to control the ARDUINO board
* an ARDUINO C script to control the ESC

## Mobile application

### Requirements

1. The application should be able to communicate with the ARDUINO board via bluetooth.
2. The mobile application should connects to the board automatically at startup.
3. The application should be controlled by touch events and through the hardware volume buttons too.
4. The mobile device's volume buttons should control the board's current maximum speed.

### ARDUINO application

1. The ARDUINO board should be able to control the ESC according to the current maximum speed.
2. The ARDUINO board should be able to accept commands via bluetooth