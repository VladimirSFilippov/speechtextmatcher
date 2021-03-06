package common.algorithms.hmm.training;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import common.LogMath;
import common.algorithms.hmm.Node;
import common.algorithms.hmm.State;
import common.algorithms.hmm.StateExit;
import common.exceptions.ImplementationError;
import dataExporters.LinesExporter;

public class StatesTrainer
{
    private Map<State, SingleStateTrainer> stateTrainers =
            new HashMap<State, SingleStateTrainer>();
    private Map<StateExit, TransitionTrainer> transitionTrainers =
            new HashMap<StateExit, TransitionTrainer>();
    private float totalLikelihood = 0;
    
    public StatesTrainer(ArrayList<SingleStateTrainer> stateTrainers,
                         ArrayList<TransitionTrainer> transitionTrainers)
    {
        for (SingleStateTrainer trainer : stateTrainers) {
            this.stateTrainers.put(trainer.getState(), trainer);
        }
        for (TransitionTrainer trainer : transitionTrainers) {
            this.transitionTrainers.put(trainer.getTransition(), trainer);
        }
    }

    public void retrainStateTrainersWithObservationSequenceForPossibleModel(
        double[][] observationSequence, Node possibleModel) throws ImplementationError
    {
        NodeLogLikelihoodsCalculator likelihoodsCalculator = new NodeLogLikelihoodsCalculator();
        ObservationSequenceLogLikelihoods sequenceLikelihoods =
            likelihoodsCalculator.calculate(observationSequence, possibleModel);
        
        int index = 0;
        for (NodeLogLikelihoods likelihood : sequenceLikelihoods) {
            if (likelihood.getObservation() != observationSequence[index]) ++index;
            if (likelihood.getObservation() != observationSequence[index]) 
                throw new ImplementationError("observations are not sequential: " + index);
            State nodeState = likelihood.getNode().getState();
            double[] observation = likelihood.getObservation();
            this.stateTrainers.get(nodeState).addObservation(observation, likelihood.getLogLikelihood());
//            for (ArcLogLikelihood arcLikelihood : likelihood) {
//                StateExit arcStateExit = arcLikelihood.getArc().getExit();
//                if (arcStateExit == null) throw new ImplementationError("null arc state exit");
//                if (!this.transitionTrainers.containsKey(arcStateExit)) continue;
//                this.transitionTrainers.get(arcStateExit)
//                    .addObservation(arcLikelihood.getLogLikelihood());
//                this.transitionTrainers.get(arcStateExit)
//                    .addStateObservation(likelihood.getLogLikelihood());
//            }
        }
        this.totalLikelihood += sequenceLikelihoods.getLogLikelihood();
    }

    public double retrainingFinished() throws ImplementationError
    {
//        for (TransitionTrainer trainer : this.transitionTrainers.values()) {
//            trainer.finish();
//        }
        for (SingleStateTrainer trainer : this.stateTrainers.values())
            trainer.finish();
        return this.totalLikelihood;
    }

    public void retrainStateTrainersSecondPhase(double[][] observationSequence, Node possibleModel) throws ImplementationError
    {
        NodeLogLikelihoodsCalculator likelihoodsCalculator = new NodeLogLikelihoodsCalculator();
        ObservationSequenceLogLikelihoods sequenceLikelihoods =
            likelihoodsCalculator.calculate(observationSequence, possibleModel);
        
        for (NodeLogLikelihoods likelihood : sequenceLikelihoods) {
            State nodeState = likelihood.getNode().getState();
            double[] observation = likelihood.getObservation();
            this.stateTrainers.get(nodeState).addObservationAgain(observation, likelihood.getLogLikelihood());
        }
    }
}
