package com.crawljax.plugins.crawloverview;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.BrowserConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.Form;
import com.crawljax.core.configuration.InputSpecification;
import com.crawljax.plugins.crawloverview.CrawlOverview;
import com.crawljax.plugins.crawloverview.model.OutPutModel;
import com.crawljax.rules.TempDirInTargetFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by keheliya on 2014-03-07.
 */
public class StateExplorer {
    private static final Logger LOG = LoggerFactory
            .getLogger(StateExplorer.class);
    //private static final String URL = "http://css-tricks.com/examples/DynamicPage/#index.php";
	private static final String URL = "http://avantgarde-labs.de/";
  // private static final String URL = "http://demo.crawljax.com/";
	private static final long WAIT_TIME_AFTER_RELOAD = 200;
	private static final long WAIT_TIME_AFTER_EVENT = 200;
	private static OutPutModel result;


    public static void main(String[] args) throws IOException {

        CrawljaxConfiguration.CrawljaxConfigurationBuilder builder = CrawljaxConfiguration.builderFor(URL);

       // builder.crawlRules().insertRandomDataInInputForms(false);

        // click these elements
        //builder.crawlRules().clickDefaultElements();
        //builder.crawlRules().click("div");

        builder.setMaximumStates(20);
		builder.setMaximumRunTime(300,TimeUnit.SECONDS);
       // builder.setMaximumDepth(2);
       // builder.crawlRules().clickElementsInRandomOrder(true);

        // Set timeouts
        builder.crawlRules().waitAfterReloadUrl(WAIT_TIME_AFTER_RELOAD, TimeUnit.MILLISECONDS);
        builder.crawlRules().waitAfterEvent(WAIT_TIME_AFTER_EVENT, TimeUnit.MILLISECONDS);

		int URL_hash = URL.hashCode();
		String output_loc = "target/test-data/state-explorer/"+URL_hash;
        File target = new File(output_loc);
        if (target.exists()) {
			delete(target);
		}
		boolean created = target.mkdirs();
		checkArgument(created, "Could not create "+output_loc);

        // We want to use two browsers simultaneously.
        builder.setBrowserConfig(new BrowserConfiguration(EmbeddedBrowser.BrowserType.FIREFOX, 1));
//        CrawlOverview plugin = new CrawlOverview();
		CrawlOverviewMod plugin = new CrawlOverviewMod();
        builder.addPlugin(plugin);
        CrawljaxRunner crawljax = new CrawljaxRunner(builder.build());
        builder.setOutputDirectory(target);
        crawljax.call();

        result = plugin.getResult();
		int minWidth=0;
		int minHeight=0;
		File node = new File(output_loc+"/screenshots");
		if(node.isDirectory()){
			String[] subNode = node.list();
			for(String filename : subNode){
				if(!filename.contains("small"))
				{
					BufferedImage bimg = ImageIO.read(new File(output_loc+"/screenshots/"+filename));
					minWidth          = (minWidth!=0 && minWidth<bimg.getWidth())?minWidth:bimg.getWidth();
					minHeight         = (minHeight!=0 && minHeight<bimg.getHeight())?minHeight:bimg.getHeight();
				}
			}
			System.out.println("min width:"+minWidth+" minheight:"+minHeight);
		}



    }

	public static void delete(File file)
			throws IOException{

		if(file.isDirectory()){

			//directory is empty, then delete it
			if(file.list().length==0){

				file.delete();
				System.out.println("Directory is deleted : "
						+ file.getAbsolutePath());

			}else{

				//list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					//construct the file structure
					File fileDelete = new File(file, temp);

					//recursive delete
					delete(fileDelete);
				}

				//check the directory again, if empty then delete it
				if(file.list().length==0){
					file.delete();
					System.out.println("Directory is deleted : "
							+ file.getAbsolutePath());
				}
			}

		}else{
			//if file, then delete it
			file.delete();
			System.out.println("File is deleted : " + file.getAbsolutePath());
		}
	}


}
