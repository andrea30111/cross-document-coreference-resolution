package edu.oregonstate.training;

import java.util.List;

import edu.oregonstate.classifier.Parameter;
import edu.oregonstate.general.DoubleOperation;
import edu.oregonstate.search.State;
import edu.stanford.nlp.dcoref.CorefCluster;

public class Online extends ITraining {
	
	/**
	 * implement the batch 
	 */
	public Parameter train(List<String> paths, Parameter para, double learningRate) {
		double[] previousWeight = para.getWeight();
		int violation = para.getNoOfViolation();
		int numberOfInstance = 0;
		
		// use to update weight
		Parameter finalParameter = para.makeCopy();
		double[] finalWeight = finalParameter.getWeight();
		double[] finalTotalWeight = finalParameter.getTotalWeight();
		
		for (String path : paths) {
			List<List<List<String>>> dataset = reader.readData(path);
			List<List<String>> goodDataset = dataset.get(0);
			List<List<String>> badDataset = dataset.get(1);
			
			for (int index = 0; index < goodDataset.size(); index++){
				List<String> goodRecords = goodDataset.get(index);
				List<String> badRecords = badDataset.get(index);
				// get the data
				List<State<CorefCluster>> goodStates = reader.processString(goodRecords);
				List<State<CorefCluster>> badStates = reader.processString(badRecords);
				
				if (!incorporateZeroVector) {
					if (reader.isAllZero(goodStates)) continue;
				}
				
				// form constraint
				for (State<CorefCluster> goodState : goodStates) {
					for (State<CorefCluster> badState : badStates) {
						numberOfInstance += 1;
						
						// if loss score equal, do not consider this kind of constraint
						double gLossScore = goodState.getF1Score();
						double bLossScore = badState.getF1Score();
						if (gLossScore == bLossScore) {
							continue;
						}
						
						// get the features of good state and bad state 
						double[] gNumericalFeatures = goodState.getNumericalFeatures();
						double[] bNumericalFeatures = badState.getNumericalFeatures();
						
						// calculate the action score of good state and bad state	
						double goodCostScoreForUpdating = DoubleOperation.time(finalWeight, gNumericalFeatures);
						double badCostScoreForUpdating = DoubleOperation.time(finalWeight, bNumericalFeatures);
						
						double goodCostScoreForCounting = DoubleOperation.time(previousWeight, gNumericalFeatures);
						double badCostScoreForCounting = DoubleOperation.time(previousWeight, bNumericalFeatures);
						if (goodCostScoreForCounting <= badCostScoreForCounting) {
							violation += 1;
						}

						// violated current constraint
						if (goodCostScoreForUpdating <= badCostScoreForUpdating) {
							double[] direction = DoubleOperation.minus(gNumericalFeatures, bNumericalFeatures);							
							double[] term = DoubleOperation.time(direction, learningRate);
							finalWeight = DoubleOperation.add(finalWeight, term);
							finalTotalWeight = DoubleOperation.add(finalTotalWeight, finalWeight);
						}
					}
				}
			}
		}
		
		return new Parameter(finalWeight, para.getVariance(), finalTotalWeight, violation, numberOfInstance);
	}

}
