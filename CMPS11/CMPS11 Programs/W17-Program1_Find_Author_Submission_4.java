import java.util.regex.*;
import java.util.*;
import java.io.*;

/**
 * This program uses 5 simple linguistic features to try and identify the authorship of an 'unknown' text.
 * This is based on an assignment created by Michelle Craig from the University of Toronto.
 * http://nifty.stanford.edu/2013/craig-authorship-detection/
 * 
 * In the comments below a "word" is a String without embedded white space characters and no
 * leading or trailing non-word characters (the word characthers are _,a-z,A-Z, and 0-9).
 * A 'token' is a String without embedded white space characters (i.e. what you get when you call next() from the
 * class Scanner).
 */
class FindAuthor {
  static final int NUM_FEATURES = 5;
  
  /**
   * This program expects to be passed the name of the unknown file as the first command line argument
   * and the name of a directory containing nothing but signature files for known authors as the second
   * command line argument.
   */
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.out.println("Usage: java FindAuthor unknownAuthorFileName signaturesDirectory");
      System.exit(1);
    }
    
    // read the new text file from the unknown author
    Scanner file = new Scanner(new File(args[0])); 
    ArrayList<String> text = new ArrayList<String>();
    while (file.hasNext()) {
      text.add(file.next());
    }
    System.out.println("num tokens = " + text.size());
    // Compute the signature of the unknown author
    double[] features = new double[NUM_FEATURES];
    features[0] = averageWordLength(text);
    features[1] = typeTokenRatio(text);
    features[2] = hapaxLegomanaRatio(text);
    features[3] = averageSentenceLength(text);
    features[4] = avgSentenceComplexity(text);
    Signature unknown = new Signature(args[0], features);
    System.out.println(unknown);
    
    double[] weights = {11.0, 33.0, 50.0, 0.41, 4.0};
    
    // compare the computed signature with those of know authors using the given weights
    File[] files = new File(args[1]).listFiles();
    
    // initialize to nobody and some crazy big (bad) score.
    double bestScore = 1.0e30; // something really big
    String bestAuthor = "nobody";
    // try each author looking for the best match
    for (int i = 0; i < files.length; i++) {
      if (files[i].getName().endsWith("stats")) {
        Signature sig = readSignature(files[i]);
        double score = compareSignatures(unknown.feature, sig.feature, weights);
        System.out.println(sig.author + ":" + score);
        if (score < bestScore) {
          bestScore = score;
          bestAuthor = sig.author;
        } 
      }
    }
    System.out.println("Best author is " + bestAuthor + " with score " + bestScore);      
  }
  
  /**
   * Return a version of String str in which all letters have been
   * converted to lowercase and non-word characters (anything but _,A-Z,a-z,0-9) 
   * have been stripped from both ends. Inner non-word characters are left untouched. 
   * If str does not contain any white space characters, then cleanUp() will convert
   * a "token" into a "word" (see definitions in the opening comment).
   */
  static String cleanUp(String str) {
    Pattern p = Pattern.compile("(\\W*)(.*?)(\\W*)");
    Matcher m = p.matcher(str);
    m.matches();
    return str.substring(m.end(1), m.end(2)).toLowerCase();    
  }
  
  /**
   * Return the average length of all words (as defined above) in text. 
   * text is a non-empty list of strings.
   * At least one line in text contains a word.
   */
  static double averageWordLength(ArrayList<String> text) {
    int total = 0;
    int wordCount = 0;
    for (int i = 0; i < text.size(); i++) {
      String word = cleanUp(text.get(i));
      if (word.length() > 0) {
        wordCount++;
        total += word.length();
      }
    }
    return total / (double) wordCount;
  }
  
  /**
   * Return the type token ratio (TTR) for this text.
   * TTR is the number of different words divided by the total number of words.
   * text is a non-empty list of strings.
   * At least one line in text contains a word
   */
  static double typeTokenRatio(ArrayList<String> text){
    ArrayList<String> differentWords = new ArrayList<String>();
    int wordCount = 0;
    for (String str : text) {
      String newWord = cleanUp(str);
      if (!newWord.isEmpty()) {
        wordCount++;
        if (!differentWords.contains(newWord)) {
          differentWords.add(newWord);
        }
      }
    }
    return (double) differentWords.size() / wordCount;
  }
  
  
  
  /**
   * Return the hapaxLegomana ratio for this text.
   * This ratio is the number of words that occur exactly once divided
   * by the total number of words.
   * text is a list of strings.
   * At least one line in text contains a word.
   */
  static double hapaxLegomanaRatio(ArrayList<String> text) {
    ArrayList<String> words = new ArrayList<String>();
    int wordCount = 0;
    int onceCount = 0;
    
    for (String str : text) {
      String newWord = cleanUp(str);
      if (!newWord.isEmpty()) {
        wordCount++;
        words.add(newWord);
      }
    }
    
    for (int i = 0; i < words.size(); i++) {
      onceCount++;
      for (int j = 0; j < words.size(); j++) {
        if (words.get(i).equals(words.get(j)) && i != j) {
          onceCount--;
          break;
        }
      }
    }
    
    return (double) onceCount/wordCount;
  }
  
  
  /**
   * Return the average number of words per sentence in text.
   * text is guaranteed to have at least one sentence.
   * Terminating punctuation is defined as !?.
   * A sentence is defined as a non-empty sequence of words
   * terminated by a token ending in terminating punctuation 
   * or end of file.
   */
  static double averageSentenceLength(ArrayList<String> text) {
    int sentenceCount = 0;
    
    double average = 0;
    
    int wordCount = 0;
    
    for (int i = 0; i < text.size(); i++) {
      String token = text.get(i);
      int tokenLength = token.length();
      if (token.charAt(tokenLength-1) != '!' && token.charAt(tokenLength-1) != '?' &&
          token.charAt(tokenLength-1) != '.') {
        if (!cleanUp(token).isEmpty()) {
          wordCount++;
        }
      } else {
        if (tokenLength > 1) {
          String str = token.substring(0, tokenLength - 1);
          if (!cleanUp(str).isEmpty()) {
            wordCount++;
          }
        }
        if (wordCount > 0) {
          sentenceCount++;
          average = average * (double) (sentenceCount - 1) / sentenceCount + (double) wordCount / sentenceCount;
          wordCount = 0;
        }
      }
    }
    
    return average;
  }
  
  /**
   * Return the average number of phrases per sentence.
   * Terminating punctuation defined as !?.
   * A sentence is defined as a non-empty sequence of words
   * terminated by a token ending in terminating punctuation 
   * or end of file.
   * Phrases are subsequences of a sentences terminated by a token
   * ending with ,;: or by the end of the sentence.
   */
  
  static double avgSentenceComplexity( ArrayList<String> text ) {
    int sentCount = 0;
    int phraseCount = 0;
    
    // Average number of pharases per sentence
    double avg = 0;
    
    StringBuilder sentenceBuilder = new StringBuilder();
    
    for (int i = 0; i < text.size(); i++) {
      // Read token one at a time and check for one of specified character
      // at the end
      String token = text.get(i);
      int tokenLength = token.length();
      if (token.charAt(tokenLength-1) != '!' && token.charAt(tokenLength-1) != '?' &&
          token.charAt(tokenLength-1) != '.') {
        sentenceBuilder.append(token);
        sentenceBuilder.append(' ');
      } else {
        // We hit terminating character. It can be by itself or attached to the end of a string.
        // If at the end of another string, read from beginning of string to terminating character
        if (tokenLength > 1) {
          String str = token.substring(0, tokenLength);
          sentenceBuilder.append(str);
        }
        
        // Done with building one full sentence. Let's determine number of phrases
        // in this sentence.
        String sentence = sentenceBuilder.toString();
        if (!sentence.isEmpty()) {
          sentCount++;
        }
        
        int phIndBegin = 0;
        
        for (int j = 0; j < sentence.length(); j++) {
          char ch = sentence.charAt(j);
          if (ch == ':' || ch == ',' || ch == ';') {
            // Need to be sure that this character appears at the end of token.
            // We can check it by checking if there is a space to the right of it.
            if (sentence.charAt(j+1) == ' ') {
              if (j - phIndBegin > 0) {
                phIndBegin = j + 1;
                phraseCount++;
              }
            }
          }
        }
        
        // Capture last phrase that may run to the end of the file
        if (phIndBegin <= sentence.length()) {
          phraseCount++;
        }
        
        // Can update average and reset phraseCount.
        if (phraseCount > 0) {
          // Ready to calculate next average
          avg = avg * (double) (sentCount - 1) / sentCount + (double) phraseCount / sentCount;
          
          // Let's reset phrase count and sentenceBuilder for the next sentence
          phraseCount = 0;
          sentenceBuilder = new StringBuilder();
        }
      }
    }
    return avg;
    
  }
  
  /**
   * Return a non-negative real number indicating the similarity of two
   * linguistic signatures. The smaller the number the more similar the
   * signatures. Zero indicates identical signatures.
   * sig1 and sig2 are NUM_FEATURES element double arrays with the following elements
   *
   * 0 : average word length
   * 1 : TTR
   * 2 : Hapax Legomana Ratio
   * 3 : average sentence length
   * 4 : average sentence complexity
   * weight is a list of weights to apply to each
   * linguistic feature.
   */
  static double compareSignatures(double[] sig1, double[] sig2, double[] weights) {
    // To be completed
    double sum=0;
    double temp;
    for(int i = 0; i < sig1.length; i++) {
      temp= Math.abs(sig1[i]-sig2[i]);
      temp= temp*weights[i];
      sum += temp;
    }
    return sum;
  }
  
  /**
   * Read a linguistic signature from file and return it as
   * a Signature object.
   */
  
  static Signature readSignature(File file) throws IOException {
    Scanner in = new Scanner(file);
    String author = in.nextLine();
    double[] features = {0.0, 0.0, 0.0, 0.0, 0.0};
    for (int i = 0; in.hasNext(); i++) {
      features[i] = in.nextDouble();
    }
    return new Signature(author, features); 
  }
}



