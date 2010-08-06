package com.m11n.android.model;

public class Archive
{
    private static final String NS = "sdk:";    

    public static final String ELEMENT_NAME = NS + "archive";

	public static final String OS = "os";
	public static final String ARCH = "arch";
	public static final String TYPE = "type";
	public static final String CHECKSUM = NS + "checksum";
	public static final String SIZE = NS + "size";
	public static final String URL = NS + "url";

	private String os;
	private String architecture;
	private Integer size;
	private String checksumType;
	private String checksum;
	private String url;

	public String getOs()
	{
		return os;
	}

	public void setOs(String os)
	{
		this.os = os;
	}

	public String getArchitecture()
	{
		return architecture;
	}

	public void setArchitecture(String architecture)
	{
		this.architecture = architecture;
	}

	public Integer getSize()
	{
		return size;
	}

	public void setSize(Integer size)
	{
		this.size = size;
	}

	public String getChecksumType()
	{
		return checksumType;
	}

	public void setChecksumType(String checksumType)
	{
		this.checksumType = checksumType;
	}

	public String getChecksum()
	{
		return checksum;
	}

	public void setChecksum(String checksum)
	{
		this.checksum = checksum;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}
}
