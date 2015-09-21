# metaboard
This is my DIY electric longboard project, I know there are a few out there, but this is mine:)

## Vision

My vision is an electric longboard which can be rode without any kind of controller, so it's totally "hand free".

To achieve this, I would like to build a button like mechanism on the board. The button's functionality is simple, when I press it, the board is speeding up, when I release it, the board is slowing down. 
On the one hand, the button provides a simple, two phase, controlling mechanism, on the other hand, it works as a safety function, when I fall off the board, it will stop and not goes away. 

However, the ability of settting the proper maximum speed is also an important aspect. But, in my opinion, the continious speed controlling during the ride is not a need. Actually, I think, if I can set the maximum speed and the board accelerates until that speed when I step on the button is totally enough. So, I would like to create a mobile application which can set some basic parameters on the board's ESC, like:
* maximum speed
* accelerating characteristics
* breaking aggressivity

The difference between this and the other controller based solutions is that I only need a controller (my phone) when I would like to change the speed, for example once before and a few time under the ride.

## Project description

### Hardware parts
* my bustin longboard
* a 270kv electro motor
* a 5800mA LiPo battery
* Hobby King 150A ESC
* an arduino uno board (for controlling the ESC)
* mbientlab [metawear](https://www.mbientlab.com/) board  (for the convenient BLE communication between my phone and the arduino) (this is for prototyping only)

### Software
* a Java Android mobile application which can communicate with the board
* arduino C script which runs on the board
