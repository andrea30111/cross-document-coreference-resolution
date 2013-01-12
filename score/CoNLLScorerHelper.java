package edu.oregonstate.score;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.oregonstate.experiment.ExperimentConstructor;
import edu.oregonstate.io.ResultOutput;
import edu.oregonstate.util.EecbConstants;
import edu.stanford.nlp.dcoref.SieveCoreferenceSystem;

/**
 * print the score according to the CoNLL scorer
 * 
 * @author Jun Xie (xie@eecs.oregonstate.edu)
 *
 */
public class CoNLLScorerHelper {

	/* current epoch */
	private int mEpoch;

	/* log file */
	private String mLogFile;
	
	/* conll scorer path */
	private String mConllScorerPath;
	
	/* final result */
	private double finalCoNllF1Result;
	
	/* MUC loss score result */
	private double mucScoreF1Result;
	
	/* Bcubed loss score result */
	private double bcubedScoreF1Result;
	
	/* ceaf loss score result */
	private double ceafScoreF1Result;
	
	/* experiment configuration */
	private final Properties experimentProps;
	
	/*
	 * return final conll F1 result
	 */
	public double getFinalCoNllF1Result() {
		return finalCoNllF1Result;
	}
	
	/*
	 * return loss score
	 */
	public double getLossScoreF1Result() {
		String lossScoreType = experimentProps.getProperty(EecbConstants.LOSSFUNCTION_SCORE_PROP, "Pairwise");
		double score = 0.0;
		
		if (lossScoreType.equals("MUC")) {
			score = mucScoreF1Result; 
		} else if (lossScoreType.equals("BCubed")) {
			score = bcubedScoreF1Result;
		} else if (lossScoreType.equals("CEAF")) {
			score = ceafScoreF1Result;
		} else {
			score = 0.0;
		}
		
		return score;
	}
	
	public double getMUCScoreF1Result() {
		return mucScoreF1Result;
	}
	
	public double getBcubedScoreF1Result() {
		return bcubedScoreF1Result;
	}
	
	public double getCEAFScoreF1Result() {
		return ceafScoreF1Result;
	}
	
	/**
	 * constructor
	 * 
	 * @param epoch
	 * @param logFile
	 */
	public CoNLLScorerHelper(int epoch, String logFile) {
		mEpoch = epoch;
		mLogFile = logFile;
		String clusterScorePath = "/nfs/guille/xfern/users/xie/Experiment/corpus/scorer/v4/scorer.pl";
		mConllScorerPath = ExperimentConstructor.experimentProps.getProperty(EecbConstants.CONLL_SCORER_PROP, clusterScorePath);
		finalCoNllF1Result = 0.0;
		mucScoreF1Result = 0.0;
		bcubedScoreF1Result = 0.0;
		ceafScoreF1Result = 0.0;
		experimentProps = ExperimentConstructor.experimentProps;
	}
	
	/**
	 * print the final Conll score 
	 * 
	 * @param mGoldCorefCluster
	 * @param mPredictedCorefCluster
	 * @param phase
	 */
	public void printFinalCoNLLScore(String mGoldCorefCluster, String mPredictedCorefCluster, String phase) {
		try {
			ResultOutput.writeTextFile(mLogFile, "\n\n");
			ResultOutput.writeTextFile(mLogFile, "the score summary of resolution for " + phase + " on the " + mEpoch + "th iteration");
			String summary = SieveCoreferenceSystem.getConllEvalSummary(mConllScorerPath, mGoldCorefCluster, mPredictedCorefCluster);
			printScoreSummary(summary, true);
			printFinalScore(summary);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * 
	 * @param summary
	 * @param afterPostProcessing
	 */
	private void printScoreSummary(String summary, boolean afterPostProcessing) {
		String[] lines = summary.split("\n");
		if(!afterPostProcessing) {
			for(String line : lines) {
				if(line.startsWith("Identification of Mentions")) {
					ResultOutput.writeTextFile(mLogFile, line);
					return;
				}
			}
		} else {
			StringBuilder sb = new StringBuilder();
			for(String line : lines) {
				if(line.startsWith("METRIC")) sb.append(line);
				if(!line.startsWith("Identification of Mentions") && line.contains("Recall")) {
					sb.append(line).append("\n");
				}
			}
			ResultOutput.writeTextFile(mLogFile, sb.toString());
		}
	}
	
	/** 
	 * Print average F1 of MUC, B^3, CEAF_E
	 */
	private void printFinalScore(String summary) {
		Pattern f1 = Pattern.compile("Coreference:.*F1: (.*)%");
		Matcher f1Matcher = f1.matcher(summary);
		double[] F1s = new double[5];
		int i = 0;
		while (f1Matcher.find()) {
			F1s[i++] = Double.parseDouble(f1Matcher.group(1));
		}

		// MUC
		mucScoreF1Result = F1s[0];

		//BCubed
		bcubedScoreF1Result = F1s[1];

		//CEAF
		ceafScoreF1Result = F1s[3];

		finalCoNllF1Result = (F1s[0]+F1s[1]+F1s[3])/3;
		ResultOutput.writeTextFile(mLogFile, "Final score ((muc+bcub+ceafe)/3) = "+ finalCoNllF1Result);
	}
	
}