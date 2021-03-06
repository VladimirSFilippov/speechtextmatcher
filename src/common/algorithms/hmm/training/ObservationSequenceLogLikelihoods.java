package common.algorithms.hmm.training;

import java.util.ArrayList;
import java.util.Iterator;


public class ObservationSequenceLogLikelihoods implements Iterable<NodeLogLikelihoods>
{
    private final float logLikelihood;
    private final ArrayList<NodeLogLikelihoods> nodesLogLikelihoods;
    
    public ObservationSequenceLogLikelihoods(float logLikelihood,
                                             ArrayList<NodeLogLikelihoods> nodesLogLikelihoods)
    {
        this.logLikelihood = logLikelihood;
        this.nodesLogLikelihoods = nodesLogLikelihoods;
    }

    @Override
    public Iterator<NodeLogLikelihoods> iterator()
    {
        return this.nodesLogLikelihoods.iterator();
    }

    public float getLogLikelihood()
    {
        return this.logLikelihood;
    }
    
    public String toString()
    {
        String ret = this.logLikelihood + " {";
        for (NodeLogLikelihoods nodeLL : this.nodesLogLikelihoods) {
            ret += nodeLL + ",";
        }
        return ret.substring(0, ret.length() - 1) + "}";
    }
}
