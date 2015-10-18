# BoardBot

This is my DIY electric longboard project.

## Vision

My vision is an electric longboard which can be rode without any kind of "in hand controller", because carrying a controller during the ride is just simple inconvenient.
To replace the controller's functionality a button will be mounted to the boards surface, so this will be a two phase controller:

* when the button has pushed the board will increase the speed until the current maximum
* when the button has released the board will decrease the speed until it stops

The current board top speed will be about 25-30 km/h which can be too fast in some circumstances, because of that the board will be able to accept commands via bluetooth to set the

* current maximum speed
* speed increasing characteristics
* speed decreasing characteristics

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