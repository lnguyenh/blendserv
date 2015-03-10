#!/usr/bin/env bash
# Sorry for the hackiness! :)

poll_rate=5s
username="$BLENDER_USERNAME"
password="$BLENDER_PASSWORD"
hostname="$BLENDER_HOSTNAME"

   blender_icon[0]="$(pwd)/strobe_off.gif"
   blender_text[0]="The blender is now turned off"
blender_urgency[0]="normal"

   blender_icon[1]="$(pwd)/strobe.gif"
   blender_text[1]="The blender is now turned on"
blender_urgency[1]="critical"

usage() {
	echo -e "Blender notifier\n"
	echo "Usage: $(basename $0) [options]"
	echo -e "Example: $(basename $0) -u username -p password -h hostname\n"
	echo "-u <user>      Self-explanatory (default: '')"
	echo "-p <password>  Self-explanatory (default: '')"
	echo "-h <hostname>  Self-explanatory (default: '')"
	echo 
	echo "This script will by default read user/pass/host from the"
	echo "BLENDER_USERNAME, BLENDER_PASSWORD, BLENDER_HOSTNAME"
	echo "environment variables."
	echo
	echo "Note: This script expects to find strobe icons in the "
	echo "      same directory it is run from, so you might want"
	echo "      to cd into that path first before running this."
	echo
}

while [ $# -gt 0 ]; do 
	case "$1" in
		-u) username="$2";;
		-p) password="$2";;
		-h) hostname="$2";;
		-h) usage
		    exit 0;;
	esac
	shift
done

if [ -z "$hostname" -o -z "$username" -o -z "$password" ]; then
	echo -e "Error: Username, password or hostname is unset\n" >&2
	usage
	exit 1
fi

deps=(curl notify-send)
count=0

for dep in ${deps[*]}; do
	if ! which "$dep" > /dev/null 2>&1; then
		echo "Missing dependency: $dep" >&2
		((count++))
	fi
done

if [ $count -ne 0 ]; then
	echo "Error: $count dependencies not met" >&2
	exit 1
fi

unset count
unset deps

state_now=
state_last="not state_now"

while true; do
	state_now=$(curl --silent --user "$username":"$password" "$hostname")

	if [ "$state_now" -ne "0" -a "$state_now" -ne "1" ]; then
		echo "Error: Unexpected response: \"$state_now\"" >&2
	else
		if [ "$state_now" != "$state_last" ]; then
			state_last="$state_now"
			notify-send --icon="${blender_icon[$state_now]}" \
				    --urgency="${blender_urgency[$state_now]}" \
				    "Blender State Changed" \
				    "${blender_text[$state_now]}"
		fi
	fi
		  
	sleep "$poll_rate"
done
