public class Fire extends Thing {
    private static boolean burn=false;
    
    public Fire() {
        super("fire");
    }
    
    public static boolean idiot(){
        return burn;
    }
    
    public static boolean changeBurn() {
        if(burn) {
            burn=false;
        } else {
            burn=true;
        }
        return burn;
    }
    /*
                
    */
}
