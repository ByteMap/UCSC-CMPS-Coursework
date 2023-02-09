#Sources used:
#How to read all files in a directory in bash: https://stackoverflow.com/questions/10929453/read-a-file-line-by-line-assigning-the-value-to-a-variable
#How to read file line by line in bash: https://www.cyberciti.biz/faq/unix-howto-read-line-by-line-from-file/
#If statements and how to compare strings in bash: https://stackoverflow.com/questions/2237080/how-to-compare-strings-in-bash
#Modulo in bash: https://stackoverflow.com/questions/5688576/how-to-use-mod-operator-in-bash
#Note: Psuedocode was also given by Aziz during Friday lab section.

declare -i counter

for file in *; do
	counter=0
	if [ $file != "achen81.sh" ]; then
		while IFS= read -r line
		do
			if [[ $((counter % 2)) == 0 ]]; then
				echo "$file: $line"
			fi
			counter=$((counter + 1))
		done < "$file"
	fi
done