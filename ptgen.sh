#!/usr/bin/env bash

red='\033[0;31m'			# Red
nc='\033[0m'				# No color
re='^[0-9]+$'				# Regular expression to detect natural numbers

usage() { echo -e "Usage: $0 -h <height> -l <length> -s <seed>\n-h\tHeight of the pseudotree is <height> * <length>\n-l\tLength of the chains\n-s\tSeed\n" 1>&2; exit 1; }

while getopts ":h:l:s:" o; do
	case "${o}" in
	h)
		h=${OPTARG}
		if ! [[ $h =~ $re ]] ; then
			echo -e "${red}Height must be a number!${nc}\n"
			usage
		fi
		;;
	l)
		l=${OPTARG}
		if ! [[ $l =~ $re ]] ; then
			echo -e "${red}Length must be a number!${nc}\n"
			usage
		fi
		;;
	s)
		s=${OPTARG}
		if ! [[ $s =~ $re ]] ; then
			echo -e "${red}Seed must be a number!${nc}\n"
			usage
		fi
		;;
	\?)
		echo -e "${red}-$OPTARG is not a valid option!${nc}\n"
		usage
		;;
	esac
done
shift $((OPTIND-1))

if [ -z "${h}" ] || [ -z "${l}" ] || [ -z "${s}" ]; then
	echo -e "${red}Missing one or more required options!${nc}\n"
	usage
fi

java -cp .:* PTGen $h $l $s
rc=$?
exit $rc
