/**
 * @author Mikael Gustavsen Saksi, mikgus3
 */
package mikgus3;

import java.io.Serializable;
import java.math.BigDecimal;

public class SavingsAccount extends Account implements Serializable{
	
	private boolean freeWithDraw;
	private BigDecimal withDrawInterest;
	
	public SavingsAccount() {
		super(new BigDecimal(1.2),"Sparkonto");
		this.freeWithDraw = true;
		this.withDrawInterest = new BigDecimal(2);
	}
	
	public BigDecimal getWithDrawInterest() {
		return withDrawInterest;
	}
	
	/**
	 * Withdraw money from account
	 * @param withDrawal - The amount to withdraw from account 
	 */
	@Override
	public boolean withDraw(BigDecimal withDrawAmmount) {
		if(getBalance().compareTo(withDrawAmmount) < 0)
			return false;
		
		//if no free withdraws, multiply with withdrawinterest
		if(!freeWithDraw) {
			withDrawAmmount = calculateWithDrawInterest(withDrawAmmount);
			BigDecimal newBalance = getBalance().subtract(withDrawAmmount);
			if(newBalance.compareTo(BigDecimal.ZERO) >= 0) {
				setBalance(newBalance);
				addTransaction(withDrawAmmount.negate(), getBalance());
				return true;
			}
			return false;
		}
		//set freewithdraw to false after first withdraw
		BigDecimal newBalance = getBalance().subtract(withDrawAmmount);
		setBalance(newBalance);
		addTransaction(withDrawAmmount.negate(), getBalance());
		freeWithDraw = false;
		return true;
	}

	
	 //Calculates interestRate at time of closure
	@Override
	public BigDecimal calculateInterestRate() {
		return getBalance().multiply(getInterestRate()).divide(new BigDecimal(100));
	}
	
	//calculate withDrawAmmount when no more free withDraws
	private BigDecimal calculateWithDrawInterest(BigDecimal withDrawAmmount) {
		BigDecimal ammountWithDrawInterest = withDrawAmmount.add(withDrawAmmount.multiply(getWithDrawInterest().divide(new BigDecimal(100))));
		return ammountWithDrawInterest;
	}
}
