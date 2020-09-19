package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import fighting.Character;
import loader.ResourceLoader;
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
	
	/** The output action print writer */
	private PrintWriter aWriters[];
	
	/** The output success print writer */
	private PrintWriter sWriters[];
	
	/** String of actions */
	private final String[] actionName = { "NEUTRAL", "STAND", "FORWARD_WALK", "DASH", "BACK_STEP", "CROUCH", "JUMP", "FOR_JUMP", "BACK_JUMP", "AIR",
			"STAND_GUARD", "CROUCH_GUARD", "AIR_GUARD", "STAND_GUARD_RECOV", "CROUCH_GUARD_RECOV", "AIR_GUARD_RECOV", "STAND_RECOV", "CROUCH_RECOV", "AIR_RECOV", "CHANGE_DOWN", "DOWN", "RISE", "LANDING",
			"THROW_A", "THROW_B", "THROW_HIT", "THROW_SUFFER", "STAND_A", "STAND_B", "CROUCH_A", "CROUCH_B", "AIR_A", "AIR_B", "AIR_DA", "AIR_DB", "STAND_FA", "STAND_FB", "CROUCH_FA", "CROUCH_FB",
			"AIR_FA", "AIR_FB", "AIR_UA", "AIR_UB", "STAND_D_DF_FA", "STAND_D_DF_FB", "STAND_F_D_DFA", "STAND_F_D_DFB",
			"STAND_D_DB_BA", "STAND_D_DB_BB", "AIR_D_DF_FA", "AIR_D_DF_FB", "AIR_F_D_DFA", "AIR_F_D_DFB", "AIR_D_DB_BA",
			"AIR_D_DB_BB", "STAND_D_DF_FC" };
	
	private final String[] successName = {"HIGH_HIT", "LOW_HIT", "COUNTER_HIT", "PUNISH", "KNOCKDOWN", "SPECIAL_ENDER",
			"ANTI_AIR", "MEATY", "THROW", "BLOCK", "WHIFF", "REVERSAL", "PROJECTILE_HIT"};
	
	/**
	 * Singleton private constructor
	 * Opens up the HashMaps
	 */
	private FeedbackAnalysis() {
		this.actions = new ArrayList<HashMap<Action, Integer>>(2);
			//	new HashMap<Action, Integer>();
		this.successes = new ArrayList<HashMap<Success, Integer>>(2);
		
		this.aWriters = new PrintWriter[2];
		String apath = "./feedbackActionData";
		new File(apath).mkdir();
		
		this.sWriters = new PrintWriter[2];
		String spath = "./feedbackSuccessData";
		new File(spath).mkdir();
		
		for (int i = 0; i < 2; i++) {
			this.actions.add(new HashMap<Action, Integer>());
			this.successes.add(new HashMap<Success, Integer>());
		}
		
		for (int i = 0; i < 2; i++) {
			String afileName = "/" + (i == 0 ? "P1" : "P2") + "ActionFile.csv";
			this.aWriters[i] = ResourceLoader.getInstance().openWriteFile(apath + afileName, true);
			
			String sfileName = "/" + (i == 0 ? "P1" : "P2") + "SuccessFile.csv";
			this.sWriters[i] = ResourceLoader.getInstance().openWriteFile(spath + sfileName, true);
			
			writeHeader(i);
			
			//Initialize Array Lists with Zeroes so nothing is null
			for(Action a : Action.values()) {
				actions.get(i).put(a, 0);
			}
			for(Success s : Success.values()) {
				successes.get(i).put(s, 0);
			}

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
	
	private void writeHeader(int i) {
		try {
			for(String s : this.actionName) {
				this.aWriters[i].print(s + ",");
			}
			this.aWriters[i].println();
			for(String s: this.successName) {
				this.sWriters[i].print(s + ", ");
			}

			this.sWriters[i].println();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void outputActionCount() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < Action.TOTAL.ordinal(); j++) {
				this.aWriters[i].print(this.actions.get(i).get(Action.values()[j]) + ",");
			}

			this.aWriters[i].println();
			this.aWriters[i].flush();
		}
	}
	
	public void outputSuccessCount() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < Success.TOTAL.ordinal(); j++) {
				this.sWriters[i].print(this.successes.get(i).get(Success.values()[j]) + ",");
			}

			this.sWriters[i].println();
			this.sWriters[i].flush();
		}
	}
	
	public void closeAllWriters() {
		for (int i = 0; i < 2; i++) {
			this.aWriters[i].close();
			this.sWriters[i].close();
		}
		this.successes.clear();
	}
	
}
