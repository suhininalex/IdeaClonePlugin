package net.suhininalex.plugintest;

import java.nio.Buffer;
import java.util.Random;
import java.util.regex.MatchResult;

public class Main {

    /** Counts all the tokens in the active list (and displays them). This is an expensive operation. */
    protected void showTokenCount1() {
        String MMFfile = null;
        String lexFile = null;
        String fillerFile = null;
        String gramFile = null;

        if (args[i].equals("-lex")) {
            lexFile = args[++i];
        } else if (args[i].equals("-gram")) {
            gramFile = args[++i];
        }

        for (int i=0;i<args.length;i++) {
            if (args[i].equals("-lex")) {
                lexFile = args[++i];
            } else if (args[i].equals("-gram")) {
                gramFile = args[++i];
            } else if (args[i].equals("-mmf")) {
                MMFfile = args[++i];
            } else if (args[i].equals("-filler")) {
                fillerFile = args[++i];
            }
        }
        // output = same files + extension ".conv"
        if (MMFfile!=null) {
            // conversion des phonemes et des mots
            NamesConversion nc = new NamesConversion();
            nc.buildPhoneConversion(MMFfile);
            nc.buildWordConversion(lexFile);
            System.out.println("converting phones in MMF to "+MMFfile+".conv");
            nc.convertMMF(MMFfile);
            if (lexFile!=null) {
                System.out.println("converting phones and words in lexicon to "+lexFile+".conv");
                nc.convertLexicon(lexFile);
            }
            if (fillerFile!=null) {
                System.out.println("converting phones in filler to "+fillerFile+".conv");
                nc.convertLexicon(fillerFile);
            }
            if (gramFile!=null) {
                System.out.println("converting words in gram to "+gramFile+".conv");
                nc.convertWordGrammar(gramFile);
            }
        }
    }

    /** Counts all the tokens in the active list (and displays them). This is an expensive operation. */
    protected void showTokenCount2() {
        String configFile = null;
        String frontEndName = null;
        String inputFile = null;
        String inputCtl = null;
        String outputFile = null;
        String format = "binary";

        for (int i = 0; i < argv.length; i++) {
            if (argv[i].equals("-c")) {
                configFile = argv[++i];
            }
            if (argv[i].equals("-name")) {
                frontEndName = argv[++i];
            }
            if (argv[i].equals("-i")) {
                inputFile = argv[++i];
            }
            if (argv[i].equals("-ctl")) {
                inputCtl = argv[++i];
            }
            if (argv[i].equals("-o")) {
                outputFile = argv[++i];
            }
            if (argv[i].equals("-format")) {
                format = argv[++i];
            }
        }

        if (frontEndName == null || (inputFile == null && inputCtl == null)
                || outputFile == null || format == null) {
            System.out
                    .println("Usage: FeatureFileDumper "
                            + "[ -config configFile ] -name frontendName "
                            + "< -i input File -o outputFile | -ctl inputFile -i inputFolder -o outputFolder >");
            System.exit(1);
        }

        logger.info("Input file: " + inputFile);
        logger.info("Output file: " + outputFile);
        logger.info("Format: " + format);

        try {
            URL url;
            if (configFile != null) {
                url = new File(configFile).toURI().toURL();
            } else {
                url = FeatureFileDumper.class
                        .getResource("frontend.config.xml");
            }
            ConfigurationManager cm = new ConfigurationManager(url);
            FeatureFileDumper dumper = new FeatureFileDumper(cm, frontEndName);

            if (inputCtl == null)
                dumper.processFile(inputFile, outputFile, format);
            else
                dumper.processCtl(inputCtl, inputFile, outputFile, format);
        } catch (IOException ioe) {
            System.err.println("I/O Error " + ioe);
        } catch (PropertyException p) {
            System.err.println("Bad configuration " + p);
        }
    }
}
