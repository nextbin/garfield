package com.latios.garfield.cli;

import com.latios.garfield.task.WatchingTask;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author zebin
 * @since 2016-10-05.
 */
public class WatchingCli {

    private static final Logger LOG = Logger.getLogger(WatchingCli.class);
    private static final String OPTION_FIELD_FREQUENCY = "f";
    private static final String OPTION_LONG_FIELD_FREQUENCY = "frequency";
    private static final String OPTION_FILED_INSTANCE = "i";
    private static final String OPTION_LONG_FILED_INSTANCE = "instance";

    public static Options options() {
        Options options = new Options();
        options.addOption(OPTION_FIELD_FREQUENCY, OPTION_LONG_FIELD_FREQUENCY, true, "(可选)频率,单位分钟,默认60");
        options.addOption(OPTION_FILED_INSTANCE, OPTION_LONG_FILED_INSTANCE, false, "立刻执行");
        return options;
    }

    public static void main(String[] args) throws Exception {
        LOG.info("args: " + Arrays.asList(args));
        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = parser.parse(options(), args);
        if (commandLine.hasOption(OPTION_FILED_INSTANCE)) {
            LOG.info("run instance");
            WatchingTask task = new WatchingTask();
            task.run();
            LOG.info("finish check, exit now");
            return;
        }
        int minute = Integer.parseInt(commandLine.getOptionValue(OPTION_FIELD_FREQUENCY, "60"));
        LOG.info(String.format("frequency is %s minutes", minute));
        while (true) {
            LOG.info("start monitor list...");
            WatchingTask task = new WatchingTask();
            task.run();
            LOG.info("finish monitor list.");
            TimeUnit.MINUTES.sleep(minute);
        }
    }

}
