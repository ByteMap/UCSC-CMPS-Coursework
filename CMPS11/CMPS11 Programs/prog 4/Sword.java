class Sword extends Thing{
    private static boolean haveSword = false;
    public Sword(){
        super("sword");
    }
    void swing(Player player) {
        Adventure.results.setText("You swing the sword.\n"+
                            "You quickly wonder what angered you so much to make you swing your deadly weapon.");
    }
    public static boolean hasSword(){
        return haveSword;
    }
    
    //Added this function, changes the inPossession boolean to the opposite value
    public boolean changeInPossession() {
        if(haveSword) {
            haveSword=false;
        } else {
            haveSword=true;
        }
        return haveSword;
    }
}