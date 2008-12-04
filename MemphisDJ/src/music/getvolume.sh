#!/bin/bash

mixerctl=$(which mixerctl)
aumix=$(which aumix)
if [ "x" != "x$mixerctl" ]; then
	vol=$($mixerctl outputs.master | sed -e "s/^.*=\([0-9]*\),.*/\1/")
	echo $(($vol*100/255))
elif [ "x" != "x$aumix" ]; then
	$aumix -wq | cut -d " " -f 3
fi