package com.m11n.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
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
				
				if(first && entry.isDirectory())
				{
					root = outputDir + File.separator + entry.getName();
					
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

	public static String untargz(File archive, File outputDir)
	{
		String s = archive.getAbsolutePath();
		String root = null;
		boolean first = true;
		
		while(s.contains("tar") || s.contains("gz") || s.contains("tgz"))
		{
			s = s.substring(0, s.lastIndexOf(".")); 
		}
		
		s = s + ".tar";
		
		try
		{
			GZIPInputStream input = new GZIPInputStream(new FileInputStream(archive));
			FileOutputStream fos = new FileOutputStream(new File(s));

			org.apache.commons.compress.utils.IOUtils.copy(input, fos);
			
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(fos);
			
			TarArchiveInputStream tis = new TarArchiveInputStream(new FileInputStream(s));

			for(TarArchiveEntry entry=tis.getNextTarEntry(); entry!=null;)
			{
				unpackEntries(tis, entry, outputDir);
				
				if(first && entry.isDirectory())
				{
					root = outputDir + File.separator + entry.getName();
				}

				entry = tis.getNextTarEntry();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return root;
	}
	
	private static void unpackEntries(TarArchiveInputStream tis, TarArchiveEntry entry, File outputDir)
	throws IOException
	{
		if (entry.isDirectory())
		{
			createDir(new File(outputDir, entry.getName()));
			File subDir = new File(outputDir, entry.getName());
			
			for(TarArchiveEntry e : entry.getDirectoryEntries())
			{
				unpackEntries(tis, e, subDir);
			}
			
			return;
		}

		File outputFile = new File(outputDir, entry.getName());
		
		if (!outputFile.getParentFile().exists())
		{
			createDir(outputFile.getParentFile());
		}
		
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

		try
		{
			byte[] content = new byte[(int)entry.getSize()];
			
			tis.read(content);
			
			if(content.length>0)
			{
				IOUtils.copy(new ByteArrayInputStream(content), outputStream);
				
				// checks for --x--x--x flags
				if((entry.getMode() & 0000111)>0)
				{
					outputFile.setExecutable(true);
				}
			}
		}
		finally
		{
			IOUtils.closeQuietly(outputStream);
		}
	}
	
	public static String ungzip(File archive, File outputDir)
	{
		String s = archive.getAbsolutePath();
		
		if(s.endsWith("gzip") || s.endsWith("gz"))
		{
			s = s.substring(0, s.lastIndexOf(".")); 
		}
		
		try
		{
			org.apache.commons.compress.utils.IOUtils.copy(new GzipCompressorInputStream(new FileInputStream(archive)), new FileOutputStream(new File(s)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return s;
	}

	private static void createDir(File dir)
	{
		dir.mkdirs();
	}
}