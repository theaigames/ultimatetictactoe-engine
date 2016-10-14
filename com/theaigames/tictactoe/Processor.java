/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package com.theaigames.tictactoe;

import java.util.ArrayList;
import java.util.List;

import com.theaigames.game.player.AbstractPlayer;
import com.theaigames.tictactoe.field.Field;
import com.theaigames.tictactoe.moves.Move;
import com.theaigames.tictactoe.moves.MoveResult;
import com.theaigames.tictactoe.player.Player;
import com.theaigames.game.GameHandler;

public class Processor implements GameHandler {
	
	private int mMoveNumber = 1;
	private int mRoundNumber = -1;
	private List<Player> mPlayers;
	private List<Move> mMoves;
	private List<MoveResult> mMoveResults;
	private Field mField;
	private int mGameOverByPlayerErrorPlayerId = 0;

	public Processor(List<Player> players, Field field) {
		mPlayers = players;
		mField = field;
		mMoves = new ArrayList<>();
		mMoveResults = new ArrayList<>();
	}

	@Override
	public void playRound(int roundNumber) {
	    System.out.println(String.format("playing round %d", roundNumber));
	    mRoundNumber = roundNumber;
		for (Player player : mPlayers) {
			if (!isGameOver()) {
				player.sendUpdate("round", roundNumber);
				player.sendUpdate("move", mMoveNumber);
				player.sendUpdate("field", mField.toString());
				player.sendUpdate("macroboard", mField.macroboardToString());
				String response = player.requestMove("move");
				if (!parseResponse(response, player)) {
					response = player.requestMove("move");
					if (!parseResponse(response, player)) {
					    mGameOverByPlayerErrorPlayerId = player.getId(); /* Too many errors, other player wins */
					}
				}
				mMoveNumber++;
			}
		}
	}
	
	/**
	 * Parses player response and inserts disc in field
	 * @return : true if valid move, otherwise false
	 */
	private Boolean parseResponse(String r, Player player) {
		String[] parts = r.split(" ");
		String oldFieldPresentationString = mField.toPresentationString(player.getId(), true);
		if (parts[0].equals("place_move")) {
		    try {
    			int column = (int) Double.parseDouble(parts[1]);
    			int row = (int) Double.parseDouble(parts[2]);
    			
    			if (mField.addMove(column, row, player.getId())) {
                    recordMove(player, oldFieldPresentationString);
                    return true;
                } else {
                    player.getBot().outputEngineWarning(mField.getLastError());
                }
		    } catch (Exception e) {
		        createParseError(player, r);
		    }
		} else {
		    createParseError(player, r);
		}
		recordMove(player, oldFieldPresentationString);
		return false;
	}
	
	private void createParseError(Player player, String input) {
	    mField.setLastError("Error: failed to parse input");
        player.getBot().outputEngineWarning(String.format("Failed to parse input '%s'", input));
	}
	
	private void recordMove(Player player, String oldFieldPresentationString) {
		Move move = new Move(player);
		move.setMove(mField.getLastX(), mField.getLastY());
		move.setIllegalMove(mField.getLastError());
		mMoves.add(move);
		
		MoveResult moveResult = new MoveResult(player, move, oldFieldPresentationString, mField);
		moveResult.setMoveNumber(mMoveNumber);
		mMoveResults.add(moveResult);
	}
	
	@Override
	public int getRoundNumber() {
		return this.mRoundNumber;
	}

	@Override
	public AbstractPlayer getWinner() {
		int winner = mField.getWinner();
		if (mGameOverByPlayerErrorPlayerId > 0) { /* Game over due to too many player errors. Look up the other player, which became the winner */
			for (Player player : mPlayers) {
				if (player.getId() != mGameOverByPlayerErrorPlayerId) {
					return player;
				}
			}
		}
		if (winner != 0) {
			for (Player player : mPlayers) {
				if (player.getId() == winner) {
					return player;
				}
			}
		}
		return null;
	}

	@Override
	public String getPlayedGame() {
		return "";
	}


	/**
	 * Returns a List of Moves played in this game
	 * @return : List with Move objects
	 */
	public List<Move> getMoves() {
		return mMoves;
	}

	public Field getField() {
		return mField;
	}

	@Override
	public boolean isGameOver() {
		return (!mField.isMoveAvailable() || getWinner() != null);
	}
}