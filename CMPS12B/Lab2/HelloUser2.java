//-----------------------------------------------------------------------------
// HelloUser2.java
// Prints "Hello <USER>"
//-----------------------------------------------------------------------------
class HelloUser2{
    public static void main( String[] args ){
        String userName = System.getProperty("user.name");
        System.out.println("Hello "+userName);
            }
}
