/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.itidez.plugins.derpdisplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_5_R2.IScoreboardCriteria;
import net.minecraft.server.v1_5_R2.Packet;
import net.minecraft.server.v1_5_R2.Packet206SetScoreboardObjective;
import net.minecraft.server.v1_5_R2.Packet207SetScoreboardScore;
import net.minecraft.server.v1_5_R2.Packet208SetScoreboardDisplayObjective;
import net.minecraft.server.v1_5_R2.Scoreboard;
import net.minecraft.server.v1_5_R2.ScoreboardBaseCriteria;
import net.minecraft.server.v1_5_R2.ScoreboardObjective;
import net.minecraft.server.v1_5_R2.ScoreboardScore;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author iTidez
 */
public class ScoreboardManager {
  
	/**
	 * Here be dragons! I have done some testing, need to do more.
	 *  
	 * No comments for you. This was hard to code so it will be hard to read.
	 * :P i'll add comments when i get time.
	 * 
	 */
 
	private Map<String, Scoreboard> scoreBoards = new HashMap<String, Scoreboard>();
	private Map<String, Boolean> sent = new HashMap<String, Boolean>();
 
	public void createScoreboard(String name){
		scoreBoards.put(name, new Scoreboard());
	}
 
	public void createObjective(String name, String obj){
		if(scoreBoards.containsKey(name)){
			//scoreBoards.get(name).a(obj, new ScoreboardBaseObjective(obj));
                        scoreBoards.get(name).registerObjective(obj, new ScoreboardBaseCriteria(obj));
		}
	}
 
	public void setObjectiveItem(String name, String obj, String itemName, int value){
		if(scoreBoards.containsKey(name)){
                        ScoreboardScore scoreItem = scoreBoards.get(name).getPlayerScoreForObjective(itemName, scoreBoards.get(name).getObjective(obj));
                        scoreItem.setScore(value);
		}
	}
 
	public void removeScoreboard(String name){
		if(scoreBoards.containsKey(name)){
			scoreBoards.remove(name);
		}
	}
 
	public void removeObjective(String name, String obj){
		if(scoreBoards.containsKey(name)){
			//scoreBoards.get(name).k(scoreBoards.get(name).b(obj));
                        scoreBoards.get(name).unregisterObjective(scoreBoards.get(name).getObjective(obj));
		}
	}
 
	public void removeObjectiveItem(String name, String obj, String itemName){
		if(scoreBoards.containsKey(name)){
			//scoreBoards.get(name).c(itemName);
                        scoreBoards.get(name).resetPlayerScores(itemName);
		}
	}
 
	@SuppressWarnings("unchecked")
	private void sendScoreboardData(Player player, String scoreboard, int flag){
		//Collection<ScoreboardObjective> data = (Collection<ScoreboardObjective>) scoreBoards.get(scoreboard).c();
                Collection<ScoreboardObjective> data = (Collection<ScoreboardObjective>) scoreBoards.get(scoreboard).getScores();
		List<Packet206SetScoreboardObjective> objectives = new ArrayList<Packet206SetScoreboardObjective>();
 
		List<ScoreboardObjective> tempObjectives = new ArrayList<ScoreboardObjective>();
 
		for(ScoreboardObjective current : data){
			if(!tempObjectives.contains(current)){
				tempObjectives.add(current);
				objectives.add(new Packet206SetScoreboardObjective(current, flag));
			}
		}
 
		for(Packet206SetScoreboardObjective current : objectives){
			sendPacket(player, current);
		}
 
		sendAllItems(player, scoreboard, flag);
	}
 
	public void sendScoreboardData(Player player, String scoreboard){
		if(sent.containsKey(player.getName()) && sent.get(player.getName()))
			sendScoreboardData(player, scoreboard, 1);
		sendScoreboardData(player, scoreboard, 0);
		sent.put(player.getName(), true);
	}
 
	public void resetScoreboardData(Player player, String scoreboard){
		if(sent.containsKey(player.getName()) && sent.get(player.getName())){
			sendScoreboardData(player, scoreboard, 1);
			sent.put(player.getName(), false);
		}
	}
 
 
	public void displayObjective(Player player, String scoreboard, String obj){
		//ScoreboardObjective objective = scoreBoards.get(scoreboard).b(obj);
                ScoreboardObjective objective = scoreBoards.get(scoreboard).getObjective(obj);
		sendPacket(player, new Packet208SetScoreboardDisplayObjective(1, objective));
	}
 
	@SuppressWarnings("unchecked")
	public void sendAllItems(Player player, String scoreboard, int flag){
 
		List<Packet207SetScoreboardScore> objectivesItems = new ArrayList<Packet207SetScoreboardScore>();
		for(ScoreboardScore current : (ArrayList<ScoreboardScore>) scoreBoards.get(scoreboard).getScores())
			objectivesItems.add(new Packet207SetScoreboardScore(current, flag));
 
		for(Packet207SetScoreboardScore current : objectivesItems){
			sendPacket(player, current);
		}
	}
 
    private void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}