#!/bin/bash
# Supply a .json file of events where each event has a url where the event was taken from.
# Each url has a domain, and the unique domains will be listed with a count of how many times wach occurs as an origin of an event
cat $1 | egrep "\"url\"\s*:\s*\"http(s)?://" | python extract_domain_from_url.py | sort | uniq -c | sort -r
