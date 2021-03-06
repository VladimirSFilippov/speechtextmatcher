package common.algorithms.hmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import common.exceptions.ImplementationError;

public class BestSequenceFinder
{
    private final Node endingNode = new Node("", null);
    
    private class NodeScore
    {
        private final NodeScore bestPreviousNodeScore;
        private final Node node;
        private final float score;
        
        NodeScore(float score, Node node, NodeScore previous)
        {
            this.node = node;
            this.score = score;
            this.bestPreviousNodeScore = previous;
        }

        public ArrayList<String> generateSequence()
        {
            ArrayList<String> soFar =
                (bestPreviousNodeScore != null) ?
                    bestPreviousNodeScore.generateSequence() :
                        new ArrayList<String>();
            soFar.add(node.getName());
            return soFar;
        }

        public ArrayList<NodeScore> createNext(double[] observation) throws ImplementationError
        {
            if (this.node == endingNode) return new ArrayList<NodeScore>();
            ArrayList<NodeScore> ret = new ArrayList<NodeScore>();
            float observationScore = this.node.getState().observationLogLikelihood(observation);
            for (Arc arc : this.node) {
                float score = this.score + observationScore + arc.getExit().getLogLikelihood();
                Node nextNode = arc.getLeadingToNode();
                if (nextNode == null) nextNode = endingNode;
                ret.add(new NodeScore(score, nextNode, this));
            }
            return ret;
        }
        
        public String toString()
        {
            return "{" + this.node.getName() + " " + this.score + "}";
        }
    }
    
    public String[] findBestSequence(ArrayList<double[]> observations, Node model) throws ImplementationError
    {
        Map<Node, NodeScore> bestNodeScores = new HashMap<Node, NodeScore>();
        bestNodeScores.put(model, new NodeScore(0, model, null));
        for (double[] observation : observations) {
            bestNodeScores = createNext(observation, bestNodeScores);
        }
        return bestNodeScores.get(endingNode).generateSequence().toArray(new String[0]);
    }

    private Map<Node, NodeScore> createNext(double[] observation, Map<Node, NodeScore> bestNodeScores) throws ImplementationError
    {
        Map<Node, NodeScore> next = new HashMap<Node, NodeScore>();
        
        for (Node node : bestNodeScores.keySet()) {
            NodeScore score = bestNodeScores.get(node);
            ArrayList<NodeScore> nextScores = score.createNext(observation);
            for (NodeScore nextScore : nextScores) {
                if (!next.containsKey(nextScore.node))
                    next.put(nextScore.node, nextScore);
                else {
                    NodeScore currentScore = next.get(nextScore.node);
                    if (currentScore.score < nextScore.score)
                        next.put(node, nextScore);
                }
            }
        }
        return next;
    }
}
