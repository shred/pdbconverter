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
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.shredzone.pdbconverter.handler.ExportHandler;
import org.shredzone.pdbconverter.handler.ExportOptions;

/**
 * PdbConverter's main class.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 524 $
 */
@SuppressWarnings("static-access")
public class PdbConverter {
    
    private static final String OPT_CATEGORY = "category";
    private static final String OPT_SPLIT = "split";
    private static final String OPT_FROM = "from";
    private static final String OPT_UNTIL = "until";
    
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
        CLI_OPTIONS.addOption(OptionBuilder
                .withArgName("input")
                .withLongOpt("input")
                .withDescription("input pdb/mdb file")
                .isRequired().hasArg()
                .create("i"));

        CLI_OPTIONS.addOption(OptionBuilder
                .withArgName("output")
                .withLongOpt("output")
                .withDescription("converted output file")
                .isRequired().hasArg()
                .create("o"));
        
        CLI_OPTIONS.addOption(OptionBuilder
                .withArgName("converter")
                .withLongOpt("converter")
                .withDescription("converter to be used")
                .hasArg()
                .create("c"));

        CLI_OPTIONS.addOption(OptionBuilder
                .withArgName(OPT_CATEGORY)
                .withLongOpt("category")
                .withDescription("only output records of this category")
                .hasArg()
                .create("t"));
        
        CLI_OPTIONS.addOption(OptionBuilder
                .withArgName(OPT_SPLIT)
                .withLongOpt("split")
                .withDescription("write each category into a separate file")
                .create("s"));

        CLI_OPTIONS.addOption(OptionBuilder
                .withArgName(OPT_FROM)
                .withLongOpt("from")
                .withDescription("only output records starting from this date")
                .hasArg()
                .create("f"));

        CLI_OPTIONS.addOption(OptionBuilder
                .withArgName(OPT_UNTIL)
                .withLongOpt("until")
                .withDescription("only output records until this date (exclusive)")
                .hasArg()
                .create("u"));
    }

    private static final Options GUI_CLI_OPTIONS = new Options();
    static {
        GUI_CLI_OPTIONS.addOption(OptionBuilder
                .withArgName("gui")
                .withLongOpt("gui")
                .withDescription("open the GUI")
                .isRequired()
                .create());
    }

    /**
     * Main invocation.
     */
    public static void main(String[] args) {
        try {
            CommandLineParser parser = new GnuParser();
            CommandLine cmd = parser.parse(GUI_CLI_OPTIONS, args);
            
            if (cmd.hasOption("gui")) {
                new PdbConverterGui();
                return;
            }
        } catch (ParseException ex) {
            // Ignore and attempt to parse CLI_OPTIONS
        }

        try {
            CommandLineParser parser = new GnuParser();
            CommandLine cmd = parser.parse(CLI_OPTIONS, args);
            
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
