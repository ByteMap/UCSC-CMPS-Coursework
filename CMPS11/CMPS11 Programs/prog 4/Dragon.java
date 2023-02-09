public class Dragon extends Thing { 
    private static boolean mad = false;
    public Dragon() {
        super("Dragon");
    }
    public static boolean angry() {
        return mad;
    }
    
    public static boolean changeMood() {
        if(mad) {
            mad = false;
        } else {
            mad = true;
        }
        return mad;
    }
}