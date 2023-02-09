static void print(IntListElement list, int n) {
    if (list != null) {
        System.out.println(n);
        print(list.next, n+1);
        System.out.println(list.data);
    }
} 