package com.example.brandon.neotokyo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Random;


public class TbmpGameActivity extends ActionBarActivity {

    Dice[] dice = new Dice[6];

    GameState turnData;

    int curP;   //index of current player
    int tokyoP; //index of player in tokyo
    boolean isTokyoHit; //True is tokyo was attacked this turn


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tbmp_game);
        /*for(int i = 0; i < 6; i++){
            dice[i] = -1;
        }*/

        turnData = new GameState(3); //TODO: replace with player count
        curP = 0;
        tokyoP = -1; //no one in tokyo yet
        isTokyoHit = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tbmp_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*public void rollDice(View view){
        TextView[] t = new TextView[6];
        t[0] = (TextView)findViewById(R.id.die0);
        t[1] = (TextView)findViewById(R.id.die1);
        t[2] = (TextView)findViewById(R.id.die2);
        t[3] = (TextView)findViewById(R.id.die3);
        t[4] = (TextView)findViewById(R.id.die4);
        t[5] = (TextView)findViewById(R.id.die5);

        for (int i = 0; i < 6; i++){

            dice[i].roll();
            t[i].setText(dice[i].image);
        }
    }

    /*public void resolveDice(View view){
        int numHearts = 0;
        int numEnergy = 0;
        int numClaws = 0;
        int numOf1 = 0;
        int numOf2 = 0;
        int numOf3 = 0;
        int vp = 0;

        for (int i = 0; i < 6; i++){
            if (dice[i].getValue() == 0){ //Energy
                numEnergy++;
            }
            else if (dice[i].getValue() == 1){
                numOf1++;
            }
            else if (dice[i].getValue() == 2){
                numOf2++;
            }
            else if (dice[i].getValue() == 3){
                numOf3++;
            }
            else if (dice[i].getValue() == 4){ //Claw
                numClaws++;
            }
            else if (dice[i].getValue() == 5){ //Heart
                numHearts++;
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
        turnData.player.get(curP).updateVictoryPoint(vp);
        turnData.player.get(curP).updateEnergy(numEnergy);
        if(curP != tokyoP) {
            turnData.player.get(curP).updateHealth(numHearts);
        }

        //attack another player or take tokyo
        if(numClaws > 0){
            if(tokyoP < 0){
                tokyoP = curP;
            }
            else if(curP != tokyoP){ //current player not in tokyo
                turnData.player.get(tokyoP).takeDamage(numClaws);
            }
            else { //current player is in tokyo
                for(int i = 0; i < turnData.player.size(); i++){
                    if(tokyoP != i){
                        turnData.player.get(i).takeDamage(numClaws);
                    }
                }
            }
        }

        updateStats();
    }

    public void endTurn(View view){
        curP++;
        if (curP >= turnData.player.size()){
            curP = 0;
        }

        byte[] person = turnData.persist();
        turnData.unpersist(person);

        updateStats();
    }

    /*public void updateStats(){
        Player tempPlayer = turnData.player.get(0);
        ((TextView)findViewById(R.id.heart0)).setText(Integer.toString(tempPlayer.getHealth()));
        ((TextView)findViewById(R.id.vp0)).setText(Integer.toString(tempPlayer.getVictoryPoint()));
        ((TextView)findViewById(R.id.energy0)).setText(Integer.toString(tempPlayer.getEnergy()));

        tempPlayer = turnData.player.get(1);
        ((TextView)findViewById(R.id.heart1)).setText(Integer.toString(tempPlayer.getHealth()));
        ((TextView)findViewById(R.id.vp1)).setText(Integer.toString(tempPlayer.getVictoryPoint()));
        ((TextView)findViewById(R.id.energy1)).setText(Integer.toString(tempPlayer.getEnergy()));

        tempPlayer = turnData.player.get(2);
        ((TextView)findViewById(R.id.heart2)).setText(Integer.toString(tempPlayer.getHealth()));
        ((TextView)findViewById(R.id.vp2)).setText(Integer.toString(tempPlayer.getVictoryPoint()));
        ((TextView)findViewById(R.id.energy2)).setText(Integer.toString(tempPlayer.getEnergy()));

        if(tokyoP == 0){
            ((TextView)findViewById(R.id.inTokyo0)).setText("Yes");
            ((TextView)findViewById(R.id.inTokyo1)).setText("No");
            ((TextView)findViewById(R.id.inTokyo2)).setText("No");
        }
        else if(tokyoP == 1){
            ((TextView)findViewById(R.id.inTokyo0)).setText("No");
            ((TextView)findViewById(R.id.inTokyo1)).setText("Yes");
            ((TextView)findViewById(R.id.inTokyo2)).setText("No");
        }
        else if(tokyoP == 2){
            ((TextView)findViewById(R.id.inTokyo0)).setText("No");
            ((TextView)findViewById(R.id.inTokyo1)).setText("No");
            ((TextView)findViewById(R.id.inTokyo2)).setText("Yes");
        }

        // TODO: Fix this
        //for (int i = 0; i < turnData.player.size(); i++){
        //    Player tempPlayer = turnData.player.get(i);
        //    ((TextView)findViewById(R.id.heart0)).setText(Integer.toString(tempPlayer.getHealth()));
        //    ((TextView)findViewById(R.id.vp0)).setText(Integer.toString(tempPlayer.getVictoryPoint()));
        //    ((TextView)findViewById(R.id.energy0)).setText(Integer.toString(tempPlayer.getEnergy()));
        //}

    }*/
}
