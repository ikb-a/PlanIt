#!/usr/bin/env python

## 
# Read from stdin: A series of lines of comma separated words.
# Every two lines are a pair which are treated as a single query for word similarity.
# The result of each query is a matrix of similarity values. A final empty line signals the end of the input.
# Written to stdout: A series of matrices, in python formatting.
##

import socket
import sys
import os

def main():

	#connect
	server_path = os.environ.get('GENSIM_SERVER')
	if not server_path:
		sys.stderr.write("Could not locate the gensim server. Please make sure the environment variable GENSIM_SERVER points to its directory\n")
	server_address = server_path + "/uds_socket"
	sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)	
	try:
		sock.connect(server_address)
		sys.stderr.write("Connected\n")
	except socket.error, msg:
                sys.stderr.write("Could not connect\n")
		sys.exit(1)

	#read the queries
	queries	= get_queries(sys.stdin)

	if len(queries) < 1:
		sys.stderr.write("No input was read\n")
		exit(1)
        else:
                sys.stderr.write("read a query: " + str(queries) + "\n")

	#communicate
	try:
		for query in queries:
			send_request(sock, query)
			response = get_response(sock)
			sys.stderr.write("got a response:\n")
			sys.stdout.write(response)		

	except Exception:
		sock.close()
		exit(1)	

	#cleanup
	sock.close()	
	exit(0)

def get_queries(input_source):
	"""
	Calls read() on the input_source until a double newline is reached
	The format of the input should match what is specificed at the top of this file
	Returns queries in the formateed expected for the server
	Returns None  if the input is not well formed
	"""

	try:
		data = ""
		while 1:
			rec = input_source.read(256)
			if rec == None or len(rec) < 1:
				break
			data = data + rec
			if rec.find("\n\n") != -1:
				break
	
		lines = data.split("\n")
		return [lines[i] + "\n" + lines[i+1] + "\n"  for i in range(0, len(lines) - 2, 2)]
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
