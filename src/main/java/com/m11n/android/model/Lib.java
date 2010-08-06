package com.m11n.android.model;

public class Lib
{
    private static final String NS = "sdk:";    

	public static final String ELEMENT_NAME = "lib";

    public static final String NAME = NS + "name";    
    public static final String DESCRIPTION = NS + "description";    

    private String name;
	private String description;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
