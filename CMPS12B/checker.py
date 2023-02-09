# This script runs some simple tests on an assignment solution. It mostly checks for the right naming convention,
# correct executable generation, and testing on simple instances with unique solutions. 
# 
# Run it as: python3 checker.py <ASSN_NAME> <OPTIONAL PATH TO MAIN FOLDER>
#     where <ASSN_NAME> is something like HW1, Lab1, etc. Typically, there is some folder with this name
#     containing solution files.
#     <OPTIONAL PATH TO MAIN FOLDER> is the location of the folder containing the directory for the assignment.
#     If not specified, this is assumed to be the current folder.
# 
# This requires spec files to run correctly, so please see the README for the right setup.
# 
# The output is all in the console. "PASS" denotes that the checker, well, passed the solution. "FAIL"
# is typically accompanied by error messages detailing what went wrong.
# 
# C. Seshadhri, Sept 2017.
#

import sys
import os
import subprocess


################################# 
# function generate_index 
# Task: Parse the input string to find the index.
# Input: strings flag_string, line. line is the entire line from the spec file, and flag_string 
#         is the flag string parse from the beginning of the line
# Output: positive integer, if index was successfully found. Output is -1 if parse was unsuccessful
# 
# This works by simply dropping the first character in flag_string, and parse the string to an int.
# ###############################

def generate_index(flag_string,line):
    try:
        number = int(flag_string[1:]) # remove first character and parse remainder into int
    except ValueError: # couldn't parse into int
        print("Ignoring invalid spec line: "+line) # print message on console, saying line will be ignored
        return -1
    return number

###################################


### main code begins here

if len(sys.argv) < 2 or len(sys.argv) > 3: # not the right number of arguments
    print("Incorrect number of command line arguments")
    print("Usage: python3 checker.py <ASSN_NAME> <OPTIONAL PATH TO MAIN FOLDER>")
    sys.exit(-1)

if len(sys.argv) == 3: # path to main folder specified
    main_folder = sys.argv[2]
else: # no path specified, so it's current folder
    main_folder = '.'


try: # get list of existing spec files
    specs = os.listdir('Spec')
except: # spec folder probably doesn't exist
    print('There is no folder Spec. Look at the setup instructions in the README.')
    sys.exit(-1)

timeout_thresh = 60 # threshold in seconds for timeout of running code

assn = sys.argv[1]

if assn+"-spec.txt" not in specs: # spec file for this assignment not present
    print('The spec file '+assn+'-spec.txt is not in folder Spec. Look at setup instructions in the README.')
    sys.exit(-1)

# now we parse the spec file

with open('Spec/'+assn+'-spec.txt','r') as f_ptr: # open spec file
    all_lines = f_ptr.readline() # all lines of file
    total_lines = len(all_lines) # number of lines

    spec = dict() # spec is stored as dictionary    
    
    # following stored as lists, with maximum possible length of total_lines
    spec['f'] = ['']*total_lines # list of necessary files
    spec['c'] = ['']*total_lines # list of commands for executables
    spec['e'] = ['']*total_lines # list of executables
    spec['p'] = ['']*total_lines # list of commands producing output
    spec['t'] = ['']*total_lines # list of test input files
    spec['o'] = ['']*total_lines # list of generated output files
    spec['i'] = ['']*total_lines # list of ideal output files


    # now generate the spec by reading spec file
    for line in f_ptr.readlines(): # iterate over lines
        if line[0] == '#': # this is a comment
            continue
        tokens = line.strip().split(':') # split into tokens
        if len(tokens) != 2: # something wrong with line
            print('Ignoring '+line) # print line and continue to next line
            continue

        flag = tokens[0].strip()  # the first part is the flag
        instruction = tokens[1].strip()  # the second part is the filename/command

        # we use flag[0], the first character of flag, as the key into the dictionary spec. The value is a list
        # Then, we use int(flag[1:]), what remains in flag, to index into the list where the instruction/filename is added
        index = generate_index(flag, line) # generate index corresponding to the flag
        if index == -1: # generate_index was unsuccessful
            continue     # ignore line, and move on to next

        # we found valid index
        spec[flag[0]][index] = instruction # insert instruction into appropriate list, as given by flag[0]

# at this point, spec has been populated appropriately.
# it remains to check that the spec is satisfied

# get into main folder
try: # try to get into main folder
    os.chdir(main_folder)
except: # couldn't do so
    print('Something wrong with path specified. You gave '+main_folder)
    sys.exit(-1)


# we're in folder now, so get the files in the assignment folder
try:  # try to get solution files, by looking into folder for assn
    assn_files = os.listdir(assn)
except: # something went wrong, probably folder doesn't exist
    print("There is no folder named "+assn)
    print("FAIL")
    sys.exit(0)

# now check the spec of necessary files, test input, and output files

required = spec['f'] + spec['t'] + spec['i'] # concatenated list of required files

for sol_file in required: # looping over necessary files in spec
    if len(sol_file) == 0: # empty string in the list, from initializaing spec['f'] as long list
        continue # move on to next item in spec
    if sol_file not in assn_files: # necessary file not in the assignment folder
        print('File '+sol_file+' not in '+assn+' folder. Please make sure it is there.') # print message explaining failure
        print('FAIL')
        sys.exit(0)
    # file is present, so print message
    print('File '+sol_file+' found')

# all files in spec['f'] are in folder
print('======================================')
print('All files are present in folder. Good!')
print('======================================')

os.chdir(assn) # get into assignment folder

# now check the spec of executable creation

for i in range(0,len(spec['c'])): # loop over spec['c'] keeping track of index
    command = spec['c'][i] # get command
    executable = spec['e'][i] # corresponding executable
    if len(command) == 0: # empty string from initialization
        continue # move on to next item in spec

    command_list = command.split(' ') # annoying, subprocess.run requires the command to be a list, where the first argument is command, the rest are arguments

    try:
        subprocess.run(command_list,timeout=timeout_thresh) # run the command in the terminal, with timeout of 60 seconds
    except subprocess.TimeoutExpired:  # some error in running command
        print(command+': Took more than '+timeout_thresh+' seconds') # print out command
        print('FAIL')
        sys.exit(0)

    except Exception as e:  # some error in running command
        print('Error in running command: '+command) # print out command
        print('Python exception: '+str(e))
        print('FAIL')
        sys.exit(0)

    # command was successful, now have to check if executable was created
    print(command+': ran successfully')
    if executable not in os.listdir(): # check if executable file is in current directory
        print('Error: Executable '+executable+' is not created by running command '+command) # print out error message
        print('FAIL')
        sys.exit(0)
    # executable was in directory
    print(executable+' was created')

# all executable commands create the right executables
print('======================================')
print('All executables created correctly. Better!')
print('======================================')

# now check the spec of output production

for i in range(0,len(spec['p'])): # loop over production commands
    prod_command = spec['p'][i]
    test = spec['t'][i]
    output = spec['o'][i]
    ideal = spec['i'][i]

    if len(prod_command) == 0: # empty production command from initialization
        continue # move on to next item in spec

    to_run = prod_command.split(' ')+[test]+[output] # the actual command to run takes in list, with input and output files as command line arguments. Observe that we concatenate lists to get the final list to give to subprocess.run

    try:
        subprocess.run(to_run,timeout=60) # run the command in terminal, with timeout of 60 seconds
    except subprocess.TimeoutExpired: # some problem in running command, possible timeout
        print('Error in running command: '+to_run+'. It might be taking too long to produce output or throwing error.')
        print('FAIL')
        sys.exit(0)

    # command was successful, now have to check if output was created properly
    if output not in os.listdir():
        print('Error: output file '+output+' not created by running command '+command) # print error message
        print('FAIL')
        sys.exit(0)

    # output was created properly, now have check if it is identical to ideal file. If not, then we need to print the first line of difference
    f_output = open(output,'r') # open output file and ideal files
    f_ideal = open(ideal,'r')

    output_lines = f_output.readlines() # lines of output file
    ideal_lines = f_ideal.readlines() # lines of ideal file

    output_len = len(output_lines) # no. of lines in output file
    ideal_len = len(ideal_lines) # no. of lines in ideal file

    # first loop over the smaller, and compare with the large

    for i in range(0,min(output_len,ideal_len)): # looping over smaller file
        if output_lines[i] != ideal_lines[i]: # lines are not the same
            print("Error in output generation. Your program is wrong on a test case. Specifically, mismatch at line "+str(i))
            print("In output file, "+output+", generated by your program, line is: "+output_lines[i])
            print("In my output file, "+ideal+", line is: "+ideal_lines[i])
            print('FAIL')
            sys.exit(0)

    # by now, smaller file is identical to prefix of larger file

    if output_len != ideal_len: # file sizes not equal, so print out messages explaining that
        print("Error in output generation. The output file, "+str(output)+", generated by your program has "+str(output_len)+" lines")
        print("My output file, "+ideal+", has "+str(ideal_len)+" lines")

        if output_len < ideal_len: # output file is smaller than it should be
            print("The following line should be present at line "+str(output_len)+" of your output. But it is not.")
            print(ideal_lines[output_len])
        else: # output file is larger than in should be
            print("The following line at position "+str(ideal_len)+" in your output file should not be there.")
            print(output_lines[ideal_len])

        print('FAIL')
        sys.exit(0)

# output files and ideal files match up!
print('All output files are correct. Excellent!')
print('PASS')


 
