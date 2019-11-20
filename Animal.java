import java.io.*;
import java.util.*;

/* To populate the knowledge tree, the very first line will be the subject title.
 *   Every line afterwards will begin with an integer (1 to signify an internal (question)
 *   node or (0) to signifiy an external (guess or leaf) node. This is followed by a single space
 *   and the remainder of the line will be the question or guess. The tree is stored in preorder
 *   traversal. Also thought it would be more accurate to rename this as a "Guessing Game" since
 *   a player could input anything as their answer.
 */

public class Animal{
    private static int wins = 0;
    private static int losses = 0;
    private static int total = 0;

    private static Scanner stdin = new Scanner(System.in);
    public static BufferedReader br = null;
    public static String line;
    private String subject;
    private Node root;


    public Animal(BufferedReader file) throws IOException {
        subject = file.readLine();
        root = readTree(file);
    }


    public void saveTree(PrintWriter file) throws IOException {
        file.println(subject);
        writeTree(file, root);
    }

    public void playGame() throws IOException {
        Node current = root;

        if (!askYesNo(subject)) {
            return;
        }
        while (current.isQuestion()) {
            if (askYesNo(current.getQuestion())) {
                current = current.getYesBranch();
            } else {
                current = current.getNoBranch();
            }
        }

        if (!askYesNo(current.getGuess())) {   // Wrong guess - find out what user was thinking of and get a new question for future use.
            String userAnswer, userQuestion;

            System.out.print("I give up, what were you thinking of? ");
            userAnswer = consoleIn.readLine();
            losses++;
            total++;
            System.out.println("Please enter a yes/no question that would distinguish a(n) " + userAnswer + " from a(n) " + current.getGuess() + ".");
            userQuestion = consoleIn.readLine();

            if (askYesNo("For a(n) " + userAnswer + " the answer would be")) {   // Extend the tree appropriately
                current.convertToQuestion(userQuestion, new Node(current.getGuess()), new Node(userAnswer));
            } else {
                current.convertToQuestion(userQuestion, new Node(userAnswer), new Node(current.getGuess()));
            }
        } else {
            System.out.println("Yay I win! I knew it all along.");
            wins++;
            total++;
        }
    }

    public static void main (String[ ] args) throws IOException {    // Access file containing initial knowledge base
        System.out.println("This is Justin's Guessing Game!");
        System.out.print("Would you like to clear the file? ");
        String ans = consoleIn.readLine();
        if (ans.equalsIgnoreCase("YES".substring(0, ans.length()))){
            deleteLines("src/AnimalGame.txt");
            wins=0;
            losses=0;
            total=0;
        }
        else if (ans.equalsIgnoreCase("NO".substring(0, ans.length()))){

        } else {
            System.out.println("Please answer yes or no" );
        }

        System.out.println("Think of something and I will try to guess it!");

        BufferedReader knowledgeIn = new BufferedReader(new FileReader("src/AnimalGame.txt"));

        Animal theGame = new Animal(knowledgeIn); // Create the game
        knowledgeIn.close();

        do {    // Play the game as many times as the user wants
            System.out.print("Would you like to view w/l stats? ");
            String answ = consoleIn.readLine();
            if (answ.equalsIgnoreCase("YES".substring(0, answ.length()))){
                System.out.println("Games won by me: " + wins);
                System.out.println("Games lost by me: " + losses);
                System.out.println("My win percentage: " + (double)wins/total*100 + "%");
            }
            else if (answ.equalsIgnoreCase("NO".substring(0, answ.length()))){

            } else {
                System.out.println("Please answer yes or no" );
            }
            theGame.playGame();
        } while (askYesNo("Shall we play again"));


        PrintWriter knowledgeOut = new PrintWriter(new FileWriter("src/AnimalGame.txt"));  // save the knowledge base to file
        theGame.saveTree(knowledgeOut);
        knowledgeOut.close();

        System.exit(0);
    }

    public static void deleteLines(String filepath) throws IOException { //
        FileWriter newfile = new FileWriter("src/AnimalGame.txt", false);
        BufferedWriter writer = new BufferedWriter(newfile); //wrap filewriter into bufferedwriter

        String lineToSave1 = "Guessing Game, are you ready?"; //default tree
        String lineToSave2 = "1 Is it a mammal";
        String lineToSave3 = "0 Trout";
        String lineToSave4 = "0 Cow";

        writer.write(lineToSave1);
        writer.newLine();
        writer.write(lineToSave2);
        writer.newLine();
        writer.write(lineToSave3);
        writer.newLine();
        writer.write(lineToSave4);
        writer.close();
    }

    /* Private methods - auxiliary to public methods above */

    private static Node readTree(BufferedReader file) throws IOException { //Read a tree stored in preorder in a file
        boolean isQuestion = ((char) file.read() == '1');  //Read the information for this node
        file.skip(1);		//Skip over single blank space
        String contents = file.readLine();

        if (isQuestion) { //Construct the node, reading subtrees recursively if needed
            Node ifNo = readTree(file);
            Node ifYes = readTree(file);
            return new Node(contents, ifNo, ifYes);
        } else {
            return new Node(contents);
        }
    }

    private static void writeTree(PrintWriter file, Node root) throws IOException { //Write a tree to a file in preorder
        file.print(root.isQuestion() ? 1 : 0);
        file.print(" ");
        if (root.isQuestion()) {
            file.println(root.getQuestion());
            writeTree(file, root.getNoBranch());
            writeTree(file, root.getYesBranch());
        } else {
            file.println(root.getGuess());
        }
    }

    private static boolean askYesNo(String question) throws IOException { //Ask the user a yes-no question
        String answer;
        do {  // Ask the user the question, read answer, convert to all caps
            System.out.print(question + "? ");
            answer = consoleIn.readLine();

            if (answer.equalsIgnoreCase("YES".substring(0, answer.length()))) {   // Check to see if answer was yes or no.  If so, return appropriate value - else ask again.
                return true;
            }
            else if (answer.equalsIgnoreCase("NO".substring(0, answer.length()))) {
                return false;
            } else {
                System.out.println("Please answer yes or no");
            }
        } while (true);
    }

    /* The game tree is composed of two kinds of nodes - question
     *	(internal) nodes and guess (leaf) nodes.  The content of
     *	a question node is the question to ask; of a guess node,
     *	the answer to propose.  A guess node can be turned into a
     * 	question node when a guess fails
     */

    private static class Node { //Constructor for a question node

        Node(String question, Node ifNo, Node ifYes) {
            isQuestion = true;
            contents = question;
            this.lchild = ifNo;
            this.rchild = ifYes;
        }

        Node(String guess) { //Constructor for a guess node
            isQuestion = false;
            contents = guess;
            lchild = null;
            rchild = null;
        }

        boolean isQuestion(){ //Accessor for whether a node represents a question or a guess
            return isQuestion;
        }

        String getQuestion() { //Accessor for question stored in a node.
            return contents;
        }

        Node getNoBranch() { //Accessor for "no" branch from a question node.
            return lchild;
        }

        Node getYesBranch() { //Accessor for "yes" branch from a question node.
            return rchild;
        }

        String getGuess(){ //Accessor for guess stored in a node.
            return contents;
        }

        void convertToQuestion(String question, Node ifNo, Node ifYes){ //Convert a guess node to a question node
            isQuestion = true;
            contents = question;
            lchild = ifNo;
            rchild = ifYes;
        }

        private boolean isQuestion;	// True for question, false for guess
        private String contents;	// Question or quess as the case may be
        private Node lchild, rchild;// "No" and "Yes" branches for a question
    }

    private static BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));  // Wrap System.in in a BufferedReader object so we can use readLine()

}
