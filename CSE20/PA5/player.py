class Player:
	def __init__(self, name, sign):
		self.name = name
		self.sign = sign
	def get_sign(self):
		# return an instance sign
		return self.sign

	def get_name(self):
		# return an instance name
		return self.name

	def convert_to_index(self, userInput):
		index = 0
		y_column = ord(userInput[0].lower()) - 97
		x_column = int(userInput[1])
		if x_column == 1:
			x_column = x_column - 1
		for i in range(x_column + 1):
			index = index + i
		return index + y_column

	def verify_input(self, board, userInput):
		while((len(userInput) != 2) or userInput[0] not in "ABCabc" or userInput[1] not in "123" or not board.isempty(self.convert_to_index(userInput))):
			print("You did not choose correctly.")
			userInput = input("{}, {}: Enter a cell [A-C][1-3]:".format(self.name, self.sign))
		return self.convert_to_index(userInput)

	def choose(self, board):
		# prompt the user to choose a cell
		# if the user enters a valid string and the cell on the board is empty, update the board
		# otherwise print a message that the input is wrong and rewrite the prompt
		# use the methods board.get_size(), board.isempty(index), and board.set(index, sign)
		# you need to convert A1, B1, …, C3 cells into index values from 1 to 9
		# you can do the conversion here in the player.py or in the board.py
		# this implemenation is up to you
		userInput = self.verify_input(board, input("{}, {}: Enter a cell [A-C][1-3]:".format(self.name, self.sign)))
		board.set(userInput, self.get_sign())
