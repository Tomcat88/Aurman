#!/bin/bash
AURMAN=$(readlink -f $(realpath $0))
java -jar $(sed -e 's/aurman.sh//'<<< "$AURMAN")target/uberjar/aurman-0.1.0-standalone.jar $*
