/**
 * pdbconverter - Convert Palm PDB files into more common formats
 *
 * Copyright (C) 2009 Richard "Shred" Körber
 *   http://pdbconverter.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.shredzone.pdbconverter;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.shredzone.commons.pdb.CalendarFactory;
import org.shredzone.pdbconverter.handler.ExportHandler;
import org.shredzone.pdbconverter.handler.ExportOptions;

/**
 * PdbConverter's main class.
 *
 * @author Richard "Shred" Körber
 */
public class PdbConverter {

    private static final String OPT_CATEGORY = "category";
    private static final String OPT_SPLIT = "split";
    private static final String OPT_FROM = "from";
    private static final String OPT_UNTIL = "until";
    private static final String OPT_HELP = "help";

    private static final DateFormat yearDateFmt = new SimpleDateFormat("yyyy");
    private static final DateFormat monthDateFmt = new SimpleDateFormat("yyyy-MM");
    private static final DateFormat dayDateFmt = new SimpleDateFormat("yyyy-MM-dd");
    static {
        yearDateFmt.setLenient(true);
        monthDateFmt.setLenient(true);
        dayDateFmt.setLenient(true);
    }

    private static final Options CLI_OPTIONS = new Options();
    static {
        CLI_OPTIONS.addOption(Option.builder("i")
                .longOpt("input")
                .argName("file")
                .desc("input pdb/mdb file")
                .required()
                .hasArg()
                .build());

        CLI_OPTIONS.addOption(Option.builder("o")
                .longOpt("output")
                .argName("file")
                .desc("converted output file")
                .required()
                .hasArg()
                .build());

        CLI_OPTIONS.addOption(Option.builder("c")
                .longOpt("converter")
                .argName("converter")
                .desc("converter to be used")
                .hasArg()
                .build());

        CLI_OPTIONS.addOption(Option.builder("t")
                .longOpt(OPT_CATEGORY)
                .argName("category")
                .desc("only output records of this category")
                .hasArg()
                .build());

        CLI_OPTIONS.addOption(Option.builder("s")
                .longOpt(OPT_SPLIT)
                .desc("write each category into a separate file")
                .build());

        CLI_OPTIONS.addOption(Option.builder("f")
                .longOpt(OPT_FROM)
                .argName("date")
                .desc("only output records starting from this date")
                .hasArg()
                .build());

        CLI_OPTIONS.addOption(Option.builder("u")
                .longOpt(OPT_UNTIL)
                .argName("date")
                .desc("only output records until this date (exclusive)")
                .hasArg()
                .build());

        CLI_OPTIONS.addOption(Option.builder("?")
                .longOpt(OPT_HELP)
                .desc("show this help and exit")
                .build());
    }

    private static final Options GUI_CLI_OPTIONS = new Options();
    static {
        GUI_CLI_OPTIONS.addOption(Option.builder()
                .longOpt("gui")
                .desc("open the GUI")
                .required()
                .build());
    }

    /**
     * Main invocation.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            // Open GUI directly if there is no argument
            new PdbConverterGui();
            return;
        }

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(GUI_CLI_OPTIONS, args);

            if (cmd.hasOption("gui")) {
                new PdbConverterGui();
                return;
            }
        } catch (ParseException ex) {
            // Ignore and attempt to parse CLI_OPTIONS
        }

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(CLI_OPTIONS, args);

            if (cmd.hasOption(OPT_HELP)) {
                printHelp();
                System.exit(0);
            }

            if (cmd.hasOption("gui")) {
                new PdbConverterGui();
                return;
            }

            String infile = cmd.getOptionValue("input");
            String outfile = cmd.getOptionValue("output");
            String converter = cmd.getOptionValue("converter", "zip");

            ExportHandler handler = ConverterRegister.findHandler(converter);
            if (handler == null) {
                System.err.println("Unknown converter: " + converter);
                printHelp();
                System.exit(1);
            }

            File in = new File(infile);
            File out = new File(outfile);

            ExportOptions options = new ExportOptions();
            options.setSplit(cmd.hasOption(OPT_SPLIT));
            options.setCategory(cmd.getOptionValue(OPT_CATEGORY));
            options.setFrom(parseDate(cmd.getOptionValue(OPT_FROM)));
            options.setUntil(parseDate(cmd.getOptionValue(OPT_UNTIL)));

            handler.export(in, out, options);

        } catch (IOException ex) {
            System.err.println("Could not convert: " + ex.getMessage());

        } catch (ParseException ex) {
            System.err.println("Parsing failed: " + ex.getMessage());
            printHelp();
        }
    }

    /**
     * Parses a date string.
     *
     * @param str
     *            Date string to be parsed. May be {@code null}.
     * @return {@link Calendar} object, or {@code null} if a null was passed in.
     * @throws ParseException
     *             The date string could not be parsed
     */
    private static Calendar parseDate(String str) throws ParseException {
        if (str == null) return null;

        Date result;
        try {
            result = dayDateFmt.parse(str);
        } catch (java.text.ParseException ex) {
            try {
                result = monthDateFmt.parse(str);
            } catch (java.text.ParseException ex2) {
                try {
                    result = yearDateFmt.parse(str);
                } catch (java.text.ParseException ex3) {
                    throw new ParseException("Bad date format: " + str);
                }
            }
        }

        Calendar cal = CalendarFactory.getInstance().create();
        cal.setTime(result);
        return cal;
    }

    /**
     * Outputs a help page.
     */
    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("pdbconverter", CLI_OPTIONS);
        System.out.println();
        formatter.printHelp("pdbconverter", GUI_CLI_OPTIONS);
        System.out.println();
        System.out.println("Available converters:");
        for (ExportHandler handler : ConverterRegister.getHandlers()) {
            System.out.printf("  %-20s %s", handler.getName(), handler.getDescription()).println();
        }
    }

}
