package phonemeAligner.hmmBased;

import graphemesToPhonemesConverters.GraphemesToPolishPhonemesConverter;
import graphemesToPhonemesConverters.TextToPhonemeSequenceConverter;

import java.util.ArrayList;

import common.AudioLabel;
import common.GenericListContainer;
import common.algorithms.DataByTimesExtractor;
import common.algorithms.hmm.Node;
import common.algorithms.hmm.training.Trainer;
import common.exceptions.ImplementationError;

public class PhonemeSequencesHMMTrainer
{
    private DataByTimesExtractor<double[]> extractor;
    private HMMGraphFromPhonemeSequenceCreator hmmGraphCreator
        = new HMMGraphFromPhonemeSequenceCreator(
                new TextToPhonemeSequenceConverter(new GraphemesToPolishPhonemesConverter()));
    
    public PhonemeSequencesHMMTrainer(ArrayList<double[]> audioData, double totalTime)
    {
        this.extractor = new DataByTimesExtractor<double[]>(
                new GenericListContainer<double[]>(audioData), totalTime, 0);
    }
    
    PhonemeHMM trainModel(AudioLabel[] chunks) throws ImplementationError
    {
        System.err.println("training hmm");
        double[][][] trainingData = new double[chunks.length][][];
        Node[] chunkGraphs = new Node[chunks.length];
        
        int count = 0;
        for (AudioLabel chunk : chunks) {
            ArrayList<double[]> extracted = this.extractor.extract(chunk.getStart(), chunk.getEnd());
            trainingData[count] = extracted.toArray(new double[0][0]);
            chunkGraphs[count] = this.hmmGraphCreator.create(chunk.getLabel());
            ++count;
        }
        new Trainer().trainModel(trainingData, chunkGraphs);
        return new PhonemeHMM(this.hmmGraphCreator);
    }
}
