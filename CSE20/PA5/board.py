class Board:
	def __init__(self):
		self.sign = " "
		self.size = 3
		self.board = list(self.sign * self.size**2)
		self.winner = ""

	def get_size(self):
		# return the board size (an instance size)
		return self.size

	def get_winner(self):
		# return the winner (a sign O or X) (an instance winner)
		return self.winner

	def set(self, index, sign):
		# mark the cell specified by the index with the sign (X or O)
		self.board[index] = sign
		
	def isempty(self, index):
		# return True if the cell specified by the index is empty (not marked with X or O)
		if self.board[index] == " ":
			return True
		else:
			return False
		
	def isdone(self):
		done = False
		# check all game terminating conditions, if one of them is present, assign the var done to True
		# depending on conditions assign the instance var winner to O or X
		if self.board[0] == self.board[1] and self.board[1] == self.board[2] and not self.isempty(0):
			done = True
			self.winner = self.board[0]
		elif self.board[3] == self.board[4] and self.board[4] == self.board[5] and not self.isempty(3):
			done = True
			self.winner = self.board[3]
		elif self.board[6] == self.board[7] and self.board[7] == self.board[8] and not self.isempty(6):
			done = True
			self.winner = self.board[6]
		elif self.board[0] == self.board[4] and self.board[4] == self.board[8] and not self.isempty(0):
			done = True
			self.winner = self.board[0]
		elif self.board[2] == self.board[4] and self.board[4] == self.board[6] and not self.isempty(2):
			done = True
			self.winner = self.board[2]
		elif " " not in self.board:
			done = True
		return done

	def show(self):
		x_label = "   A   B   C"
		cell_divider = " +---+---+---+"
		index = 0
		print("\n")
		print(x_label)
		for x in range(self.get_size()):
			print(cell_divider)
			print(str(x + 1), end="")
			for y in range(self.get_size()):
				print("| ", end = "")
				print(self.board[index], end = " ")
				index += 1
			print("|")
		print(cell_divider + "\n")
