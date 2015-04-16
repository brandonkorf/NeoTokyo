package com.example.brandon.neotokyo;

import android.util.Log;
import android.view.View;

import com.example.brandon.neotokyo.Player;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Brandon on 4/5/2015.
 *
 * GameState Class
 * Passes and holds player information
 */
public class GameState {

    public static final String TAG = "EBGameState";
    ArrayList<Player> player;
    Dice[] dice = new Dice[6];
    int curP;   //index of current player
    int tokyoP; //index of player in tokyo
    boolean isTokyoHit; //True is tokyo was attacked this turn


    public GameState(){
        player = new ArrayList<>();
        for (int i = 0; i < 2; i++){ //default to 2 players
            player.add(new Player());
        }
    }

    public GameState(int numPlayers) {
        player = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++){
            player.add(new Player());
        }
    }

    public void resolveDice(View view){
        int numHearts = 0;
        int numEnergy = 0;
        int numClaws = 0;
        int numOf1 = 0;
        int numOf2 = 0;
        int numOf3 = 0;
        int vp = 0;

        for (int i = 0; i < 6; i++){
            switch (dice[i].getValue()) {
                case 0: numEnergy++;
                    break;
                case 1:  numOf1++;
                    break;
                case 2:  numOf2++;
                    break;
                case 3:  numOf3++;
                    break;
                case 4:  numClaws++;
                    break;
                case 5:  numHearts++;
                    break;
                default:
                    break;
            }
        }

        if(numOf1 >= 3){
            numOf1 -= 3;
            vp = vp + 1 + numOf1;
        }
        if(numOf2 >= 3){
            numOf2 -= 3;
            vp = vp + 2 + numOf2;
        }
        if(numOf3 >= 3){
            numOf3 -= 3;
            vp = vp + 3 + numOf3;
        }

        //update current player's stats
        player.get(curP).updateVictoryPoint(vp);
        player.get(curP).updateEnergy(numEnergy);
        if(curP != tokyoP) {
            player.get(curP).updateHealth(numHearts);
        }

        //attack another player or take tokyo
        if(numClaws > 0){
            if(tokyoP < 0){
                tokyoP = curP;
            }
            else if(curP != tokyoP){ //current player not in tokyo
                player.get(tokyoP).takeDamage(numClaws);
            }
            else { //current player is in tokyo
                for(int i = 0; i < player.size(); i++){
                    if(tokyoP != i){
                        player.get(i).takeDamage(numClaws);
                    }
                }
            }
        }
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();

        try {
            for(int i = 0; i < player.size(); i++){
                JSONObject tempVal = new JSONObject();
                tempVal.put("heart",player.get(i).getHealth());
                tempVal.put("vp", player.get(i).getVictoryPoint());
                tempVal.put("energy", player.get(i).getEnergy());
                retVal.put("Player"+Integer.toString(i),tempVal);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String st = retVal.toString();

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of GameState.
    static public GameState unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new GameState();
        }

        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }

        Log.d(TAG, "====UNPERSIST \n" + st);

        GameState retVal = new GameState();

        try {
            JSONObject obj = new JSONObject(st);

            for (int i = 0; i < obj.length(); i++){
                String playerName = "Player"+Integer.toString(i);
                if (obj.has(playerName)){
                    JSONObject playerObj = obj.getJSONObject(playerName);
                    if(playerObj.has("heart")){
                        retVal.player.get(i).updateHealth(obj.getInt("heart"));
                    }
                    if(playerObj.has("vp")){
                        retVal.player.get(i).updateHealth(obj.getInt("vp"));
                    }
                    if(playerObj.has("energy")){
                        retVal.player.get(i).updateHealth(obj.getInt("energy"));
                    }
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return retVal;
    }
}
