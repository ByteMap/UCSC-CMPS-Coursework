//-----------------------------------------------------------------------------
//  Name: Andy Chen
//  CruzID: achen81
//  Assignment Name: Programming Assignment 3
//-----------------------------------------------------------------------------
public class Matrix {
    private class Entry {
        int column;
        double data;
        
        Entry(int column, double data) {
            this.column = column;
            this.data = data;
        }
        
        public boolean equals(Object x) {
            Entry entry = (Entry)x;
            if(column == entry.column && data == entry.data) {
                return true;
            }
            else {
                return false;
            }
        }
        public String toString() {
            String string = "(" + column + ", " + data + ")";
            return string;
        }
    }
    // Constructor
    private int size;
    private int NNZ;
    private List []row;
    public Matrix(int n) { // Makes a new n x n zero Matrix. pre: n>=1
        if(n >= 1) {
            size = n;
            NNZ = 0;
            row = new List[size+1];
            for(int i = 1; i < size+1; i++) {
                row[i] = new List();
            }
        }
        else {
            throw new RuntimeException("Exception on Matrix(int n): Creating a Matrix of Size 0");
        }
    }

    // Access functions
    public int getSize() { // Returns n, the number of rows and columns of this Matrix
        return size;
    }

    public int getNNZ() { // Returns the number of non-zero entries in this Matrix
        return NNZ;
    }

    public boolean equals(Object x) {
        Matrix matrix = (Matrix) x;
        if(matrix.size != size || matrix.NNZ != NNZ) {
            return false;
        }
        for(int i = 1; i < size+1; i++) {
            List list = row[i];
            List matrixList = matrix.row[i];
            if((matrixList.length() != list.length()) || (matrixList.equals(list) == false)) {
                return false;
            }
        }
        return true;
    }// overrides Object's equals() method

    // Manipulation procedures
    public void makeZero() { // sets this Matrix to the zero state
        NNZ = 0;
        row = new List[size+1];
        for(int i = 1; i < size+1; i++) {
            row[i] = new List();
        }
    }

    public Matrix copy() {
        if(getSize() > 0) {
            Matrix toReturn = new Matrix(getSize());
            for(int i = 1; i < size+1; i++) {
                List list = row[i];
                List toReturnList = toReturn.row[i];
                if(list.length() > 0) {
                    list.moveFront();
                    while(list.index >= 0) {
                        Entry listEntry = (Entry)list.get();
                        toReturnList.append(listEntry);
                        toReturn.NNZ++;
                        list.moveNext();
                    }
                }
            }
            return toReturn;
        }
        else {
            throw new RuntimeException("Exception On Copy(): Copying A Nonexisting Matrix");
        }
    }// returns a new Matrix having the same entries as this Matrix

    public void changeEntry(int i, int j, double x) {
        Entry entry = new Entry(j, x);
        List list = row[i];
        List tempList = list;
        if(list.length() == 0 && x == 0.0) {
            return;
        }
        else if(list.length() == 0 && x != 0.0) {
            list.append(entry);
            NNZ++;
            return;
        }
        Entry front = (Entry)list.front();
        Entry back = (Entry)list.back();
        if(j < front.column) {
            if(x == 0.0) {
                return;
            }
            else {
                list.prepend(entry);
                NNZ++;
                return;
            }
        }
        else if(j > back.column) {
            if(x == 0.0) {
                return;
            }
            else {
                list.append(entry);
                NNZ++;
                return;
            }
        }
        else {
            list.moveFront();
            while(list.index >= 0) {
                Entry listEntry = (Entry)list.get();
                if(j == listEntry.column && x == 0.0) {
                    list.delete();
                    NNZ--;
                    return;
                }
                else if(j == listEntry.column && x != 0.0) {
                    listEntry.data = x;
                    return;
                }
                list.moveNext();
            }
            list.moveFront();
            tempList.moveFront();
            tempList.moveNext();
            while(list.index >= 0 && tempList.index >= 0) {
                Entry listEntry = (Entry)list.get();
                Entry tempEntry = (Entry)tempList.get();
                if(j > listEntry.column && j < tempEntry.column) {
                    list.insertAfter(entry);
                    NNZ++;
                    return;
                }
                list.moveNext();
                tempList.moveNext();
            }
        }
    }
    // changes ith row, jth column of this Matrix to x
    // pre: 1<=i<=getSize(), 1<=j<=getSize()

    public Matrix scalarMult(double x) {
        Matrix matrix = new Matrix(getSize());
        matrix.NNZ = NNZ;
        if(x == 0) {
            matrix.makeZero();
        }
        else {
            for(int i = 1; i < size+1; i++) {
                List list = row[i];
                List matrixList = matrix.row[i];
                if(list.length() != 0) {
                    list.moveFront();
                    while(list.index() >= 0) {
                        Entry listEntry = (Entry)list.get();
                        Entry tempEntry = new Entry(listEntry.column, listEntry.data * x);
                        matrixList.append(tempEntry);
                        list.moveNext();
                    }
                }
            }
        }
        //System.out.println(matrix);
        return matrix;
    }
    // returns a new Matrix that is the scalar product of this Matrix with x
    Matrix add(Matrix M) {
        if(this.equals(M)) {
            Matrix matrix = new Matrix(getSize());
            matrix = M;
            return matrix.scalarMult(2.0);
        }
        else if(getSize() == M.getSize()) {
            Matrix matrix = new Matrix(getSize());
            for(int i = 1; i < size+1; i++) {
                List list = row[i];
                List toAdd = M.row[i];
                List result = matrix.row[i];
                if(list.length() != 0 && toAdd.length() != 0) {
                    list.moveFront();
                    toAdd.moveFront();
                    while(list.index() >= 0 && toAdd.index() >= 0) {
                        Entry listEntry = (Entry)list.get();
                        Entry toAddEntry = (Entry)toAdd.get();
                        if(listEntry.column == toAddEntry.column) {
                            Entry temp = new Entry(listEntry.column, listEntry.data + toAddEntry.data);
                            if(temp.data != 0.0) {
                                result.append(temp);
                                matrix.NNZ++;
                            }
                            list.moveNext();
                            toAdd.moveNext();
                        }
                        else if(listEntry.column < toAddEntry.column) {
                            result.append(listEntry);
                            matrix.NNZ++;
                            list.moveNext();
                        }
                        else if(listEntry.column > toAddEntry.column) {
                            result.append(toAddEntry);
                            matrix.NNZ++;
                            toAdd.moveNext();
                        }
                    }
                }
                else if(list.length() != 0 && toAdd.length() == 0) {
                    list.moveFront();
                    while(list.index() >= 0) {
                        Entry listEntry = (Entry)list.get();
                        result.append(listEntry);
                        matrix.NNZ++;
                        list.moveNext();
                    }
                }
                else if(list.length() == 0 && toAdd.length != 0) {
                    toAdd.moveFront();
                    while(toAdd.index() >= 0) {
                        Entry toAddEntry = (Entry)toAdd.get();
                        result.append(toAddEntry);
                        matrix.NNZ++;
                        toAdd.moveNext();
                    }
                }
            }
            return matrix;
        }
        else {
            throw new RuntimeException("Exception On Add(): getSize() != M.getSize()");
        }
    }
    // returns a new Matrix that is the sum of this Matrix with M
    // pre: getSize()==M.getSize()

    Matrix sub(Matrix M) {
        if(this.equals(M)) {
            Matrix matrix = new Matrix(getSize());
            matrix.NNZ = 0;
            return matrix;
        }
        else if(getSize() == M.getSize()) {
            Matrix matrix = new Matrix(getSize());
            for(int i = 1; i < size+1; i++) {
                List list = row[i];
                List toSub = M.row[i];
                List result = matrix.row[i];
                if(list.length() != 0 && toSub.length() != 0) {
                    list.moveFront();
                    toSub.moveFront();
                    while(list.index() >= 0 && toSub.index() >= 0) {
                        Entry listEntry = (Entry)list.get();
                        Entry toSubEntry = (Entry)toSub.get();
                        if(listEntry.column == toSubEntry.column) {
                            Entry temp = new Entry(listEntry.column, listEntry.data - toSubEntry.data);
                            if(temp.data != 0.0) {
                                result.append(temp);
                                matrix.NNZ++;
                            }
                            list.moveNext();
                            toSub.moveNext();
                        }
                        else if(listEntry.column < toSubEntry.column) {
                            result.append(listEntry);
                            matrix.NNZ++;
                            list.moveNext();
                        }
                        else if(listEntry.column > toSubEntry.column) {
                            Entry temp = new Entry(toSubEntry.column, toSubEntry.data * -1);
                            result.append(temp);
                            matrix.NNZ++;
                            toSub.moveNext();
                        }
                    }
                }
                else if(list.length() != 0 && toSub.length() == 0) {
                    list.moveFront();
                    while(list.index() >= 0) {
                        Entry listEntry = (Entry)list.get();
                        result.append(listEntry);
                        list.moveNext();
                    }
                }
                else if(list.length() == 0 && toSub.length != 0) {
                    toSub.moveFront();
                    while(toSub.index() >= 0) {
                        Entry toSubEntry = (Entry)toSub.get();
                        Entry temp = new Entry(toSubEntry.column, toSubEntry.data * -1);
                        result.append(temp);
                        toSub.moveNext();
                    }
                }
            }
            return matrix;
        }
        else {
            throw new RuntimeException("Exception On Add(): getSize() != M.getSize()");
        }
    }
    // returns a new Matrix that is the difference of this Matrix with M
    // pre: getSize()==M.getSize()

    Matrix transpose() {
        Matrix matrix = new Matrix(getSize());
        for(int i = 1; i < size+1; i++) {
            List list = row[i];
            if(list.length() != 0) {
                list.moveFront();
                while(list.index() >= 0) {
                    Entry listEntry = (Entry)list.get();
                    matrix.changeEntry(listEntry.column, i, listEntry.data);
                    //System.out.println(listEntry.column);
                    list.moveNext();
                }
            }
        }
        return matrix;
    }
    // returns a new Matrix that is the transpose of this Matrix

    Matrix mult(Matrix M) {
        if(getSize() == M.getSize()) {
            Matrix matrix = new Matrix(getSize());
            Matrix transposed = new Matrix(getSize());
            transposed = M.transpose();
            for(int i = 1; i < size+1; i++) {
                List list = row[i];
                List result = matrix.row[i];
                //System.out.println(list);
                for(int j = 1; j < size+1; j++) {
                    List toMult = transposed.row[j];
                    double sum = 0.0;
                    //System.out.println(size);
                    if(list.length() != 0 && toMult.length() != 0) {
                        list.moveFront();
                        toMult.moveFront();
                        while(list.index() >= 0 && toMult.index() >= 0) {
                            Entry listEntry = (Entry)list.get();
                            //System.out.println(listEntry);
                            Entry multEntry = (Entry)toMult.get();
                            if(listEntry.column == multEntry.column) {
                                sum = sum + (listEntry.data * multEntry.data);
                                list.moveNext();
                                toMult.moveNext();
                            }
                            else if(listEntry.column < multEntry.column) {
                                list.moveNext();
                            }
                            else if(listEntry.column > multEntry.column) {
                                toMult.moveNext();
                            }
                        }
                        if(sum != 0) {
                            Entry entry = new Entry(j, sum);
                            result.append(entry);
                            matrix.NNZ++;
                        }
                    }
                }
            }
            return matrix;
        }
        else {
            throw new RuntimeException("Exception on Mult(): getSize() != M.getSize()");
        }
    }
    // returns a new Matrix that is the product of this Matrix with M
    // pre: getSize()==M.getSize()

    // Other functions
    public String toString() {
        int counter = NNZ;
        String string = new String();
        for(int i = 1; i < size+1; i++) {
            List list = row[i];
            if(list.length() > 0) {
                string = string + i + ": ";
                list.moveFront();
                while(list.index >= 0) {
                    Entry entry = (Entry)list.get();
                    string = string + entry.toString();
                    list.moveNext();
                    counter--;
                }
                    string = string + "\n";
            }
        }
        return string;
    }// overrides Object's toString() method
}
