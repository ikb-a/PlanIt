#!/usr/bin/python

# Supply a list of strings, where on each line there is one url.
# The domain of each url is returned with the rest of the url stripped.

import sys, fileinput

def main ():

    for line in fileinput.input(sys.argv[2:]):

        start = line.find("://") + 3
        end = line[start:].find("/") + start
        sys.stdout.write(line[start:end] + "\n")

if __name__ == "__main__":
    main()
