class Final8 {
    public static void main(String[] args) {
        foo(new ClassOne());
        foo(new ClassTwo());
    }
    static void foo(ClassOne one) {
        someFunc(one);
    }
    static void someFunc(ClassOne one) {
        System.out.println("one " + one.get());
    }
    static void someFunc(ClassTwo two) {
        System.out.println("two " + two.get());
    }
}
class ClassOne {
    int data = 123;
    public int get() {
        return data;
    }
    public int mystery() {
        return get()*10;
    }
}
class ClassTwo extends ClassOne {
    int data = 234;
    public int get() {
        return data;
    }
}