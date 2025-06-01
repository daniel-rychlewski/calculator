import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Pattern;

public class Calculator {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[+-]?(\\d+([.,]\\d*)?|[.,]\\d+)([eE][+-]?\\d+)?$");
    private static final String SINGLE_NUMBER = "^[+-]?\\d+([.,]\\d*)?([eE][+-]?\\d+)?$";
    private static final String NUMBER_PRESENCE = ".*\\d.*";

    private static final int EXPONENTIAL_THRESHOLD = 10;
    private static final int ZERO_THRESHOLD = 4;
    private static final char DEFAULT_SEPARATOR = '.';

    public String calculate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty expression");
        }

        char decimalSeparator = detectDecimalSeparator(expression);
        String s = validateAndPreprocess(expression);

        if (s.matches(SINGLE_NUMBER) &&
                !s.contains("(") && !s.contains(")")) {
            throw new IllegalArgumentException("Nothing to calculate - only one number provided");
        } else if (!s.matches(NUMBER_PRESENCE)) {
            throw new IllegalArgumentException("No numbers in expression");
        }

        double result = evaluateExpression(s, decimalSeparator);
        return formatNumber(result, decimalSeparator);
    }

    private char detectDecimalSeparator(String expression) {
        int dotCount = countMatches(expression, '.');
        int commaCount = countMatches(expression, ',');

        if (dotCount > 0 && commaCount > 0) {
            throw new IllegalArgumentException("Mixed decimal separators found");
        }

        if (dotCount > 0) return '.';
        if (commaCount > 0) return ',';
        return DEFAULT_SEPARATOR;
    }

    private int countMatches(String str, char ch) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == ch) count++;
        }
        return count;
    }

    private String validateAndPreprocess(String expression) {
        String s = expression.replaceAll("\\s+", "");

        int bracketCount = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') bracketCount++;
            if (c == ')') bracketCount--;
            if (bracketCount < 0) break;
        }
        if (bracketCount != 0) {
            throw new IllegalArgumentException("Unbalanced brackets");
        }

        return s;
    }

    private double evaluateExpression(String s, char decimalSeparator) {
        Deque<Double> numbers = new ArrayDeque<>();
        int i = 0;
        char prevOp = '+';

        while (i < s.length()) {
            char c = s.charAt(i);

            if (c == '(') {
                int j = i + 1;
                int balance = 1;
                while (j < s.length() && balance > 0) {
                    if (s.charAt(j) == '(') balance++;
                    if (s.charAt(j) == ')') balance--;
                    j++;
                }
                double num = evaluateExpression(s.substring(i + 1, j - 1), decimalSeparator);
                processOperation(numbers, prevOp, num);
                i = j;
                continue;
            }

            if (Character.isDigit(c) || c == decimalSeparator) {
                StringBuilder numStr = new StringBuilder();
                i = processNumber(s, decimalSeparator, numbers, i, prevOp, numStr);
                continue;
            }

            if (c == '-' && (i == 0 || s.charAt(i - 1) == '(' ||
                    isOperator(s.charAt(i - 1)))) {
                // Handle unary minus
                i++;
                StringBuilder numStr = new StringBuilder("-");
                i = processNumber(s, decimalSeparator, numbers, i, prevOp, numStr);
                continue;
            }

            if (isOperator(c)) {
                prevOp = c;
                i++;
                continue;
            }

            throw new IllegalArgumentException("Invalid character: " + c);
        }

        double result = 0;
        while (!numbers.isEmpty()) {
            result += numbers.pop();
        }
        return result;
    }

    private int processNumber(String s, char decimalSeparator, Deque<Double> numbers, int i, char prevOp, StringBuilder numStr) {
        boolean hasExponent = false;
        while (i < s.length() && (Character.isDigit(s.charAt(i)) ||
                s.charAt(i) == decimalSeparator ||
                s.charAt(i) == 'e' || s.charAt(i) == 'E' ||
                (hasExponent && (s.charAt(i) == '+' || s.charAt(i) == '-')))) {
            if (s.charAt(i) == 'e' || s.charAt(i) == 'E') hasExponent = true;
            numStr.append(s.charAt(i));
            i++;
        }
        double num = parseNumber(numStr.toString(), decimalSeparator);
        processOperation(numbers, prevOp, num);
        return i;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private void processOperation(Deque<Double> numbers, char op, double num) {
        switch (op) {
            case '+':
                numbers.push(num);
                break;
            case '-':
                numbers.push(-num);
                break;
            case '*':
                numbers.push(numbers.pop() * num);
                break;
            case '/':
                if (num == 0) throw new ArithmeticException("Division by zero");
                numbers.push(numbers.pop() / num);
                break;
        }
    }

    private double parseNumber(String numStr, char decimalSeparator) {
        if (!NUMBER_PATTERN.matcher(numStr).matches()) {
            throw new IllegalArgumentException("Invalid number format: " + numStr);
        }
        numStr = numStr.replace(decimalSeparator, '.');
        return Double.parseDouble(numStr);
    }

    private String formatNumber(double number, char decimalSeparator) {
        if (number == 0) return "0";

        String numStr;
        double absNum = Math.abs(number);

        if (shouldUseExponential(absNum)) {
            numStr = String.format("%.6e", number);
            numStr = numStr.replaceAll("\\.?0+(?=e)", "")
                    .replaceAll("e([+-])0", "e$1");
        } else {
            numStr = String.format("%."+EXPONENTIAL_THRESHOLD+"f", number);
            numStr = numStr.replaceAll("\\.?0+$", "");
            if (numStr.endsWith(".")) {
                numStr = numStr.substring(0, numStr.length() - 1);
            }
        }

        numStr = numStr.replace('.', decimalSeparator);
        return numStr;
    }

    private boolean shouldUseExponential(double absNum) {
        if (absNum == 0) return false;
        return absNum >= Math.pow(10, EXPONENTIAL_THRESHOLD) ||
                absNum <= Math.pow(10, -ZERO_THRESHOLD);
    }
}