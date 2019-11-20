import edu.princeton.cs.algs4.*;

import java.util.NoSuchElementException;

public class BST<Key extends Comparable<Key>, Value>{
    private Node root;             // root of BST

    private class Node {
        private Key key;           // sorted by key
        private Value val;         // associated data
        private Node left, right;  // left and right subtrees
        private int size;          // number of nodes in subtree

        public Node(Key key, Value val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }

    private boolean check() { //makes sure it is a valid binary tree
        if (!isBST()){
            System.out.println("Not in symmetric order");
        }
        if (!isSizeConsistent()){
            System.out.println("Subtree counts not consistent");
        }
        if (!isRankConsistent()){
            System.out.println("Ranks not consistent");
        }
        return isBST() && isSizeConsistent() && isRankConsistent();
    }

    private boolean isBST() { // does this binary tree satisfy symmetric order?
        return isBST(root, null, null);
    }
    private boolean isBST(Node x, Key min, Key max) { //is the tree rooted at x a BST with all keys strictly between min and max?
        if (x == null){
            return true;
        }
        if (min != null && x.key.compareTo(min) <= 0){
            return false;
        }
        if (max != null && x.key.compareTo(max) >= 0){
            return false;
        }
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    }

    private boolean isSizeConsistent(){// are the size fields correct?
        return isSizeConsistent(root);
    }
    private boolean isSizeConsistent(Node x) {
        if (x == null){
            return true;
        }
        if (x.size != size(x.left) + size(x.right) + 1){
            return false;
        }
        return isSizeConsistent(x.left) && isSizeConsistent(x.right);
    }

    private boolean isRankConsistent() {
        for (int i = 0; i < size(); i++) {
            if (i != rank(select(i))) {
                return false;
            }
        }
        for (Key key : keys()) {
            if (key.compareTo(select(rank(key))) != 0) {
                return false;
            }
        }
        return true;
    }
    public int rank(Key key) { // Number of keys in the subtree less than key.
        if (key == null){
            throw new IllegalArgumentException("argument to rank() is null");
        }
        return rank(key, root);
    }
    private int rank(Key key, Node x) {
        if (x == null){
            return 0;
        }
        int cmp = key.compareTo(x.key);
        if(cmp < 0) {
            return rank(key, x.left);
        }
        else if (cmp > 0) {
            return 1 + size(x.left) + rank(key, x.right);
        } else {
            return size(x.left);
        }
    }
    public Key select(int k) {  // Return key of rank k.
        if (k < 0 || k >= size()) {
            throw new IllegalArgumentException("argument to select() is invalid: " + k);
        }
        Node x = select(root, k);
        return x.key;
    }
    private Node select(Node x, int k) {
        if (x == null){
            return null;
        }
        int t = size(x.left);
        if (t > k){
            return select(x.left,  k);
        }
        else if (t < k){
            return select(x.right, k-t-1);
        } else {
            return x;
        }
    }
    public Iterable<Key> keys() {
        if (isEmpty()){
            return new Queue<Key>();
        }
        return keys(min(), max());
    }
    public Iterable<Key> keys(Key lo, Key hi) {
        if (lo == null){
            throw new IllegalArgumentException("first argument to keys() is null");
        }
        if (hi == null){
            throw new IllegalArgumentException("second argument to keys() is null");
        }

        Queue<Key> queue = new Queue<Key>();
        keys(root, queue, lo, hi);
        return queue;
    }

    private void keys(Node x, Queue<Key> queue, Key lo, Key hi) {
        if (x == null){
            return;
        }
        int cmplo = lo.compareTo(x.key);
        int cmphi = hi.compareTo(x.key);
        if (cmplo < 0){
            keys(x.left, queue, lo, hi);
        }
        if (cmplo <= 0 && cmphi >= 0){
            queue.enqueue(x.key);
        }
        if (cmphi > 0){
            keys(x.right, queue, lo, hi);
        }
    }

    public boolean contains(Key key){ // Does this symbol table contain the given key?
        if (key == null){
            throw new IllegalArgumentException("argument to contains() is null");
        }
        return get(key) != null;
    }
    public Value get(Key key){
        return get(root, key);
    }
    private Value get(Node x, Key key) {
        if (key == null){
            throw new IllegalArgumentException("calls get() with a null key");
        }
        if (x == null){
            return null;
        }
        int cmp = key.compareTo(x.key);
        if(cmp < 0){
            return get(x.left, key);
        } else if (cmp > 0) {
            return get(x.right, key);
        } else {
            return x.val;
        }
    }

    public void delete(Key key){ // Removes the specified key and its associated value from this symbol table.
        if (key == null){
            throw new IllegalArgumentException("calls delete() with a null key");
        }
        root = delete(root, key);
        assert check();
    }

    private Node delete(Node x, Key key) {
        if (x == null) return null;

        int cmp = key.compareTo(x.key);
        if(cmp < 0) {
            x.left  = delete(x.left,  key);
        }
        else if (cmp > 0) {
            x.right = delete(x.right, key);
        } else {
            if (x.right == null) {
                return x.left;
            }
            if (x.left  == null) {
                return x.right;
            }
            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }


    public int height(){// Returns the height of the BST (for debugging)
        return height(root);
    }

    private int height(Node x) {
        if (x == null){
            return -1;
        }
        return 1 + Math.max(height(x.left), height(x.right));
    }


    public boolean isEmpty(){ // Returns true if this symbol table is empty.
        return size() == 0;
    }
    public int size(){
        return size(root);
    }

    private int size(Node x) { // return number of key-value pairs in BST rooted at x
        if (x == null){
            return 0;
        } else {
            return x.size;
        }
    }

    public Key max(){ // Returns the largest key in the symbol table.
        if (isEmpty()){
            throw new NoSuchElementException("calls max() with empty symbol table");
        }
        return max(root).key;
    }
    private Node max(Node x) {
        if (x.right == null) {
            return x;
        } else {
            return max(x.right);
        }
    }


    public Key min(){ // Returns the smallest key in the symbol table.
        if (isEmpty()){
            throw new NoSuchElementException("calls min() with empty symbol table");
        }
        return min(root).key;
    }
    private Node min(Node x) {
        if (x.left == null){
            return x;
        } else {
            return min(x.left);
        }
    }
    public void deleteMin() { // Removes the smallest key and associated value from the symbol table
        if (isEmpty()){
            throw new NoSuchElementException("Symbol table underflow");
        }
        root = deleteMin(root);
        assert check();
    }

    private Node deleteMin(Node x) {
        if (x.left == null) {
            return x.right;
        }
        x.left = deleteMin(x.left);
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    public void put(Key key, Value val) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        if (val == null) {
            delete(key);
            return;
        }
        root = put(root, key, val);
        assert check();
    }

    private Node put(Node x, Key key, Value val) { // Inserts the specified key-value pair into the symbol table, overwriting the old value with the new value if the symbol table already contains the specified key.
        if (x == null) {
            return new Node(key, val, 1);
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left  = put(x.left,  key, val);
        }
        else if (cmp > 0 ){
            x.right = put(x.right, key, val);
        } else {
            x.val = val;
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    public void inOrder(Node node){ // traverse (prints out) the keys in inorder order.
        if (node == null) {
            return;
        }

        inOrder(node.left);  // first recur on left child
        System.out.print(node.key + " "); // then print the data of node
        inOrder(node.right);

    }

    public void postOrder(Node node){ // traverses (prints out) the keys in postorder order.
        if (node == null) {
            return;
        }

        postOrder(node.left); // first recur on left subtree
        postOrder(node.right); // then recur on right subtree
        System.out.print(node.key + " "); // now deal with the node

    }

    public void preOrder(Node node){ // traverses (prints out) the keys in preorder order.
        if (node == null) {
            return;
        }

        System.out.print(node.key + " ");  // first print data of node
        preOrder(node.left);  // then recur on left subtree
        preOrder(node.right); // now recur on right subtree

    }

    public void levelOrder(Node node){ // traverse (prints out) the keys as levels, left to right.
        int h = height(root);
        int i;
        for (i=1; i<=h+1; i++) {
            givenLevel(root, i);
        }
    }

    public void givenLevel(Node root, int level){
        if (root == null) {
            return;
        } else if (level == 1) {
            System.out.print(root.key + " ");
        } else if (level > 1) {
            givenLevel(root.left, level-1);
            givenLevel(root.right, level-1);
        }

    }

    public static void main(String[] args){ // BST driver
        BST<Integer, Integer> st = new BST<Integer, Integer>();
        StdOut.println("Binary Search Tree Runner, please enter starting number of nodes ");
        int n = StdIn.readInt();
        for (int i = 0; i<n; i++) {
            StdOut.println("Enter a number ");
            int key = StdIn.readInt();
            st.put(key, i);
        }
        while(true){
           System.out.println("Enter '1' if you would like to insert a node, '2' if you would like to delete a node, '3' if you would like to see the tree traversed in different ways, and '4' to exit.");
           int x = StdIn.readInt();
           if(x==1){
               System.out.println("What value would you like to insert?");
               System.out.println("Here are the values in order?");
               st.inOrder(st.root);
               System.out.println("");
               int y = StdIn.readInt();
               System.out.println("Where would you like to insert");
               int z = StdIn.readInt();
               st.put(y,z);
               System.out.println("In order: ");
               st.inOrder(st.root);
               System.out.println("\nPost order: ");
               st.postOrder(st.root);
               System.out.println("\nPre order: ");
               st.preOrder(st.root);
               System.out.println("\nLevel order:");
               st.levelOrder(st.root);
               System.out.println("");
           }
           if(x==2){
                System.out.println("What value would you like to delete?");
                System.out.println("Here are the values in order?");
                st.inOrder(st.root);
                System.out.println("");
                int d = StdIn.readInt();
                st.delete(d);
               System.out.println("In order: ");
               st.inOrder(st.root);
               System.out.println("\nPost order: ");
               st.postOrder(st.root);
               System.out.println("\nPre order: ");
               st.preOrder(st.root);
               System.out.println("\nLevel order:");
               st.levelOrder(st.root);
               System.out.println("");
           }
           if(x==3){
               System.out.println("In order: ");
               st.inOrder(st.root);
               System.out.println("\nPost order: ");
               st.postOrder(st.root);
               System.out.println("\nPre order: ");
               st.preOrder(st.root);
               System.out.println("\nLevel order:");
               st.levelOrder(st.root);
               System.out.println("");
           }
           if(x==4){
               break;
           }
        }

    }

}
