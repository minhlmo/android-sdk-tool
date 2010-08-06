package com.m11n.android.model;

import java.util.ArrayList;
import java.util.List;

public class Item
{
    protected static final String NS = "sdk:";    
    public static final String VERSION = NS + "version";    
    public static final String VENDOR = NS + "vendor"; 
    public static final String NAME = NS + "name"; 
    public static final String API_LEVEL = NS + "api-level"; 
    public static final String CODENAME = NS + "codename"; 
    public static final String REVISION = NS + "revision"; 
    public static final String MIN_TOOLS_REV = NS + "min-tools-rev"; 
    public static final String DESCRIPTION = NS + "description"; 
    public static final String DESC_URL = NS + "desc-url"; 
    public static final String ARCHIVES = NS + "archives";
    public static final String LIBS = NS + "libs";
    public static final String LICENSE = NS + "license";
    public static final String OBSOLETE = NS + "obsolete";

	private String name;
	private String vendor;
	private String apiLevel;
	private String revision;
	private String minimumToolsRevision;
	private String codename;
	private String version;
	private String description;
	private String descriptionUrl;
	private License license;
	private Boolean obsolete = false;
	private List<Archive> archives = new ArrayList<Archive>();
	private List<Lib> libs = new ArrayList<Lib>();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getVendor()
	{
		return vendor;
	}

	public void setVendor(String vendor)
	{
		this.vendor = vendor;
	}

	public String getApiLevel()
	{
		return apiLevel;
	}

	public void setApiLevel(String apiLevel)
	{
		this.apiLevel = apiLevel;
	}

	public String getRevision()
	{
		return revision;
	}

	public void setRevision(String revision)
	{
		this.revision = revision;
	}

	public String getMinimumToolsRevision()
	{
		return minimumToolsRevision;
	}

	public void setMinimumToolsRevision(String minimumToolsRevision)
	{
		this.minimumToolsRevision = minimumToolsRevision;
	}

	public String getCodename()
	{
		return codename;
	}

	public void setCodename(String codename)
	{
		this.codename = codename;
	}

	public String getVersion()
    {
    	return version;
    }

	public void setVersion(String version)
    {
    	this.version = version;
    }

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDescriptionUrl()
	{
		return descriptionUrl;
	}

	public void setDescriptionUrl(String descriptionUrl)
	{
		this.descriptionUrl = descriptionUrl;
	}

	public License getLicense()
	{
		return license;
	}

	public void setLicense(License license)
	{
		this.license = license;
	}

	public Boolean getObsolete()
	{
		return obsolete;
	}

	public void setObsolete(Boolean obsolete)
	{
		this.obsolete = obsolete;
	}

	public List<Archive> getArchives()
	{
		return archives;
	}

	public void setArchives(List<Archive> archives)
	{
		this.archives = archives;
	}

	public List<Lib> getLibs()
	{
		return libs;
	}

	public void setLibs(List<Lib> libs)
	{
		this.libs = libs;
	}
}
