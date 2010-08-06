package com.m11n.android.model;

import java.util.LinkedList;
import java.util.List;

public class Repository
{
	private License license;
	private List<Platform> platforms = new LinkedList<Platform>();
	private List<AddOn> addOns = new LinkedList<AddOn>();
	private List<Extra> extras = new LinkedList<Extra>();
	private List<Sample> samples = new LinkedList<Sample>();
	private List<Doc> docs = new LinkedList<Doc>();
	private List<Tool> tools = new LinkedList<Tool>();

	public License getLicense()
	{
		return license;
	}

	public void setLicense(License license)
	{
		this.license = license;
	}

	public List<Platform> getPlatforms()
	{
		return platforms;
	}

	public void setPlatforms(List<Platform> platforms)
	{
		this.platforms = platforms;
	}

	public List<AddOn> getAddOns()
	{
		return addOns;
	}

	public void setAddOns(List<AddOn> addOns)
	{
		this.addOns = addOns;
	}

	public List<Extra> getExtras()
	{
		return extras;
	}

	public void setExtras(List<Extra> extras)
	{
		this.extras = extras;
	}

	public List<Sample> getSamples()
	{
		return samples;
	}

	public void setSamples(List<Sample> samples)
	{
		this.samples = samples;
	}
	
	public List<Doc> getDocs()
    {
    	return docs;
    }

	public void setDocs(List<Doc> docs)
    {
    	this.docs = docs;
    }

	public List<Tool> getTools()
    {
    	return tools;
    }

	public void setTools(List<Tool> tools)
    {
    	this.tools = tools;
    }

	public void addItem(Item item)
	{
		if(item instanceof AddOn)
		{
			addOns.add((AddOn)item);
		}
		else if(item instanceof Extra)
		{
			extras.add((Extra)item);
		}
		else if(item instanceof Platform)
		{
			platforms.add((Platform)item);
		}
		else if(item instanceof Sample)
		{
			samples.add((Sample)item);
		}
		else if(item instanceof Doc)
		{
			docs.add((Doc)item);
		}
		else if(item instanceof Tool)
		{
			tools.add((Tool)item);
		}
	}
}
