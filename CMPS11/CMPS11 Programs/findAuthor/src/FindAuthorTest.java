import junit.framework.TestCase;
import java.util.*;

/**
 * A JUnit test case class.
 * Every method starting with the word "test" will be called when running
 * the test with JUnit.
 * 
 */
public class FindAuthorTest extends TestCase {
  static boolean approx(double x, double y) {
    return Math.abs(x-y) < 0.0001;
  }
  // basic avg word length test
  public void testaverageWordLength() {
    String[] textArray = {"James", "Fennimore", "Cooper",
      "Peter,", "Paul,", "and", "Mary"};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }
    
    double avg = FindAuthor.averageWordLength(text);
    assertTrue("average word length of the sample should be 5.142857142857143 but was " 
                 + avg, approx(avg,5.142857142857143));   
  }
// test that non-words aren't affecting the avg word length
  public void testaverageWordLength1() {
    String[] textArray = {"James", "Fennimore", "Cooper",
      "Peter,", "Paul,", "and", "Mary", "!#$@!",".", "**********************"};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }
    
    double avg = FindAuthor.averageWordLength(text);
    assertTrue("average word length of the sample should be 5.142857142857143 but was " 
                 + avg, approx(avg,5.142857142857143));   
  }
  // basic test
  public void testtypeTokenRatio() {
    String[] textArray = {"James", "Fennimore", "Cooper",
      "Peter,", "Paul,", "and", "Mary", "James", "Gosling"};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }
    
    double ttr = FindAuthor.typeTokenRatio(text);
    assertTrue("TTR of the sample should be 0.8888888888888 but was "+ttr,approx(ttr,0.8888888888));   
  }
  // make sure non-words are ignored
  public void testtypeTokenRatio2() {
    String[] textArray = {"James", "Fennimore", "Cooper",
      "Peter,", "Paul,", "and", "Mary", "James", "Gosling","!#$@!",".", "**********************", "James","and"};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }
    
    double ttr = FindAuthor.typeTokenRatio(text);
    assertTrue("TTR of the sample should be 0.72727272727272729 but was "+ttr,approx(ttr,0.72727272727272729));   
  }
  // basic test James occurs 3 times other just once
  public void testhapaxLegomanaRatio() {
    String[] textArray = {"James", "Fennimore", "Cooper",
      "Peter,", "Paul,", "and", "Mary", "James", "Gosling", "James"};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }
    
    double hlr = FindAuthor.hapaxLegomanaRatio(text);
    assertTrue("Hapax Legomana Ratio of the sample should be 0.7 but was "+hlr,approx(hlr,0.7));   
  }
  // James occurs only twice
  public void testhapaxLegomanaRatio2() {
    String[] textArray = {"James", "Fennimore", "Cooper",
      "Peter,", "Paul,", "and", "Mary", "James", "Gosling"};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }
    
    double hlr = FindAuthor.hapaxLegomanaRatio(text);
    assertTrue("Hapax Legomana Ratio of the sample should be 0.7777777777777778 but was "+hlr,approx(hlr,0.7777777777777778));   
  }
  // make sure non-words are ignored, also two words appear more than once, James and "and"
  public void testhapaxLegomanaRatio3() {
    String[] textArray = {"James", "Fennimore", "Cooper",
      "Peter,", "Paul,", "and", "Mary", "James", "Gosling","!#$@!",".", "**********************", "James","and"};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }
    
    double hlr = FindAuthor.hapaxLegomanaRatio(text);
    assertTrue("Hapax Legomana Ratio of the sample should be 0.54545454545454541 but was "+hlr,approx(hlr,0.54545454545454541));   
  }
  // basic test - both sentences end with period
  public void testaverageSentenceLength() {
    String[] textArray = {"The", "time", "has", "come,", "the", "Walrus", "said",
      "To", "talk", "of", "many", "things:", "of", "shoes", "-", "and", "ships", "-", "and", "sealing", "wax,",
      "Of", "cabbages;", "and", "kings.",
      "And", "why", "the", "sea", "is", "boiling", "hot;",
      "and", "whether", "pigs", "have", "wings."};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }
    
    double avg = FindAuthor.averageSentenceLength(text);
    assertTrue("Average sentence length of the sample should be 17.5 but was "+avg,approx(avg,17.5));   
  }
  // uses other sentence endings and includes non-words.
  public void testaverageSentenceLength2() {
    String[] textArray = {"The", "time", "has", "**********************","come,", "the", "Walrus", "said",
      "To", "talk", "of", "many", "thi-ngs:", "of", "shoes", "-", "and", "ships", "-", "and", "sealing", "wax", ",",
      "Of", "cabbages;", "and","!#$@", "kings","?",
      "And", "why", "the", "sea", "is", "boi.ling", "hot;",
      "and", "whe;ther", "pigs", "have", "win.gs!"};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }    
    double avg = FindAuthor.averageSentenceLength(text);
    assertTrue("Average sentence length of the sample should be 17.5 but was "+avg,approx(avg,17.5));   
  }
  // Has an empty sentence that should not be counted.
  public void testaverageSentenceLength3() {
    String[] textArray = {"One", "two.", "!", "Three", "four."};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }
    
    double avg = FindAuthor.averageSentenceLength(text);
    assertTrue("Average sentence length of the sample should be 2 but was "+avg,approx(avg,2));   
  }
  // basic phrase testing
  public void testavgSentenceComplexity() {
    String[] textArray = {"The", "time", "has", "come,", "the", "Walrus", "said",
      "To", "talk", "of", "many", "things:", "of", "shoes", "-", "and", "ships", "-", "and", "sealing", "wax,",
      "Of", "cabbages;", "and", "kings.",
      "And", "why", "the", "sea", "is", "boiling", "hot;",
      "and", "whether", "pigs", "have", "wings."};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }
    
    double avg = FindAuthor.avgSentenceComplexity(text);
    assertTrue("Average sentence complexity of the sample should be 3.5 but was "+avg,approx(avg,3.5));   
  }
  // include some punctuation in the middle of words and other sentence ending punctuation
  public void testavgSentenceComplexity2() {
    String[] textArray = {"The", "time", "has", "**********************","come,", "the", "Walrus", "said",
      "To", "talk", "of", "many", "thi-ngs:", "of", "shoes", "-", "and", "ships", "-", "and", "sealing", "wax", ",",
      "Of", "cabbages;", "and","!#$@", "kings","?",
      "And", "why", "the", "sea", "is", "boi.ling", "hot;",
      "and", "whe;ther", "pigs", "have", "win.gs!"};
    ArrayList<String> text = new ArrayList<String>();
    for (String str : textArray) {
      text.add(str);
    }
    
    double avg = FindAuthor.avgSentenceComplexity(text);
    assertTrue("Average sentence complexity of the sample should be 3.5 but was "+avg,approx(avg,3.5));   
  }
  // the values from the assignment example
  public void testcompareSignatures() {
    double[] sig1 = { 4.4, 0.1, 0.05, 10.0, 2.0};
    double[] sig2 = { 4.3, 0.1, 0.04, 16.0, 4.0};
    double[] weight = { 11, 33, 50, 0.4, 4};
    
    
    double value = FindAuthor.compareSignatures(sig1, sig2, weight);
    assertTrue("Compare signatures of the sample should be 12 but was "+value,approx(value,12));   
  }  
  // just to test some other values than the sample from the assignment spec
  public void testcompareSignatures2() {
    double[] sig1 = { 4.4, 0.1, 0.05, 10.0, 2.0};
    double[] sig2 = { 4.3, 0.1, 0.04, 16.0, 4.0};
    double[] weight = { 10, 30, 40, 0.7, 2};
    
    
    double value = FindAuthor.compareSignatures(sig1, sig2, weight);
    assertTrue("Compare signatures of the sample should be 9.6 but was "+value,approx(value,9.6));   
  }  
}
