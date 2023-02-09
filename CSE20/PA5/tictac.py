# main program
# assignment: programming assignment 5
# author: Andy Chen
# date: 6/1/20
# file: tictac.py emulates the tic tac toe game
# input: Board coordinates and the sign 'X' or 'O'
# output: board progression of tic tac toe
from board import Board
from player import Player
print("Welcome to TIC-TAC-TOE Game!")
while True:
	board = Board()
	player1 = Player("Bob", "X")
	player2 = Player("Alice", "O")
	turn = True
	while True:
		board.show()
		if turn:
			player1.choose(board)
			turn = False
		else:
			player2.choose(board)
			turn = True
		if board.isdone():
			break
	board.show()
	if board.get_winner() == player1.get_sign():
		print(f"{player1.get_name()} is a winner!")
	elif board.get_winner() == player2.get_sign():
		print(f"{player2.get_name()} is a winner!")
	else:
		print("It is a tie!")
	ans = input("Would you like to play again? [Y/N] ").upper()
	if (ans != "Y"):
		break
print("Goodbye!")