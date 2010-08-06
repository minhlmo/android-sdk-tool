package com.m11n.android.model;

public class License
{
	public static final String ELEMENT_NAME = "sdk:license";

	private String text;

	public String getText()
    {
    	return text;
    }

	public void setText(String text)
    {
    	this.text = text;
    }
}
