import traceback

class PythonWorker(object):

	def __init__(self, func):
		self.func = func

	def run(self):
		try:
			self.func()
			#return 0 if no errors occured
			print('0')
		except Exception, err:
			print(traceback.format_exc())
