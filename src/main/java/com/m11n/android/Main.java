package com.m11n.android;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.m11n.android.model.AddOn;
import com.m11n.android.model.Doc;
import com.m11n.android.model.Extra;
import com.m11n.android.model.Platform;
import com.m11n.android.model.Repository;
import com.m11n.android.model.Sample;
import com.m11n.android.model.Tool;

public class Main
{
	private static final Logger logger = LogManager.getLogger(Main.class);
	
	public static void main(String[] args)
	throws Exception
	{
		Options options = createOptions();
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		
		try
        {
			cmd = parser.parse( options, args); 
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e);
        	
			usage(options);
			
			return;
        }
        
		check(options, cmd);
		
		String rootDir = cmd.getOptionValue("home", "/tmp/androidparent");
		String revision = cmd.getOptionValue("rev", "06");
		String os = cmd.getOptionValue("os", AndroidSdkTool.getDefaultOperatingSystem());
		String architecture = cmd.getOptionValue("arch", null);
		boolean overwrite = cmd.hasOption("rw");
		boolean sdk = Boolean.valueOf(cmd.getOptionValue("sdk", "true")).booleanValue();
		boolean verbose = cmd.hasOption("verbose");
		
		// NOTE: only 86 supported
		if(architecture==null && ("linux".equals(os) || "mac".equals(os)))
		{
			architecture = "86"; 
		}
		
		// TODO: check for valid parameters

		AndroidSdkTool tool = new AndroidSdkTool(revision, os, architecture, rootDir, overwrite, verbose);
		
		if(sdk)
		{
			// step 1: download the SDK
			String sdkFile = tool.downloadSdk();
			
			// step 2: install SDK
			tool.install(sdkFile, tool.getInstallDir(), true);
		}

		// step 3: refresh and parse repository
		Repository repository = tool.downloadRepository();
		
		if(verbose)
		{
			logger.info("\n\n" + repository.getLicense().getText());
			logger.info("\n\n");
		}
		
		// platform
		for(Platform platform : repository.getPlatforms())
		{
			String file = tool.downloadItem(platform, os);
			
			tool.install(file, tool.getInstallDir(platform), true);
		}
		
		// add-on
		for(AddOn addOn : repository.getAddOns())
		{
			String file = tool.downloadItem(addOn, os);

			tool.install(file, tool.getInstallDir(addOn), true);
			tool.writeSourceProperties(addOn);
		}
		
		// extra
		for(Extra extra : repository.getExtras())
		{
			String file = tool.downloadItem(extra, os);

			if(file!=null)
			{
				tool.install(file, tool.getInstallDir(extra), false);
			}
		}
		
		// sample
		for(Sample sample : repository.getSamples())
		{
			String file = tool.downloadItem(sample, os);

			tool.install(file, tool.getInstallDir(sample), true);
		}
		
		// doc
		for(Doc doc : repository.getDocs())
		{
			String file = tool.downloadItem(doc, os);

			tool.install(file, tool.getInstallDir(doc), true);
		}
		
		// tool
		for(Tool t : repository.getTools())
		{
			String file = tool.downloadItem(t, os);

			tool.install(file, tool.getInstallDir(t), true);
		}
	}
	
	private static Options createOptions()
	{
		Options options = new Options();
		
		options.addOption("home", true, "Android SDK parent (required)");
		options.addOption("rev", true, "SDK Revision (optional; default: 06)");
		options.addOption("os", true, "Operating system (optional; default: operating system of this machine)");
		options.addOption("arch", true, "Architecture (optional; default: null; for mac and linux will be set automatically to '86')");
		options.addOption("sdk", false, "Download SDK (optional; default: true)");
		options.addOption("rw", false, "Overwrite downloaded files/re-download (optional; default: false)");
		options.addOption("verbose", false, "Additional information, more feedback (optional; default: false)");
		options.addOption("help", false, "Help");
		
		return options;
	}
	
	private static void check(Options options, CommandLine cmd)
	{
		boolean success = true;
		
        if(options==null || cmd==null)
        {
			usage(options);

			return;
        }

        if(cmd.hasOption("rev") || cmd.hasOption("arch"))
		{
			success = cmd.hasOption("rev") && cmd.hasOption("arch");
		}

		if(!cmd.hasOption("home"))
		{
			success = false;
		}
		
		if(cmd.hasOption("help"))
		{
			success = false;
		}

		if(!success)
		{
			usage(options);
			
			System.exit(0);
		}
	}
	
	private static void usage(Options options)
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "android-sdk-tool", options );
	}
}
