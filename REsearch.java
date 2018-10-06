//Name: Andy Shen 
//ID: 1304441
//Name: Sampson Ward
//ID: 1312744
import java.io.*;
import java.util.*;


public class REsearch {
    static ArrayList<Character> stringArray = new ArrayList<>();
    static ArrayList<State> stateList = new ArrayList<>();
    static boolean[] visitedStates;
    static State scanState = new State("Scan", -1, -1);
    static String reader;
    static String branchState = "▓";
    static String literalList = "┘";
    static String notLiteralList = "┌";
    static String wildcard = "■";
    static BufferedReader br;


    public static void main(String[] args) throws IOException {

        REsearch search = new REsearch();


        if(args.length < 1){
            System.err.println("Please provide a text file");
        }

        FileReader file = new FileReader(args[0]);

        search.readStates();
	visitedStates = new boolean[stateList.size()];

        br = new BufferedReader(file);
        reader = br.readLine();
        for (char c : reader.toCharArray()) {
            stringArray.add(c);
        }


        while (reader != null) {


            if (search.lineIsMatch(stringArray.toString())) {

                /*prints the line in a manner which is readable*/
                for (int i = 0; i < stringArray.size(); i++) {
                    System.out.print(stringArray.get(i));
                }

                System.out.println();
                /*Clear the line from the string array*/
                stringArray.clear();
                /*read new line*/
                reader = br.readLine();
                /*foreach character insert into stringArray*/
                if(reader != null) {
                    for (char c : reader.toCharArray()) {
                        stringArray.add(c);
                    }
                }
                /*Start search on new line*/
                search.lineIsMatch(stringArray.toString());

            }
            /*else no match on line, start new search on new line*/
            else {
                stringArray.clear();
                reader = br.readLine();
                if(reader != null) {
                    for (char c : reader.toCharArray()) {
                        stringArray.add(c);
                    }
                }
                search.lineIsMatch(stringArray.toString());
            }
        }
    }

    /*-----------------------------*/
    /*Reads states from standard in*/
    /*-----------------------------*/
    public void readStates(){

        String reader;

	InputStreamReader ip = new InputStreamReader(System.in);
        BufferedReader br1 = new BufferedReader(ip);
	

	try{
        while((reader = br1.readLine()) != null){

            String[] var = reader.split("█");           //Split at special character

            String symb = var[0];                             //Symbol is the character in the state
            int next1 = Integer.parseInt(var[1]);             //N1 value in state
            int next2 = Integer.parseInt(var[2]);             //N2 N2 value in state
            State state = new State(symb, next1, next2);      //Store State in public StateList
            stateList.add(state);
	}
	

        }catch(Exception e){
		System.err.println(e);
	}

    }

    /*-----------------------------------------*/
    /*Loops through line and checks for matches*/
    /*-----------------------------------------*/
    public boolean lineIsMatch(String line) {
        for (int i = 0; i < line.length(); i++) {

            if(stringMatch(line, i)) {
                return true;
            }
        }
        return false;
    }

    /*-----------------------------------------------------------------*/
    /*Checks if a string has matched or not and also builds the Dequeue*/
    /*-----------------------------------------------------------------*/
    public boolean stringMatch(String s, int i) {

	

        Dequeue deq = new Dequeue();

        deq.put(scanState);                     //Start but adding the scan state
        deq.push(stateList.get(0));             //add the initial state

        while(deq.head!=null) {

            State st = deq.pop();

            if(st.n1_ == scanState.n1_) {       //if that state was the scan state
		for(int k = 0; k < visitedStates.length; k++){
			visitedStates[k] = false;
		}
                
                if(deq.head == null){           //and the dequeue is not empty
                    return false;               //done
                }
                else{
                    deq.put(scanState);         //else if it's not empty put the scan state back on the bottom
                    continue;                   //continue the loop
                }
            }

            else if (st.char_.compareTo(branchState) < 0){           //else if the character is not the branching char
                String checkMatch = stringArray.get(i).toString();

                if(String.valueOf(st.char_.charAt(0)).compareTo(literalList) == 0){

                    for (int j = 1; j < st.char_.length(); j++) {
                        if(String.valueOf(st.char_.charAt(j)).compareTo(checkMatch) == 0){
                            i++;                                            // Increment point in string as if it has matched

                            /*If at the end of the state then return true e.g match has been found*/
                            if(st.n1_ >= stateList.size() || st.n2_ >= stateList.size()){
                                return true;
                            }
                            /*else if the end of line is reached return false e.g No match*/
                            else if(i >= stringArray.size()){
                                return false;
                            }

                            insertStates(st, deq);
                        }
                    }
                }

                if(String.valueOf(st.char_.charAt(0)).compareTo(notLiteralList) == 0){
                    for (int k = 0; k < stringArray.size(); k++) {

                        for (int j = 1; j < st.char_.length(); j++) {

                            // if(s.contains(String.valueOf(st.char_.charAt(j)))) {
                            if (stringArray.get(k).compareTo(st.char_.charAt(j)) == 0) {

                                if (st.n1_ >= stateList.size() || st.n2_ >= stateList.size()) {

                                    return false;
                                }

                                /*if the end of line is reached return false e.g No match*/
                                if (i >= stringArray.size()) {
                                    return false;
                                }

                                insertStates(st, deq);

                            }
                        }
                    }
                    i++;        // Increment point in string as if it has matched

                    /*If at the end of the state then return true e.g match has been found*/
                    if (st.n1_ >= stateList.size() || st.n2_ >= stateList.size()) {
                        return true;
                    }
                    /*else if the end of line is reached return false e.g No match*/
                    else if (i >= stringArray.size()) {
                        return false;
                    }

                    insertStates(st, deq);
                }


                /*If the character is a WILDCARD*/
                if(st.char_.compareTo(wildcard) == 0){
                    i++;                                            // Increment point in string as if it has matched

                    /*If at the end of the state then return true e.g match has been found*/
                    if(st.n1_ >= stateList.size() || st.n2_ >= stateList.size()){
                        return true;
                    }
                    /*else if the end of line is reached return false e.g No match*/
                    else if(i >= stringArray.size()){
                        return false;
                    }

                    insertStates(st, deq);
                }


                /*If the character matches that of the current point in the line*/
                if(st.char_.compareTo(checkMatch) == 0){
                    i++;                                        //Increment to next possible character to match

                    if(st.n1_ >= stateList.size() || st.n2_ >= stateList.size()){
                        return true;
                    }
                    else if(i >= stringArray.size()){
                        return false;
                    }

                    insertStates(st, deq);
                }
                else{

                    if(st.n1_ == scanState.n1_) {       //if that state was the scan state
                        if(deq.head == null){           //and the dequeue is empty
                            return false;               //done
                        }
                        else{
                            deq.put(scanState);         //else if it's not empty put the scan state back on the bottom
                            i++;                        //increment where the string is pointing
                            if(i >= stringArray.size()){
                                return false;
                            }
                            continue;                   //continue the loop
                        }
                    }
                }
            }
            else{

                if(st.n1_ >= stateList.size() || st.n2_ >= stateList.size()){
                    return true;
                }
                else if(i >= stringArray.size()){
                    return false;
                }

                insertStates(st, deq);
            }
        }
        return false;
    }

       /*------------------------------------------------*/
    /*Either Puts or pushes the state onto the Dequeue*/
    /*------------------------------------------------*/
    public void insertStates(State st, Dequeue deq){

        State insert1 = stateList.get(st.n1_);
        State insert2 = stateList.get(st.n2_);

        /*if the next states match then only add one state*/
        if(st.n1_ == st.n2_){
            /*if the state to insert is a branching state then push*/
	    if(visitedStates[st.n1_] == false){
		    if(insert1.char_.compareTo(branchState) == 0) {
		        deq.push(insert1);
			visitedStates[st.n1_] = true;
		
		    }
		    /*else put*/
		    else{
		        deq.put(insert1);
		    }
	    }
            
        }
        /*else if the next states differ insert both*/
        else{

    	    if(visitedStates[st.n1_] == false){
		    if(insert1.char_.compareTo(branchState) == 0) {
		        deq.push(insert1);
			visitedStates[st.n1_] = true;
		
		    }
		    /*else put*/
		    else{
		        deq.put(insert1);
		    }
	    }
    	    if(visitedStates[st.n2_] == false){
		    if(insert2.char_.compareTo(branchState) == 0) {
		        deq.push(insert2);
			visitedStates[st.n2_] = true;
		
		    }
		    /*else put*/
		    else{
		        deq.put(insert2);
		    }

	    }
        }
    }
    public static class State{
        String char_;
        int n1_;
        int n2_;


        public State(String getChar, int n1, int n2){

            char_ = getChar;
            n1_ = n1;
            n2_ = n2;
        }
    }

    public static class Dequeue{

        public class Node{
            State state_;
            Node next_;
        }

        Node head;
        Node tail;
        Node curr;


        /*push new values onto the FRONT of the dequeue*/
        public void push(State s){
            Node node = new Node();
            node.state_ = s;
            node.next_ = head;
            head = node;
            if(tail == null) {
                tail = node;
            }
        }

        /*Pops something off the top of the dequeue*/
        public State pop(){
            State r = head.state_;
            if(head.next_ != null) {
                curr = head;
                head = head.next_;
                curr.next_= null;

            }
            else{
                head = null;
            }
            return r;
        }

        /*Put something into the BACK of the dequeue*/
        public void put(State s){
            Node node = new Node();
            node.state_ = s;
            if(tail!=null) {
                tail.next_ = node;
                tail = tail.next_;
            }
            else {
                head = node;
                tail = node;
            }

        }

    }

}
