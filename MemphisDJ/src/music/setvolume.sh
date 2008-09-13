#!/bin/bash

mixerctl=$(which mixerctl)
amixer=$(which amixer)
if [ "x" != "x$mixerctl" ]; then
	$mixerctl -w outputs.mono=255
	$mixerctl -w outputs.master=$1
#	$mixerctl -w inputs.dac=255
elif [ "x" != "x$amixer" ]; then
	vol=$(($1*100/255))
	$amixer -q set PCM $vol
fi