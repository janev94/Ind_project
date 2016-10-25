package goController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GoBoard {

	private int width;
	private int height;
	private StoneOwner playerToMove;
	private BoardParser parser;
	
	private Stone[][] stonePositions;
	
	public GoBoard(int width, int height) {
		this.width = width;
		this.height = height;
		stonePositions = new Stone[width][height];
		playerToMove = StoneOwner.BLACK;
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++)
			{
				stonePositions[x][y] = new Stone(StoneOwner.EMPTY);
			}
		}
	}
	
	
	public void setStonePositions(Stone[][] stonePositions) {
		this.stonePositions = stonePositions;
	}
	
	/**
	 * Reports whether a move at position (i, j) is legal. Note i and j are used
	 * as if accessing a matrix A with A[i][j]. I.e. I represents the row number
	 * and j represents the column, indexed from 0.
	 * @param row
	 * @param column
	 * @param playerPlacing
	 * @return
	 */
	public boolean isLegalMove(int row, int column, StoneOwner playerPlacing)
	{
		if(stonePositions[row][column].getOwner() != StoneOwner.EMPTY)
			return false;
		if(playerToMove != playerPlacing)
			return false;
		if(!isInBounds(row, column))
			return false;
		//TODO: check for self capture
		
		Set<Stone> fiendlyGroup = getGroup(row, column, playerPlacing);
		
		StoneOwner opposingPlayer = playerPlacing.getOpposingColour();

		Set<Set<Stone>> enemyGroups = new HashSet<>();
		
		enemyGroups.add(getGroup(row + 1, column, opposingPlayer));
		enemyGroups.add(getGroup(row - 1, column, opposingPlayer));
		enemyGroups.add(getGroup(row, column + 1, opposingPlayer));
		enemyGroups.add(getGroup(row, column - 1, opposingPlayer));
		
		enemyGroups.removeIf(x -> x == null);

		if(enemyGroups.size() > 0)
		{
			for(Set<Stone> group : enemyGroups)
			{
				//count liberties, if liberties = 0, remove group from board
				//update capture flag
			}
		}

		//If capture flag is not set, check whether
		//friendly group has no liberties => self capture
		
		//TODO: check for same state
		
		
		return true;
	}
	
	/**
	 * Checks whether there's an enemy group at board position (row, column)
	 * @param row
	 * @param column
	 * @param enemyColour
	 * @return null if there is no enemy group at that position,
	 *  otherwise the group containing position (row, column)
	 */
	private Set<Stone> checkEnemyGroupAt(int row, int column, StoneOwner enemyColour)
	{
		Set<Stone> result = null;
		if(isInBounds(row, column)) {
			if(stonePositions[row][column].getOwner() == enemyColour) {
				result = getGroup(row, column, enemyColour);
			}
		}
		
		return result;
	}
	
	private Set<Stone> getGroup(int row, int column, StoneOwner colour)
	{
		Set<Stone> group = new HashSet<>();
		
		visit(row, column, colour, group);
		
		return group;
	}
	
	private void visit(int row, int column, StoneOwner colour, Set<Stone> group) {
		if(isInBounds(row, column)) {
			if(stonePositions[row][column].getOwner() == colour) {
				group.add(stonePositions[row][column]);
			}
			visit(row + 1, column, colour, group);
			visit(row - 1, column, colour, group);
			visit(row, column + 1, colour, group);
			visit(row, column - 1, colour, group);
		}
	}
	
	private boolean isInBounds(int row, int column) {
		return row >= 0 && row < height && column >= 0 && column < width ;
	}


	public void playMove(int x, int y, StoneOwner owner)
	{
		//TODO: fill in move logic
	}
}
