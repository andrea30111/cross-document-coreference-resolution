package edu.oregonstate.dataset;

import edu.stanford.nlp.dcoref.Document;

/**
 * get training data
 * 
 * @author Jun Xie (xie@eecs.oregonstate.edu)
 *
 */
public interface IDataSet {

	public Document getData(String topics, boolean goldOnly);
}
