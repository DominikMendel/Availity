/* AUTHOR : Dominik Mendel
 * For demonstrative purposes only */
import java.util.Deque;
import java.util.LinkedList;

/*
 * Problem statement: You are tasked to write a checker that validates the
 * parentheses of a LISP code. Write a program (in Java or JavaScript) which
 * takes in a string as an input and returns true if all the parentheses in the
 * string are properly closed and nested. */
public class Solution {
    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            System.out.println("Input detected. Verifying each element for proper LISP parenthesis:");
            for (String input : args) {
                System.out.println("Input : " + input + " validation = " + validateLispParentheses(input));
            }
        } else {
            System.out.println("Custom test cases since no given input:");
            String test1 = "(Hello world)"; // True
            String test2 = "((Hello bad world)"; // False
            String test3 = "()()()()()"; // True
            String test4 = "(((((Lots (of) (Irritating) (Superfluous) (Parentheses))))))"; // True
            String test5 = "(((((Lots (of) (Irritating) (Superfluous) (Parentheses)))))))))"; // False
            System.out.println("Input : " + test1 + " validation = " + validateLispParentheses(test1));
            System.out.println("Input : " + test2 + " validation = " + validateLispParentheses(test2));
            System.out.println("Input : " + test3 + " validation = " + validateLispParentheses(test3));
            System.out.println("Input : " + test4 + " validation = " + validateLispParentheses(test4));
            System.out.println("Input : " + test5 + " validation = " + validateLispParentheses(test5));
        }
    }

    // Validates LISP code input has proper balanced parentheses.
    // Assumes any character not '()' is code input and disregards it.
    public static boolean validateLispParentheses(String input) {
        if (input == null || input.isEmpty()) {
            System.out.println("Empty or null input. Returning false.");
            return false;
        }

        System.out.println("Validating input : " + input);

        Deque<Character> stack = new LinkedList<Character>();
        for (int index = 0; index < input.length(); index++) {
            char characterAtPoint = input.charAt(index);
            switch (characterAtPoint) {
                case '(' :
                    // Add open parenthesis to list.
                    stack.addLast('(');
                    break;
                case ')' :
                    // Remove item from stack if closing parenthesis has a matching open available.
                    if (stack.peekFirst() != null && stack.peekFirst() == '(') {
                        stack.removeFirst();
                    } else {
                        // If a ')' is found with no matching '(' then the input is immediately off balance
                        // and can return false.
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }

        // If there are items left in the stack, there was not enough closing parenthesis.
        return stack.size() == 0;

    }
}
