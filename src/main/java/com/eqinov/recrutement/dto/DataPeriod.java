package com.eqinov.recrutement.dto;
/**
 * @author Elhadji BADIANE
 */
public interface DataPeriod {
	
	public String getPeriod();
	
	public double getMean();
	
	public default int getMonth() {
		String period = this.getPeriod();
		return Integer.parseInt(period.substring(4, 6));
	}
}
