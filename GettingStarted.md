# Introduction #

The update tool of the Android SDK has dependencies on SWT and doesn't run (without tricks) in a headless environment. This basic tool provides an alternative to update the Android SDK in any environment (GUI or non-GUI). Only requirement is a properly installed Java JRE 1.6+. All necessary dependencies for the tool are packaged in the jar (no additional files necessary).


# Installation #

Download the file "android-sdk-tool-cli.jar" from the Downloads section and place it in any directory. The Android Base SDK must be installed before running the update tool (will be possible with this tool in one of the next releases).


# Usage #

| Option | Argument | Description |
|:-------|:---------|:------------|
| -home | /some/path | Android SDK parent (required) |
| -rev | 06 | SDK Revision (optional; default: 06) |
| -os | linux|windows|mac | Operating system (optional; default: operating system of this machine) |
| -arch | null|86 | Architecture (optional; default: null; for mac and linux will be set automatically to '86') |
| -rw |  | Overwrite downloaded files/re-download (optional; default: false) |
| -verbose |  | Additional information, more feedback (optional; default: false) |
| -agree |  | Agree with EULA (optional; default: ask for agreement) |
| -help |  | Help |

Example usage on the command line:

```
java -jar android-sdk-tool-cli-1.0.5.jar -home /opt
```


# Note #

At the moment only complete updates are possible (all platforms, add-ons, samples, extras, docs, tools). If packages were already downloaded before and the "-rw" flag is not set then existing packages will not be downloaded again.

**New**: a complete installation from scratch is now possible for all three operating systems.

More sophisticated workflows might be added later. Please create issues if you have any additional requirements or you find any bugs.