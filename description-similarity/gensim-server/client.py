#!/usr/bin/env python

##
# Read from stdin: A series of lines of comma separated words.
# Every two lines are a pair which are treated as a single query for word similarity.
# The result of each query is a matrix of similarity values. A final empty line signals the end of the input.
# Written to stdout: A series of matrices, in python formatting.
##

import socket
import os
import sys
import fileinput

def main():

	#read the queries
	queries	= get_queries()

	if queries == None or len(queries) < 1:
		sys.stderr.write("No input was read\n")
		exit(1)

	#connect
	server_address = os.environ.get("GENSIM_SERVER")
	if not server_address:
		sys.stderr.write("Server address could not be located")
	server_address = server_address + "/uds_socket"	
	sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)	
	try:
		sock.connect(server_address)
	except socket.error, msg:
                sys.stderr.write(str(msg) + "\n")
		sys.exit(1)



	#communicate
	try:
		for query in queries:
			send_request(sock, query)
			response = get_response(sock)
			sys.stdout.write(response)
			sys.stdout.write("\n")

	except Exception:
		sock.close()
		exit(1)	

	#cleanup
	sock.close()	
	exit(0)

def get_queries():
	"""
	The format of the input should match what is specificed at the top of this file
	Returns queries in the formateed expected for the server
	Returns None  if the input is not well formed
	"""

        try:
                lines = sys.stdin.read().split("\n")
                while len(lines) and len(lines[-1]) == 0:
                        lines.pop()
                return [lines[i] + "\n" + lines[i+1] + "\n"  for i in range(0, len(lines) - 1, 2)]
        except Exception:
                return None

def send_request(connection, request):
	"""
	Sends a request (a string) to a connection. Returns True if the request was sent.
	"""
	try:
		connection.sendall(request)
		return True
	except Exception:
		return False

def get_response(connection):
        """
	Reads and returns the entire response.
	The response is newline terminated.
	None is returned if the entire response could not be read.
        """

        data = ""
        rec = ""

        try:
                while 1:
                        rec = connection.recv(256)
                        if not rec:
                                break
			data = data + rec
			if rec.count("\n") > 0:
				break
        except Exception:
		return None

        return data
	

if __name__ == "__main__":
	main()
