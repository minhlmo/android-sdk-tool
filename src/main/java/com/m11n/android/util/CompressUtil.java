package com.m11n.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

public class CompressUtil
{
	public static String unzip(File archive, File outputDir)
	{
		boolean first = true;
		String root = null;
		
		try
		{
			ZipFile zipfile = new ZipFile(archive);
			
			for (Enumeration<? extends ZipEntry> e = zipfile.entries(); e.hasMoreElements();)
			{
				ZipEntry entry = e.nextElement();
				unzip(zipfile, entry, outputDir);
				
				if(first)
				{
					if(entry.isDirectory())
					{
						root = entry.getName();
					}
					
					first = false;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return root;
	}

	private static void unzip(ZipFile zipfile, ZipEntry entry, File outputDir)
	throws IOException
	{

		if (entry.isDirectory())
		{
			createDir(new File(outputDir, entry.getName()));
			return;
		}

		File outputFile = new File(outputDir, entry.getName());
		
		if (!outputFile.getParentFile().exists())
		{
			createDir(outputFile.getParentFile());
		}

		BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

		try
		{
			IOUtils.copy(inputStream, outputStream);
		}
		finally
		{
			IOUtils.closeQuietly(outputStream);
			IOUtils.closeQuietly(inputStream);
		}
	}

	private static void createDir(File dir)
	{
		dir.mkdirs();
	}
}