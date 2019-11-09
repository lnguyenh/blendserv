#!/usr/bin/env python3
# Usage example:
# Get current blender state:
# curl --user user:pass http://hostname:port
# Set current blender state (to on)
# curl --user user:pass --data 'blender=1' http://hostname:port
from datetime import datetime

from gpiozero import LED
import threading
import http.server
import base64
import json

# Configuration
USER = 'froop'
PASSWORD = 'froopberry'
TIMEOUT = 4
PORT = 8192

# Constants
ON = 'on'
OFF = 'off'


class Blender(object):

	def __init__(self, timeout):
		# https://gpiozero.readthedocs.io/en/stable/recipes.html#pin-numbering
		# Connect the GPIO-18 pin and a ground pin (Ground 6 for example)
		self.pin = LED(18)
		self.timeout = timeout
		self.timeout_thread = None
		self.state = 0
		self.off()

	def handle_timeout(self):
		print("Timeout")
		self.off()

	def ensure_blender_turns_off(self):
		self.timeout_thread = threading.Timer(
			self.timeout,
			self.handle_timeout,
			[]
		)
		self.timeout_thread.start()

	def on(self):
		print('Setting blender to ON')
		self.pin.on()
		self.ensure_blender_turns_off()

		self.state = ON

	def off(self):
		print('Setting blender to OFF')
		self.pin.off()
		self.state = OFF

	def toggle(self):
		if self.state == ON:
			self.off()
		else:
			self.on()

	def cleanup(self):
		self.pin.close()


blender = Blender(timeout=TIMEOUT)


class CustomServerHandler(http.server.BaseHTTPRequestHandler):

	def do_HEAD(self):
		self.send_response(200)
		self.send_header('Content-type', 'application/json')
		self.end_headers()

	def do_AUTHHEAD(self):
		self.send_response(401)
		self.send_header(
			'WWW-Authenticate', 'Basic realm="The Satflandaren"')
		self.send_header('Content-type', 'application/json')
		self.end_headers()

	def do_GET(self):
		key = self.server.get_auth_key()

		if not self.headers.get('Authorization'):
			self.do_AUTHHEAD()
			response = {
				'success': False,
				'error': 'No auth header received'
			}
		elif self.headers.get('Authorization') == 'Basic ' + str(key):
			self.send_response(200)
			self.send_header('Content-type', 'application/json')
			self.end_headers()
			response = {
				'success': True,
				'message': 'Saftbandaren is online and lamp is {}'.format(blender.state)
			}
		else:
			self.do_AUTHHEAD()
			response = {
				'success': False,
				'error': 'Invalid credentials'
			}

		self.wfile.write(bytes(json.dumps(response), 'utf-8'))

	def do_POST(self):
		key = self.server.get_auth_key()

		''' Present frontpage with user authentication. '''
		if self.headers.get('Authorization') is None:
			self.do_AUTHHEAD()
			response = {
				'success': False,
				'error': 'No auth header received'
			}

		elif self.headers.get('Authorization') == 'Basic ' + str(key):
			self.send_response(200)
			self.send_header('Content-type', 'application/json')
			self.end_headers()

			response = {
				'path': self.path,
			}

			# Add new paths here
			# base_path = urlparse(self.path).path
			# if base_path == '/path1':
			#	# Do some work
			#	pass

			blender.toggle()
			response['message'] = 'Saftblandaren toggled'

		else:
			self.do_AUTHHEAD()

			response = {
				'success': False,
				'error': 'Invalid credentials'
			}

		self.wfile.write(bytes(json.dumps(response), 'utf-8'))


class CustomHTTPServer(http.server.HTTPServer):
	key = ''

	def __init__(self, address, handlerClass=CustomServerHandler):
		super().__init__(address, handlerClass)

	def set_auth(self, username, password):
		self.key = base64.b64encode(
			bytes('%s:%s' % (username, password), 'utf-8')).decode('ascii')

	def get_auth_key(self):
		return self.key

print('Saftblandaren started at {}'.format(datetime.now()))
server = CustomHTTPServer(('', PORT))
server.set_auth(USER, PASSWORD)
try:
	server.serve_forever()
except Exception as e:
	print(e)
	pass
blender.cleanup()
server.server_close()
print('Saftblandaren exited at {}'.format(datetime.now()))
