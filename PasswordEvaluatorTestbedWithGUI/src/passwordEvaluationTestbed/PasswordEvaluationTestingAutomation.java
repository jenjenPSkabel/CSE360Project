package passwordEvaluationTestbed;

/*******
 * <p> PasswordEvaluator Class </p>
 * 
 * <p> Description: This class is that automates all the test case</p>
 * 
 * 
 * @author ASU CSE360
 * 
 * @version 1.00	2024-10-02 Documenting what is in this class.
 * 
 */

public class PasswordEvaluationTestingAutomation {
	
	static int numPassed = 0;
	static int numFailed = 0;
	
	
	/**********
	 * This is the main method where the automated password tests are performed.
	 * It runs a series of test cases using the performTestCase method to evaluate different passwords.
	 */
	public static void main(String[] args) {
		System.out.println("____________________________________________________________________________");
		System.out.println("\nTesting Automation");
		// It has an uppercase letter, a lowercase letter, a digit, a special character, and is 8 characters long.
		// Should pass
		performTestCase(1, "Aa!15678", true);
		
		// It has an uppercase letter and a special character but is too short.
		// Should fail
		performTestCase(2, "A!", false);
		
		// Test case checks if the evaluator behaves consistently.
		// Should pass but we are testing to see if the expectation part works
		performTestCase(3, "Aa!15678", false);
		
		// Checks whether the evaluator detects short passwords properly.
		// Should fail but testing to see if it is true.
		performTestCase(4, "A!", true);
		// Checks whether the evaluator detects having nothing inputted.
		// Should fail but testing to see if it is true.
		performTestCase(5, "", true);
		
		// Starting my own test cases:
		// Password is missing an uppercase letter but the rest is valid
		// Should fail
		performTestCase(6, "abc!1234", false); 
		
		// Password is missing a lowercase letter but the rest is valid
		// Should Fail
		performTestCase(7, "ABC!1234", false); 
		
		// Password is missing a special character but the rest is valid
		// Should Fail
		performTestCase(8, "Abc12345", false);
		
		// Password is too short but the rest is valid
		// Should Fail
		performTestCase(9, "A!1a", false); 
		
		// Seeing if a space is allowed and that it is not a valid special character but the rest is valid
		// Should Fail
		performTestCase(10, "Abc!123 !", false); 
		
		// Valid password that meets all conditions
		// Should Be true. 
		performTestCase(11, "Abc!1234", true); 
		
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: "+ numPassed);
		System.out.println("Number of tests failed: "+ numFailed);
	}

	
	/**********
	 * This method performs a single test case. It takes in the test case number, the input password,
	 * and whether the password is expected to pass or fail. It then evaluates the password using
	 * the PasswordEvaluator class and compares the result to the expected outcome.
	 *
	 * testCase the number of the current test case
	 * inputText the password being tested
	 * expectedPass whether the password is expected to pass validation
	 */
	private static void performTestCase(int testCase, String inputText, boolean expectedPass) {
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		// Call the PasswordEvaluator to evaluate the input password
		String resultText= PasswordEvaluator.evaluatePassword(inputText);
		
		System.out.println();
		
		// Determine whether the test passed or failed based on the expected outcome
		if (resultText != "") {
			if (expectedPass) {
				System.out.println("***Failure*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				// Increment failed test count
				numFailed++;
			}
			else {			
				System.out.println("***Success*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				// Increment passed test count
				numPassed++;
			}
		}
		
		else {	
			if (expectedPass) {	
				System.out.println("***Success*** The password <" + inputText + 
						"> is valid, so this is a pass!");
				// Increment passed test count
				numPassed++;
			}
			else {
				System.out.println("***Failure*** The password <" + inputText + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				// Increment failed test count
				numFailed++;
			}
		}
		// Output the evaluation summary for the test case
		displayEvaluation();
	}
	
	
	/**********
	 * This method displays the current state of the password evaluator, indicating whether
	 * each of the password requirements (uppercase, lowercase, digit, special character, and length)
	 * has been satisfied.
	 */
	private static void displayEvaluation() {
		if (PasswordEvaluator.foundUpperCase)
			System.out.println("At least one upper case letter - Satisfied");
		else
			System.out.println("At least one upper case letter - Not Satisfied");

		if (PasswordEvaluator.foundLowerCase)
			System.out.println("At least one lower case letter - Satisfied");
		else
			System.out.println("At least one lower case letter - Not Satisfied");
	

		if (PasswordEvaluator.foundNumericDigit)
			System.out.println("At least one digit - Satisfied");
		else
			System.out.println("At least one digit - Not Satisfied");

		if (PasswordEvaluator.foundSpecialChar)
			System.out.println("At least one special character - Satisfied");
		else
			System.out.println("At least one special character - Not Satisfied");

		if (PasswordEvaluator.foundLongEnough)
			System.out.println("At least 8 characters - Satisfied");
		else
			System.out.println("At least 8 characters - Not Satisfied");
	}
}
