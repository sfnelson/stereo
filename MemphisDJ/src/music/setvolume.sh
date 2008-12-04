#!/bin/bash

mixerctl=$(which mixerctl)
amixer=$(which amixer)
if [ "x" != "x$mixerctl" ]; then
	vol=$(($1*255/100))
	$mixerctl -w outputs.mono=255
	$mixerctl -w outputs.master=$vol
#	$mixerctl -w inputs.dac=255
elif [ "x" != "x$amixer" ]; then
	$amixer -q set PCM $1
fi