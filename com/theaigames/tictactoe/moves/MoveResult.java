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

package com.theaigames.tictactoe.moves;

import com.theaigames.tictactoe.field.Field;
import com.theaigames.tictactoe.player.Player;

public class MoveResult {
	private String mOldFieldPresentationString, mNewFieldPresentationString;
	private int mMoveNumber = 0;
	private Player mPlayer;
	private Move mMove;

	public MoveResult(Player player, Move move, String oldFieldPresentationString, Field newField) {
	    mPlayer = player;
	    mMove = move;
	    mOldFieldPresentationString = oldFieldPresentationString;
	    mNewFieldPresentationString = newField.toPresentationString(mPlayer.getId(), false);
	}
	
	public String getOldFieldPresentationString() {
	    return mOldFieldPresentationString;
	}
	
	public String getNewFieldPresentationString() {
        return mNewFieldPresentationString;
    }

	public Player getPlayer() {
		return mPlayer;
	}
	
	public Move getMove() {
	    return mMove;
	}
	
	public void setMoveNumber(int moveNumber) {
		mMoveNumber = moveNumber;
	}
	
	public int getMoveNumber() {
		return mMoveNumber;
	}
}
