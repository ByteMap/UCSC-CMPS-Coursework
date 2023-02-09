# assignment: programming assignment 4
# author: Andy Chen
# date: 5/19/20
# file: hangman.py is a program that emulates the hangman game
# input: game options and character inputs
# output: word progression and hp left

import random

dictionary_file = "dictionary.txt"

def import_dictionary (dictionary_file):
	max_size = 12
	dictionary = {}
	try:
		to_parse = open(dictionary_file, 'r');
		for word in to_parse:
			word = word.strip()
			if len(word) < max_size:
				if len(word) in dictionary:
					dictionary[len(word)].append(word)
				else:
					dictionary[len(word)] = [word]
			else:
				if max_size in dictionary:
					dictionary[max_size].append(word)
				else:
					dictionary[max_size] = [word]
	except Exception:
		print("Cannot import dictionary")
	return dictionary

def setup_word(dictionary):
	word_list = []
	try:
		word_size = int(input("Please choose a size of a word to be guessed [3 - 12, default any]: "))
		if word_size > 12 or word_size < 3:
			word_size = random.randint(3, 12)
			print("A dictionary word of any size will be chosen.")
		else:
			print("The word size is set to {}.".format(word_size))
		return random.choice(dictionary.get(word_size))
	except:
		word_size = random.randint(3, 12)
		print("A dictionary word of any size will be chosen.")
		return random.choice(dictionary.get(word_size))

def setup_lives():
	try:
		lives = int(input("Please choose a number of lives [1 - 10, default 5]: "))
		if lives < 1 or lives > 10:
			print("You have 5 lives.")
			print("")
			return 5
		else:
			print("You have {} lives.".format(lives))
			print("")
			return lives
	except:
		print("You have 5 lives.")
		print("")
		return 5

def choose_letter(letters_chosen):
	userinput = input("Please choose a new letter > \n")
	while(not userinput.isalpha() or len(userinput) > 1 or userinput.upper() in letters_chosen):
		if userinput.upper() in letters_chosen:
			print("You have already chosen this letter.")
		userinput = input("Please choose a new letter > \n")
	return userinput

def get_game_options(dictionary):
	word = setup_word(dictionary)
	lives = setup_lives()
	return word, lives

def game_won(word, letters_chosen):
	word_stripped = ""
	for char in word:
		if char.isalpha():
			word_stripped += char

	for char in word_stripped:
		if not char.upper() in letters_chosen:
			return False
	return True


def execute_game(word, lives):
	letters_chosen = []
	letters_displayed = []
	lives_left = []
	for char in word:
		if not char.isalpha():
			letters_displayed.append(char + " ")
		else:
			letters_displayed.append("__ ")
	for lives_counter in range(0, lives):
		lives_left.append("O")
	while lives >= 0:
		print("Letters chosen:", end=" ")
		print(', '.join(letters_chosen))
		print(''.join(letters_displayed), end=" ")
		print("lives: {}".format(lives), end=" ")
		print(''.join(lives_left))
		print("")

		chosen_letter = choose_letter(letters_chosen).upper()
		letters_chosen.append(chosen_letter)
		if chosen_letter in word.upper():
			print("You guessed right!")
			print("")
			index = 0
			for char in word:
				if char.upper() == chosen_letter:
					letters_displayed[index] = char.upper() + " "
				index += 1
			if(game_won(word, letters_chosen)):
				print("Congratulations!!! You won!The word was {}!".format(word.upper()))
				print("")
				return
		else:
			print("Your guessed wrong, you lost one life.")
			print("")
			lives_left[-lives] = "x"
			lives -= 1
	if lives < 0:
		print("You lost! The word was {}!".format(word.upper()))
		print("")

def main():
	dictionary = import_dictionary(dictionary_file)
	print("Welcome to Hangman Game!")
	print("")
	while True:
		word, lives = get_game_options(dictionary)
		execute_game(word, lives)
		play_again = input("Would you like to play again [Y/N]? \n")
		if play_again == 'Y' or play_again == 'y':
			continue
		else:
			print("Goodbye!")
			break

if __name__ == '__main__':
	main()