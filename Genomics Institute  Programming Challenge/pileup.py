import sys
import re

def ref_chromosome(file, chr):
	#First delimit all of the alphabet characters to only get the chromosome number
	#Then join that number (in a list) to an empty string
	chromosome_number = ''.join(re.split('[a-zA-Z]', chr))
	file.write("Chromosome %s " % chromosome_number)

def ref_position(file, pos):
	file.write("Position %s " % pos)

def consensus_base(file, original_base, coverage, pile, base_qualities):
	#If the sequencing coverage is less than 5x or greater than or equal to 100x,
	#then the calculated consensus base will dfault to the original reference base
	if float(coverage) < 5 or float(coverage) >= 100:
		file.write("Base %s \n" % original_base)
		return
	#If the sequence coverage is greater than or equal to 5x, but less than 100x
	else:
		#First thin out the pile of any bases with a quality score of under 40 and delimit
		#all of the characters that are not needed (i.e. $, ^x, ACGT) before passing it through the method
		thinned_out = thin_out(''.join(re.split('\\^.|\\$|\\+\\d+[ACGT]+|\\+\\d+[acgt]+|\\-\\d+[ACGT]+|\\=\\d+[acgt]+',pile)), base_qualities)
		#Now thin out the pile even further by getting rid of the ',' and '.' as you only need
		#the bases that are not the same as the original reference base
		#Additionally, convert the string to all uppercase as the bases do not differ if they are
		#lower case or upper case. (i.e. the pdf example stated that a and A counts as 2 A's)
		thinned_out_more = ''.join(re.split('\\.|,', thinned_out)).upper()
		for element in thinned_out_more:
			element_count = thinned_out_more.count(element)
			if float(element_count)/float(len(thinned_out)) >= 0.8:
				file.write("Base %s \n" % element)
				return
		file.write("Base %s \n" % original_base)

def thin_out(pile, base_qualities):
	new_pile = []
	#Traverse through either the pile or base_qualites; doesnt matter which one as they have
	#the same size
 	for element, quality_char in zip(pile, base_qualities):
 		#Calculate the quality score of the current base_quality character
 		quality_score = ord(quality_char) - 33
 		#If the quality score is greater than or equal to 40, then append the corresponding
 		#position pile character to the new_pile
 		if quality_score >= 40:
 			new_pile.append(element)
 	#conver the new_pile list to a string and return the new_pile
 	return ''.join(new_pile)


def main():
	#For every input file
	for arg in sys.argv[1:]:
		#Open the input file with the intention to read and create an output file with the intention to write
		pile_file = open(arg, "r")
		output_file = open("output", "w")
		#Read the input file (pile file) line by line
		linelist = pile_file.readlines()
		for line in linelist:
			#For every line, delimit that tab and store that in a list
			lineTabDelim = line.split("\t")
			#Now find the chromosome number, position, and consensus base
			ref_chromosome(output_file, lineTabDelim[0])
			ref_position(output_file, lineTabDelim[1])
			consensus_base(output_file, lineTabDelim[2], lineTabDelim[3], lineTabDelim[4], lineTabDelim[5])

if __name__ == "__main__":
	main()
