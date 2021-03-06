-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
JSymphonic version 0.3.0a2
Release date:    03.10.2009
README date:     03.10.2009
Website:         http://symphonic.sourceforge.net



-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
Contents
-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
 1.0)    What's New!
   1.1)    version 0.3.0 alpha 2
   1.2)    version 0.3.0 alpha 1
   1.3)    version 0.2.x
 2.0)    Warning!
 3.0)    System Requirements
   3.1)    Java version 1.5 or higher
   3.2)    Files
 4.0)    Using JSymphonic
   4.1)    Full Usage and Help
   4.2)    First Run
   4.3)    Transferring your files to your MP3 player
   4.4)    About the transcode feature
   4.5)    About the third generation
 5.0)    Troubleshooting
 6.0)    Licence Information
 7.0)    Symphonic Team Members



-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
1.0)  What's New!
-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
1.1)  Version 0.3.0 alpha 2

1.1.1)  New Features:
 - support of APE (Monkey's Audio)
 - support of AAC (M4A, MP4)
 - OGG, FLAC, APE and WMA tag reading ability
 - Improve error reporting when a transfer fails
 - Java 1.5 compatibility (for MacOS users)
 - Add a "Stop transfer" button
 - Improve the automatic configuration (at first start-up)
 - Ignore tag ability
 - Add a progress bar when scheduling track importation
 - Add chinese language
 - New icons

1.1.2)  Bug fixes
 - Transfers no longer need FFMPEG to start
 - "unknow" and "no name" bugs
 - Incorrect values in the progress bar while transfering
 - Delete title
 - Error while building database for an empty player
 - Unlimited track deletion


1.2)  Version 0.3.0 alpha 1

1.2.1)  New Features:
 - New interface
 - Transcode feature (Please note that the transcode feature needs FFMPEG to
   work, read section 4.4) About the transcode feature" for more info)
 - Profile support
 - Logging to a file
 - Database handling improvements
 - Transfer speed improvement for third generation

1.2.2)  Bug fixes
 - "[", "]", "{" and "}" characters are now supported in local music (files
   and/or paths)
 - exporting of files with "/" in the tag
 - many others


1.3)  Version 0.2.x

1.3.1)  New Features
 - support for OMA files (limited)
 - MacOS compatibility (java 1.5 compatibility)
 - multi-lingual capacity (english and french available)
 - debug mode is available with "-debug" argument
 - support for old generation (NW-HD5n, NW-Exxx, read section "4.5) About the
   third generation" for more 



-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
2.0)  Warning!
-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
This is an alpha version of JSymphonic. It has been released only for testing
purposes, and it is a work in progress. You are using this software at your own
risk, and the Symphonic Team is not responsible for any damage that may or may
not come from the use of Symphonic.



-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
3.0)  System Requirements
-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
3.1)  Java  (http://www.java.com)

JSymphonic has been developed using Java so that this software could be run on
a variety of Operating Systems, such as Windows, MAC OS, and Linux. JSymphonic
will run best with the latest version (1.5+) of the Java Virtual Machine, found
at "http://www.java.com".


3.1.1)  For MacOS users

If you have a Mac with a 64 bits Intel processor and MacOS 10 Leopard, Java 1.6
is installed to your computer, but it is hidden. Use the following command to
link java 1.6 to your program folder:

$ sudo ln -s /System/Library/Frameworks/JavaVM.framework/Versions/1.6/Commands/java /usr/bin/java6 

Then any Java application can be launched with Java 1.6 using the command:

$ java6 -jar /PathOfTheApplication/NameOfTheApplication.jar 


3.2)  Files

You have downloaded a zip file containing the program : "JSymphonic_0.X.jar"
and this READ ME.

The file "JSymphonic_0.X.jar" can be placed anywhere (it is meant to be placed
within the player). If the program doesn't find the player, you will have to
set the correct path in the properties window.

A file named "JSymphonic.xml" will be automaticly created in the same folder as
the program to save your preferences.

To work, a folder named "OMGAUDIO" or "omgaudio" must exist at the root of the
player.



-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
4.0)  Using JSymphonic
-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
4.1) Full Usage and Help
You can find more information on our website http://symphonic.sourceforge.net


4.2) First Run

You will need to set the MP3 player's path when you run JSymphonic for the first
time if the auto-detection fails. You can do this by clicking on the
"JSymphonic" menu and choosing "Properties". Then click on the first tab
"Device and Path profiles". Click the "Edit" button and set each path
individually. "Device Path" is where you can find the MP3 Player (this is 
usually a drive letter for Windows users). You can set the base path for you 
music files under "Local Path", and "Export Path" is where you would like your 
music files to end up after you export them from your MP3 player. "Temporary 
Folder" is for those files that need to be transcoded before sending to the 
device. It is very important to choose the proper option from "Device 
Generation" as well.


4.3) Transferring Files to your MP3 Player

On the left hand side of the screen you will find a tree view of the files and 
folders found on your computer in the folder you set under "Local Path" in the 
properties window (see section 4.2) choose among these files and click the "+" 
button on the toolbar to import to your MP3 player. Once you have scheduled all
the changes you want to make, apply them with the "Apply" button.

To export files from your MP3 player to your computer, select the files in the 
right hand side and click the "Export" button. After applying your changes, the
files will be available in your "export" folder (see section 4.2).


4.4) About the transcode feature

It is now possible to add OGG, FLAC, WMA, MPC, APE,... files into your player. 
JSymphonic is going to use FFMPEG to transcode these files to MP3 before sending
them to your player. This means that JSymphonic needs to know where to find
FFMPEG.

 4.4.1) If You Are Running Linux And Want To Use The Transcode Feature

You need to have installed FFMPEG with MP3 support. You may compile FFMPEG from
the sources (google to find the sources) but your distribution may offer you a
package.

For Ubuntu users (<= 8.04), Hardy release and earlier can have a package for
FFMPEG in the Medibuntu repository.

For Ubuntu users (>= 8.10), Intrepid release offers a package of FFMPEG but it 
is compiled WITHOUT MP3 support. You have to install this version first and, to 
enable MP3 support, you need to enable Multiverse repositories and install the 
following packages:
libavcodec-unstripped-51
libavdevice-unstripped-52
libavformat-unstripped-52
libavutil-unstripped-49
libpostproc-unstripped-51
libswscale-unstripped-0

4.4.2) If You Are Running Windows And Want To Use The Transcode Feature

You may need to download FFMPEG here:
Mirror 1:
http://surfnet.dl.sourceforge.net/sourceforge/symphonic/ffmpeg-win32.zip
Or mirror 2:
http://internap.dl.sourceforge.net/sourceforge/symphonic/ffmpeg-win32.zip

Download a zip file named "ffmpeg-win32.zip". This file needs to be unzipped
and its content placed in the same folder has the "JSymphonic_0.X.jar" or in a 
subfolder named "ffmpeg-win32" or "JSymphonic" in the folder containing the 
"JSymphonic_0.X.jar".

4.4.3) If You are running MacOS X And Want To Use The Transcode Feature

You will need to install "ffmpegX".  There should be little to no trouble
finding the package.


4.5) About the third generation

The third generation groups "protected players" which encrypt OMA and MP3 files.
Protected players are: NW-HD5, NW-E10x/E2xx/E3xx/E4xx/E5xx.

JSymphonic can handle these players if the file "DvID.dat" is present in the 
player. This file contains the key used to encrypt the files. This file is 
generated when one uses Sony's MP3 File Manager.  File Manager creates a folder
"MP3FM" at the root of the player and creates the "DvID.dat" file into this 
folder. 

If this link to MP3 File Manager doesn't work please use your favourite search
engine to find it.

http://www.sonydigital-link.com/DNA/downloads/downloads.asp?r=www.google.fr&c=WM&sc=NWF&t=lp&f=lp_mp3&l=en

It has been most people's experience that you will need to use Windows XP or 
earlier to install MP3FM on your player/computer.  This may be annoying to some,
however since JSymphonic only needs this one file, and all the files are
installed directly on your player anyway, you only need to borrow someone's
Windows machine to download and install this.

The file "DvID.dat" can be called "DvID.dat" or "DvID.DAT" (JSymphonic is case 
sensitive so be careful). It can be placed into :
- MP3FM folder
- the root of the player
- OMGAUDIO folder



-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
5.0)  Troubleshooting
-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

You may find that JSymphonic is freezing with nothing displayed on the left side
(where "Local path" and "Export path" contents should be displayed). In fact, 
JSymphonic is building a tree view of your "local" and "export" folder, and if 
they are large, this may take a while. Just be patient.

If you want to report a bug, please use our SourceForge forum:
http://sourceforge.net/forum/forum.php?forum_id=747001
It is advised to provided a log of the execution when reporting a bug.

You can find information about JSymphonic at its website:
http://symphonic.sourceforge.net



-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
6.0)  Licence Information
-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
This program is free software. You can redistribute it and/or modify it under 
the terms of the GNU General Public License  - version 2 or later, as published 
by the Free Software Foundation: http://www.gnu.org/licenses/gpl.html.

This program is distributed in the hope that it will be useful, but WITHOUT ANY 
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
PARTICULAR PURPOSE. See the GNU General Public License for more details.



-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
7.0)  Symphonic Team Members
-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
If you would like more information on Symphonic, or would like to join our team 
please contact us at http://symphonic.sourceforge.net

7.1)  JSymphonic
Naurd (founder)
Nicolas_cardoso
Danizmax

7.2)  Symphonic plugin, openSonyDb
Garthps
Mephx

7.3)  Translators
Henning (German)
Lenborje (Swedish)
Mayday85 (Russian)
Mephx (Portuguese)
Nicolas_cardoso (French)
Danizmax (Slovenian)
Orhan and Haiiit53 (Turkish)
Herchu (Spanish)
Hpeat (Czech and Slovak)

7.4)  Documentation, website and logo
Metricjester
Ulluss
Pahra


Thanks to the testers of the pre-alpha version (Pipo, Simon, Herchu)
