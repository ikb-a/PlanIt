#!/usr/bin/env python

##
# Running this script will initializer the gensim server.
# To interact with the server use the client.py script
#
# To make a word-similarity-matrix 'request', the expected format is two lines of comma separated words
#
# As an optional argument, the path to a word2vec model can be supplied, otherwise a default is used
##

import socket
import sys
import os
import gensim
import string

word2vec_model_path = "/home/ikba/Sum2016/Word2Vec/GoogleNews-vectors-negative300.bin"
word2vec_model = None

def main():

	#use the supplied word2vec model
	if len(sys.argv) > 1:
		word2vec_model_path = sys.argv[1]

        sys.stderr.write("\n")

	sys.stderr.write("Loading Word2Vec model, this may take several minutes ...\n")
	get_word2vec_model()
	sys.stderr.write("Model loaded. Preparing to accept requests...\n")

	#server address
	server_address = os.environ.get("GENSIM_SERVER")
	if not server_address:
		sys.stderr.write("Server address could not be located")
	server_address = server_address + "/uds_socket"	
	try:
		os.unlink(server_address)
	except OSError:
		if os.path.exists(server_address):
			raise

	#socket
	sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
	sock.bind(server_address)
	sock.listen(1)

	#communication
	while 1:
		sys.stdout.write("Ready for requests.\n")
		#accept a single connection to work with		
		connection, client_address, = sock.accept()

		try:
                        sys.stderr.write("Connected to client\n")
			#request / response
			while 1:
				raw_request = get_request(connection)
				if raw_request == None or len(raw_request) < 1:
					break
				request = parse_input(raw_request)
				response = ""
				if request:
					response = compute_response(request)
				send_response(connection, response)

				print "(S):"
                                print "request = " + str(request)
                                print "response = " + str(response)
		#cleanup
		finally:
                        sys.stderr.write("Client disconnected\n")
			connection.close()
        sys.stderr.write("Server closing\n")
	exit(0)

def get_request(connection):
	"""
	Get all of he input sent from the connection.
	This function will read input from the socket until two newlines chars have been read
	"""
        data = ""
        rec = ""
	num_newlines_rec = 0
	try:
	        while num_newlines_rec < 2:
        	        rec = connection.recv(256)
        	        if not rec:
				break
			else:
				data = data + rec
				num_newlines_rec += rec.count("\n")
	except Exception:
		pass

	return data

def parse_input(raw_input):
	r"""
	Parse the raw input from a connection into two word lists
	Return None if the input could not be parsed

	>>> parse_input("a,b,c\nd,e,f,g\n")
	(['a', 'b', 'c'], ['d', 'e', 'f', 'g'])

	>>> parse_input("12345")

	"""

	to_return = None

	try:
		splitted = raw_input.split("\n")
		s1 = splitted[0]
		s2 = splitted[1]
		words1 = s1.split(",")
		words2 = s2.split(",")
		to_return = (words1, words2)
	except IndexError:
		pass

	return to_return

def compute_response(request):
	"""
	Computes and returns the response to send as a string.
	request should be two lists of words
	The response is a string of a matrix (CSV)
	"""

	model = get_word2vec_model()

	matrix = [[str(get_word_similarity(w1, w2)) for w2 in request[1]] for w1 in request[0]]

        matrix = string.join([string.join(matrix[i], ",") for i in range(len(matrix))], "\n")
	
	return matrix

def get_word_similarity(token1, token2):
        """
        This is a wrapper for the similarity function of the word2vec model.
        Returns the similarity of token1, token2 or -1 if either word was not in the vocabulary
        """
        try:
                return get_word2vec_model().similarity(token1, token2)
        except KeyError:
                return -1

def send_response(connection, response):
	"""
	Sends a response message to a socket.
	Returns True if the message was sent.
	"""

	try:
		connection.sendall(response + "\n\n")
		return True
	except Exception:
		return False

def get_word2vec_model():

	global word2vec_model
	global word2vec_model_path

	if word2vec_model == None:
		try:
			word2vec_model = gensim.models.Word2Vec.load_word2vec_format(word2vec_model_path, binary=True)		
		except Exception:
			sys.stderr.write("Gensim could not be loaded, using a placeholder model instead\n")
			word2vec_model = Word2VecPlaceHolder()
	return word2vec_model

class Word2VecPlaceHolder():
	def similarity(self, w1, w2):
		return -1

if __name__ == "__main__":

	from tendo import singleton
	me = singleton.SingleInstance()

	main()
