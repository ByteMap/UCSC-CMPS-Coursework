import java.io.*;
import java.util.*;

public class Bard {
	
	public static void main(String args[]) throws IOException {
		
		Scanner input = new Scanner(new FileInputStream("Shakespeare.txt"));
        //The .useDeleimiter() method takes care of all the special characters in the Shakespeare.txt file, leaving only the
        //words to deal with
		input.useDelimiter("[^A-Za-z]");
		Scanner inputCmd = new Scanner(new FileInputStream("input.txt"));
		PrintWriter out = new PrintWriter(new FileWriter(new File("analysis.txt")));
        //Create two Hashmaps: a hashmap that contains frequency and a hashmap that contains the wordlength
		HashMap<String, Integer> freqmap = new HashMap<String, Integer>();
        HashMap<String, Integer> lengthmap = new HashMap<String, Integer>();
        int wordLength;
		
        //Traverses through Shakespeare.txt and adds each word that occurs in the .txt file into a frequency hashmap and
        //a word length hashmap
		while(input.hasNext()) {
            String word = input.next();
            wordLength = word.length();
			Integer frequency = freqmap.get(word);
            
            //If a word already exist in the frequency hashmap, re-add it into the hashmap "freqmap" and increment its value (the frequency)
            //by one
			if(frequency != null) {
				freqmap.put(word, frequency + 1);
			}
            //If a word does not exist in the hashmap, add it into both the frequency map and set the frequency to 1, and the lengthmap, which,
            // only needs to be added in once
			else {
                freqmap.put(word, 1);
                lengthmap.put(word, wordLength);
            
			}
		}
		
        //Reads in each line of the command input (input.txt)
		while(inputCmd.hasNextLine()) {
			String list = inputCmd.nextLine();
			String[] split = list.split("\\s+");
            //If the first word that is split is not a number (indicating that it is not the number pair and is instead the command to give out the
            //frequency of the given word, traverse through the whole hashmap to see if the word in the command input file matches the word in the
            //hashmap, then have it print out the word and the frequency
			if(!isDigit(split[0])){
				for(String word: freqmap.keySet()) {
					if(list.equals(word)) {
						out.println(word + " " + freqmap.get(word));
					}
				}
			}
            //If the first word is a number, indicating that it is a number pair command from the command input file, parse the String array of
            //split into length and ammount, then run through the method that sorts the hashmap and returns an array of words that is the most
            //frequent in terms of its wordlength
            else {
                int length = Integer.parseInt(split[0]);
                int amount = Integer.parseInt(split[1]);
                String[] receiveSorted = sortMapGet(length, amount, freqmap, lengthmap);
                if(receiveSorted.length == 0) {
                    out.println("No words of length " + length + " exists\n");
                    return;
                }
                else {
                    //For loop runs through the array that the method "sortMapGet" returns and prints it in order
                    for (int i = 0; i < receiveSorted.length; i++) {
                        out.print(receiveSorted[i] + " ");
                    }
                }
                out.println();
            }
		}
        input.close();
        inputCmd.close();
        out.close();
	}
    
    //Method to check if the input commands is a digit or a word, returns true if it is a number and false if it is
    //not a number
    public static boolean isDigit(String input) {
        for(int i = 0; i < input.length(); i++) {
            if(!Character.isDigit(input.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static String[] sortMapGet(int length, int amount, HashMap <String, Integer> freqmap , HashMap <String, Integer> lengthmap){
        
        //Creates a new Hashmap that only takes in keys that corresponds to the wordlength that is being asked in the
        //parameter and takes in the frequency corresponding to the key
        HashMap<String, Integer> toSort = new HashMap<String, Integer>();
        String[] toReturn = new String[amount];
        //Loops through the Hashmap "lengthmap" and adds the key corresponding to the word length that is being asked
        //as well as the frequency that is from that key
        for(HashMap.Entry<String, Integer> entry: lengthmap.entrySet()) {
            String name = entry.getKey();
            Integer wordLength = entry.getValue();
            if(wordLength == length) {
                toSort.put(name, freqmap.get(name));
            }
        }
        
        //Turn the new Hashmap into an array for easy bubble sorting.
        String[] sortKey = new String[toSort.size()];
        Integer[] sortFrequency = new Integer[toSort.size()];
        int counter = 0;
        for(HashMap.Entry<String, Integer> entry: toSort.entrySet()) {
            sortKey[counter] = entry.getKey();
            sortFrequency[counter] = entry.getValue();
            counter++;
        }
        
        //Sort the two arrays "sortKey" and "sortFrequency" through a bubble sort using the two temporary variables "Integer temp"
        //and "String tempString"
        Integer temp = 0;
        String tempString = "";
        for(int i = 0; i < sortKey.length; i++) {
            for(int j = 1; j < sortKey.length-1; j++) {
                if(sortFrequency[j-1] < sortFrequency[j]) {
                    temp = sortFrequency[j-1];
                    tempString = sortKey[j-1];
                    sortFrequency[j-1] = sortFrequency[j];
                    sortKey[j-1] = sortKey[j];
                    sortFrequency[j] = temp;
                    sortKey[j] = tempString;
                }
            }
        }
        
        //From the amount that is being asked in the parameter, store the sorted key (which is a String) into the string array "toReturn" which,
        //only returns amount of words that it is given, and return that string array
        if(sortKey.length < amount) {
            for(int i = 0; i < sortKey.length; i++) {
                toReturn[i] = sortKey[i];
            }
        }
        else {
            for(int j = 0; j < amount; j++) {
                toReturn[j] = sortKey[j];
        }
        return toReturn;
    }
}
