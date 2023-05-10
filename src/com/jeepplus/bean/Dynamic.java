/**
 * 
 */
package com.jeepplus.bean;

import onbon.bx06.area.DynamicBxArea;
import onbon.bx06.cmd.dyn.DynamicBxAreaRule;

/**
 * @author admin
 *
 */
public class Dynamic {

	private DynamicBxAreaRule dynamicBxAreaRule;

	private DynamicBxArea dynamicBxArea;

	/**
	 * @param dynamicBxAreaRule
	 * @param dynamicBxArea
	 */
	public Dynamic(DynamicBxAreaRule dynamicBxAreaRule, DynamicBxArea dynamicBxArea) {
		super();
		this.dynamicBxAreaRule = dynamicBxAreaRule;
		this.dynamicBxArea = dynamicBxArea;
	}

	/**
	 * @return the dynamicBxAreaRule
	 */
	public DynamicBxAreaRule getDynamicBxAreaRule() {
		return dynamicBxAreaRule;
	}

	/**
	 * @param dynamicBxAreaRule the dynamicBxAreaRule to set
	 */
	public void setDynamicBxAreaRule(DynamicBxAreaRule dynamicBxAreaRule) {
		this.dynamicBxAreaRule = dynamicBxAreaRule;
	}

	/**
	 * @return the dynamicBxArea
	 */
	public DynamicBxArea getDynamicBxArea() {
		return dynamicBxArea;
	}

	/**
	 * @param dynamicBxArea the dynamicBxArea to set
	 */
	public void setDynamicBxArea(DynamicBxArea dynamicBxArea) {
		this.dynamicBxArea = dynamicBxArea;
	}

}
