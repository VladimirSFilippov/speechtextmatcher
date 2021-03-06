package common.algorithms.hmm.training;

import java.util.ArrayList;
import common.LogMath;
import common.exceptions.ImplementationError;

public class SequenceScorer
{
    public ObservationSequenceLogLikelihoods scoreForSequence(
        ArrayList<double[]> sequence,
        NodeScorer[][] scorers) throws ImplementationError
    {
        ArrayList<NodeLogLikelihoods> nodesLogLikelihoods = new ArrayList<NodeLogLikelihoods>();
        for (int i = 0; i < sequence.size(); ++i) {
            for (int j = 0; j < scorers[i].length; ++j) {
                scorers[i][j].scoreForObservation(sequence.get(i));
                NodeLogLikelihoods scored =
                    createNodeLogLikelihoods(
                            scorers[i][j],
                            sequence.get(i),
                            (i + 1 < sequence.size()) ? sequence.get(i + 1) : null);
                nodesLogLikelihoods.add(scored);
            }
        }
        
        float sequenceScore = Float.NEGATIVE_INFINITY;
        for (int j = 0; j < scorers[scorers.length - 1].length; ++j) {
            if (scorers[scorers.length - 1][j].isExitting() == false) continue;
            sequenceScore = Math.max(sequenceScore, scorers[scorers.length - 1][j].getScore());
        }
        return new ObservationSequenceLogLikelihoods(sequenceScore, nodesLogLikelihoods);
    }

    private NodeLogLikelihoods createNodeLogLikelihoods(
        NodeScorer nodeScorer, double[] observation, double[] nextObservation) throws ImplementationError
    {
//        if (nodeScorer.getScore() > 0)
//            throw new ImplementationError("node likelihood is greater than 0: " + nodeScorer.getScore());
        ArrayList<ArcLogLikelihood> arcLikelihoods = new ArrayList<ArcLogLikelihood>();
        
//        LogMath result = new LogMath();
//        for (NodeScorerArc arc : nodeScorer) {
//            arcLikelihoods.add(new ArcLogLikelihood(arc.getArc(), arc.getScore()));
//            result.logAdd(arc.getScore());
//        }
//        float arcSum = result.getResult(-Float.MAX_VALUE);
//        if (arcSum != nodeScorer.getScoreWithoutObservation())
//            throw new ImplementationError(
//                    "sum of probabilities of incoming arcs is different" +
//                    " than probability of reaching node (" +
//                    arcSum + " < " + nodeScorer.getScore() + ")");
        
        return new NodeLogLikelihoods(
                nodeScorer.getNode(),
                observation,
                nextObservation,
                nodeScorer.getScore(),
                nodeScorer.getScoreWithoutObservation(),
                arcLikelihoods);
    }
}
