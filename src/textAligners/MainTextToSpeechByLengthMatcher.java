package textAligners;

import speechDetection.OfflineSpeechRecognizer;
import common.AudioLabel;
import common.Speeches;
import common.Text;
import dataExporters.AudacityLabelsExporter;
import dataProducers.TextImporter;
import dataProducers.WaveImporter;


public class MainTextToSpeechByLengthMatcher
{
	public static void main(String[] args)
	{
    	String waveFile = args[0];
    	String textFile = args[1];
    	String labelsOutputPath = args[2];

    	WaveImporter waveImporterForOfflineSpeechRecognition = new WaveImporter(
    	        waveFile, "../textAligners/config_nospeech_nomel.xml");
    	OfflineSpeechRecognizer speechRecognizer = new OfflineSpeechRecognizer(20, 10);
    	
    	waveImporterForOfflineSpeechRecognition.registerObserver(speechRecognizer);
    	waveImporterForOfflineSpeechRecognition.process();
    	waveImporterForOfflineSpeechRecognition.done();
    	
    	System.err.println("extracting speeches");
    	Speeches speeches = speechRecognizer.findSpeechParts();
    	if (args.length > 3)
    	{
	    	String speechesOutputPath = args[3];
	        AudioLabel[] speechLabels = new AudioLabel[speeches.size()];
	        for (int i = 0; i < speechLabels.length; ++i) {
	        	speechLabels[i] = new AudioLabel(i + "", speeches.get(i).getStartTime(), speeches.get(i).getEndTime());
	        }
	    	new AudacityLabelsExporter(speechesOutputPath).export(speechLabels);
    	}
    	System.err.println("speeches: " + speeches.size());
    
        Text text = new Text(new TextImporter(textFile), speeches.getTotalTime());
        
        TextToSpeechByLengthAligner byLengthAligner = new TextToSpeechByLengthAligner();
        IncrementalTextToSpeechAligner incrementalAligner =
        		new IncrementalTextToSpeechAligner(byLengthAligner);
        System.err.println("aligning");
        AudioLabel[] matched = byLengthAligner.findMatching(text, speeches);
    	new AudacityLabelsExporter(labelsOutputPath).export(matched);
        System.err.println("END");
	}
}
