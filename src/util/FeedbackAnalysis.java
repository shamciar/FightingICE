package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.PrintWriter;

import loader.ResourceLoader;
import manager.GraphicManager;
import setting.GameSetting;
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
	
	/**
	 * Writes the header for the output files
	 * 
	 * @param i player index
	 */
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
	
	/**
	 * Writes the data for the regular actions
	 */
	public void outputActionCount() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < Action.TOTAL.ordinal(); j++) {
				this.aWriters[i].print(this.actions.get(i).get(Action.values()[j]) + ",");
			}

			this.aWriters[i].println();
			this.aWriters[i].flush();
		}
	}
	
	/**
	 * Writes the data for the success actions
	 */
	public void outputSuccessCount() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < Success.TOTAL.ordinal(); j++) {
				this.sWriters[i].print(this.successes.get(i).get(Success.values()[j]) + ",");
			}

			this.sWriters[i].println();
			this.sWriters[i].flush();
		}
	}
	
	/**
	 * Closes all of the writers
	 */
	public void closeAllWriters() {
		for (int i = 0; i < 2; i++) {
			this.aWriters[i].close();
			this.sWriters[i].close();
		}
		//this.successes.clear();
	}
	
	/**
	 * Outputs the feedback to the screen
	 * 
	 * @param winner the winner of the match
	 */
	public void outputFeedback(int winner) {
		
		String feedback = "";
		
		//TODO decide feedback paradigm
		//If win: Analyze the player's playstyle
		//And give feedback to reinforce that
		if(winner == 1) {
			Success playerSuccess = this.findHighestSuccess(successes.get(0));
			switch(playerSuccess) {
			case ANTI_AIR:
				feedback = "You excel in anti-airing the opponent!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try finding ways to increase anti-air damage!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to patient to bait out other mistakes!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case BLOCK:
				feedback = "You excel at blocking the opponent's attacks!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try punishing unsafe attacks with quick moves!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to interrupt gaps with quick attacks!";	
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case COUNTER_HIT:
				feedback = "You excel in interrupting the opponent!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try confirming your interrupts into longer combos!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may want to watch your opponent's hit reaction!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case HIGH_HIT:
				feedback = "You excel in hitting the opponent from above!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try confirming these hits into longer combos!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to mix low hits into your strikes!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case KNOCKDOWN:
				feedback = "You excel in knocking the opponent down!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try getting closer to the opponent while they're on the ground!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to attack while the opponent rises!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case LOW_HIT:
				feedback = "You excel in hitting the opponent from below!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try confirming these hits into longer combos!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to use lows while the opponent backs away!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case MEATY:
				feedback = "You excel in hitting the opponent while they rise from the ground!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try continuing your pressure after these attacks connect!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to bait and punish wakeup reversals from your opponent!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case PROJECTILE_HIT:
				feedback = "You excel in hitting the opponent with projectiles!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Keep pushing the opponent out so they cannot reach you!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to bait the opponent into jumping into your anti-air!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case PUNISH:
				feedback = "You excel in punishing the opponent's unsafe attacks!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try finding other ways to open your opponent up!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to keep an eye out for bait, frame traps, and staggers!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case REVERSAL:
				feedback = "You excel in attacking as soon as possible!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try finding the best time to use an invincible reversal!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to keep an eye out for bait attacks!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case SPECIAL_ENDER:
				feedback = "You excel in ending your combos with special moves!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try determining which special move grants the best situation!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to test early-ending setups for your character!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case THROW:
				feedback = "You excel in throwing your opponent!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Use throws to open up a patient opponent!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to see how your opponent reacts after being thrown!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			case TOTAL:
				break;
			case WHIFF:
				feedback = "You excel in baiting whiff punishes!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try following up bait attacks with buffered special moves!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				feedback = "You may also want to move in and out of your opponent's attack range!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 564);
				break;
			default:
				break;
			}
		} else if (winner == - 1) {
			Success playerSuccess = this.findHighestSuccess(successes.get(1));
			switch(playerSuccess) {
			case ANTI_AIR:
				feedback = "Your opponent hit you with a lot of anti-airs!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try staying on the ground instead of jumping!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case BLOCK:
				feedback = "Your opponent blocked a lot of your attacks!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try throwing the opponent while they block!";	
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case COUNTER_HIT:
				feedback = "Your opponent interrupted a lot of your attacks!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try only attacking when you have a certain oppening!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case HIGH_HIT:
				feedback = "Your opponent hit you from above many times!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try anti-airing or blocking high!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case KNOCKDOWN:
				feedback = "Your opponent knocked you down a lot!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Avoid getting put into combo situations!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case LOW_HIT:
				feedback = "Your opponent hit you low many times!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try blocking low and reacting to highs!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case MEATY:
				feedback = "Your opponent hit you while you were rising from the ground!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try using invincible reversals to beat these attacks!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case PROJECTILE_HIT:
				feedback = "Your opponent hit you with a lot of projectiles!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Walk and block your way in until you can safely jump in and attack!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case PUNISH:
				feedback = "Your opponent punished you a lot!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try using safer, unpunishable attacks!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case REVERSAL:
				feedback = "Your opponent found a lot of opponenings in your attacks!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try baiting out reversals by blocking after staggers!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case SPECIAL_ENDER:
				feedback = "Your opponent ended a lot of combos in special moves!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try predicting the setup that follows each special move!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case THROW:
				feedback = "Your opponent threw you a lot!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try jumping straight upwards if you think your opponent will throw you!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			case TOTAL:
				break;
			case WHIFF:
				feedback = "Your opponent whiffed a lot of attacks!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 500);
				feedback = "Try punishing these whiff attacks while they stick out!";
				GraphicManager.getInstance().drawString(feedback, GameSetting.STAGE_WIDTH / 2 - feedback.length() * 5 - 30, 532);
				break;
			default:
				break;
			}
		}
	}
	
	private Success findHighestSuccess(HashMap<Success, Integer> successes) {
		
		Map.Entry<Success, Integer> maxEntry = null;
		for(Map.Entry<Success, Integer> entry : successes.entrySet()) {
			if(maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
				maxEntry = entry;
			}
		}
		
		return maxEntry.getKey();
	}
	
}
