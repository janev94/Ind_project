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
	
	public GoBoard(int height, int width) {
		this.width = width;
		this.height = height;
		
		stonePositions = new Stone[width][height];
		playerToMove = StoneOwner.BLACK;
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++)
			{
				stonePositions[x][y] = new Stone(StoneOwner.EMPTY, x, y);
			}
		}
	}
	
	
	public Stone[][] getStonePositions() {
		return stonePositions;
	}
	
	
	public void setStonePositions(Stone[][] stonePositions) {
		this.stonePositions = stonePositions;
	}

	public StoneOwner getPlayerToMove() {
		return playerToMove;
	}
	
	public void setPlayerToMove(StoneOwner playerToMove) {
		this.playerToMove = playerToMove;
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
		if(!isInBounds(row, column))
			return false;
		if(stonePositions[row][column].getOwner() != StoneOwner.EMPTY)
			return false;
		if(playerToMove != playerPlacing)
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

		boolean enemyCapture = false;
		
		if(enemyGroups.size() > 0)
		{
			for(Set<Stone> group : enemyGroups)
			{
				if( countLiberties(group) == 0) {
					enemyCapture = true;
				}
			}
		}

		if(!enemyCapture) {
			if (countLiberties(fiendlyGroup) == 0)
				return false;
		}
		
		//If capture flag is not set, check whether
		//friendly group has no liberties => self capture
		
		//TODO: check for same state
		
		
		return true;
	}

	/**
	 * given a Stone stone, return all its neighbour positions
	 * @param stone
	 * @return a set of neighbor positions
	 */
	public Set<Stone> getNeighbors(Stone stone)
	{
		Set<Stone> result = new HashSet<>();
		if(isInBounds(stone.getRow() + 1, stone.getCol()))
			result.add(stonePositions[stone.getRow() + 1][stone.getCol()]);
		
		if(isInBounds(stone.getRow() - 1, stone.getCol()))
			result.add(stonePositions[stone.getRow() - 1][stone.getCol()]);
		
		if(isInBounds(stone.getRow(), stone.getCol() + 1))
			result.add(stonePositions[stone.getRow()][stone.getCol() + 1]);
		
		if(isInBounds(stone.getRow(), stone.getCol() - 1))
			result.add(stonePositions[stone.getRow()][stone.getCol() - 1]);
		
		return result;
	}
	
	/**
	 * Returns all the groups formed by the player from the given colour in the whole board
	 * @return
	 */
	public Set<Set<Stone>> getGroups(StoneOwner searchedOwner) {
		Set<Stone> visited = new HashSet<>();
		Set<Set<Stone>> groups = new HashSet<>();
		Set<Set<Stone>> result = iterateBoard(0, 0, visited, groups, searchedOwner);
		
		return result;
	}
	
	
	/**
	 * Find the group where this stone belongs to
	 * @param stone
	 * @return
	 */
	public Set<Stone> findGroup(Stone stone) {
		List<Stone> toVisit = new ArrayList<>();
		Set<Stone> visited = new HashSet<>();
		Set<Stone> group = new HashSet<>();
		
		Set<Stone> result = visit(toVisit, visited, group, stone.getOwner());
		
		return result;
	}
	
	private Set<Stone> visit(List<Stone> toVisit, Set<Stone> visited, Set<Stone> group, StoneOwner searchedOwner)
	{
		if(toVisit.isEmpty())
			return group;
		
		Stone current = toVisit.get(0);
		Set<Stone> toVisitNeighbors = new HashSet<>();
		Set<Stone> neighbors = getNeighbors(current);
		for(Stone neighbor: neighbors)
		{
			if(!visited.contains(neighbor) && neighbor.getOwner() == searchedOwner)
				toVisitNeighbors.add(neighbor);
		}
		
		//update collections
		toVisit.addAll(toVisitNeighbors);
		visited.add(current);
		group.add(current);
		
		return visit(toVisit, visited, group, searchedOwner);
	}
	
	
	public Set<Set<Stone>> iterateBoard(int startHeight, int startWidth, Set<Stone> visited, Set<Set<Stone>> groups, StoneOwner searchedOwner)
	{
		Stone currentStone = stonePositions[startHeight][startWidth];
		Set<Stone> group;
		if(!visited.contains(currentStone) && currentStone.getOwner() == searchedOwner)
			group = findGroup(currentStone);
		else
			group = new HashSet<>();
		
		Stone nextStone = nextStone(currentStone);
		
		if(nextStone != null)
		{
			//update collections
			visited.addAll(group);
			groups.add(group);
			return iterateBoard(nextStone.getCol(), nextStone.getRow(), visited, groups, searchedOwner);
		}
		else{
			groups.add(group);
			return groups;
		}
	}
	
	private Stone nextStone(Stone current)
	{
		if(current.getCol() < width - 2) return stonePositions[current.getCol() + 1][current.getRow()];
		if(current.getRow() < height - 2) return stonePositions[current.getCol()][current.getRow() + 1];
		else return null;
	}
	
	
	private int countLiberties(Set<Stone> group) {
		Set<Stone> libertyPositions = new HashSet<>();
		for(Stone stone : group) {
			if(isLiberty(stonePositions[stone.getRow() + 1][stone.getCol()]))
				libertyPositions.add(stonePositions[stone.getRow() + 1][stone.getCol()]);
			if(isLiberty(stonePositions[stone.getRow() - 1][stone.getCol()]))
				libertyPositions.add(stonePositions[stone.getRow() - 1][stone.getCol()]);
			if(isLiberty(stonePositions[stone.getRow()][stone.getCol() + 1]))
				libertyPositions.add(stonePositions[stone.getRow()][stone.getCol() + 1]);
			if(isLiberty(stonePositions[stone.getRow()][stone.getCol() - 1]))
				libertyPositions.add(stonePositions[stone.getRow()][stone.getCol() - 1]);
		}
			
		return libertyPositions.size();
	}


	private boolean isLiberty(Stone stone) {
		if(!isInBounds(stone.getRow(), stone.getCol()))
			return false;
		return stone.getOwner() == StoneOwner.EMPTY;
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
		
		visit_old(row, column, colour, group);
		
		return group;
	}
	
	private void visit_old(int row, int column, StoneOwner colour, Set<Stone> group) {
		if(isInBounds(row, column)) {
			if(stonePositions[row][column].getOwner() == StoneOwner.EMPTY)
				return;
			if(stonePositions[row][column].getOwner() == colour) {
				group.add(stonePositions[row][column]);
			}
			visit_old(row + 1, column, colour, group);
			visit_old(row - 1, column, colour, group);
			visit_old(row, column + 1, colour, group);
			visit_old(row, column - 1, colour, group);
		}
	}
	
	public boolean isInBounds(int row, int column) {
		return row >= 0 && row < height && column >= 0 && column < width ;
	}


	public void playMove(int x, int y, StoneOwner owner)
	{
		//TODO: fill in move logic
	}
}
