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
    private static final String URL = "http://demo.crawljax.com";
    private static OutPutModel result;


    public static void main(String[] args) throws IOException {

        CrawljaxConfiguration.CrawljaxConfigurationBuilder builder = CrawljaxConfiguration.builderFor(URL);

       // builder.crawlRules().insertRandomDataInInputForms(false);

        // click these elements
        //builder.crawlRules().clickDefaultElements();
        //builder.crawlRules().click("div");

       // builder.setMaximumStates(10);
       // builder.setMaximumDepth(3);
       // builder.crawlRules().clickElementsInRandomOrder(true);

        // Set timeouts
       // builder.crawlRules().waitAfterReloadUrl(WAIT_TIME_AFTER_RELOAD, TimeUnit.MILLISECONDS);
        //builder.crawlRules().waitAfterEvent(WAIT_TIME_AFTER_EVENT, TimeUnit.MILLISECONDS);

        File target = new File("target/test-data/state-explorer");
        if (!target.exists()) {
            boolean created = target.mkdirs();
            checkArgument(created, "Could not create target/test-data dir");
        }
        // We want to use two browsers simultaneously.
        builder.setBrowserConfig(new BrowserConfiguration(EmbeddedBrowser.BrowserType.FIREFOX, 1));
        CrawlOverview plugin = new CrawlOverview();
        builder.addPlugin(plugin);
        CrawljaxRunner crawljax = new CrawljaxRunner(builder.build());
        builder.setOutputDirectory(target);
        crawljax.call();

        result = plugin.getResult();

    }


}
