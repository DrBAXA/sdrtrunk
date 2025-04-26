package com.coder;

import io.github.dsheirer.buffer.INativeBuffer;
import io.github.dsheirer.preference.UserPreferences;
import io.github.dsheirer.source.SourceException;
import io.github.dsheirer.source.tuner.Tuner;
import io.github.dsheirer.source.tuner.manager.DiscoveredTuner;
import io.github.dsheirer.source.tuner.manager.TunerManager;
import io.github.dsheirer.source.tuner.recording.RecordingTuner;
import io.github.dsheirer.source.tuner.recording.RecordingTunerConfiguration;
import io.github.dsheirer.spectrum.ComplexDftProcessor;
import io.github.dsheirer.spectrum.DFTSize;
import io.github.dsheirer.spectrum.converter.ComplexDecibelConverter;

import java.util.Arrays;

public class Main {

    public static void main(String[] ignoredArgs) throws InterruptedException, SourceException {

        // Creating mock tuner that uses recorded file
        RecordingTunerConfiguration recordingTunerConfiguration = new RecordingTunerConfiguration();
        recordingTunerConfiguration.setPath("C:\\sdr\\records\\157rewritten.wav");
        Tuner tuner = new RecordingTuner(new UserPreferences(), null, recordingTunerConfiguration);

        // Discrete Fourier Transform step (where raw samples conversed to signal power / frequency)
        ComplexDftProcessor<INativeBuffer> dftProcessor = new ComplexDftProcessor<>();
        // count of histogram bins (histogram resolution)
        // do not know why but 8192 produces 4096 bins for histogram
        dftProcessor.setDFTSize(DFTSize.FFT08192);
        // how often to create histogram // we may want to create it more frequently
        // and use some integration (adding up previous 20/30/100 ) to reduce noisiness
        dftProcessor.setFrameRate(20);
        tuner.getTunerController().addBufferListener(dftProcessor);
        ComplexDecibelConverter complexDecibelConverter = new ComplexDecibelConverter();
        dftProcessor.addConverter(complexDecibelConverter);
        complexDecibelConverter.addListener(results -> {

            // here we should put our processing
            // results as 4096 size array
            System.out.println(Arrays.toString(results));
        });

        tuner.start();
        // there is ~ 43 seconds of recorder spectrum in file
        Thread.sleep(43000);
        dftProcessor.stop();
        System.exit(0);
    }
}