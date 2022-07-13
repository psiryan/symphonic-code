/*
 * FFMpegToolBox.java
 *
 * Created on 4 septembre 2006, 19:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.naurd.media.jsymphonic.toolBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import org.naurd.media.jsymphonic.system.sony.nw.NWGeneric;
import org.naurd.media.jsymphonic.system.sony.nw.NWGenericListener;
import org.naurd.media.jsymphonic.title.Mp3;
import org.naurd.media.jsymphonic.title.Title;

/**
 *
 * @author pballeux
 */
public class FFMpegToolBox {
    private boolean ffmpegDetected = false;
    private String ffmpegPath = "";
    private java.io.InputStream processin = null;
    private java.io.OutputStream processout = null;
    private java.io.InputStream filein = null;
    private java.io.OutputStream fileout = null;
    private boolean stopMe = false;
    private boolean convertEnded = false;
    private NWGeneric nwGeneric;
    private static String devicePath;

        
    //Other
    private static Logger logger = Logger.getLogger("org.naurd.media.jsymphonic.toolBox.FFMpegToolBox");
    
    public enum FileFormat {
        MP3,
        WMA,
        OGG,
        MPC,
        WAV
    }
    public enum FileBitrate{
        RATE64,
        RATE128,
        RATE192,
        RATE256,
        RATE320
    }
    public void stopMe(){
        stopMe=true;
    }
    
    /** Creates a new instance of FFMpegToolBox */
    public FFMpegToolBox() {
        try{
            java.lang.Process p = Runtime.getRuntime().exec("ffmpeg");
            ffmpegDetected = true;
        } catch(Exception e ){
            // ffmpeg has not been found from the environment variable, let's try on the local folders
            String ffmpegExePath = devicePath + "\\ffmpeg.exe";
            File ffmpegExe = new File(ffmpegExePath);

            if(!ffmpegExe.exists()) {
                // ffmpeg has not been found in the base folder of the device, let's try in a JSymphonic folder
                ffmpegExePath= devicePath + "\\JSymphonic\\ffmpeg.exe";
                ffmpegExe = new File(ffmpegExePath);
            }

            if(!ffmpegExe.exists()) {
                // ffmpeg has not been found in the base folder of the device, let's try in a ffmpeg-win32 folder
                ffmpegExePath= devicePath + "\\ffmpeg-win32\\ffmpeg.exe";
                ffmpegExe = new File(ffmpegExePath);
            }

            if(!ffmpegExe.exists()) {
                // ffmpeg has not been found in the base folder of the device, let's try in a JSymphonic/ffmpeg-win32 folder
                ffmpegExePath= devicePath + "\\JSymphonic\\ffmpeg-win32\\ffmpeg.exe";
                ffmpegExe = new File(ffmpegExePath);
            }

            /*if(!ffmpegExe.exists()) {
                // ffmpeg has not been found in the device, let's try to read the configuration file
                ffmpegExePath = "test";//TODO = JSymphonic.settings.getValue("FfmpegPath", "invalid path");
                ffmpegExe = new File(ffmpegExePath);
            }*/

            if(ffmpegExe.exists()) {
                // FFMPEG has been found, save the path
                ffmpegPath = ffmpegExePath;
                ffmpegDetected = true;
            }
            else {
                // ffmpeg has not been found
                logger.warning("FFMPEG not detected...");
            }
        }
    }
    
    /**
     * Convert a title to MP3 using ffmpeg. This function copies the tag from the source to the destination (the tag will exist in the Title object, not in the MP3 file !!). The source should be compatible with FFMPEG
     * 
     * @param source
     * @param destination
     * @param bitrate
     * @param nwGeneric instance of NWGeneric needed to inform the GUI of the progress of the encodage
     * @return the instance of Mp3 refering to the transcoded title if succeeded, null otherwise.
     */
    public Mp3 convertToMp3(Title source, Mp3 destination,int bitrate, NWGeneric nwGeneric) {
        this.nwGeneric = nwGeneric;
        
        // Use "convertToMp3" method to convert the file
        try {
            convertToMp3(source.getSourceFile(), destination.getSourceFile(), bitrate);
        } catch (IOException ex) {
            return null;
            //Logger.getLogger(FFMpegToolBox.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Re-instance the mp3 object to read the information about the file (bitrate,...)
        destination = new Mp3(destination.getSourceFile());
        
        // Write the tag in the destination object
        destination.setTag(source.getTag());
        
        return destination;
    }
    
    /**
     * Convert a file to MP3 using ffmpeg. This function only handle files, to the resulting file has no ID3Tag. The source should be compatible with FFMPEG.
     * 
     * @param source the original file to be transcoded.
     * @param destination the MP3 file to be created.
     * @param bitrate the bitrate to use to create the MP3 file.
     * @return the return value of ffmpeg.
     * @throws java.io.IOException
     */
    public int convertToMp3(File source, File destination,int bitrate) throws java.io.IOException{
        int retValue = 0; // value to to returned

        String format = "mp3"; // format to encode is always MP3 (for video, this should specify the container (avi, ogm,...) )
        String codec = "libmp3lame"; // codec to encode is always MP3
        String frequency = "44100"; // frequency to encode is always 44100Hz
        
        String exec = ""; // string used to set the command line to call ffmpeg
        
        if(!ffmpegDetected){
            // If ffmpeg has not been detected, method can stop now
            logger.warning("FFMPEG not detected...");
            return -1;
        }
        
        //// Define the command line to call ffmpeg:
        // -i -: input is the standard input
        // -ac 2: number of channel is 2
        // -ar f: frequency is f
        // -ab b: bitrate is b
        // -f f: format is f
        // - : output is the standard output
        // Note that input and output are taken as standard input and output to to feed by oursefves, so that you know how the encoding is going
        if(ffmpegPath.length() > 0) {
            // A path is needed to invoke the command:
            exec = "\"" + ffmpegPath + "\"";
        }
        else{
            // Just putting the name is enough
            exec = "ffmpeg";
        }

//        exec += "  -i - -y -ac 2 -ar " + frequency + " -ab " + bitrate + "k -f " + format + " -acodec "+ codec + " -"; // the "-acodec" is not mandatory since we are precising the output format. Moreover, the version of FFMPEG in Ubuntu 8.10 repository uses "libmp3lame" to identify the MP3 audio codec whereas compiled version given in the sourceforge Symphonic download page uses "mp3"
        exec += "  -i - -y -ac 2 -ar " + frequency + " -ab " + bitrate + "k -f " + format + " -";

        logger.info("ffmpeg path:" + ffmpegPath);
        logger.info("ffmpeg is call with command:" + exec);

        // Create a process with the call to ffmpeg
        java.lang.Process p = Runtime.getRuntime().exec(exec);
        
        // Get standard input, output and error
        processin = p.getInputStream();
        processout = p.getOutputStream();
        java.io.InputStream processerror = p.getErrorStream();
        
        // Get stream from the input and output files
        filein = new FileInputStream(source);
        fileout = new FileOutputStream(destination);
               
        // Compute the total size of the file
        final long totalSize = source.length();
        
        // Create a new thread, this thread will feed in ffmpeg with the source file to encode
        Thread read = new Thread(){
            @Override
            public void run(){
                int countin = 0; // number of bytes read from source in a loop-iteration
                long totalin = 0; // number of bytes read from source from the begining
                long currentin  = 0; // current number of bytes read from source in a loop-iteration (to compute the speed)
                float speed = 0; // the speed of the encodage
                long computeTime = 0; // time when the encodage start
                byte[] bsin = new byte[4096]; // buffer for the source
 
                try{
                    // In a loop, we read data from the source and feed the ffmpeg process
                    while (countin!=-1 && !stopMe){ // While input file has bytes
                        countin = filein.read(bsin); // Read a amount of bytes
                        currentin+=countin; // Count the amount of bytes read since the last speed computation

                        if (System.currentTimeMillis() - computeTime>1000){ // If one second passed
                            speed = currentin / ((System.currentTimeMillis() - computeTime)/1000f) / 1024f; // Compute the speed in ko/s
                            currentin = 0; // Initialize the number of bytes read for the next speed computation
                            computeTime = System.currentTimeMillis(); // Initialize the time for next speed computation
                            logger.fine("ffmpeg is running, speed:" + speed);
                        }
                        
                        if (countin > 0){ // If bytes have been read
                            totalin += countin; // Count the number of bytes read since the beginning 
                            if (totalSize!=0){ // Check that the read size is not null
                                nwGeneric.sendFileProgressChanged(NWGenericListener.ENCODING,(totalin*100)/totalSize, speed); // Inform GUI
                            }
                            else {
                                nwGeneric.sendFileProgressChanged(NWGenericListener.ENCODING,0, speed); // Inform GUI
                            }
                            
                            processout.write(bsin, 0, countin); // write in the ffmpeg thread
                        }
                    }

                
                } catch(Exception e){
                    e.printStackTrace();
                }
                try{
                    // Once finished, close the stream
                    filein.close();
                    processout.close();
                } 
                catch(Exception e){}
            }
        };
        
        // Start the read thread
        read.start();
        
        //Small pause to give time to the reader to start...
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
        // Create a new thread, this thread will get the output of ffmpeg to fill in the output file
        Thread write = new Thread(){
            @Override
            public void run(){
                int countout = 0; // number of bytes read from the standard output in a loop-iteration
                long totalout = 0; // number of bytes read from the standard output from the begining
                byte[] bsout = new byte[4096]; // buffer for the destination
                
                try{
                    // In a loop, we read data from the tandard output and fill in the output file
                    while (countout != -1){
                        countout = processin.read(bsout); // read the standard output
                        
                        if (countout>0){
                            totalout+=countout; // save the number of bytes written
                            fileout.write(bsout,0,countout); // write in the destination
                        }
                    }
                    
                    // Once finished, close the files
                    fileout.close();
                    processin.close();
                    
                    // Encoded is finished, put convertEnded to true, to warn main thread
                    convertEnded=true;
                } 
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        
        // Start the second thread
        write.start();
        
        
        byte[] bserr = null; // buffer to read the standard error
        // While the encodage is running, watch out the standard error, in case of trouble
        while(!convertEnded){
            try{
                bserr = new byte[processerror.available()]; // check if something is available
                
                // If something as happen, read it, print it
                if (bserr.length>0){
                    processerror.read(bserr); // read standard error
                    String error = new String(bserr); // convert to a string
                    
                    // If the output doesn't contain the sequence "size=" nor "Input #0" (which are a fine info sent by ffmpeg), we consider that it is an error and print it
                    if(!error.contains("size=") && !error.contains("Input #0")){
                        logger.severe(error);
                        retValue = -1; // change the value to to returned since an error certainly occured
                    }
                }
                
                Thread.sleep(100); // wait a while before checking again
            } 
            catch(Exception e){}
        }
        
        // Re put convertEnded for next encodage
        convertEnded=false;
        
        // Clear references to the streams
        filein=null;
        fileout=null;
        
        // Wait for the end of the ffmpeg process
        try{
            p.waitFor();
        } 
        catch(Exception e){}
        
        // Get the value returned by the ffmpeg process
        //retValue = p.exitValue();
        
        // Clear references to the process streams
        processin=null;
        processout=null;
        p=null;
        
        /*
        //// Now, as ffmpeg don't write tag info, let's do that:
        // Instance a new Tag to have information (title, artist,...)
        Tag sourceTitleTag = new Tag(Title.getTitleFromFile(source));
        
        // Instance the destination as an MP3 object
        Title destinationTitle = new Mp3(destination);
        
        // Add the tag to the destination
        destinationTitle.writeTag(sourceTitleTag);
           */     
        // Return the value returned by the ffmpeg process
        return retValue;
    }
    
    
    public static boolean isFFMpegPresent() {
        /*
        try{
            java.lang.Process p = Runtime.getRuntime().exec("ffmpeg");
            return true;
        } catch(Exception e ){
            return false;
        }
         */
        FFMpegToolBox ffmpeg = new FFMpegToolBox();
        return ffmpeg.isFFMpegDetected();
        
    }
    
    private boolean isFFMpegDetected() {
        return ffmpegDetected;
    }

    public static void setDevicePath(String devicePath) {
        FFMpegToolBox.devicePath=devicePath;
    }

    public static String getDevicePath() {
        return devicePath;
    }
}
