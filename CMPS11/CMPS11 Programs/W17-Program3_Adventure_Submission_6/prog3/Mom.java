
public class Mom extends Thing {
	static private int HP =3;
	public Mom() {
		super("Mom");
	}
	boolean canBeCarried() {
	    return false;
	}
	static public int getHP(){
		return HP;
	}
	static public void loseHP(){
		HP--;
	}
}
