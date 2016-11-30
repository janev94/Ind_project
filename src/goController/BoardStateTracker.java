package goController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardStateTracker {

	List<Stone[][]> states;
	
	public BoardStateTracker() {
		states  = new ArrayList<Stone[][]>();
	}
	

	/**
	 * Function that adds the state currentState only if currentState is not equal
	 * to any of the previous states
	 * @param currentState - the state to be added
	 */
	public void addState(Stone[][] currentState) {
		if(isSameAsPrevious(currentState))
			return;

		Stone[][] currentStateCopy = new Stone[currentState.length][currentState[0].length];
		for(int i = 0; i < currentState.length; i++){
			for(int j = 0; j < currentState[i].length; j++)
			{
				currentStateCopy[i][j] = new Stone(currentState[i][j]);
			}
		}
		
		states.add(currentStateCopy);
	}
	
	public boolean isSameAsPrevious(Stone[][] currentState){
		for(Stone[][] state: states)
		{
			if(Arrays.deepEquals(currentState, state))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public List<Stone[][]> getStates() {
		return states;
	}
}
