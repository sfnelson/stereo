#!/usr/bin/env perl

while (<>) {
    while (m{(.)}g) {
	printf("%2x", ord($1));
    }
    print "\n";
}
