package goController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardStateTracker {

	List<Stone[][]> states;
	
	public BoardStateTracker() {
		states  = new ArrayList<Stone[][]>();
	}
	
	public BoardStateTracker(BoardStateTracker tracker)
	{
		states = new ArrayList<Stone[][]>();
		for(Stone[][] state : tracker.states)
		{
			states.add(deepCopyState(state));
		}
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
	
	public boolean isSameAsPrevious(Stone[][] currentState) {
		boolean result = false;
		for(Stone[][] state: states)
		{
			if(Arrays.deepEquals(currentState, state))
			{
				//we have found the identical state, no point continuing the loop
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	public List<Stone[][]> getStates() {
		return states;
	}
	
	/**
	 * Deep copy the current state
	 * @param currentState - the state to be added
	 */
	public static Stone[][] deepCopyState(Stone[][] currentState) {

		Stone[][] currentStateCopy = new Stone[currentState.length][currentState[0].length];
		for(int i = 0; i < currentState.length; i++){
			for(int j = 0; j < currentState[i].length; j++)
			{
				currentStateCopy[i][j] = new Stone(currentState[i][j]);
			}
		}
		
		return currentStateCopy;
	}
}
