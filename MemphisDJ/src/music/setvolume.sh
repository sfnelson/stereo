#!/bin/bash

mixerctl=$(which mixerctl)
aumix=$(which aumix)
if [ "x" != "x$mixerctl" ]; then
	vol=$(($1*255/100))
	$mixerctl -w outputs.mono=255
	$mixerctl -w outputs.master=$vol
#	$mixerctl -w inputs.dac=255
elif [ "x" != "x$aumix" ]; then
	$aumix -w $1
fi