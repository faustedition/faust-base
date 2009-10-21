/**
 * 
 */
package de.faustedition.web.genesis;

import java.io.Serializable;

public class ParalipomenonReference implements Serializable
{
	private String name;
	private String portfolio;
	private String manuscript;

	ParalipomenonReference(String name, String portfolio, String manuscript)
	{
		this.name = name;
		this.portfolio = portfolio;
		this.manuscript = manuscript;
	}

	public String getName()
	{
		return name;
	}

	public String getPortfolio()
	{
		return portfolio;
	}

	public String getManuscript()
	{
		return manuscript;
	}
}