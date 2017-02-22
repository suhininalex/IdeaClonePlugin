package net.suhininalex.plugintest;

import java.nio.Buffer;
import java.util.Random;
import java.util.regex.MatchResult;

public class Main {

    /** Counts all the tokens in the active list (and displays them). This is an expensive operation. */
    protected void showTokenCount1() {
        for (int i = 0; i < cepstrum.length; i++) {
            if (numberMelFilters > 0) {
                double[] melcosine_i = melcosine[i];
                int j = 0;
                cepstrum[i] += (beta * melspectrum[j] * melcosine_i[j]);
                for (j = 1; j < numberMelFilters; j++) {
                    cepstrum[i] += (melspectrum[j] * melcosine_i[j]);
                }
                cepstrum[i] /= period;
            }
        }
    }

    /** Counts all the tokens in the active list (and displays them). This is an expensive operation. */
    protected void showTokenCount2() {
        for (int i = 0; i <= LPCOrder; i++) {

            if (numberPLPFilters > 0) {
                double[] cosine_i = cosine[i];
                int j = 0;
                autocor[i] += (beta * plpspectrum[j] * cosine_i[j]);

                for (j = 1; j < numberPLPFilters; j++) {
                    autocor[i] += (plpspectrum[j] * cosine_i[j]);
                }
                autocor[i] /= period;
            }
        }
    }
}
