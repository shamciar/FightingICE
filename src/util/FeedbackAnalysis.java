package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import fighting.Character;
import enumerate.Action;
import enumerate.Success;

/**
 * This will be the main class that handles input tracing,
 * player analysis, and feedback.
 * While the DebugActionData class provides
 * action input logging functionality,
 * it is for debugging purposes only.
 * Therefore, I am choosing to rework the input tracing system.
 * 
 * @author shamciar
 */
public class FeedbackAnalysis {

	/** The map of actions by the player */
	private ArrayList<HashMap<Action, Integer>> actions;
	
	/** The map of winning actions by the player */
	private ArrayList<HashMap<Success, Integer>> successes;
	
	/** The Singleton instance of this data */
	private static FeedbackAnalysis instance;
	
	/**
	 * Singleton private constructor
	 * Opens up the HashMaps
	 */
	private FeedbackAnalysis() {
		this.actions = new ArrayList<HashMap<Action, Integer>>(2);
			//	new HashMap<Action, Integer>();
		this.successes = new ArrayList<HashMap<Success, Integer>>(2);
		
		for (int i = 0; i < 2; i++) {
			this.actions.add(new HashMap<Action, Integer>());
			this.successes.add(new HashMap<Success, Integer>());
		}

		
		Logger.getAnonymousLogger().log(Level.INFO, "Create instance: " + FeedbackAnalysis.class.getName());
		Logger.getAnonymousLogger().log(Level.INFO, "beginning player analysis...");
	}
	
	
	/**
	 * Singleton Instance Getter
	 * 
	 * @return the instance of the feedback analyzer
	 */
	public static FeedbackAnalysis getInstance() {
		if(instance == null) {
			instance = new FeedbackAnalysis();
		}
		
		return instance;
	}
	
	/**
	 * Increase the counter for the specified action
	 * 
	 * @param action the action to increment
	 * @param player the player index
	 */
	public void addAction(Action action, int player) {
		if(actions.get(player).get(action) == null) {
			actions.get(player).put(action, 1);
		} else {
			actions.get(player).replace(action, actions.get(player).get(action) + 1);
		}
	}
	
	/**
	 * Increase the counter for the specified success
	 * 
	 * @param success the success to increment
	 * @param player the player index
	 */
	public void addSuccess(Success success, int player) {
		if(successes.get(player).get(success) == null) {
			successes.get(player).put(success, 1);
		} else {
			successes.get(player).replace(success, successes.get(player).get(success) + 1);
		}
	}
	
}
