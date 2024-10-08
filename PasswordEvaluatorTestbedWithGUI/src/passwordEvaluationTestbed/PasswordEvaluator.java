package passwordEvaluationTestbed;


/*******
 * <p> PasswordEvaluator Class </p>
 * 
 * <p> Description: This class is what decides if what the user inputs is a valid password </p>
 * 
 * 
 * @author ASU CSE360
 * 
 * @version 1.00	2024-10-02 documenting what is in this class.
 * 
 */


public class PasswordEvaluator {

	
	
	/**********************************************************************************************

	Attributes
	
	**********************************************************************************************/
	
	// These are names of the evaluators and what they are initialized as.
	// The names of the variables specify their function and each is initialize as required
	public static String passwordErrorMessage = "";
	public static String passwordInput = "";
	public static int passwordIndexofError = -1;
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;
	private static String inputLine = "";
	private static char currentChar;
	private static int currentCharNdx;
	private static boolean running;

	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/

	/**********
	 * This method states what the Input is going to say
	 */
	private static void displayInputState() {
		System.out.println(inputLine);
		System.out.println(inputLine.substring(0,currentCharNdx) + "?");
		System.out.println("The password size: " + inputLine.length() + "  |  The currentCharNdx: " + 
				currentCharNdx + "  |  The currentChar: \"" + currentChar + "\"");
	}
	/**********
	 * This method checks if the password meets the required criteria:
	 * - At least one uppercase letter
	 * - At least one lowercase letter
	 * - At least one digit
	 * - At least one special character
	 * - Minimum length of 8 characters
	 *
	 * It returns an error message if the password is invalid, or an empty string if valid.
	 */
	public static String evaluatePassword(String input) {
	    // Initialize variables for tracking password state
	    passwordErrorMessage = "";
	    passwordIndexofError = 0;
	    inputLine = input;
	    currentCharNdx = 0;
	    
	    // Check if the password is empty
	    if (input.length() <= 0) return "*** Error *** The password is empty!";
	    
	    // Start checking characters in the password
	    currentChar = input.charAt(0);
	    passwordInput = input;
	    foundUpperCase = false;
	    foundLowerCase = false;
	    foundNumericDigit = false;
	    foundSpecialChar = false;
	    foundLongEnough = false;
	    running = true;

	    // Loop through each character in the input
		while (running) {
			displayInputState(); // Debug output of current password state
			
			// Check for uppercase, lowercase, digits, or special characters
			// Along with the rest of the cases.
			if (currentChar >= 'A' && currentChar <= 'Z') {
				System.out.println("Upper case letter found");
				foundUpperCase = true;
			} else if (currentChar >= 'a' && currentChar <= 'z') {
				System.out.println("Lower case letter found");
				foundLowerCase = true;
			} else if (currentChar >= '0' && currentChar <= '9') {
				System.out.println("Digit found");
				foundNumericDigit = true;
			} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
				System.out.println("Special character found");
				foundSpecialChar = true;
			} else {
				passwordIndexofError = currentCharNdx;
				// Mark error position
				return "*** Error *** An invalid character has been found!";
			}
			// Check if the password is at least 8 characters long
			if (currentCharNdx >= 7) {
				System.out.println("At least 8 characters found");
				foundLongEnough = true;
			}
			// Move to the next character
			currentCharNdx++;
			// Stop if we've checked all characters
			if (currentCharNdx >= inputLine.length())
				running = false;
			else
				currentChar = input.charAt(currentCharNdx);
			
			System.out.println();
		}
		
		// Error message if any conditions are not met
		String errMessage = "";
		if (!foundUpperCase)
			errMessage += "Upper case; ";
		
		if (!foundLowerCase)
			errMessage += "Lower case; ";
		
		if (!foundNumericDigit)
			errMessage += "Numeric digits; ";
			
		if (!foundSpecialChar)
			errMessage += "Special character; ";
			
		if (!foundLongEnough)
			errMessage += "Long Enough; ";
		
		
		// Return result: empty string if valid, error message if not
		if (errMessage == "")
			return "";
		
		passwordIndexofError = currentCharNdx;
		return errMessage + "conditions were not satisfied";

	}
}
