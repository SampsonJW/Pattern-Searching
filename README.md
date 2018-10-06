# Pattern-Searching
Regex pattern searcher using Finite State Machine. 

Overview:     Implement a regexp pattern searcher using the FSM, deque and compiler techniques 
outlined in class. Your solution must consist of two programs: one called REcompile.java and the
other called REsearch.java. The first of these must accept a regexp pattern as a command-line argument
and produce as standard output a description of the corresponding FSM , such that each line of output includes four things: 
the state-number, the input-symbol(s) this state must match (or branch-state indicator), 
and two numbers indicating the two possible next states if a match is made.
The second program must accept, as standard input, the output of the first program, 
then it must execute a search for matching patterns within the text of a file whose name is given as a command-line argument.
Each line of the text file that contains a match is output to standard out just once, 
regardless of the number of times the pattern might be satisfied in that line. 
