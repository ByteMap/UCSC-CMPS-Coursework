class CmdArgs {
    public static void main(String args[]) {
        System.out.println(longest(args));
    }
    static String longest(String[] words) {
        String max = "";
        for(int i = 0; i < words.length; i++) {
            if(words[i].length() > max.length()) {
                max = words[i];
            }   
        }
        return max;
    }
}