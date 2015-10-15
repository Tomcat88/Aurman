#!/bin/sh

lein uberjar
sudo ln -s $(pwd)/aurman.sh /usr/bin/aurman
