package com.m11n.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.m11n.android.model.AddOn;
import com.m11n.android.model.Archive;
import com.m11n.android.model.Doc;
import com.m11n.android.model.Extra;
import com.m11n.android.model.Item;
import com.m11n.android.model.Lib;
import com.m11n.android.model.License;
import com.m11n.android.model.Platform;
import com.m11n.android.model.Repository;
import com.m11n.android.model.Sample;
import com.m11n.android.model.Tool;
import com.m11n.android.util.CompressUtil;

/**
 * 
 * @see https://wiki.mozilla.org/User:Bear:AndroidNotes
 *
 */
public class AndroidSdkTool
{
	private static final Logger logger = LogManager.getLogger(AndroidSdkTool.class);
	
	private String rootDir;
	private String os;
	private String revision;
	private String architecture;
	private String repositoryUrl = "http://dl-ssl.google.com/android/repository/";
	private String sdkUrl = "http://dl.google.com/android/";
	private String downloadDir;
	private Boolean overwrite = true;
	private Boolean verbose = true;
	private DocumentBuilder builder;
	private Map<String, String> apiLevelToVersion = new HashMap<String, String>();

	public AndroidSdkTool(String revision, String os, String architecture, String rootDir, boolean overwrite, boolean verbose)
	throws Exception
	{
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		this.revision = revision;
		this.os = os;
		this.architecture = architecture;
		this.rootDir = rootDir;
		this.overwrite = overwrite;
		this.verbose = verbose;
		
		downloadDir = System.getProperty("java.io.tmpdir") + "/android.tmp/" + os + "/";

		File tmp = new File(downloadDir);
		
		if(!tmp.exists())
		{
			tmp.mkdirs();
		}
	}

	public Repository downloadRepository()
	{
		String file = "repository.xml";
		
		try
		{
        	download(repositoryUrl + file, downloadDir + file, overwrite);

        	if(verbose)
        	{
    			logger.info(repositoryUrl + file + " downloaded to " + downloadDir + file);
        	}

			return parse(new FileInputStream(new File(downloadDir + file)));
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public String downloadSdk()
	{
		String file = "android-sdk_r" + revision + "-" + os + (architecture==null? "" : "_" + architecture) + "." + ("linux".equals(os)? "tgz" : "zip");
		
		try
		{
			download(sdkUrl + file, downloadDir + file, overwrite);

        	if(verbose)
        	{
    			logger.info(sdkUrl + file + " downloaded to " + downloadDir + file);
        	}

			return downloadDir + file;
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	public boolean install(String fromFile, String toDir, boolean ignoreRoot)
	{
		try
        {
			File tmp = new File(downloadDir);
			
	        String root = null;
	        
	        if(verbose)
	        {
	        	logger.info("Installing " + fromFile + " to directory " + toDir);
	        }
	        
	        if(fromFile.endsWith("zip"))
	        {
		        root = CompressUtil.unzip(new File(fromFile), tmp);
	        }
	        else if(fromFile.endsWith("tgz") || fromFile.endsWith("tar.gz"))
	        {
		        root = CompressUtil.untargz(new File(fromFile), tmp);
	        }

        	File to = null;

        	if(ignoreRoot && root!=null)
	        {
	        	to = new File(toDir);
	        	to.mkdir();
	        }
	        else
	        {
	        	to = new File(toDir + "/" + root);
	        	to.mkdir();
	        }
        	
        	FileUtils.copyDirectory(new File(root), to);
	        
        	if(verbose)
        	{
    			logger.info(fromFile + " installed to " + to.getAbsolutePath());
        	}
			
	        return true;
        }
        catch (Exception e)
        {
			logger.error(e.getMessage(), e);
        }
        
        return false;
	}
	
	public void writeSourceProperties(AddOn addOn)
	{
		File file = new File(getInstallDir(addOn) + File.separator + "source.properties");
		
		try
        {
			String platformVersion = apiLevelToVersion.get(addOn.getApiLevel())==null? "" : apiLevelToVersion.get(addOn.getApiLevel());
			String pr = addOn.getRevision();
			
			if(pr.length()>1 && pr.startsWith("0"))
			{
				pr = pr.substring(1);
			}
			
			if(verbose)
			{
				logger.info("Writing source.properties: " + file.getAbsolutePath());
				logger.info("API Level " + addOn.getApiLevel() + " maps to " + platformVersion);
			}
			
			StringBuffer buf = new StringBuffer();
			
		    buf.append("Pkg.Desc=" + addOn.getDescription() + "\n");
		    buf.append("Pkg.UserSrc=false" + "\n"); // TODO: where does this come from?
		    buf.append("Platform.Version=" + platformVersion + "\n");
		    buf.append("Pkg.Revision=" + addOn.getRevision() + "\n");
		    buf.append("AndroidVersion.ApiLevel=" + addOn.getApiLevel() + "\n");

		    FileUtils.writeStringToFile(file, buf.toString());
        }
        catch (Exception e)
        {
	        logger.error(e.toString(), e);
        }
	}

	public String downloadItem(Item item, String os)
	{
		String file = null;
		
		for (Archive archive : item.getArchives())
		{
			if (os.equals(escapeOs(archive.getOs())))
			{
				file = archive.getUrl();
				break;
			}
		}
		
		if(file==null)
		{
			for (Archive archive : item.getArchives())
			{
				if ("any".equals(archive.getOs()))
				{
					file = archive.getUrl();
					break;
				}
			}
		}

		try
		{
			if (file != null)
			{
				download(repositoryUrl + file, downloadDir + file, overwrite);

	        	if(verbose)
	        	{
	    			logger.info(item.getDescription() + " (" + repositoryUrl + file + " downloaded to " + downloadDir + file + ")");
	        	}

	        	return downloadDir + file;
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}

		return null;
	}
	
	public String escapeOs(String os)
	{
		if(os.toLowerCase().contains("win"))
		{
			return "windows";
		}
		else if(os.toLowerCase().contains("mac"))
		{
			return "mac";
		}
		else if(os.toLowerCase().contains("linux"))
		{
			return "linux";
		}
		else
		{
			return os;
		}
	}
	
	public boolean isLinux()
	{
		return (os.toLowerCase().contains("linux"));
	}
	
	public boolean isWindows()
	{
		return (os.toLowerCase().contains("windows"));
	}
	
	public boolean isMac()
	{
		return (os.toLowerCase().contains("mac"));
	}

	private void download(String url, String toFile, boolean overwrite)
	throws Exception
	{
    	if(verbose)
    	{
			logger.info("Downloading: " + url + " ...");
    	}

		if(overwrite || !new File(toFile).exists())
		{
			Exception e = null;

			HttpEntity entity = null;
			
			try
			{
				HttpClient httpclient = new DefaultHttpClient();

				HttpGet httpget = new HttpGet(url);
				HttpResponse response = httpclient.execute(httpget);
				entity = response.getEntity();

				if (entity != null)
				{
					entity.writeTo(new FileOutputStream(new File(toFile)));
				}
			}
			catch (Exception ex)
			{
				e = ex;
			}
			finally
			{
				if (entity != null)
				{
					entity.consumeContent();
				}

				if (e != null)
				{
					throw e;
				}
			}
		}
		else if(verbose)
		{
			logger.warn("File exists: " + toFile);
		}

		if(verbose)
    	{
			logger.info("... done.");
    	}
	}

	private Repository parse(InputStream is)
	throws IOException, SAXException
	{
		Repository repository = new Repository();

		Document doc = builder.parse(is);
		
		Element root = doc.getDocumentElement();
		
		NodeList children = root.getChildNodes();
		
		for(int i=0; i<children.getLength(); i++)
		{
			Node node = children.item(i);
			
			if(License.ELEMENT_NAME.equals(node.getNodeName()))
			{
				License license = new License();
				
				license.setText(node.getTextContent());
				
				repository.setLicense(license);
			}
			else
			{
				Item item = parseItem(node);
				
				if(item!=null)
				{
					repository.addItem(item);
				}
			}
		}
		
		return repository;
	}
	
	private Item parseItem(Node parent)
	{
		Item item = null;
		
		if(Platform.ELEMENT_NAME.equals(parent.getNodeName()))
		{
			item = new Platform();
		}
		else if(AddOn.ELEMENT_NAME.equals(parent.getNodeName()))
		{
			item = new AddOn();
		}
		else if(Extra.ELEMENT_NAME.equals(parent.getNodeName()))
		{
			item = new Extra();
		}
		else if(Sample.ELEMENT_NAME.equals(parent.getNodeName()))
		{
			item = new Sample();
		}
		else if(Doc.ELEMENT_NAME.equals(parent.getNodeName()))
		{
			item = new Doc();
		}
		else if(Tool.ELEMENT_NAME.equals(parent.getNodeName()))
		{
			item = new Tool();
		}
		
		if(item!=null)
		{
			NodeList children = parent.getChildNodes();
			
			for(int i=0; i<children.getLength(); i++)
			{
				Node node = children.item(i);
				
				if(Item.NAME.equals(node.getNodeName()))
				{
					item.setName(node.getTextContent());
				}
				else if(Item.API_LEVEL.equals(node.getNodeName()))
				{
					item.setApiLevel(node.getTextContent());
				}
				else if(Item.VERSION.equals(node.getNodeName()))    
				{
					item.setVersion(node.getTextContent());
				}
				else if(Item.VENDOR.equals(node.getNodeName())) 
				{
					item.setVendor(node.getTextContent());
				}
				else if(Item.CODENAME.equals(node.getNodeName())) 
				{
					item.setCodename(node.getTextContent());
				}
				else if(Item.REVISION.equals(node.getNodeName())) 
				{
					item.setRevision(node.getTextContent());
				}
				else if(Item.MIN_TOOLS_REV.equals(node.getNodeName())) 
				{
					item.setMinimumToolsRevision(node.getTextContent());
				}
				else if(Item.DESCRIPTION.equals(node.getNodeName())) 
				{
					item.setDescription(node.getTextContent());
				}
				else if(Item.DESC_URL.equals(node.getNodeName())) 
				{
					item.setDescriptionUrl(node.getTextContent());
				}
				else if(Item.ARCHIVES.equals(node.getNodeName()))
				{
					item.setArchives(parseArchives(node));
				}
				else if(Item.LIBS.equals(node.getNodeName()))
				{
					item.setLibs(parseLibs(node));
				}
				else if(Item.LICENSE.equals(node.getNodeName()))
				{
					// TODO: implement this
					logger.debug("Passing license...");
				}
				else if(Item.OBSOLETE.equals(node.getNodeName()))
				{
					item.setObsolete(true);
				}
			}
		}
		
		return item;
	}

	private List<Archive> parseArchives(Node parent)
	{
		List<Archive> archives = new ArrayList<Archive>();
		
		NodeList children = parent.getChildNodes();
		
		for(int k=0; k<children.getLength(); k++)
		{
			Node archiveNode = children.item(k);
			NodeList archiveNodeChildren = archiveNode.getChildNodes();
			
			Archive archive = new Archive();
			boolean propertySet = false;
			
			NamedNodeMap attributes = archiveNode.getAttributes();
			
			if(attributes!=null)
			{
				for(int j=0; j<attributes.getLength(); j++)
				{
					Node attribute = attributes.item(j);
					
					if(Archive.OS.equals(attribute.getNodeName()))
					{
						archive.setOs(attribute.getTextContent());
						propertySet = true;
					}
					else if(Archive.ARCH.equals(attribute.getNodeName()))
					{
						archive.setArchitecture(attribute.getTextContent());
						propertySet = true;
					}
				}
			}
			
			for(int i=0; i<archiveNodeChildren.getLength(); i++)
			{
				Node node = archiveNodeChildren.item(i);
				
				if(Archive.CHECKSUM.equals(node.getNodeName()))
				{
					archive.setChecksum(node.getTextContent());
					
					archive.setChecksumType(node.getAttributes().getNamedItem(Archive.TYPE).getTextContent());
					propertySet = true;
				}
				else if(Archive.SIZE.equals(node.getNodeName()))
				{
					String value = node.getTextContent();
					
					if(value!=null)
					{
						archive.setSize(Integer.valueOf(value));
						propertySet = true;
					}
				}
				if(Archive.URL.equals(node.getNodeName()))
				{
					archive.setUrl(node.getTextContent());
					propertySet = true;
				}
			}

			if(propertySet)
			{
				archives.add(archive);
				propertySet = false;
			}
		}
		
		return archives;
	}

	private List<Lib> parseLibs(Node parent)
	{
		List<Lib> libs = new ArrayList<Lib>();
		
		// TODO: implement this
		
		return libs;
	}

	public String getRepositoryUrl()
    {
    	return repositoryUrl;
    }

	public void setRepositoryUrl(String repositoryUrl)
    {
    	this.repositoryUrl = repositoryUrl;
    }

	public String getSdkUrl()
    {
    	return sdkUrl;
    }

	public void setSdkUrl(String sdkUrl)
    {
    	this.sdkUrl = sdkUrl;
    }

	public String getDownloadDir()
    {
    	return downloadDir;
    }

	public void setDownloadDir(String downloadDir)
    {
    	this.downloadDir = downloadDir;
    }

	public Boolean getOverwrite()
    {
    	return overwrite;
    }

	public void setOverwrite(Boolean overwrite)
    {
    	this.overwrite = overwrite;
    }
	
	public Boolean getVerbose()
    {
    	return verbose;
    }

	public void setVerbose(Boolean verbose)
    {
    	this.verbose = verbose;
    }

	public String getRootDir()
    {
    	return rootDir;
    }

	public void setRootDir(String rootDir)
    {
    	this.rootDir = rootDir;
    }

	public String getOs()
    {
    	return os;
    }

	public void setOs(String os)
    {
    	this.os = os;
    }

	public String getRevision()
    {
    	return revision;
    }

	public void setRevision(String revision)
    {
    	this.revision = revision;
    }

	public String getArchitecture()
    {
    	return architecture;
    }

	public void setArchitecture(String architecture)
    {
    	this.architecture = architecture;
    }

	public String getInstallDir()
	{
		return rootDir + (rootDir.endsWith(File.separator)? "":File.separator) + "android-sdk-" + os + (architecture!=null? "_" + architecture : "");
	}
	
	public String getInstallDir(Platform platform)
	{
		if(verbose)
		{
			logger.info("Mapping API level " + platform.getApiLevel() + " to platform version " + platform.getVersion());
		}

		try
        {
			apiLevelToVersion.put(platform.getApiLevel(), platform.getVersion());
			
			return getInstallDir() + File.separator + "platforms" + File.separator + "android-" + platform.getApiLevel();
        }
        catch (Exception e)
        {
	        logger.error(e.toString(), e);
        }
        
        return null;
	}
	
	public String getInstallDir(AddOn addOn)
	{
		String name = "addon_" + addOn.getName() + "_" + addOn.getVendor() + "_" + addOn.getApiLevel();
		name = name.replaceAll("\\ ", "_");
		name = name.replaceAll("\\.", "");
		name = name.toLowerCase();
		
		return getInstallDir() + File.separator + "add-ons" + File.separator + name;
	}
	
	public String getInstallDir(Sample sample)
	{
		return getInstallDir() + File.separator + "samples" + File.separator + "android-" + sample.getApiLevel();
	}
	
	public String getInstallDir(Doc doc)
	{
		return getInstallDir() + File.separator + "docs" + File.separator;
	}
	
	public String getInstallDir(Tool tool)
	{
		return getInstallDir() + File.separator + "tools" + File.separator;
	}
	
	public String getInstallDir(Extra extra)
	{
		return getInstallDir() + File.separator;
	}

	public static String getDefaultOperatingSystem()
	{
		String os = System.getProperty("os.name").toLowerCase();

		if(os.indexOf( "win" ) >= 0)
		{
			return "windows";
		}
		else if(os.indexOf( "mac" ) >= 0)
		{
			return "mac";
		}
		else if(os.indexOf( "linux" ) >= 0)
		{
			return "linux";
		}
		else
		{
			return "unknown";
		}
	}
 
	public static String getDefaultArchitecture()
	{
		String arch = System.getProperty("os.arch").toLowerCase();

		// NOTE: only mac and linux SDKs have architecture params
		if(!"win".equals(getDefaultOperatingSystem()) && arch.indexOf( "x86" ) >= 0)
		{
			return "86";
		}
		else
		{
			return null;
		}
	}
 
}
