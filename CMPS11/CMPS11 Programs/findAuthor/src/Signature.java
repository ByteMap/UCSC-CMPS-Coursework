/**
 * Used to store a feature array along with the name of the author.
 */
class Signature {
  double[] feature;
  String author;
  Signature(String author, double[] values) {
    this.author = author;
    feature = new double[values.length];
    for (int i = 0; i < values.length; i++) {
      feature[i] = values[i];
    }
  }
  public String toString() {
    String result = author + "\n";
    for(double v : feature) {
      result+= "\n" + v;
    }
    return result;
  }
}


