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
    //assign the variables 
      double count=0;
      double word = 0;
      double average= 0;
      String a=" ";
      //make a loop to get the word length
      for(int i=0; i<text.size();i++)
      {
        a= cleanUp(text.get(i));
        word= word+a.length();
        //count the word only if it is an actual word and not any ordinary token
        if(a.length()!=0)
        {
          count++;
        }
      }
      //calculate the length of words
       average= word/count;
      return average;
  }
  /**
   * Return the type token ratio (TTR) for this text.
   * TTR is the number of different words divided by the total number of words.
   * text is a non-empty list of strings.
   * At least one line in text contains a word
   */
  static double typeTokenRatio(ArrayList<String> text){
    //variables
    double count=0;
    double word=0;
    String a=" ";
    //create an arrayList to store the different words
    ArrayList<String>dif= new ArrayList<String>();
    
    for(int i=0;i<text.size();i++)
    {
      a=cleanUp(text.get(i));
    if(a.length()!=0)
    {
      count++;
      if(!dif.contains(a))
      {
        dif.add(a);
      }
    }
    }
    //increase word count with the length of the arraylist
    for (int j=0; j<dif.size();j++)
     {
        word++;
     }

    double rate= word/count;
    return rate;
}

  
  /**
   * Return the hapaxLegomana ratio for this text.
   * This ratio is the number of words that occur exactly once divided
   * by the total number of words.
   * text is a list of strings.
   * At least one line in text contains a word.
   */
  
static double hapaxLegomanaRatio(ArrayList<String> text) {
    double count=0;
    double word=0;
    String a=" ";
    //create two arrayLists
    ArrayList<String>dif= new ArrayList<String>();
    ArrayList<String>once= new ArrayList<String>();
    
    for(int i=0;i<text.size();i++)
    {
    a=cleanUp(text.get(i));
    //check if token is a word
    if(a.length()!=0)
    {
      count++;
      //check if the first array contains element
      if(!once.contains(a))
        once.add(a);
      else 
        //otherwise add the the second array
        dif.add(a);
    }
    }
    //remove all elements that in diff and once
    once.removeAll(dif);
    for(int g=0; g<once.size();g++)
      word++;
    double rate= word/count;
    return rate;
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
    double slen=0;
    double word=0;
    double count=0;
    String a=" ";
    
    //word counter
    for(int i=0;i<text.size();i++)
    {
    a=cleanUp(text.get(i));
    if(a.length()!=0)
    {
      word++;
      count++;
    }
    String b=(text.get(i));
    //check if the last character is a sentence terminating phrase
       if((b.charAt(b.length()-1)==('.')||b.charAt(b.length()-1)==('?')||b.charAt(b.length()-1)==('!'))&& count>0) 
       {
         slen++;
         count=0;
       }
    } 
    double rate= word/slen;
    return rate;
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
    double slen=0;
    double count=0;
    double count1=0;
    double phrase=0;
    String a=" ";
    //word counter
    for(int i=0;i<text.size();i++)
    {
    a=cleanUp(text.get(i));
    //check if the token is a word
    if(a.length()!=0)
    {
      count++;
      count1++;
    }
    
     String b=(text.get(i));
     //check if the last token has has a sentence terminating character
     //increase sentence length count
     //set count= 0
     if((b.charAt(b.length()-1)==('.')||b.charAt(b.length()-1)==('?')||b.charAt(b.length()-1)==('!'))&& count>0) 
       {
         slen++;
         count=0;
       }
    //check if the last token has has a phrase terminating character
     //increase phrase length count
     
     if((b.charAt(b.length()-1)==(',')||b.charAt(b.length()-1)==(':')||b.charAt(b.length()-1)==(';'))&& count1>0) 
       {
         phrase++;
         count1=0;
       }
     }
    //calculate the rate 
    double rate= ((phrase+slen)/slen);
    return rate;
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
   double diff=0;
   double sum=0;
   for(int i=0; i<sig1.length;i++)
   {
     //calculate diff by using the difference of the two arrays multiplied by the weight array
     diff= Math.abs(sig1[i]-sig2[i])*weights[i];
     sum=sum+diff;
   }
    return sum;
  }
  
  /**
   * Read a linguistic signature from file and return it as
   * a Signature object.
   */
  static Signature readSignature(File file) throws IOException {
    Scanner input = new Scanner(System.in);
      int num = input.nextInt();
      int array[] = new int[num];
      for (int i = 0 ; i < array.length; i++ ) {
           array[i] = input.nextInt();
           
    }
      
    double[] features = {0.0, 0.0, 0.0, 0.0, 0.0};
    return new Signature("needs to be completed", features);
  } 
}

