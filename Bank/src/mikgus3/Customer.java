/**
 * @author Mikael Gustavsen Saksi, mikgus3
 */
package mikgus3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Customer implements Serializable{
	
	private String name;
	private String surname;
	private final String pNo;
	private ArrayList<Account> accounts;
	

	public Customer(String name, String surname, String pNo) {
		this.name = name;
		this.surname = surname;
		this.pNo = pNo;
		this.accounts = new ArrayList<Account>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getpNo() {
		return pNo;
	}
	
	public List<Account> getAccounts() {
		return List.copyOf(accounts);
	}
	
	/**
	 * returns account
	 * @param accountId - the account to found 
	 */
	public Account getAccount(int accountId) {
		for (Account account : accounts) {
			if(account.getAccountNmbr() == accountId)
				return account;
		}
		return null;
	}
	
	/**
	 * adds a new account to the list accounts
	 * checks to see if account already exists
	 * @param newAccount - the account to be added
	 */
	public void addAccount(Account newAccount) {
		for (Account account : accounts) {
			if((account.getAccountNmbr() == newAccount.getAccountNmbr())) {
				return;
			}
		}
		accounts.add(newAccount);
	}
	
	/**
	 * removes the specified account
	 * @param removeAccount - the account to remove
	 */
	public void removeAccount(Account removeAccount) {
		Iterator<Account> itr = accounts.iterator();
		while(itr.hasNext()) {
			int nmbr = itr.next().getAccountNmbr();
			if(nmbr == removeAccount.getAccountNmbr()) {
				itr.remove();
			}
		}
	}
	
	public void removeAllAccounts() {
		accounts.clear();
	}

}
