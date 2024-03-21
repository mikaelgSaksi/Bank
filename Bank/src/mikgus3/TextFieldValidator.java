/**
 * @author Mikael Gustavsen Saksi, mikgus3
 */
package mikgus3;

import javax.swing.JOptionPane;

public class TextFieldValidator {

	public String validateTextField(String text, String fieldName) {
		if (text == null || text.isEmpty() || text.isBlank()) {
			displayError(fieldName + " cannot be empty!");
			return null;
		}
		return text;
	}

	public int validateIntegerField(String text, String fieldName) {
		try {
			if (text == null || text.isEmpty() || text.isBlank()) {
				displayError(fieldName + " cannot be empty!");
				return -1;
			}
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			displayError(fieldName + " must be a valid integer!");
			return -1;
		}
	}
	
	public String validateSSNField(String text, String fieldName) {
		if(text == null || text.isEmpty() || text.isBlank() || !text.matches("\\d{6}-\\d{4}")) {
			displayError(fieldName + " is not in expected format");
			return null;
		}
		return text;
		
	}
	
	private void displayError(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

}
