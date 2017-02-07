package goController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO: relate DuplicateStateTracker and BoardStaetTracker by inheritance

public class DuplicateStateTracker {


	List<Stone[][]> states;
	List<List<StoneOwner>> playersToMove;
	
	public DuplicateStateTracker() {
		states  = new ArrayList<Stone[][]>();
		playersToMove = new ArrayList<>(2);
	}
	

	public boolean addState(Stone[][] currentState, StoneOwner currentPlayer) {
		int stateIdx = isSameAsPrevious(currentState);
		//state was found
		if(stateIdx != -1)
		{
			
			if(playersToMove.get(stateIdx).contains(currentPlayer))
			{
				return false;
			}
			
			//This state exists but opponent has opening
			playersToMove.get(stateIdx).add(currentPlayer);
			return true;
		}
		
		Stone[][] currentStateCopy = new Stone[currentState.length][currentState[0].length];
		for(int i = 0; i < currentState.length; i++){
			for(int j = 0; j < currentState[i].length; j++)
			{
				currentStateCopy[i][j] = new Stone(currentState[i][j]);
			}
		}
		
		states.add(currentStateCopy);
		
		List<StoneOwner> playerToMove = new ArrayList<>(2);
		playerToMove.add(currentPlayer);
		playersToMove.add(playerToMove);
		
		return true;
	}
	
	
	private int isSameAsPrevious(Stone[][] currentState) {
		
		int idx = -1;
		for(int i=0; i < states.size(); i++)
		{
			if(Arrays.deepEquals(currentState, states.get(i)))
			{
				//we have found the identical state, no point continuing the loop
				idx = i;
				break;
			}
		}
		
		return idx;
	}
	
	
	public List<Stone[][]> getStates() {
		return states;
	}
	
}
