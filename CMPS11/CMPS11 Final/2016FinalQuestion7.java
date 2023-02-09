class Final7 {
    public static void main(String[] args) {
        someFunc(new ClassOne());
        someFunc(new ClassTwo());
    }
    static void someFunc(ClassOne one) {
        System.out.println(one.mystery());
        System.out.println(one.get());
    }
}
class ClassOne {
    int data = 123;
    public int get() {
        return data;
    }
    public int mystery() {
        return get()*(-1);
    }
}
class ClassTwo extends ClassOne {
    int data = 234;
    public int get() {
        return data;
    }
}