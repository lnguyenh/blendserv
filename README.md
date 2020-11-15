# Blendserv

## What is it?
Blendserv is a simple webserver that runs on a Raspberry Pi, allowing to toggle a red flash siren with a simple http request. Perfect for some extra fun when deploying production code at work for example.

<img src="siren.jpg" width="50%"><img src="gpio.jpg" width="50%">

## Requirements
- A Raspberry Pi with Raspbian installed
- Python3 installed
- The python package `gpiozero` installed
 
## Installation
1. Copy the file `server/blendserv.py` in the folder `/home/pi/blendserv/server/`
2. Place the file `server/blendserv.service` in `/lib/systemd/system/`
4. Install blendser.service  by running `sudo systemctl enable blendserv`
5. Run` sudo systemctl start blendserv`

## Usage
- Make a POST request at http://YOUR-PI-IP-ADDRESS:8192 with the credentials you (can) define in `blendserv.py`. This will toggle the red flash siren for 4 seconds. 
- Make a GET request to get the current status of the siren.

