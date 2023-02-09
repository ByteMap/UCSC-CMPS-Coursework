#Name: Andy Chen
#Email: achen81@ucsc.edu
#Lab Assignment: Lab 5: Decimal to Binary Converter
#Due Date: 2/28/18

#Pseudocode:
#Stage 1: Take User Input
#Program Argument takes in user input and stores the address of it into $a1
#Load $a1 into $s1 to store the address of the first digit of the user input using "ld (Load Doubleword)"
#Create a bit mask and store it into $t0 that represents 2^31 (100...000) that will be used later to print out the binary number
#End of Stage 1.
#
#Stage 2: Convert To Binary
#Load $s1 first byte into $t1
#Move $s1 into $s0
#Subtract $t1 by 45, which is ASCII for "-" and store it in $s2
	#If $s2 is not equal to 0, go to "grab_byte:"
	#$s1 = $s1 + 1
	#Load byte of $s1 into $t1
	#end_if
#Grab_Byte:
#$t1 = $t1 - 48
#$s1 = $s1 + 1
#Move $t1 into $s0
	#start_loop_convert:
	#Load Byte $s1 into $t1
	#If $t1 subtracted by 0 (null) equals 0, go to end_loop_convert.
		#$s0 = $s0*10
		#$t1 = $t1 - 48 (ASCII for 0)
		#$s0 = $s0 + $t1
		#$s1 = $s1 + 1
	#end if
#Branch back to "start_loop_convert"
#end_loop_convert:
#
#Stage 3: Convert To Negative
#If $s2 (negative or not negative) is not equal to 0, go to the label "start_print_loop"
	#Flip the bits of $s0 by xor bit-masking it with 2^32-1 and then add a 1
#end_if
#
#Stage 4: Print
#start_print_loop:
	#Move $s0 into $t5 so that $s0 will not be overridden by the bitmask
	#$t5 = $t5 andi 2^31 (bitmask of 100000...00000)
	#If $t5 is equal to 0 go to label print_0:
	#Print out 1
	#Branch to Shift_Right (b shift_right:)
	#print_0:
	#Print 0
	#shift_right:
	#Shift logical right $t0
	#If $t0 = 0, go to "end_print_loop:"
	#Branch back to "start_print_loop"
#end_print_loop: 
#
#Syscall 10


.text 
Stage_1:
ld $s1, ($a1)				#This stage only creates the registers that will be used later on in this program.
li $t0, 2147483648			#$s1 is used to load whatever is in $a1 (the user input), $t0 is treated as the bitmask
li $t2, 10				#2^31 that will be used in the printing loop, $t2 is used to multiply $s0 in order to get the
li $t3, 1				#sum of the binary number that is converted from ASCII, $t3 is a register that will be used to
li $t4, 0				#print 1 in the printing loop, and $t4 will be used to print out the 0 in the printing loop.
end_Stage_1:

Stage_2:
lb $t1, ($s1)				#This first block of code in stage 2 loads the first byte of $s1 (which contains the first byte
move $s0, $s1				#of the user input) and loads it into $t1. Then $s1 is copied down into $s0 so it will be used
lb $s2, ($s1)				#to store the calculated binary number of the user input later on. The first byte of $s1 is then also
subi $s2, $s2, 45			#loaded into $s2, where it will then be subtracted by 45 (ASCII for "-") and stored back into $s2.

bnez $s2, grab_byte			#This second block of code checks if $s2 is zero (negative) after subtracting it by the ASCII value
	addi $s1, $s1, 1		#of "-" (45). If it is not equal to zero, then go to the label "grab_byte", else, increment the 
	lb $t1, ($s1)			#memory location of $s1 by one and load that byte of $s1+1 into $t1.

	grab_byte:			#This third block is the "grab_byte" where it will subtract $t1, which, has the first byte that represents
	subi $t1, $t1, 48		#the first number of the user's input and subtract it by 48 (ASCII for "0") and store it back into $t1.
	addi $s1, $s1, 1		#Then the memory location of $s1 will be incremented by 1 and the updated binary equivialent  of the user's 
	move $s0, $t1			#first input byte will be grabbed and stored into $s0.
	
	start_loop_convert:			#This is the block where the conversion of ASCII to decimal representation of binary starts. At the
		lb $t1, ($s1)			#beginning of the loop, the byte where $s1 is pointint at is loaded into $t1. $t1 is then subtracted
		subi $t1, $t1, 0		#by 0 (ASCII for "null") in order to check if the end of the user input string has been reached. If $t1	
						#is equal to 0, meaning the last character is null, the program will be directed to the branch
		beqz $t1, end_loop_convert	#"end_loop_convert". Else, the program will continue reading through the loop where $s0 (the 
		multu $s0, $t2    		#register that will contain the calculated binary number) is multiplied by $t2 (10) and is then 
		mflo $s0			#stored back into $s0. Then, $t1, which, contains the byte to where $s1 is pointing at, will be 
		subi $t1, $t1, 48		#converted into binary equivalent of that stored byte (by subtracting 48 from it) and stored back
						#into $t1. $s0 is then added with the result of $t1, then, $s1 will be incremented by 1 to point
		addu $s0, $s0, $t1 		#to the next byte and then the program will be directed back to the start of the branch 
		addi $s1, $s1, 1		#"start_loop_convert"
		b start_loop_convert
	end_loop_convert:
end_stage_2_if:
end_Stage_2:

Stage_3:
bnez $s2, end_Stage_3
	xori $s0, 4294967295		#Stage 3 is where the negative sign will be used. If $s2, which contains the result if
	addi $s0, $s0, 1		#the user input is negative or not, is not equal to 0 (meaning that its not negative), 
end_Stage_3:				#the program will be directed to the branch "end_Stage_3". Else, the bits of $s0 will be 
					#flipped and 1 will be added to the result of $s0, which, will make $s0 negative in binary.

la $a0, user_input			#This block of code basically prints out the prompt "User input number:" followed up with
li $v0, 4				#$s0, which, is the stored converted user input, then a new line is printed using the 
syscall					#"newLine" label in the .data section of this program and loading the address of it into
move $a0, $s0				#$a0
li $v0, 1
syscall
la $a0, newLine
li $v0, 4
syscall

Stage_4:
la $a0, result				#Before starting the loop for Stage 4 to print out the binary number using bitmasks, 	
li $v0, 4				#the prompt "This number in binary is:" is printed out first
syscall

start_print_loop:			#Starting this loop to print out the binary number, I first move $s0 into $t5 so that 
	move $t5, $s0			#s0 will not be overridden by the bit mask. The program then ANDS $t5 with $t0 (2^31 
	and $t5, $t5, $t0		#or 100000..00000) and stores it back into $t5. The program then checks if $t0 is equal	
	beqz $t5, print_0		#to 0 or not and if it is equal to 0, the program will go to the label "print_0" and then
		li $v0, 1		#print out 0 using $t4(0) and moving it into $a0 and proceed through the loop. Else, it will
		move, $a0, $t3		#move $t3(1) into $a0 and print out a 1 then the program will go to the branch "shift_right"
		syscall
		b shift_right
	
		print_0:		
		li $v0, 1
		move $a0, $t4
		syscall
		
		shift_right:			#In this block "shift_right", the program will use the function srl, which, will logically
		srl $t0, $t0, 1			#shift $t0 1 bit to the right, then it checks if $t0 is equal to 0, meaning that $t0 has
		beqz $t0, end_print_loop	#been right logically shifted to the point where it equals 0. If $t0 is equal to 0, the 
		b start_print_loop		#program will go to the label "end_print_loop" and then end the loop then the program,
	end_stage_4_if:				#else, the program will go back to the branch "start_print_loop" and go through the loop
end_print_loop:					#again until the condition to exit the loop has been met

li $v0, 10
syscall
		
.data
user_input: .asciiz "User input number:\n"
newLine: "\n"
result: "This number in binary is:\n"