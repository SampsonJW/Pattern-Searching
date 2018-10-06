//Name: Andy Shen 
//ID: 1304441
//Name: Sampson Ward
//ID: 1312744
import java.util.ArrayList;

public class REcompile {

    static String regex;
    static int j = 0;
    static char p[];
    static ArrayList<Integer> alt = new ArrayList<>();
    static String cha = "";
    static ArrayList<Integer> literals = new ArrayList<>();
    static ArrayList<String> ch = new ArrayList<>();
    static ArrayList<Integer> next1 = new ArrayList<>();
    static ArrayList<Integer> next2 = new ArrayList<>();
    static int state = 0;
    public static void main(String[] args) {

        try {
            if (args.length != 1) {

                System.err.println("Enter a regex");

            }
            regex = args[0];
            p = regex.toCharArray();

            REcompile compile = new REcompile();
            //Start state
            compile.SetState(state, "▓", 1, 1);
            state++;

            compile.Parse();

        }catch(Exception e){}
    }

    public void Parse(){

        Expression();

        if(j < p.length ){
            System.out.println("Error parse error");
        }

        PrintState();

    }

    public int Expression(){

        int r;
        int f;
        int t1;
        f = state -1;

        r = t1 = Term();

        if(j >= p.length){
            return r;
        }
     if(p[j] == '|'){
        //So a branching state points to the right alternation branching state
         if (next1.get(f) == next2.get(f)) {
             next1.set(f, state);
             next2.set(f, state);
         }
         //Add the pointer
         literals.add(state-1);
         state++;
         SetState(state-1,  "▓", t1 , state);
         alt.add(state-1);

         //changes branching state pointer to point to the right state for alternation
         next2.set(f, state - 1);
         j++;
         r = Expression();

          //for loop that makes the literals point to the end of expression
         for(int i = state-1; i > 0; i--) {

             if(ch.get(i) != ""){
                 if(literals.contains(i)) {

                     next1.set(i, r+1);
                     next2.set(i, r+1);
                 }
             }
         }
         if(j >= p.length){
             return r;
         }
         return r;
        }
        return r;

    }

    public int Term(){
        int r, t1, t2, f;
        f = state-1;
        r=t1=Factor();

        if(j >= p.length){
            return r;
        }
        if(p[j] == '*'){
            // makes a dummy state before the expression so it can points over it
            SetState(f+1, "▓", r+1, state);
            state++;
            //Makes a branching state to loop
            SetState(state, "▓", state+1, r);
            //Sets the dummy state to point to the right place
            next1.set(f+1, state);
            r =state;
            state++;
            j++;
            if(j >= p.length){
                return r;
            }
        }
        else if(p[j] == '+'){
            //So it can loop back on the expression
            SetState(state, "▓", state+1, f+1);
            r = state;
            state++;
            j++;
            if(j >= p.length){
                return r;
            }

        }
        else if(p[j] == '?'){
            //Creates a dummy state so it can point past the expression
            if(isVocab(p[j-1])) {
                SetState(r, "", state + 1, state + 1);
                next1.set(t1, t1+1);
                next2.set(t1, state+1);
                //Changes the state to point ahead as a dummy state was inserted
                if (next1.get(t1+1) == next2.get(t1+1)) {
                    next1.set(t1+1, state+1);
                    next2.set(t1+1, state+1);
                }
            }else {
                next1.set(f+1, t1-1);
                next2.set(f+1, state);
            }
            r = state;
            state++;
            j++;
            if(j >= p.length){
                return r;
            }
        }

        if (j < p.length && (isVocab(p[j]) || p[j] == '(' || isFactor(p[j]))){
            //creates state at start of expression
            if(p[j] == '('){
                SetState(state,  "", state+1, state+1);
                state++;
            }

            t2 = Term();

            if(j+1 >= p.length){
                return r;
            }

            //returns the start point of concatenation for the alternation
            if(j >= p.length || p[j+1] == '*'  || p[j+1] == '?'  ||  p[j+1] =='+'){
                return t2;
            }

            return r;
        }
        if(j >= p.length){
            return r;
        }
        return r;
    }

    public int Factor(){
        int r;
        if (isVocab(p[j])) {
            if(j+1 < p.length) {
                if (p[j + 1] != '*') {
                    if(p[j] == '.'){
                        //Creates a state with special wild card symbol
                        SetState(state, cha = "■", state + 1, state + 1);
                    }else {
                        SetState(state, cha = "" + p[j], state + 1, state + 1);
                    }
                }
                else{
                    SetState(state, "" + p[j], state + 2, state + 2);
                }
            }else{
                SetState(state, cha = "" + p[j], state + 1, state + 1);
            }
                r = state;
                state++;
                j++;

                if(j >= p.length){
                    return r;
                }
                return r;
        }

        if(p[j] == '(') {
            //Clears the of literals that was made for the alternation
           literals.clear();
           SetState(state,  "▓", state+1, state+1);
           j++;
           if(j >= p.length){
               System.out.println("error");
           }
           state++;
           r = Expression();
           if(p[j] == ')') {
               if(j+1 < p.length) {
                   if(p[j+1] != '|') {
                       System.out.println("clear" + p[j + 1]);
                       literals.clear();
                   }
               }
               j++;
               if(j >= p.length){
                   return r;
               }
               return r;
           }
           else {
               System.out.println("Error");
           }
        }

        if(p[j] == '\\'){
            //Matches the next character
            j++;
            SetState(state, cha = "" + p[j], state+1, state+1);
            r = state;
            state++;
            j++;
            if(j >= p.length){
                return r;
            }
            return r;
        }

        if(p[j] == '['){
            j++;
            if(p[j] == ']'){

                cha +=  p[j];
                j++;
            }
            List();
            if(p[j] == ']'){
                SetState(state, '┘'+cha, state+1, state+1);
                j++;
                r = state;
                state++;
                if(j >= p.length){
                    return r;
                }
                return r;
            }
        }

        //Checks if its list
        if(p[j] == '!'){
            r = state;
            j++;
            if(p[j] == '['){
                j++;
                //if the first thing in the list is a closing bracket
                if(p[j] == ']'){
                    cha +=  p[j];
                    j++;
                }
                List();
                //the list must have a closing bracket
                if(p[j] == ']'){
                    SetState(state, '┌'+cha, state+1, state+1);
                    j++;
                    if(p[j] == '!'){
                        r = state;
                        state++;
                        j++;
                        return r;
                    }else{
                        System.out.println("Error");
                    }
                    if(j >= p.length){
                        return r;
                    }
                }
            }else{
                System.out.println("Error ! must follow with a [");
            }
        }
        r = state;
        return r;
    }

    //Checks if its a factor
    public boolean isFactor(char p){
        String factor = "\\.[!";
        if(factor.indexOf(p) >=0){

            return true;
        }else{

            return false;
        }

    }

    //Accepts all things in the list
    public void List(){

        while (p[j] != ']'){

            cha += p[j];
            j++;

            //if the list is empty
            if(j >= p.length){
                System.out.println("Error");
            }
        }
    }

    //Checks if its not a vocab
    public boolean isVocab(char p){
        String invalid = "*+|?()[]!\\''";
        if(invalid.indexOf(p) >= 0){
            return false;
        }else{
            return true;
        }
    }
    //Creates a state
    public void SetState(int s, String c, Integer n1, Integer n2){
        try {

            ch.add(s, c);
            next1.add(s, n1);
            next2.add(s, n2);
        }catch(Exception e){System.out.println(e);}

    }
    //Prints all states
    public void PrintState(){
        for(int i = 0; i <= ch.size(); i++) {
            System.out.println(ch.get(i) + "█" + next1.get(i) + "█" + next2.get(i));
        }

    }
}

