package phonemeScorers.trainers;

import java.util.ArrayList;
import java.util.HashMap;

import phonemeScorers.IPhonemeScorer;
import phonemeScorers.SingleGaussianPhonemeScorer;



import common.AudioLabel;
import common.GenericListContainer;
import common.LogMath;
import common.algorithms.DataByTimesExtractor;
import common.algorithms.gaussian.MultivariateNormalDistribution;
import common.exceptions.ImplementationError;
import common.statistics.MultivariateDataStatistics;

public class PhonemeSingleGaussianTrainer
{
    public IPhonemeScorer[] trainPhonemes(
        ArrayList<AudioLabel> phonemeLabels,
        ArrayList<double[]> data,
        double totalTime) throws ImplementationError
    {
        HashMap<String, ArrayList<AudioLabel>> sortedLabels = sortLabels(phonemeLabels);
        
        ArrayList<IPhonemeScorer> trainedScorers = new ArrayList<IPhonemeScorer>();
        for (String key : sortedLabels.keySet()) {
            trainedScorers.add(trainScorer(key, sortedLabels.get(key), data, totalTime));
        }
        return trainedScorers.toArray(new IPhonemeScorer[0]);
    }

    private IPhonemeScorer trainScorer(
        String phoneme, ArrayList<AudioLabel> labelList, ArrayList<double[]> data, double totalTime)
    {
        DataByTimesExtractor<double[]> dataExtractor =
                new DataByTimesExtractor<double[]>(new GenericListContainer<double[]>(data), totalTime, 0);
            
        ArrayList<double[]> phonemeData = new ArrayList<double[]>();
        double transitionScore = 0;
        for (AudioLabel label : labelList) {
            ArrayList<double[]> extracted = dataExtractor.extract(label.getStart(), label.getEnd());
            if (extracted.size() > 0)
                transitionScore += 1.0 / (double)extracted.size();
            else transitionScore += 1.0;
            phonemeData.addAll(extracted);
        }
        transitionScore /= labelList.size();
        double[][] phonemePoints = new double[phonemeData.size()][];
        for (int i = 0; i < phonemeData.size(); ++i)
            phonemePoints[i] = phonemeData.get(i);
        MultivariateNormalDistribution model =
                new MultivariateDataStatistics(phonemePoints).getDistribution();
        if (Math.log(transitionScore) == Double.POSITIVE_INFINITY)
            throw new ImplementationError(phoneme + " " + labelList.size() + " " + transitionScore);
        return new SingleGaussianPhonemeScorer(model, LogMath.linearToLog(transitionScore), phoneme);
    }

    private HashMap<String, ArrayList<AudioLabel>> sortLabels(ArrayList<AudioLabel> labels)
    {
        HashMap<String, ArrayList<AudioLabel>> out = new HashMap<String, ArrayList<AudioLabel>>();
        for (AudioLabel label : labels) {
            String key = label.getLabel();
            ArrayList<AudioLabel> labelList =
                (out.containsKey(key)) ? out.get(key) : new ArrayList<AudioLabel>();
            labelList.add(label);
            out.put(key, labelList);
        }
        return out;
    }
}
