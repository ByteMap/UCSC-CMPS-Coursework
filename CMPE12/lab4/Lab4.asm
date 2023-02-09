#Name: Andy Chen
#Email: achen81@ucsc.edu
#Program File: Lab4.asm
#Due Date: 2/16/18
.text
main:
	#Asks the user for an integer				
	li $v0, 4			#Load Service 4 into the v0 register, which, prints out the prompt message into the console.
	la $a0, prompt			#Syscall is then used as a call-action where it performs the operation of printing.
	syscall 
	
	#Saves user data
	li $v0, 5			#Replace Service 4 with Service 5 into the v0 register, which, waits for
	syscall				#an input from the user before the program does anything else, then, moves
	move $s0, $v0			#the user data into a saved register. 

	li $s2, 36			#These 3 lines are treated as storage for the the divisors of n where n is the integer that 
	li $s3, 9			#represents the counter for the loop. These divisors are also used to determine if the counter
	li $s4, 4			#will be treated as DEAD(divisible by 4), BEEF(divisible by 9), or DEADBEEF(divisible by 4 AND 9 aka 36).
				
	start_loop:
						
		beq $s1, $s0, end_loop		#These two lines make up the start of the loop where "beq" compares if $s1(The counter) is equal to
		addi $s1, $s1, 1		#$s0(The user input). If they are equal, "beq" directs the program to the "end_loop" label, which, will
		start_if:			#end the loop. If they are not equal, the counter then increments 1 and the program will then read down
			div $s1, $s2		#line by line unless directed otherwise.
			mfhi $s5
			bnez $s5, start_else1		#This block is the first if statement where the first condition is to divide
			li $v0, 4			#$s1(the incrementing value) by $s2 (36, which is also means divisible by both 4 and 9)
			la $a0, DEADBEEF		#then find the remainder of it using mfhi and store it in $s5. If the remainder
			syscall				#is equal to zero, then the program will carry through line by line (which prints out DEADBEEF) until it
			b start_loop			#hits "b start_loop" where it will loop to the beginning where the labellabel starts. 
			#syscall				#Else, if it is not equal to 0, the branch will be directed to the label "start_else1".
		end_if:
		
		start_else1:
			div $s1, $s3			#This block has the same concept as the previous block, but instead of dividing the incremented
			mfhi $s5			#value($s1) by 36, $s1 is being divided by $s3(9). Once "bnez" determines that the remainder is
			bnez $s5, start_else2		#0 (the value of $s5), the program then loads the intermediate(li) 4 into $v0 calling that it is 
			li $v0, 4			#expected to print a string, and, la(Load Address) responds to li and goes into the data found at the 
			la $a0, BEEF			#bottom of this program and loads the address of BEEF, which, then would print out "BEEF". Following that,
			syscall				#the program then goes to the branch "start_loop" and loops back to the beginning of the loop. If the
			b start_loop			#remainder is not equal to 0, then "bnez" would direct the program to go to the label "start_else2" and
			syscall				#read line by line from there.
		end_else1:
			
		start_else2:
			div $s1, $s4			#This block has the exact same concept as the previous block. The only differences are that 
			mfhi $s5			#the program is dividing $s1 by $s4(4), "bnez" directs the program to read from "start_else3"
			bnez $s5, start_else3		#if the stored remainder in $s5 is not equal to 0, and that it calls to print out DEAD
			li $v0, 4			#if the stored remainder in $s5 is equal to 0, meaning that the number that is treated as a 
			la $a0, DEAD			#counter for this loop, or, the incremented number $s1 is divisible by 4.
			syscall
			b start_loop
			syscall
		end_else2:
		
		start_else3:				
			li $v0, 1			
			move $a0, $s1			#This block is only read by the program if and only if the counter or incremented number
			syscall				#is neither divisible by 4, 9, or both. The "li $v0, 1"	loads an immediate 1, which, is expecting 	
			li $v0, 4			#for an integer to be printed out. The function "move $a0, $s1" responds to the load immediate		 
			la $a0, newLine			#and moves the incremented number to $a0 where it will then be printed out into the console. The 
			syscall				#next line "li $v0, 4" loads an immediate 4, which expects a string to be printed, and the "la" command	
			b start_loop			#takes the newLine data "\n" and prints it out into the console. Finally, the program reads "b start_loop"
			syscall				#which goes back to the start of the loop" Overall, this block is used to print out the corresponding number
		end_else3:				#that is neither divisible by the integers 4, 9, or both (36).
												
	end_loop:
	
	li $v0, 10
	syscall
 		
	
.data
prompt: .asciiz "Please enter a number: "		#This block of code is basically the library for the data.
newLine: .asciiz "\n"					#Whenever the function "la"(Load Address) is called, the program gets the corresponding
DEAD: .asciiz "DEAD\n"					#address from this .data library. In addition, the .asciiz stands for a String that has
BEEF: .asciiz "BEEF\n"					#a null termination at the end, which, means that all of the elements in the .data library
DEADBEEF: .asciiz "DEADBEEF\n"				#are strings.
