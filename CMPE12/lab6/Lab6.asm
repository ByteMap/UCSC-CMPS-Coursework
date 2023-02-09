#Name: Andy Chen
#Email: achen81@ucsc.edu
#Lab Assignment: Lab 6: Vigenere Cipher
#Due Date: 3/9/18

#Pseudocode:
#EncryptChar:
#	Store return address
#	If character is less than 90, goto isLowercase branch
#		set $t0 flag to 1
#		sub char by 97 'a'
#		sub key by 65
#	end if
#
#	Else 
#		sub char and key by 65
#		goto StartCipher
#	end else	
#
#	StarCipher:
#		add character value with key value and store it into $a0
#		mod $a0 in case if overflow
#		set key to 0
#	end StartCipher
#
#	if $t0 is not equal to 0 (flag indicates lowercase) goto toLowerCase
#		set flag $t0 back to 0
#		add result $a0 by 97 and store it back into $a0
#		move $a0 into $v0
#	end if
#
#	else 
#		add result value by 65
#		move $a0 into $v0
#	end else
#end EncryptChar
#return to calling code
#
#-----------------------------------------------------------------------------------
#
#DecryptChar:
#	Store return address
#	If character is less than 90, goto EncryptIsLowercase branch
#		set $t0 flag to 1
#		sub char by 97 'a'
#		sub key by 65
#	end if
#
#	Else 
#		sub char and key by 65
#		goto StartDecipher
#	end else	
#
#	StarDecipher:
#		sub character value with key value and store it into $a0 goto toPositive
#		if $a0 is less than 0
#			add $a0 by 26
#		end if
#		set key to 0
#	end StartDecipher
#
#	if $t0 is not equal to 0 (flag indicates lowercase) goto DecryptToLowerCase
#		set flag $t0 back to 0
#		add result $a0 by 97 and store it back into $a0
#		move $a0 into $v0
#		end DecryptChar
#	end if
#
#	else 
#		add result value by 65
#		move $a0 into $v0
#	end else
#end DecryptChar
#return to calling code
#
#-----------------------------------------------------------------------------------
#
#EncryptString:
#	store return address
#	load $a0 into $t1 to keep track of address
#	load $a1 into $t4
#	move $t4 into $t6 to save first address of key
#	set $t2 to 30
#	set $t3 to 0
#		StartLoop:
#			load byte of $t1 into $a0
#			load byte of $t4 into $a1
#			if $t3 = $t2
#				end EncryptString
#			end if
#			if $a1 = 0
#				store $t6 into $t4
#				load byte of $t4 into $a1
#			end if
#			if $a0 = 0
#				end EncryptString:
#			end if
#			if $a0 < 65
#				store $a0 into $a2
#				increment $t1, $a1, $a2 by 1
#				goto StartLoop
#			end if
#			if $a0 > 122
#				store $a0 into $a2
#				increment $t1, $a1, $a2 by 1
#				goto StartLoop
#			end if						
#			if $a0 > 90
#				if $a0 < 97
#					store $a0 into $a2
#					increment $t1, $a1, $a2 by 1
#					goto StartLoop	
#				end if
#				goto StartEncrypt
#			end if
#			if $a0 < 97
#				if $a0 > 90
#					store $a0 into $a2
#					increment $t1, $a1, $a2 by 1
#					goto StartLoop	
#				end if
#				goto StartEncrypt
#			end if
#			StartEncrypt:
#				jal EncryptChar
#				store byte of $v0 into $a2
#				increment $t1, $a1, $a2 by 1
#				goto StartLoop
#			end StartEncrypt
#		end StartLoop:
#end EncryptString
#return to calling code
#
#-----------------------------------------------------------------------------------
#
#DecryptString:
#	store return address
#	load $a0 into $t1 to keep track of address
#	load $a1 into $t4
#	move $t4 into $t6 to save first address of key
#	set $t2 to 30
#	set $t3 to 0
#		StartLoop:
#			load byte of $t1 into $a0
#			load byte of $t4 into $a1
#			if $t3 = $t2
#				end EncryptString
#			end if
#			if $a1 = 0
#				store $t6 into $t4
#				load byte of $t4 into $a1
#			end if
#			if $a0 = 0
#				end EncryptString:
#			end if
#			if $a0 < 65
#				store $a0 into $a2
#				increment $t1, $a1, $a2 by 1
#				goto StartLoop
#			end if
#			if $a0 > 122
#				store $a0 into $a2
#				increment $t1, $a1, $a2 by 1
#				goto StartLoop
#			end if						
#			if $a0 > 90
#				if $a0 < 97
#					store $a0 into $a2
#					increment $t1, $a1, $a2 by 1
#					goto StartLoop	
#				end if
#				goto StartEncrypt
#			end if
#			if $a0 < 97
#				if $a0 > 90
#					store $a0 into $a2
#					increment $t1, $a1, $a2 by 1
#					goto StartLoop	
#				end if
#				goto StartEncrypt
#			end if
#			StartEncrypt:
#				jal DecryptChar
#				store byte of $v0 into $a2
#				increment $t1, $a1, $a2 by 1
#				goto StartLoop
#			end StartEncrypt
#		end StartLoop:
#end DecryptString
#return to calling code
#----------------------------------------------------------End Pseudocode-----------------------------------------------------------

.text
EncryptChar:
	nop
	sw $ra, ($sp) 				#store return address
	subi $sp, $sp, 4
	
	addi $t0, $t0, 0			#This block first creates a flag $t0 that stores 0, meaning that by default, the				
	bgt $a0, 90, __isLowerCase		#char will be uppercase. Following that, a condition is set where if the ASCII value	
	subi $a0, $a0, 65			#of the character in $a0 is greater than 90, the program will branch to "__isLowerCase".	
	subi $a1, $a1, 65			#If $a0 is not greater than 90, $a0 will be subtracted by 65 (ASCII for "A"), then,			
	b __startCipher				#$a1 will be subtracted by 65 as well. The program will branch to "__startCipher".	
						
	__isLowerCase:
		addi $t0, $t0, 1		#In "__isLowerCase", the flag $t0 will be incremented once to indicate that $a0 contains
		subi $a0, $a0, 97		#a lower case number, then $a0 will be subtracted by 97 which is ASCII for "a".
		subi $a1, $a1, 65
	#end __isLowerCase:
	
	__startCipher:				#In "__startCipher", $a0 will be added with $a1 (the key) and stored back in $a0, which,
		add $a0, $a0, $a1		#then will be modded by 26 to prevent any number overflow that will break the encryption
		rem $a0, $a0, 26		#process, then, the result of the modded number will be stored back into $a0. In addition,
		andi $a1, $a1, 0		#a1 will be reset to 0 using an AND bitmask in order to avoid trouble when EncryptString
	#end __startCipher:			#jals back here to EncryptChar
	
	bnez $t0, __toLowerCase			#After encrypting, if $t0, the flag, is not equal to zero (lowercase), go to the branch
	addi $a0, $a0, 65			#__toLowerCase. If not, add $a0 by 65 (converting it back to uppercase ASCII value) 
	move $v0, $a0				#and store it back into $a0.			
	b __endEncryptChar
	
	__toLowerCase:
		andi $t0, $t0, 0		#In __toLowerCase, $a0 is just reconverted back into the value of a lower case ASCII
		addi $a0, $a0, 97		#value. In addition, $t0 is reset to 0 so it will not cause trouble when EncryptString	
		move $v0, $a0			#jals back here to EncryptChar.			
	#end _toLowerCase:
__endEncryptChar:
addi $sp, $sp, 4 				#return to calling code
lw $ra, ($sp)
jr $ra
nop #----------------------------------------------------------End EncryptChar-----------------------------------------------------------

DecryptChar:
	nop
	sw $ra, ($sp) 				#store return address
	subi $sp, $sp, 4
	
	addi $t0, $t0, 0			#This block first creates a flag $t0 that stores 0, meaning that by default, the				
	bgt $a0, 90, __EncryptIsLowerCase	#encrypted char will be uppercase. Following that, a condition is set where if the ASCII value	
	subi $a0, $a0, 65			#of the character in $a0 is greater than 90, the program will branch to 	
	subi $a1, $a1, 65			#"__EncryptIsLowerCase". If $a0 is not greater than 90, $a0 will be subtracted by 65 			
	b __startDecipher			#(ASCII for "A"), then, $a1 will be subtracted by 65 as well. The program will branch 	
						#to "__startDecipher".	
	__EncryptIsLowerCase:
		addi $t0, $t0, 1		#In "__EncryptIsLowerCase", the flag $t0 will be incremented once to indicate that 
		subi $a0, $a0, 97		#$a0 contains a lower case number, then $a0 will be subtracted by 97 which is ASCII for "a".
		subi $a1, $a1, 65		#and $a1 will be subtracted by 65 as well.
	#end __isLowerCase:
	
	__startDecipher:			#In "__startCipher", $a0 will be subtracted by $a1 (the key) and stored back in $a0, which,
		sub $a0, $a0, $a1		#then will be checked if $a0 is a negative number or less than 0. If it is, the program
		bltz $a0, __toPositive		#will branch to "__toPositive" where $a0 will be added by 26 to become a positive number
		__backToDecrypting:		#that is the letter that it will become after decryption. In addition, $a1 will be reset
			andi $a1, $a1, 0	#to 0 in order to avoid trouble when DecryptString jals back here to DecryptChar.
		#end __backToDecrypting		
	#end __startCipher:			
	
	bnez $t0, __DecryptToLowerCase		#After decrypting, if $t0, the flag, is not equal to zero (lowercase), go to the branch
	addi $a0, $a0, 65			#__DecryptToLowerCase. If not, add $a0 by 65 (converting it back to uppercase ASCII value) 
	move $v0, $a0				#and store it back into $a0.			
	b __endDecryptChar
	
	__DecryptToLowerCase:
		andi $t0, $t0, 0		#In __DecryptToLowerCase, $a0 is just reconverted back into the value of a lower case ASCII
		addi $a0, $a0, 97		#value. In addition, $t0 is reset to 0 so it will not cause trouble when DecryptString
		move $v0, $a0			#jals back here to DecryptChar.	
		b __endDecryptChar		
	#end _toLowerCase:
	
	__toPositive:
		addi $a0, $a0, 26		#In __toPositive, $a0 will be added with 26 and the result will be stored back to $a0, which,
		b __backToDecrypting		#is the result that is expected after decryption (before being added by with ASCII values of
	#end __toPositive			#'a' or 'A', then the program will branch back to __backToDecrypting and continue decrypting.
__endDecryptChar:
addi $sp, $sp, 4 				#return to calling code
lw $ra, ($sp)
jr $ra
nop #----------------------------------------------------------End DecryptChar-----------------------------------------------------------

EncryptString:
	nop
	sw $ra, ($sp)				#store return address
	subi $sp, $sp, 4
	
	la $t1, ($a0)				#This block stores the address of $a0 into $t1 so it can be used as a pointer, then 
	la $t4, ($a1)				#it does the same thing to the key or $a1 and stores it into $t4. After that, $t4 is 
	move $t6, $t4				#is moved into $t6 where $t6 stores the address of the start of the key. In addition
	addi $t2, $t2, 30			#the $t2 register is used to set a character limit of 30 to decrypt and $t3 is a counter
	addi $t3, $t3, 0			
	
	__StartLoop:
		lb $a0, ($t1)			#This block just loads the bytes of whatever $t1 is pointing to into $a0 and whatever
		lb $a1, ($t4)			#$t4 is pointing to into $a1
		
		beq $t3, $t2, __endEncryptString	#This condition states that if 30 characters are decrypted, end the subroutine.
		beqz $a1, __ResetKey			#This coditional states that if $a1 is 0 or a null character, go to the label "__Resetkey"
							
		__Resume:
			beqz $a0, __endEncryptString	#This conditional states that if $a0 is a null character, end the subroutine
		
			blt $a0, 65, __isNotChar		#The first conditional checks if $a0 is a character less than the ASCII value of
			bgt $a0, 122, __isNotChar		#'A' and if it is, go to the label "__isNotChar". The second conditional does the
			bgt $a0, 90, __mightNotBeUpperCase	#same thing but it checks if the character has an ASCII value greater than 'z' instead.
			blt $a0, 97, __mightNotBeLowerCase	#The third conditional checks if $a0 contains a character with an ASCII value greater 
								#than 'Z' and if so, go to the label "__mightNotbeLowerCase". The fourth conditional
								#does the same thing, but checks if the character in $a0 is smaller than the ASCII
								#value of 'a' instead.
		
		__StartEncrypt:
			jal EncryptChar			#In the start of the encryption loop, the program jumps and links to EncryptChar where
			sb $v0, ($a2)			#$a0 is turned into an encrypted character and stored into $v0. Returning from the 
			addi $t1, $t1, 1		#subroutine, $a2 then is called to store the byte of whatever is in $v0 from 
			addi $t4, $t4, 1		#EncryptChar. Afterwards, $t1(pointer to $a0), $t4(pointer to $a1), and a2(the result)
			addi $a2, $a2, 1		#will be imcremented by one and the program will go back to the start of the loop. In
			addi $t3, $t3, 1		#addition, $t3 is incremented by 1 to indicate that a char has been encrypted
			b __StartLoop
		
		__mightNotBeUpperCase:			#In this block, a condition is set where if $a0 is less than the ASCII value of 'a', and
			blt $a0, 97, __isNotChar	#if it is, it means that $a0 is greater than 90 and less than 97 meaning it is not a valid
			b __StartEncrypt		#character for this program and will go to the label "__isNotChar". Else, it will go to the
							#the label "__StartEncrypt" and encrypt $a0.
			
		__mightNotBeLowerCase:			#In this block, a condition is set where if $a0 is greater than the ASCII value of 'Z', and
			bgt $a0, 90, __isNotChar	#if it is, it means that $a0 is less than 97 and greater than 90 meaning it is not a valid
			b __StartEncrypt		#character for this program and will then go to the label "__isNotChar". Else, it will go
							#to the label "__StartEncrypt" and ecnrypt $a0.
		
		__ResetKey:				#In this block, if the key has reached the null character, $t6(stored starting address of the 
			la $t4, ($t6)			#key) is loaded into $t4 and $t4 then loads its byte into $a1 where the key will start from
			lb $a1, ($t4)			#the beginning. Then the program will branch to "__Resume" and continue encrypting".
			b __Resume
		
		__isNotChar:				#In this block, if the program acknowledges that the character stored into $a0 is not valid
			sb $a0, ($a2)			#for encrypting, $a2 will be stored the special character that is in $a0, then the addresses
			addi $t1, $t1, 1		#of the pending result and of the plain text will be incremented by one and the program will
			addi $a2, $a2, 1		#then branch back to "__StartLoop". In addition, the counter $t3 will be incremented 
			addi $t3, $t3, 1		#by 1 to indicate that one character has been decrypted
			b __StartLoop	
	#end __StartLoop	
__endEncryptString:
andi $t6, 0	
andi $t2, $t2, 0
andi $t3, $t3, 0				#This block resets whatever is in the register $t6 so that no value in $t6 will mess
addi $sp, $sp, 4 				#with the program if this subroutine is called again. Also, the program will then 
lw $ra, ($sp)					#set the registers of $t2 and $t3 to 0 and return to the calling code.				
jr $ra					
nop #----------------------------------------------------------End EncryptString-----------------------------------------------------------	

DecryptString:
	nop
	sw $ra, ($sp) 				#store return address
	subi $sp, $sp, 4
	
	la $t1, ($a0)				#This block stores the address of $a0 into $t1 so it can be used as a pointer, then
	la $t4, ($a1)				#it does the same thing to the key or $a1 and stores it into $t4. After that, $t4 is 
	move $t6, $t4				#is moved into $t6 where $t6 stores the address of the start of the key. In addition
	addi $t2, $t2, 30			#the $t2 register is used to set a character limit of 30 to decrypt and $t3 is a counter
	addi $t3, $t3, 0			#that will be compared to $t2.
	
	__StartDecryptLoop:
		lb $a0, ($t1)			#This block just loads the bytes of whatever $t1 is pointing to into $a0 and whatever
		lb $a1, ($t4)			#$t4 is pointing to into $a1	
		
		beq $t3, $t2, __endDecryptString	#This condition states that if 30 characters are decrypted, end the subroutine.
		beqz $a1, __ResetDecryptKey		#This coditional states that if $a1 is 0 or a null character, go to the label "__ResetDecryptkey"
			
		
		__ResumeDecryption:
			beqz $a0, __endDecryptString			#This conditional states that if $a0 is a null character, end the subroutine
		
			blt $a0, 65, __EncryptionIsNotChar		#The first conditional checks if $a0 is a character less than the ASCII value of
			bgt $a0, 122, __EncryptionIsNotChar		#'A' and if it is, go to the label "__EncryptionIsNotChar". The second conditional
			bgt $a0, 90, __EncryptMightNotBeUpperCase	#does the same thing but it checks if the character has an ASCII value greater than 
			blt $a0, 97, __EncryptMightNotBeLowerCase	#'z' instead. The third conditional checks if $a0 contains a character with an ASCII
									#value greater than 'Z' and if so, go to the label "__EncryptMightNotbeLowerCase". 
									#The fourth conditional does the same thing, but checks if the character in $a0 is
									#smaller than the ASCII value of 'a' instead.
		__StartDecrypt:
			jal DecryptChar			#In the start of the decryption loop, the program jumps and links to DecryptChar where
			sb $v0, ($a2)			#$a0 is turned into an decrypted character and stored into $v0. Returning from the 
							#subroutine, $a2 then is called to store the byte of whatever is in $v0 from DecryptChar
			addi $t1, $t1, 1		#Afterwards, $t1(pointer to $a0), $t4(pointer to $a1), and $a2(result) will be imcremented
			addi $t4, $t4, 1		#by one and the program will go back to the start of the loop. In addition, $t3 will be
			addi $a2, $a2, 1		#incremented by 1 to indicate that one character has been decrypted
			addi $t3, $t3, 1
			b __StartDecryptLoop
		
		__EncryptMightNotBeUpperCase:				#In this block, a condition is set where if $a0 is less than the ASCII value of 'a', and
			blt $a0, 97, __EncryptionIsNotChar		#if it is, it means that $a0 is greater than 90 and less than 97 meaning it is not a valid
			b __StartDecrypt				#character for this program and will go to the label "__EncryptionIsNotChar". Else, it will	
									#go to the the label "__StartEncrypt" and encrypt $a0.
		__EncryptMightNotBeLowerCase:
			bgt $a0, 90, __EncryptionIsNotChar		#In this block, a condition is set where if $a0 is greater than the ASCII value of 'Z', and
			b __StartDecrypt				#if it is, it means that $a0 is less than 97 and greater than 90 meaning it is not a valid
									#character for this program and will then go to the label "__EncryptionIsNotChar". Else, it
									#will go to the label "__StartDecrypt" and decrypt $a0.
									
		__ResetDecryptKey:			#In this block, if the key has reached the null character, $t6(stored starting address of the 				
			la $t4, ($t6)			#key) is loaded into $t4 and $t4 then loads its byte into $a1 where the key will start from
			lb $a1, ($t4)			#the beginning. Then the program will branch to "__ResumeDecryption" and continue decrypting".
			b __ResumeDecryption
		
		__EncryptionIsNotChar:			#In this block, if the program acknowledges that the character stored into $a0 is not valid
			sb $a0, ($a2)			#for encrypting, $a2 will be stored the special character that is in $a0, then the addresses
			addi $t1, $t1, 1		#of the pending result and of the plain text will be incremented by one and the program will
			addi $a2, $a2, 1		#then branch back to "__StartDecryptLoop". In addition, the counter $t3 will be incremented 
			addi $t3, $t3, 1		#by 1 to indicate that one character has been decrypted
			b __StartDecryptLoop	
	#end __StartLoop	
__endDecryptString:			#This block resets whatever is in the register $t6 so that no value in $t6 will mess
andi $t6, 0				#with the program if this subroutine is called again. Also, the program will then 
andi $t2, $t2, 0			#set the registers of $t2 and $t3 to 0 and return to the calling code
andi $t3, $t3, 0					
addi $sp, $sp, 4 			
lw $ra, ($sp)
jr $ra
nop #----------------------------------------------------------End DecryptString-----------------------------------------------------------	
