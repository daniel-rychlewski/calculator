# Calculator

An implementation of a calculator (JDK 17). It takes a String containing at least two numbers connected with an operator (supported are: `+ - * /`), calculates and outputs the result as a String.

## Example:

Features:

* supports (positive and negative) integer and decimal numbers
  * automatically detects decimal number separator (`,` and `.` are allowed). Once chosen, the same one must be used for all inputs. The default chosen is `.`
* respects the priority of the operators `/ *` over `+ -`
* allows for brackets `()` to modify the precedence of operations
* allows for and uses exponential form below `1e-4` and above `1e10` - number of digits adjustable in `EXPONENTIAL_THRESHOLD` and `ZERO_THRESHOLD`
  * the input exponential form can use `E` or `e`, the output will use `e`
* does not allow division by `0`
* ignores spaces in the input

## Usage:

```
Calculator calculator = new Calculator();
String result = calculator.calculate("3 * -2 + 6");
System.out.println(result); // 0
```