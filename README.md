# metaboard
The metaboard is a DIY electric longboard project.

## Vision

The basic concept is to create an electro longboard which can be configured through my phone, and no controller required to ride.
To achieved the goal I would like to build a button like mechanism on the board, the button's function is simple when I press it,
the board is speeding up until a specific maximum speed, when I release it, the board is slowing down (this is very important, because when I fall off the board it will stop and not goes away).
With my phone I would be able to increase or decrease the maximum speed and set the characteristics of the acceleration and the breaking.

## Project description

### Hardware parts
* my bustin longboard
* a 270kv electro motor
* a 5800mA LiPo battery
* Hobby King 150A ESC
* an arduino uno board (for controlling the ESC)
* mbientlab [metawear](https://www.mbientlab.com/) board  (for the convenient BLE communication between my phone and the arduino)

### Software
* a Java Android mobile application, using the mbientlab SDK
* arduino C script which runs on the board
