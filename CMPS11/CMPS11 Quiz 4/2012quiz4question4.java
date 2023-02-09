class GoldTest {
    public static void main(String[] args) {
        Treasure gold = new Treasure("gold", 10);
        System.out.println(gold); // prints gold:10
        gold.add(5);
        System.out.println(gold); // prints gold:15
    }
}

class Treasure extends GoldTest {
    String s;
    int x;
    public Treasure(String s, int x) {
        this.s = s;
        this.x = x;
    }
    public int add(int a) {
        x = x + a;
        return x;
    }
    public String toString() {
        return s + ":" + x;
    }
}

