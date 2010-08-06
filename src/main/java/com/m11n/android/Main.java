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
		
		String rootDir = cmd.getOptionValue("home", "/tmp/androidhome");
		String revision = cmd.getOptionValue("rev", "06");
		String os = cmd.getOptionValue("os", "linux");
		String architecture = cmd.getOptionValue("arch", null);
		boolean overwrite = cmd.hasOption("rw");
		boolean verbose = cmd.hasOption("verbose");
		
		AndroidSdkTool tool = new AndroidSdkTool();
		
		tool.setOverwrite(overwrite);
		tool.setVerbose(verbose);

		if(cmd.hasOption("rev") && cmd.hasOption("os") && cmd.hasOption("arch"))
		{
			// step 1: download the SDK
			String sdkFile = tool.downloadSdk(revision, os, architecture);
			
			// step 2: install SDK
			//tool.install(sdkFile, rootDir);
			// TODO: implement tar gz...
		}

		// step 3: refresh and parse repository
		Repository repository = tool.downloadRepository();
		
		// platform
		for(Platform platform : repository.getPlatforms())
		{
			String file = tool.downloadItem(platform, os);

			tool.install(file, AndroidSdkTool.getInstallDir(rootDir, platform), true);
		}
		
		// add-on
		for(AddOn addOn : repository.getAddOns())
		{
			String file = tool.downloadItem(addOn, os);

			tool.install(file, AndroidSdkTool.getInstallDir(rootDir, addOn), true);
		}
		
		// extra
		for(Extra extra : repository.getExtras())
		{
			String file = tool.downloadItem(extra, os);

			if(file!=null)
			{
				tool.install(file, AndroidSdkTool.getInstallDir(rootDir, extra), false);
			}
		}
		
		// sample
		for(Sample sample : repository.getSamples())
		{
			String file = tool.downloadItem(sample, os);

			tool.install(file, AndroidSdkTool.getInstallDir(rootDir, sample), true);
		}
		
		// doc
		for(Doc doc : repository.getDocs())
		{
			String file = tool.downloadItem(doc, os);

			tool.install(file, AndroidSdkTool.getInstallDir(rootDir, doc), true);
		}
		
		// tool
		for(Tool t : repository.getTools())
		{
			String file = tool.downloadItem(t, os);

			tool.install(file, AndroidSdkTool.getInstallDir(rootDir, t), true);
		}
	}
	
	private static Options createOptions()
	{
		Options options = new Options();
		
		options.addOption("home", true, "Android home (required)");
		options.addOption("rev", true, "SDK Revision (optional; default: 06)");
		options.addOption("os", true, "Operating system (optional; default: linux)");
		options.addOption("arch", true, "Architecture (optional; default: null)");
		options.addOption("rw", false, "Overwrite downloaded files/re-download (optional)");
		options.addOption("verbose", false, "Additional information, more feedback (optional)");
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
