class PotionTest {
    public static void main(String[] args) {
        Potion potion = new Potion("healing", 10);
        System.out.println(potion); // prints healing:10
        potion.drink(3); // drink the specified amount of the problem
        System.out.println(potion); // prints healing: 7     
    }
}

class Potion { 
    String s;
    int x;
    public Potion(String s, int x) {
        this.s = s;
        this.x = x;
    }
    public int drink(int y) {
        x = x - y;
        return x;
    }
    public String toString() {
        return s + ":" + x;
    }
}