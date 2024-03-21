/**
 * @author Mikael Gustavsen Saksi, mikgus3
 */
package mikgus3;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankGui extends JFrame {

	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 500;

	private Integer accountId;

	private String confirmationMessage = "", customerDetails = "", pNo;

	private JMenuBar menuBar;

	private JMenu accountMenu, customerMenu, loadSaveMenu;
	private JMenuItem createSavings, createCredit, closeAccount, deposit, withdraw, getTransactions, createCustomer,
	deleteCustomer, getAllCustomers, getCustomer, changeCustomerName, loadBank, saveBank;

	private JTextField nameField, surnameField, pNoField, accountIdField, amountField;

	private JButton createSavingsAccountButton, createCreditAccountButton, savingsAccountButton, creditAccountButton,
	allCustomersButton;

	private JLabel confirmationLabel, imageLabel;

	private ImageIcon image;

	private JPanel currentPanel, accountPanel, messagePanel, newCustomerPanel, allCustomersPanel, accountButtonPanel,
	customerButtonPanel, optionsButtonPanel, newAccountButtonPanel;

	private JList<String> customerList;
	private DefaultListModel<String> customerListModel;

	private JList<Account> accountList;
	private DefaultListModel<Account> accountListModel;

	private Font centuryGothic;

	private BankLogic banklogic;

	private TextFieldValidator validator;

	// Sets main window with a borderlayout. currentPanel and messagePanel are
	// changed dynamically depending on user input
	public BankGui() {

		super("The Bank");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		banklogic = new BankLogic();
		validator = new TextFieldValidator(); // Helper class to validate input

		centuryGothic = new Font("Century Gothic", Font.BOLD | Font.ITALIC, 24); // Default font style

		JLabel label = new JLabel("Mikaels Bank");
		label.setForeground(Color.GRAY);
		label.setFont(new Font("Century Gothic", Font.BOLD | Font.ITALIC, 46));

		JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
		header.add(label);

		menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(FRAME_WIDTH, 30));
		createAccountMenu();
		createCustomerMenu();
		createLoadSaveMenu();
		menuBar.add(customerMenu);
		menuBar.add(accountMenu);
		menuBar.add(loadSaveMenu);
		setJMenuBar(menuBar);

		customerOptionsPanel();
		newAccountPanel();

		messagePanel = new JPanel();
		messagePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		allCustomersButton = new JButton("All customers / Home");
		allCustomersButton.addActionListener(new AllCustomersHandler());

		currentPanel = new JPanel(new GridBagLayout());
		createMainPanel();
		currentPanel.setPreferredSize(new Dimension(FRAME_HEIGHT, FRAME_WIDTH));

		add(header, BorderLayout.NORTH);
		add(messagePanel, BorderLayout.SOUTH);
		add(currentPanel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	// creates JMenu for account options
	private JMenu createAccountMenu() {
		accountMenu = new JMenu("Account");

		createSavings = new JMenuItem("Create savings account");
		createSavings.addActionListener(new CreateAccountHandler());

		createCredit = new JMenuItem("Create credit account");
		createCredit.addActionListener(new CreateAccountHandler());

		closeAccount = new JMenuItem("Close account");
		closeAccount.addActionListener(new CloseAccountHandler());

		deposit = new JMenuItem("Deposit");
		deposit.addActionListener(new DepositHandler());

		withdraw = new JMenuItem("Withdraw");
		withdraw.addActionListener(new WithdrawHandler());

		getTransactions = new JMenuItem("Transactions");
		getTransactions.addActionListener(new TransactionsHandler());

		accountMenu.add(createSavings);
		accountMenu.add(createCredit);
		accountMenu.add(closeAccount);
		accountMenu.add(deposit);
		accountMenu.add(withdraw);
		accountMenu.add(getTransactions);

		return accountMenu;
	}

	// creates JMenu for customer options
	private JMenu createCustomerMenu() {
		customerMenu = new JMenu("Customer");

		getAllCustomers = new JMenuItem("All customers");
		getAllCustomers.addActionListener(new AllCustomersHandler());

		createCustomer = new JMenuItem("New customer");
		createCustomer.addActionListener(new NewCustomerHandler());

		deleteCustomer = new JMenuItem("Remove customer");
		deleteCustomer.addActionListener(new RemoveCustomerHandler());

		getCustomer = new JMenuItem("Find customer");
		getCustomer.addActionListener(new FindCustomerHandler());

		changeCustomerName = new JMenuItem("Change customer name");
		changeCustomerName.addActionListener(new ChangeNameHandler());

		customerMenu.add(getAllCustomers);
		customerMenu.add(createCustomer);
		customerMenu.add(deleteCustomer);
		customerMenu.add(getCustomer);
		customerMenu.add(changeCustomerName);

		return customerMenu;
	}

	// create JMenu for load/save options. No functionality yet
	private JMenu createLoadSaveMenu() {
		loadSaveMenu = new JMenu("Load/Save");

		loadBank = new JMenuItem("Load bank");
		loadBank.addActionListener(new LoadBankHandler());

		saveBank = new JMenuItem("Save bank");
		saveBank.addActionListener(new SaveBankHandler());

		JMenuItem loadTransactions = new JMenuItem("Load saved transactions");
		loadTransactions.addActionListener(new LoadTransactionsHandler());

		loadSaveMenu.add(loadBank);
		loadSaveMenu.add(saveBank);
		loadSaveMenu.add(loadTransactions);

		return loadSaveMenu;
	}

	// creates main view with a splitpane with customers and their accounts
	private void createMainPanel() {
		currentPanel.removeAll();

		resetpNoAccountId(); // reset pNo and accountId when we return to main view

		createCustomersList();
		customerList = new JList<>(customerListModel);
		customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		CustomerListHandler customerListHandler = new CustomerListHandler();
		customerList.addListSelectionListener(customerListHandler);

		JScrollPane customerScrollPane = new JScrollPane(customerList);
		customerScrollPane.setPreferredSize(new Dimension(250, 100));

		allCustomersPanel = new JPanel(new GridLayout(1, 1));
		allCustomersPanel.add(customerScrollPane);

		accountList = new JList<>();
		accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		AccountListHandler accountListHandler = new AccountListHandler();
		accountList.addListSelectionListener(accountListHandler);

		JScrollPane accountScrollPane = new JScrollPane(accountList);
		accountScrollPane.setPreferredSize(new Dimension(250, 100));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, allCustomersPanel, accountScrollPane);
		splitPane.setOneTouchExpandable(true);

		optionsPanel();
		createImage("mikgus3_files/customers.png");

		currentPanel.add(splitPane, createConstraints(0, 1, 2, 1));
		currentPanel.add(optionsButtonPanel, createConstraints(0, 3, 2, 1));
		currentPanel.add(imageLabel, createConstraints(0, 7, 2, 1)); // Adding image below everything else in the y-axis

		currentPanel.revalidate();
		currentPanel.repaint();
	}

	// create option buttons for when account is selected in main view
	private void accountOptionsPanel() {
		accountButtonPanel = new JPanel();

		JButton deposit = new JButton("Deposit");
		deposit.addActionListener(new DepositHandler());

		JButton withdraw = new JButton("Withdraw");
		withdraw.addActionListener(new WithdrawHandler());

		JButton transactions = new JButton("Transactions");
		transactions.addActionListener(new TransactionsHandler());

		JButton closeAccount = new JButton("Close account");
		closeAccount.addActionListener(new CloseAccountHandler());

		accountButtonPanel.add(deposit);
		accountButtonPanel.add(withdraw);
		accountButtonPanel.add(transactions);
		accountButtonPanel.add(closeAccount);
	}

	// create option buttons for when customer is selected in main view
	private void customerOptionsPanel() {
		customerButtonPanel = new JPanel();

		JButton removeButton = new JButton("Remove customer");
		removeButton.addActionListener(new RemoveCustomerHandler());

		JButton changeButton = new JButton("Change customer name");
		changeButton.addActionListener(new ChangeNameHandler());

		customerButtonPanel.add(removeButton);
		customerButtonPanel.add(changeButton);
	}

	// create buttons for new/find customer. Always visable in main view
	private void optionsPanel() {
		optionsButtonPanel = new JPanel();

		JButton newButton = new JButton("New customer");
		newButton.addActionListener(new NewCustomerHandler());

		JButton findButton = new JButton("Find customer");
		findButton.addActionListener(new FindCustomerHandler());

		optionsButtonPanel.add(newButton);
		optionsButtonPanel.add(findButton);
	}

	// create buttons for account creation. When customer is selected
	private void newAccountPanel() {
		newAccountButtonPanel = new JPanel();

		savingsAccountButton = new JButton("Create savings account");
		savingsAccountButton.addActionListener(new CreateAccountHandler());

		creditAccountButton = new JButton("Create credit account");
		creditAccountButton.addActionListener(new CreateAccountHandler());

		newAccountButtonPanel.add(savingsAccountButton);
		newAccountButtonPanel.add(creditAccountButton);
	}

	// creates textfield with param bordertext
	private JTextField textFieldCreator(String borderText) {
		JTextField textField = new JTextField();
		textField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), borderText));
		return textField;
	}

	// creates textfield for pNo. Sets text if user is selected from main view
	private JTextField pNoTextField(String borderText) {
		JTextField textField = new JTextField();
		textField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), borderText));

		if (pNo != null) {
			textField.setText(pNo);
		}
		return textField;
	}

	// creates textfield for account. Sets text if account is selected from main
	// view
	private JTextField accountTextField(String borderText) {
		JTextField textField = new JTextField();
		textField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), borderText));

		if (accountId != null) {
			textField.setText(accountId.toString());
		}
		return textField;
	}

	// create JLabel with image. Param path to image
	private void createImage(String path) {
		imageLabel = new JLabel();
		image = new ImageIcon(path);
		imageLabel.setIcon(image);
		imageLabel.setVisible(true);
	}

	// create confirmation label depending on user action.
	private void createMessagePanel(String message) {
		messagePanel.removeAll();
		confirmationLabel = new JLabel(message);
		confirmationLabel.setHorizontalAlignment(JLabel.CENTER);
		messagePanel.add(confirmationLabel);
		messagePanel.revalidate();
		messagePanel.repaint();
	}

	// clear confirmationmessage after user action
	private void clearMessagePanel() {
		confirmationMessage = "";
		messagePanel.removeAll();
	}

	// helper method for creating customer list
	private void createCustomersList() {
		List<String> customerStrings = banklogic.getAllCustomers();
		customerListModel = new DefaultListModel<>();
		customerListModel.clear();

		for (String customerString : customerStrings) {
			customerListModel.addElement(customerString);
		}
	}

	// helper method for creating account list. Param pNo to find associated
	// accounts
	private void createAccountList(String pNo) {
		List<Account> accountList = banklogic.getAccountList(pNo);
		accountListModel = new DefaultListModel<>();
		accountListModel.clear();

		for (Account account : accountList) {
			accountListModel.addElement(account);
		}
	}

	// helper method to set gridbagconstraints for the views with gridbaglayout.
	// Define placement and size.
	private GridBagConstraints createConstraints(int gridx, int gridy, int gridw, int gridh) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridw;
		gbc.gridheight = gridh;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(2, 2, 2, 2);

		return gbc;
	}

	// helper method to obtain pNo from string
	private void parsePno(String customerString) {
		if (customerString != null) {
			String[] parts = customerString.split(" ");
			pNo = parts[0];
		}
	}

	// To clarify confirmationsmessage to see the account number/balance/account
	// type and interest rate.
	private String parseCloseDetails(String closedAccount) {
		String closeDetails = "";
		if (closedAccount != null) {
			String[] parts = closedAccount.split(" ");
			if (parts.length >= 4) {

				int endIndex = 1;
				for (int i = 1; i < parts.length; i++) {
					if (parts[i].equals("kr")) {
						endIndex = i + 1;
						break;
					}
				}
				// balance is until first "kr" instead of " " because of how the returned string
				// from account is formated
				String balance = String.join(" ", Arrays.copyOfRange(parts, 1, endIndex));
				closeDetails = "<html>Account number: " + parts[0] + "<br>Account balance: " + balance
						+ "<br>Account type: " + parts[endIndex] + "<br>Interest rate: " + parts[endIndex + 1]
								+ " kr</html>";
			}
		}
		return closeDetails;
	}

	// helper method to reset pNo and acountId when no customer is selected
	private void resetpNoAccountId() {
		accountId = null;
		pNo = null;
	}

	// helper for error messages
	private void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(BankGui.this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	// creates view for new accounts
	private class CreateAccountHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			currentPanel.removeAll();
			clearMessagePanel();
			accountPanel = new JPanel(new GridLayout(5, 1));
			((GridLayout) accountPanel.getLayout()).setVgap(4);
			pNoField = pNoTextField("Social security number: ");

			if (e.getSource() == createSavings || e.getSource() == savingsAccountButton) {
				JLabel savingsAccountLabel = new JLabel("Create Savings Account");
				savingsAccountLabel.setFont(centuryGothic);
				savingsAccountLabel.setForeground(Color.GRAY);
				accountPanel.add(savingsAccountLabel);
				createImage("mikgus3_files/savingsaccount.jpg");
				createSavingsAccountButton = new JButton("Create savings account");
				createSavingsAccountButton.addActionListener(new CreateAccountButtonHandler());
				createSavingsAccountButton.setPreferredSize(new Dimension(100, 30));
				createCreditAccountButton = null;
			}

			if (e.getSource() == createCredit || e.getSource() == creditAccountButton) {
				JLabel creditAccountLabel = new JLabel("Create Credit Account");
				creditAccountLabel.setFont(centuryGothic);
				creditAccountLabel.setForeground(Color.GRAY);
				accountPanel.add(creditAccountLabel);
				createImage("mikgus3_files/creditaccount.png");
				createCreditAccountButton = new JButton("Create credit account");
				createCreditAccountButton.addActionListener(new CreateAccountButtonHandler());
				createCreditAccountButton.setPreferredSize(new Dimension(100, 30));
				createSavingsAccountButton = null;
			}
			accountPanel.add(pNoField);

			if (createSavingsAccountButton != null) // if not initialized, add savingsaccountbutton
				accountPanel.add(createSavingsAccountButton);
			else if (createCreditAccountButton != null) // if not initialized, add creditaccountbutton
				accountPanel.add(createCreditAccountButton);

			accountPanel.add(allCustomersButton);

			currentPanel.add(accountPanel);
			currentPanel.add(imageLabel, createConstraints(0, 7, 2, 1));
			currentPanel.revalidate();
			currentPanel.repaint();

		}
	}

	// Call to banklogic create credit/savings account. Set confirmation message if
	// successful. Else error pane
	private class CreateAccountButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int accountNmbr = 0;

			if (banklogic.customerDetails(pNoField.getText()) != null) {
				customerDetails = banklogic.customerDetails(pNoField.getText());
				if (e.getSource() == createSavingsAccountButton) {
					accountNmbr = banklogic.createSavingsAccount(pNoField.getText());
					if (accountNmbr >= 0) {
						confirmationMessage = "<html><div style='text-align: center; 'line-height: 1.5;'>You have succesfully created an savings account for<br>"
								+ customerDetails + "<br>Accountnumber: " + accountNmbr + "<br></div></html>";
						createMessagePanel(confirmationMessage);
						resetpNoAccountId();
					}
				}
				if (e.getSource() == createCreditAccountButton) {
					accountNmbr = banklogic.createCreditAccount(pNoField.getText());
					if (accountNmbr >= 0) {
						confirmationMessage = "<html><div style='text-align: center; 'line-height: 1.5;'>You have succesfully created an credit account for<br>"
								+ customerDetails + "<br>Accountnumber: " + accountNmbr + "<br></div></html>";
						createMessagePanel(confirmationMessage);
						resetpNoAccountId();
					}
				}
			} else
				showErrorMessage("Customer not found!");
		}

	}

	// create view for new customer
	private class NewCustomerHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			currentPanel.removeAll();
			clearMessagePanel();

			newCustomerPanel = new JPanel(new GridLayout(6, 1));
			((GridLayout) newCustomerPanel.getLayout()).setVgap(4);

			JLabel newCustomerLabel = new JLabel("Create New Customer");
			newCustomerLabel.setFont(centuryGothic);
			newCustomerLabel.setForeground(Color.GRAY);

			resetpNoAccountId();
			nameField = textFieldCreator("First name: ");
			surnameField = textFieldCreator("Surname: ");
			pNoField = pNoTextField(
					"<html>Social security number&nbsp;&nbsp;<font size=\"2\"> (YYMMDD-XXXX):<font><html>");

			JButton createCustomerButton = new JButton("Create New Customer");
			createCustomerButton.addActionListener(new NewCustomerButtonHandler());

			newCustomerPanel.add(newCustomerLabel);
			newCustomerPanel.add(nameField);
			newCustomerPanel.add(surnameField);
			newCustomerPanel.add(pNoField);
			newCustomerPanel.add(createCustomerButton);
			newCustomerPanel.add(allCustomersButton);

			currentPanel.add(newCustomerPanel);
			currentPanel.revalidate();
			currentPanel.repaint();
		}

	}

	// call to banklogic create customer. Error pane if customer already in system
	private class NewCustomerButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String name, surname;

			name = validator.validateTextField(nameField.getText(), "Name");
			surname = validator.validateTextField(surnameField.getText(), "Surname");
			pNo = validator.validateSSNField(pNoField.getText(), "Social security number");

			if (pNo != null) {
				if (banklogic.createCustomer(name, surname, pNo)) {
					customerDetails = banklogic.customerDetails(pNo);
					confirmationMessage = "<html><div style='text-align: center;'>New customer created:<br>"
							+ customerDetails + "</div></html>";
					createMessagePanel(confirmationMessage);
				} else
					showErrorMessage("Customer already exists!");
			}
		}
	}

	// create view for removal of customer.
	private class RemoveCustomerHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentPanel.removeAll();
			clearMessagePanel();

			JPanel removeCustomerPanel = new JPanel(new GridLayout(5, 1));
			((GridLayout) removeCustomerPanel.getLayout()).setVgap(4);

			JLabel removeCustomerLabel = new JLabel("Remove Customer");
			removeCustomerLabel.setFont(centuryGothic);
			removeCustomerLabel.setForeground(Color.GRAY);

			pNoField = pNoTextField("Social security number: ");

			JButton removeCustomerButton = new JButton("RemoveCustomer");
			removeCustomerButton.addActionListener(new RemoveCustomerButtonHandler());

			removeCustomerPanel.add(removeCustomerLabel);
			removeCustomerPanel.add(pNoField);
			removeCustomerPanel.add(removeCustomerButton);
			removeCustomerPanel.add(allCustomersButton);

			createImage("mikgus3_files/delete.png");

			currentPanel.add(removeCustomerPanel);
			currentPanel.add(imageLabel, createConstraints(0, 7, 2, 1));
			currentPanel.revalidate();
			currentPanel.repaint();
		}
	}

	// call to banklogic delete customer. Error pane if customer is not found.
	// Confirmationmessage set with customer and accounts. Did not have time to
	// format account details better
	private class RemoveCustomerButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			List<String> deletedCustomer;

			pNo = validator.validateSSNField(pNoField.getText(), "Social security number");
			deletedCustomer = banklogic.deleteCustomer(pNo);

			if (deletedCustomer != null) {
				confirmationMessage = "<html><div style='text-align: center;'>You have successfully deleted customer:<br>"
						+ deletedCustomer.get(0) + "<br>";

				for (int i = 1; i < deletedCustomer.size(); i++) {
					confirmationMessage += deletedCustomer.get(i) + "<br>";
				}

				confirmationMessage += "</div></html>";
				createMessagePanel(confirmationMessage);

			} else
				showErrorMessage("Customer does not exist");
		}
	}

	// create view for changing customer name
	private class ChangeNameHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			currentPanel.removeAll();
			clearMessagePanel();

			JPanel changeNamePanel = new JPanel(new GridLayout(6, 1));
			((GridLayout) changeNamePanel.getLayout()).setVgap(4);

			JLabel changeNameLabel = new JLabel("Change customer name");
			changeNameLabel.setFont(centuryGothic);
			changeNameLabel.setForeground(Color.GRAY);

			nameField = textFieldCreator("Set new first name: ");
			surnameField = textFieldCreator("Set new surname: ");
			pNoField = pNoTextField("Social security number: ");

			JButton changeNameButton = new JButton("Change customer name");
			changeNameButton.addActionListener(new ChangeNameButtonHandler());

			changeNamePanel.add(changeNameLabel);
			changeNamePanel.add(pNoField);
			changeNamePanel.add(nameField);
			changeNamePanel.add(surnameField);
			changeNamePanel.add(changeNameButton);
			changeNamePanel.add(allCustomersButton);

			currentPanel.add(changeNamePanel);
			currentPanel.revalidate();
			currentPanel.repaint();

		}

	}

	// call to banklogic change customer name.
	// Saving old names for better confirmationmessage
	// Error if customer not found
	private class ChangeNameButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String oldName, name, surname;
			name = validator.validateTextField(nameField.getText(), "Name");
			surname = validator.validateTextField(surnameField.getText(), "Surname");
			pNo = validator.validateSSNField(pNoField.getText(), "Social security number");

			if (banklogic.findCustomer(pNo) != null) {
				oldName = banklogic.findCustomer(pNo).getName();
				oldName += " " + banklogic.findCustomer(pNo).getSurname();
				if (banklogic.changeCustomerName(name, surname, pNo)) {
					confirmationMessage = "<html><div style='text-align: center;'>Name of customer " + oldName
							+ " successfully changed! <br>" + "New name: " + name + " " + surname + "<br></dir></html>";
					createMessagePanel(confirmationMessage);
				} else
					showErrorMessage("Field can not be empty");
			} else
				showErrorMessage("Customer does not exist");
		}
	}

	// Listener for All customer / Home button
	private class AllCustomersHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			createMainPanel();
		}
	}

	// Handles selected item in customerlist. Creates account list with parsed pNo
	private class CustomerListHandler implements ListSelectionListener {
		private boolean selected = false; // keeps track of selected to make sure code in 'if' block is only executed
		// once

		@Override
		public void valueChanged(ListSelectionEvent e) {

			if (!selected && !e.getValueIsAdjusting()) {
				String selectedCustomer = customerList.getSelectedValue();
				selected = true;
				if (selectedCustomer != null) {
					currentPanel.add(newAccountButtonPanel, createConstraints(0, 2, 2, 1));
					currentPanel.add(customerButtonPanel, createConstraints(0, 4, 2, 1));

					parsePno(selectedCustomer);
					createAccountList(pNo);

					accountList.setModel(accountListModel);

					currentPanel.revalidate();
					currentPanel.repaint();
				}
				selected = false;
			}
		}
	}

	// Handles selected item in account list.
	private class AccountListHandler implements ListSelectionListener {
		private boolean selected = false; // keeps track of selected to make sure code in 'if' block is only executed
		// once

		@Override
		public void valueChanged(ListSelectionEvent e) {

			if (!selected && !e.getValueIsAdjusting()) {
				Account selectedAccount = accountList.getSelectedValue();
				selected = true;
				if (selectedAccount != null) {
					accountId = selectedAccount.getAccountNmbr();
					accountOptionsPanel();
					currentPanel.add(accountButtonPanel, createConstraints(0, 5, 2, 1));
				} else {
					currentPanel.remove(accountButtonPanel); // remove account button panel if no account is selected
				}
				selected = false;
				currentPanel.revalidate();
				currentPanel.repaint();
			}
		}
	}

	// creates deposit view
	private class DepositHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			currentPanel.removeAll();
			clearMessagePanel();

			JPanel depositPanel = new JPanel(new GridLayout(6, 1));
			((GridLayout) depositPanel.getLayout()).setVgap(4);

			// Everything beneath the label adjusts to its width for some reason. Hence the
			// empty spaces. Can't figure it out
			JLabel depositLabel = new JLabel("         Deposit        ");
			depositLabel.setFont(centuryGothic);
			depositLabel.setForeground(Color.GRAY);

			pNoField = pNoTextField("Social security number: ");
			accountIdField = accountTextField("Account number: ");
			amountField = textFieldCreator("Amount to deposit: ");

			JButton depositButton = new JButton("Deposit");
			depositButton.addActionListener(new DepositButtonHandler());

			depositPanel.add(depositLabel);
			depositPanel.add(pNoField);
			depositPanel.add(accountIdField);
			depositPanel.add(amountField);
			depositPanel.add(depositButton);
			depositPanel.add(allCustomersButton);

			createImage("mikgus3_files/deposit.png");

			currentPanel.add(depositPanel);
			currentPanel.add(imageLabel, createConstraints(0, 7, 2, 1));
			currentPanel.revalidate();
			currentPanel.repaint();
		}

	}

	// call to benklogic deposit with user input params
	// sets appropriate confirmationmessage. Error pane if customer/account not
	// found
	private class DepositButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			int amount;

			pNo = validator.validateSSNField(pNoField.getText(), "Social security number");
			accountId = validator.validateIntegerField(accountIdField.getText(), "Account number");
			amount = validator.validateIntegerField(amountField.getText(), "Amount");

			if (banklogic.deposit(pNo, accountId, amount)) {
				confirmationMessage = "<html><div style='text-align: center;'>You have successfully made a deposit of "
						+ amount + " kr to account number " + accountId;
				createMessagePanel(confirmationMessage);
			} else
				showErrorMessage("Customer or account number does not exist");
		}

	}

	// creates withdraw view
	private class WithdrawHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			currentPanel.removeAll();
			clearMessagePanel();

			JPanel withdrawPanel = new JPanel(new GridLayout(6, 1));
			((GridLayout) withdrawPanel.getLayout()).setVgap(4);

			// Everything beneath the label adjusts to its width for some reason. Hence the
			// empty spaces. Can't figure it out
			JLabel withdrawLabel = new JLabel("         Withdraw        ");
			withdrawLabel.setFont(centuryGothic);
			withdrawLabel.setForeground(Color.GRAY);

			pNoField = pNoTextField("Social security number: ");
			accountIdField = accountTextField("Account number: ");
			amountField = textFieldCreator("Amount to withdraw: ");

			JButton withdrawButton = new JButton("Withdraw");
			withdrawButton.addActionListener(new WithdrawButtonHandler());

			withdrawPanel.add(withdrawLabel);
			withdrawPanel.add(pNoField);
			withdrawPanel.add(accountIdField);
			withdrawPanel.add(amountField);
			withdrawPanel.add(withdrawButton);
			withdrawPanel.add(allCustomersButton);

			createImage("mikgus3_files/withdrawal.png");

			currentPanel.add(withdrawPanel);
			currentPanel.add(imageLabel, createConstraints(0, 7, 2, 1));
			currentPanel.revalidate();
			currentPanel.repaint();
		}

	}

	// call to banklogic withdraw with user input params
	// sets appropriate confirmationmessage. Error pane if customer/account not
	// found or insufficient funds
	private class WithdrawButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			int amount;

			pNo = validator.validateSSNField(pNoField.getText(), "Social security number");
			accountId = validator.validateIntegerField(accountIdField.getText(), "Account number");
			amount = validator.validateIntegerField(amountField.getText(), "Amount");

			if (banklogic.withdraw(pNo, accountId, amount)) {
				confirmationMessage = "<html><div style='text-align: center;'>You have successfully made a withdrawal of "
						+ amount + " kr from account number " + accountId;
				createMessagePanel(confirmationMessage);
			} else
				showErrorMessage("Input Error: Customer or account number not found, or insufficient funds. "
						+ "Please check your input and try again");
		}

	}

	// creates view for choosing account to show transactions
	private class TransactionsHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			currentPanel.removeAll();
			clearMessagePanel();

			JPanel transactionPanel = new JPanel(new GridLayout(5, 1));
			((GridLayout) transactionPanel.getLayout()).setVgap(4);

			JLabel transactionLabel = new JLabel("         Transactions        ");
			transactionLabel.setFont(centuryGothic);
			transactionLabel.setForeground(Color.GRAY);

			pNoField = pNoTextField("Social security number: ");
			accountIdField = accountTextField("Account number: ");

			JButton transactionButton = new JButton("Show transactions");
			transactionButton.addActionListener(new TransactionButtonHandler());

			transactionPanel.add(transactionLabel);
			transactionPanel.add(pNoField);
			transactionPanel.add(accountIdField);
			transactionPanel.add(transactionButton);
			transactionPanel.add(allCustomersButton);

			createImage("mikgus3_files/transactions.jpg");

			currentPanel.add(transactionPanel);
			currentPanel.add(imageLabel, createConstraints(0, 7, 2, 1));
			currentPanel.revalidate();
			currentPanel.repaint();
		}
	}

	// creates the actual scrollpane with transactions
	// call to banklogic gettransactions with user inputs
	// set confirmationmessage with specified accountId
	// Error message if no transactions in account
	private class TransactionButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			pNo = validator.validateSSNField(pNoField.getText(), "Social security number");
			accountId = validator.validateIntegerField(accountIdField.getText(), "Account number: ");

			List<String> transactions = banklogic.getTransactions(pNo, accountId);

			DefaultListModel<String> list = new DefaultListModel<>();
			if (transactions != null && !transactions.isEmpty()) {
				for (String transaction : transactions)
					list.addElement(transaction);
				confirmationMessage = "Showing transactions for account number " + accountId;
				createMessagePanel(confirmationMessage);

				JPanel transactionListPanel = new JPanel(new GridBagLayout());

				JLabel transactionsListLabel = new JLabel("Transactions for account: " + accountId);
				transactionsListLabel.setFont(new Font("Century Gothic", Font.BOLD | Font.ITALIC, 20));
				transactionsListLabel.setForeground(Color.GRAY);

				JList<String> transactionsList = new JList<>(list);
				JScrollPane scrollPane = new JScrollPane(transactionsList);
				JButton saveTransactionsbutton = new JButton("Save transactions to file");

				saveTransactionsbutton.addActionListener(new SaveTransactionsHandler());

				transactionListPanel.add(transactionsListLabel, createConstraints(0, 0, 2, 1));
				transactionListPanel.add(scrollPane, createConstraints(0, 1, 2, 1));
				transactionListPanel.add(saveTransactionsbutton, createConstraints(0, 2, 2, 2));
				transactionListPanel.add(allCustomersButton, createConstraints(0, 4, 2, 2));

				createImage("mikgus3_files/transactions.jpg");

				currentPanel.removeAll();
				currentPanel.add(transactionListPanel);
				currentPanel.add(imageLabel, createConstraints(0, 7, 2, 1));
				currentPanel.revalidate();
				currentPanel.repaint();

			} else
				showErrorMessage("No transactions for selected account. Please try again.");
		}

	}

	// Save transactions of specified account to file
	// call to banklogic saveTransactionstoFile with fileName, pNo, account id
	// if file exist, confirmation is needed before overwriting
	// catches IOException and throws error winddow
	private class SaveTransactionsHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(new File("mikgus3_files"));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt");
			chooser.setFileFilter(filter);
			int result = chooser.showSaveDialog(null);

			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = chooser.getSelectedFile();
				if (selectedFile.exists()) {
					int response = JOptionPane.showConfirmDialog(null, "This file will be overwritten. Proceed?",
							"File Overwrite warning", JOptionPane.YES_NO_OPTION);
					if (response != JOptionPane.YES_OPTION)
						return;
				}
				String fileName = selectedFile.getAbsolutePath();
				try {
					banklogic.saveTransactionsToFile(fileName, pNo, accountId);
				} catch (IOException e3) {
					showErrorMessage("Error saving data: " + e3.getMessage());
				}
			}
		}

	}

	// Show transactions in dialog window
	// call to loadSavedTransactions in banklogic
	// Error message if banklogic returns IOException
	private class LoadTransactionsHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			List<String> savedTransactions = new ArrayList<>();
			DefaultListModel<String> list = new DefaultListModel<>();

			JFileChooser chooser = new JFileChooser(new File("mikgus3_files"));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt");
			chooser.setFileFilter(filter);
			int result = chooser.showOpenDialog(null);

			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = chooser.getSelectedFile();
				String fileName = selectedFile.getAbsolutePath();
				try {
					savedTransactions = banklogic.loadSavedTransactions(fileName);
				} catch (IOException e4) {
					showErrorMessage("Error loading transactions: " + e4.getMessage());
				}
			}
			if (savedTransactions != null && !savedTransactions.isEmpty()) {
				JDialog dialog = new JDialog(BankGui.this, "Saved Transactions");
				dialog.setSize(400, 400);

				for (String string : savedTransactions) {
					list.addElement(string);
					;
				}

				JList<String> transactionList = new JList<String>(list);
				JScrollPane scrollPane = new JScrollPane(transactionList);
				JPanel panel = new JPanel(new BorderLayout());
				Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
				panel.setBorder(padding);
				panel.add(scrollPane);
				dialog.add(panel);
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		}
	}

	// create view for closing account
	private class CloseAccountHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			currentPanel.removeAll();
			clearMessagePanel();

			JPanel closeAccountPanel = new JPanel(new GridLayout(5, 1));
			((GridLayout) closeAccountPanel.getLayout()).setVgap(4);

			JLabel closeAccountLabel = new JLabel("    Close account    ");
			closeAccountLabel.setFont(centuryGothic);
			closeAccountLabel.setForeground(Color.GRAY);

			pNoField = pNoTextField("Social security number: ");
			accountIdField = accountTextField("Account number: ");

			JButton closeAccountButton = new JButton("Close Account");
			closeAccountButton.addActionListener(new CloseAccountButtonHandler());

			closeAccountPanel.add(closeAccountLabel);
			closeAccountPanel.add(pNoField);
			closeAccountPanel.add(accountIdField);
			closeAccountPanel.add(closeAccountButton);
			closeAccountPanel.add(allCustomersButton);

			currentPanel.add(closeAccountPanel);
			currentPanel.revalidate();
			currentPanel.repaint();

		}
	}

	// call to banklogic close account with user inputs.
	// parses the returned string for easier to read confirmation message
	// Error pane if customer/account not found
	private class CloseAccountButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String accountClosed, closeDetails = "";
			pNo = validator.validateSSNField(pNoField.getText(), "Social security number");
			accountId = validator.validateIntegerField(accountIdField.getText(), "Account number: ");

			if (pNo != null && accountId != -1) {
				accountClosed = banklogic.closeAccount(pNo, accountId);
				closeDetails = parseCloseDetails(accountClosed);
			}

			if (!closeDetails.isEmpty()) {
				confirmationMessage += "<html>You have successfullly closed account.<br><html>" + closeDetails;
				createMessagePanel(confirmationMessage);
			} else
				showErrorMessage("Input Error: Customer or account number not found");
		}
	}

	// creates view for finding customer
	private class FindCustomerHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			currentPanel.removeAll();
			clearMessagePanel();
			resetpNoAccountId();

			JPanel findCustomerPanel = new JPanel(new GridLayout(5, 1));
			((GridLayout) findCustomerPanel.getLayout()).setVgap(4);

			JLabel findCustomerLabel = new JLabel("   Find customer   ");
			findCustomerLabel.setFont(centuryGothic);
			findCustomerLabel.setForeground(Color.GRAY);

			pNoField = pNoTextField("Social security number: ");

			JButton findCustomerButton = new JButton("Find customer");
			findCustomerButton.addActionListener(new FindCustomerButtonHandler());

			findCustomerPanel.add(findCustomerLabel);
			findCustomerPanel.add(pNoField);
			findCustomerPanel.add(findCustomerButton);
			findCustomerPanel.add(allCustomersButton);

			currentPanel.add(findCustomerPanel);
			currentPanel.revalidate();
			currentPanel.repaint();

		}
	}

	// call to banklogic get customer with param pNo from user input
	// Error pane if customer info is null
	// set confirmation message with user and accounts
	private class FindCustomerButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			pNo = validator.validateSSNField(pNoField.getText(), "Social security number");
			List<String> customerInfo = banklogic.getCustomer(pNo);

			if (customerInfo == null) {
				showErrorMessage("Customer information not available");
				return;
			}

			confirmationMessage = "<html> Customer information: ";
			for (String c : customerInfo)
				confirmationMessage += "<br>" + c;
			confirmationMessage += "</html>";
			createMessagePanel(confirmationMessage);
		}
	}

	//Handler for loading bank. Relative path to folder in src
	//clears customerlistModel and repaints main panel 
	//catches Classnotfound & IOException and displays error window
	private class LoadBankHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(new File("mikgus3_files"));
			int result = chooser.showOpenDialog(null);

			if (result == JFileChooser.APPROVE_OPTION) {
				if (customerListModel != null)
					customerListModel.clear();
				File selectedFile = chooser.getSelectedFile();
				String fileName = selectedFile.getAbsolutePath();
				try {
					banklogic.loadCustomerAndAccounts(fileName);
				} catch (ClassNotFoundException | IOException e1) {
					showErrorMessage("Error loading data " + e1.getMessage());
				}
				createMainPanel();
			}
		}
	}

	//save bank through filechooser
	private class SaveBankHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(new File("mikgus3_files"));
			int result = chooser.showSaveDialog(null);

			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = chooser.getSelectedFile();
				if (selectedFile.exists()) {
					int response = JOptionPane.showConfirmDialog(null, "This file will be overwritten. Proceed?",
							"File Overwrite warning", JOptionPane.YES_NO_OPTION);
					if (response != JOptionPane.YES_OPTION)
						return;
				}

				String fileName = selectedFile.getAbsolutePath();
				try {
					banklogic.saveCustomerAndAccounts(fileName);
				} catch (IOException e1) {
					showErrorMessage("Error saving data " + e1.getMessage());
				}
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			BankGui banken = new BankGui();
		});
	}
}
