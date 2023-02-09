Name: Andy Chen
Email: achen81@ucsc.edu
Student ID: 1537306
Lab Assignment: Lab 7: Floating Point Calculations
Section Number: 01D

What I learned in this lab was converting floating point into binary, floating point into
decimal, decimal into floating point, adding floating points together, and multiplying
floating points together. What was surprising was that I learned that although there is a
binary representation of floating points, it does not represent the actual decimal value
of that floating point, which, was pretty odd to me. What worked well in this lab was when
I was converting a very small decimal to floating point, I can simply divide that decimal
by 2^x that would result in a number between 1-2 to get another decimal number and I can
simply work with that decimal number to convert it into base 2, then into floating point.
What didn’t work well was when I was tackling the same problem, I had tried to convert
that very small decimal number into binary, but I had realized that by multiplying it by
two over and over again, it would have at least taken 60 times of multiplying the number
by two and would not have been very efficient, plus, it was going to end up troublesome as
I could somehow mess up on a calculation on the way.

1. The largest positive number that the IEEE-745 SP floating point can express should
first find the largest exponent, which, is 127. In addition, the significand bits should
be set to 1, which, will result in 1.1111…111. By converting the binary number into
decimal, the result will be approximately 2, and by multiplying 2 by 2^127, the result
will be 3.4 * 10^38 So in conclusion, the largest is the decimal number 3.4 * 10^38. In
order to find the smallest number, you will have to take the smallest exponent bit, which,
would be 1, and by subtracting my exponent from the bias, the result would be -126.
Following that, I would need to find the smallest significand bits, which, will be 23 bits
of 0’s. As a result, I will have 1.0 * 2^-126 and the result in decimal will be 1.18 *
10^-38.

2. The reason why using IEE-745 SP use of bias representation in the exponents is faster
floating point addition and subtraction than using 2SC representation is because 2SC take
a bit more work than bias representation. For example, in the bias representation case,
you can take two exponents and subtract them by the bias (127) then set them equal to each
other and shift the numbers accordingly. For example, (1.010 * 2^2) + (1.001 * 2^1) is set
to (10.100 * 2^1) + (1.001 * 2^1). After doing that you can simply add them together. For
2SC, you will first have to flip the bits of one binary number, then add a 1 and
calculate , and if needed, you would have to do the same for the second number. After
that, you will need to add those two numbers together, which, basically takes more time
than using bias representation method because unlike bias representation of doing
calculation once, 2SC will require more than 1 calculation as well as going through each
bit and flipping them.

3.
FL_add:
	store the first number into $s1 register
	load the first bit that is in $s1 into $t0, increment $s1 by 1
	load the next 8 bits from $s1 into $t1, increment $s2 by 8
	load the last 23 bits from $s1 into $t2
	add 2^24 to $t2 so that the first digit will be 1 and store it back into $t2
	subtract the 2 bytes in $t1 by 127 and set it back into $t1

	store the second number into $s2 register
	load the first bit from $s2 to $t4, increment $s2 by 1
	load the next 8 bits in $s2 to $t5, increment $s2 by 8
	load the last 23 bits in $s2 into $t6
	add 2^24 to $t6 so that the first digit will be 1 and store it back into $t6
	subtract the 2 bytes in $t5 by 127 and set it back into $t5

	if $t1 is less than $t5
		sub $t5 by $t1 and store it into $t7
		set $t5 equal to $t1
		Arithmetic Left Shift $t6 by the amount in $t7 and store it back into $t6
	end_if

	if $t5 is less than $t1
		sub $t1 by $t5 and store it into $t7
		set $t1 equal to $t5
		Arithmetic Left Shift $t2 by the amount in $t7 and store it back into $t2
	end_if

	add $t2 by $t6 and store it into $t8
	sub the number of bits in $t8 by 23 and store it in $t0
	count to the right the number of times equal to $t0 in $t8 (radix point)
	store the left side (of the radix point) into $t1 and the right side into $t2
	$t4 = 0
	$t5 = 1
	
	Start_Loop:
		if $t2 is at the 24th bit, end_loop
		load bit of $t2 in $t3
		$t4 = $t4 + ($t3/(2 * $t5)
		branch back to start_loop
	End_Loop:

	add $t1 with $t4 and store it back into $t1
	mult $t1 by 2^$t7 and store it back into $t1 ($t1 is the result)
end FL_add:

FL_mult:
	store the first number into $s1 register
	load the first bit that is in $s1 into $t0, increment $s1 by 1
	load the next 8 bits from $s1 into $t1, increment $s2 by 8
	load the last 23 bits from $s1 into $t2
	add 2^24 to $t2 so that the first digit will be 1 and store it back into $t2
	subtract the 2 bytes in $t1 by 127 and set it back into $t1

	store the second number into $s2 register
	load the first bit from $s2 to $t4, increment $s2 by 1
	load the next 8 bits in $s2 to $t5, increment $s2 by 8
	load the last 23 bits in $s2 into $t6
	add 2^24 to $t6 so that the first digit will be 1 and store it back into $t6
	subtract the 2 bytes in $t5 by 127 and set it back into $t5

	multiply $t2 by $t6 and store it in $t2
	add $t1 by $t5 and store it into $t1
	subtract the number of bits of $t2 by 23 and store it into $t7
	count right of $t2 by $t7, store right side into $t7 and left side into $t8
	$t6 = 0
	$t5 = 1
	
	Start_Loop:
		if $t7 is at the 24th bit, end_loop
		load bit of $t7 in $t3
		$t4 = $t4 + ($t3/(2 * $t6)
		branch back to start_loop
	End_Loop:

	add $t1 with $t4 and store it back into $t1
	mult $t1 by 2^$t7 and store it back into $t1 ($t1 is the result)
	if $t4 or $t0 is a 1, multiply $t1 by negative one and store it back into $t1
end FL_mult:
	
	 