package goController;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class GoBoard {

	private int width;
	private int height;
	private StoneOwner playerToMove;
	private GoGoal goal;
	private BoardParser parser;
	private int stonesOnBoard;
	
	private Stone[][] stonePositions;
	private BoardStateTracker previousStates;
	public Stone goalStone;

	public BoardStateTracker getPreviousStates() {
		return previousStates;
	}
	
	public GoBoard(int height, int width) {
		this.width = width;
		this.height = height;
		this.stonesOnBoard = 0;
		
		stonePositions = new Stone[width][height];
		playerToMove = StoneOwner.BLACK;
		previousStates = new BoardStateTracker();
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++)
			{
				stonePositions[x][y] = new Stone(StoneOwner.EMPTY, x, y);
			}
		}
	}
	
	public GoBoard(GoBoard board) {
		this.width = board.width;
		this.height = board.height;
		this.goal = board.goal;

		this.stonePositions = BoardStateTracker.deepCopyState(board.getStonePositions());
		this.playerToMove = board.playerToMove;
		this.previousStates = new BoardStateTracker(board.previousStates);
		
	}
	
	
	public Stone[][] getStonePositions() {
		return stonePositions;
	}
	
	
	public void setStonePositions(Stone[][] stonePositions) {
		this.stonePositions = stonePositions;
		stonesOnBoard = 0;
		for(Stone[] row: stonePositions)
			for(Stone stone: row)
				if(stone.getOwner() != StoneOwner.EMPTY)
					stonesOnBoard++;
	}

	public StoneOwner getPlayerToMove() {
		return playerToMove;
	}
	
	public void setPlayerToMove(StoneOwner playerToMove) {
		this.playerToMove = playerToMove;
	}
	
	public void setGoal(GoGoal goal) {
		this.goal = goal;
		goalStone = new Stone(StoneOwner.EMPTY, goal.getHeight(), goal.getWidth());
	}
	
	public boolean isGoalReached() {
		boolean result = false;
		
		if(goal.getGoal() == GoGoalEnum.KILL)
		{
			result = stonePositions[goal.getHeight()][goal.getWidth()]
					.getOwner() == StoneOwner.EMPTY;
		}
		
		return result;
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
	public LegalMoveObj isLegalMove(int row, int column, StoneOwner playerPlacing, boolean verbose)
	{
		//create Object to keep track of move's validity and state
		LegalMoveObj result = new LegalMoveObj();
		
		//copy current board state
		Stone[][] temporaryState = BoardStateTracker.deepCopyState(stonePositions);
		
		if(!isInBounds(row, column)) {
			if(verbose)
				System.out.println("Out of bounds");
			result.setLegal(false);
			return result;
		}
		
		if(temporaryState[row][column].getOwner() != StoneOwner.EMPTY){
			if(verbose)
				System.out.println("Move on non-empty");
			result.setLegal(false);
			return result;
		}
		if(playerToMove != playerPlacing){
			if(verbose)
				System.out.println("Wrong color player placing");
			result.setLegal(false);
			return result;
		}
		
		temporaryState[row][column].setOwner(playerPlacing);
		//TODO: check for self capture
		if(isSelfCapture(playerPlacing, temporaryState))
		{
			if(verbose)
				System.out.println("Self capture");
			//temporaryState[row][column].setOwner(StoneOwner.EMPTY);
			result.setLegal(false);
			return result;
		}
		
		//TODO: check for KO
		
		//remove all captured stones, decrease number of stones on board by captured amount
		Set<Stone> capturedPieces = getCapturedPieces(playerPlacing.getOpposingColour(), temporaryState);
		List<StoneOwner> previousOwners = new ArrayList<>(capturedPieces.size());
		for(Stone stone: capturedPieces)
		{
			previousOwners.add(stone.getOwner());
			stone.setOwner(StoneOwner.EMPTY);
		}
		
		if(previousStates.isSameAsPrevious(temporaryState))
		{
			//TODO: revert state to previous
			int i=0;
			for(Stone stone: capturedPieces)
			{
				stone.setOwner(previousOwners.get(i++));
			}
			
			//change field that was originally moved to to empty
			//stonePositions[row][column].setOwner(StoneOwner.EMPTY);
			
			if(verbose)
				System.out.println("Ko violation");
			
			result.setLegal(false);
			return result;
		}
		
		//add current position to state tracker
		//TODO: isolate in state Tracker?
		
		//do not add state to tracker yet, only add when move is made
		//previousStates.addState(temporaryState);

		result.setStonePositions(temporaryState);
		result.setCapturedPieces(capturedPieces);
		result.setLegal(true);
		
		return result;
	}

	/**
	 * given a Stone stone, return all its neighbour positions
	 * @param stone
	 * @return a set of neighbor positions
	 */
	public Set<Stone> getNeighbors(Stone stone, Stone[][] tempBoard)
	{
		Set<Stone> result = new HashSet<>();
		if(isInBounds(stone.getRow() + 1, stone.getCol()))
			result.add(tempBoard[stone.getRow() + 1][stone.getCol()]);
		
		if(isInBounds(stone.getRow() - 1, stone.getCol()))
			result.add(tempBoard[stone.getRow() - 1][stone.getCol()]);
		
		if(isInBounds(stone.getRow(), stone.getCol() + 1))
			result.add(tempBoard[stone.getRow()][stone.getCol() + 1]);
		
		if(isInBounds(stone.getRow(), stone.getCol() - 1))
			result.add(tempBoard[stone.getRow()][stone.getCol() - 1]);
		
		return result;
	}
	
	/**
	 * Returns all the groups formed by the player from the given colour in the whole board
	 * @return
	 */
	public Set<Set<Stone>> getGroups(StoneOwner searchedOwner, Stone[][] tempBoard) {
		Set<Stone> visited = new HashSet<>();
		Set<Set<Stone>> groups = new HashSet<>();
		Set<Set<Stone>> result = iterateBoard(0, 0, visited, groups, searchedOwner, tempBoard);
		
		return result;
	}
	
	
	/**
	 * Find the group where this stone belongs to
	 * @param stone
	 * @param tempBoard 
	 * @return
	 */
	public Set<Stone> findGroup(Stone stone, Stone[][] tempBoard) {
		List<Stone> toVisit = new ArrayList<>();
		Set<Stone> visited = new HashSet<>();
		Set<Stone> group = new HashSet<>();
		
		toVisit.add(stone);
		
		Set<Stone> result = visit(toVisit, visited, group, stone.getOwner(), tempBoard);
		
		return result;
	}
	
	private Set<Stone> visit(List<Stone> toVisit, Set<Stone> visited, Set<Stone> group
			, StoneOwner searchedOwner, Stone[][] tempBoard)
	{
		if(toVisit.isEmpty())
			return group;
		
		Stone current = toVisit.remove(0);
		visited.add(current);
		Set<Stone> toVisitNeighbors = new HashSet<>();
		Set<Stone> neighbors = getNeighbors(current, tempBoard);
		for(Stone neighbor: neighbors)
		{
			if(!visited.contains(neighbor) && neighbor.getOwner() == searchedOwner && !toVisit.contains(neighbor))
				toVisitNeighbors.add(neighbor);
		}
		
		//update collections
		toVisit.addAll(toVisitNeighbors);
		visited.add(current);
		group.add(current);
		
		return visit(toVisit, visited, group, searchedOwner, tempBoard);
	}
	
	
	public Set<Set<Stone>> iterateBoard(int startHeight, int startWidth, Set<Stone> visited, Set<Set<Stone>> groups
			, StoneOwner searchedOwner, Stone[][] tempBoard)
	{
		Stone currentStone = tempBoard[startHeight][startWidth];
		Set<Stone> group;
		
		if(!visited.contains(currentStone) && currentStone.getOwner() == searchedOwner)
			group = findGroup(currentStone, tempBoard);
		else
			group = new HashSet<>();
		
		Stone nextStone = nextStone(currentStone);
		
		if(nextStone != null)
		{
			//update collections
			visited.addAll(group);
			groups.add(group);
			return iterateBoard(nextStone.getRow(), nextStone.getCol(), visited, groups
					, searchedOwner, tempBoard);
		}
		else{
			groups.add(group);
			return groups;
		}
	}
	
	private Stone nextStone(Stone current)
	{
		if(current.getCol() < width - 1) return stonePositions[current.getRow()][current.getCol() + 1];
		if(current.getRow() < height - 1) return stonePositions[current.getRow() + 1][0];
		else return null;
	}
	
	/**
	 * Reports whether the group has at least one liberty position
	 * @param group - the group to check
	 * @return true if group has a liberty false otherwise
	 */
	private boolean isSurrounded(Set<Stone> group, Stone[][] tempBoard)
	{
		for(Stone s: group)
		{
			Set<Stone> neighbors = getNeighbors(s, tempBoard);
			for(Stone neighbor: neighbors)
				if(isLiberty(neighbor))
					return false;
		}
		return true;
	}
	
	public Set<Stone> getCapturedPieces(StoneOwner owner, Stone[][] tempBoard)
	{
		Set<Set<Stone>> groups = getGroups(owner, tempBoard);
		Set<Stone> capturedPieces = new HashSet<>();
		
		for(Set<Stone> group: groups)
		{
			if(isSurrounded(group, tempBoard))
				capturedPieces.addAll(group);
		}
		
		return capturedPieces;
	}
	
	private boolean isSelfCapture(StoneOwner owner, Stone[][] tempBoard)
	{
		//first check if this moves captures any enemy stones
		if(getCapturedPieces(owner.getOpposingColour(), tempBoard).size() > 0)
			return false;
		
		//Then check whether it captures any self stones
		return getCapturedPieces(owner, tempBoard).size() != 0;
	}
	
	
	//old code
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
		return stone.getOwner() == StoneOwner.EMPTY || stone.getOwner() == StoneOwner.NUSED;
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
	// end old code
	
	public boolean isInBounds(int row, int column) {
		return row >= 0 && row < height && column >= 0 && column < width ;
	}


	/**
	 * Function that tries playing the move
	 *  at row - row and col - col for player - player placing. Returns true if
	 *  move was played and false otherwise
	 * @param row - the index of the row to play at(starting from 0)
	 * @param column - the index of the col to play at(starting from 0)
	 * @param playerPlacing - the colour of the player to move
	 * 
	 * @return true if move was played false otherwise
	 */
	public boolean playMove(int row, int column, StoneOwner playerPlacing)
	{
		//TODO: fill in move logic
		LegalMoveObj legalMoveObj = isLegalMove(row, column, playerPlacing, false);
		
		if(!legalMoveObj.isLegal())
			return false;
		
		//if move is legal, add temporary state to played states
		previousStates.addState(legalMoveObj.getStonePositions());
		
		//apply the move
		stonePositions = legalMoveObj.getStonePositions();
		return true;
	}
	
	
//	public void playAIMove(StoneOwner playerToMove)
//	{
//		//first find a legal move
//		List<Coordinates> coords = new ArrayList<>();
//		List<Double> moveValues = new ArrayList<>();
//		
//		for(int y=0; y < height; y++) {
//
//			for(int x=0; x < width; x++) {
//				LegalMoveObj option = isLegalMove(y, x, playerToMove, false);
//				if(option.isLegal()) {
//					coords.add(new Coordinates(x, y));
//					moveValues.add((double) option.getCapturedPieces().size());
//				}
//			}
//		}
//		
//		int idx = -1;
//		double maxValue = Double.NEGATIVE_INFINITY;
//		for(int i=0; i < moveValues.size(); i++) {
//			if(maxValue < moveValues.get(i))
//			{
//				maxValue = moveValues.get(i);
//				idx = i;
//			}
//		}
//		
//		if(idx != -1)
//			playMove(coords.get(idx).y, coords.get(idx).x, playerToMove);
//	}

	public void _print_positions()
	{
		for(int y=0; y < height; y++) {
			for(int x=0; x < width; x++) {
				System.out.print(stonePositions[y][x].getOwner() + " ");
			}
			System.out.println();
		}
	}

	DuplicateStateTracker dupCutter;
	int times = 0;
	public int goalReached = 0;
	
	public int win_x;
	public int win_y;
	//w, h
	public double minimaxAiMove(int row, int col, StoneOwner playerToMove, StoneOwner maximizingPlayer, GoBoard currentBoard, int depth)
	{
		
//		if(depth > 30)
//		{
//			//if depth is too big, ignore the rest of the tree
//			if(depth > 31)
//				System.out.println("depth is " + depth);
//			return Double.NEGATIVE_INFINITY;
//		}
		
		times++;
	//	if(times % 100000 == 0){
	//		currentBoard._print_positions();
//			System.out.println("times: " + times + " d " + depth);
	//	}
	
//		if(row == 1 && col == 0)
//		{
//			_print_positions();
//		}
		
		if(row == -1 && col == -1)
		{
			dupCutter = new DuplicateStateTracker();
		}
//		if(row == -1 && col == -1)
//		{
//			tempBoard = new GoBoard(currentBoard);
//			dupCutter = new BoardStateTracker();
//		} else {
//			tempBoard = new GoBoard(currentBoard);
//			tempBoard.playMove(row, col, playerToMove);
//			
//			//change player to move
//			//playerToMove = playerToMove.getOpposingColour();
//			tempBoard.setPlayerToMove(playerToMove.getOpposingColour());
//		}
		
//		if(dupCutter.isSameAsPrevious(tempBoard.stonePositions))
//		{
//			//if move was seen on the board before cut it
//			if(playerToMove == maximizingPlayer)
//				return Double.POSITIVE_INFINITY;
//			else
//				return Double.NEGATIVE_INFINITY;
//		} else {
//			dupCutter.addState(tempBoard.stonePositions);
//		}
		
		//
		//consider "adjacent" all possible moves
		//
		
		//assign a infinite value to a winning move
		if(currentBoard.isGoalReached()){
			goalReached++;
			return Double.POSITIVE_INFINITY;
		}
		
		List<Coordinates> coords = new ArrayList<>();
		//List<Double> moveValues = new ArrayList<>();
		
		for(int y=0; y < height; y++) {
			for(int x=0; x < width; x++) {
				if(!fillingOwnEye(y, x, currentBoard)) {
					LegalMoveObj option = currentBoard.isLegalMove(y, x, playerToMove, false);
					if(option.isLegal()) {
						coords.add(new Coordinates(x, y));
						//moveValues.add((double) option.getCapturedPieces().size());
					}
				}
			}
		}
		
		//TODO: check if white has two eyes
		if(coords.size() == 0) {
			int liberties = 0;
			int r = 0,  c = 0;
			//List<Integer> libertyList = new ArrayList<>();
			
			//there were no legal moves for one of the players,
			//check if any legal move for the opponent, not filling own eyes
			currentBoard.setPlayerToMove(playerToMove.getOpposingColour());
			playerToMove = playerToMove.getOpposingColour();
			for(int y=0; y < height; y++) {
				for(int x=0; x < width; x++) {
					if(currentBoard.stonePositions[y][x].getOwner() == StoneOwner.EMPTY)
					{
						if(!fillingOwnEye(y, x, currentBoard))
						{
							LegalMoveObj obj = currentBoard.isLegalMove(y, x, playerToMove.getOpposingColour(), false);
							if(obj.isLegal())
							{
								coords.add(new Coordinates(x, y));
//								List<Stone> capturedStones = new ArrayList<>(obj.getCapturedPieces());
//								if(capturedStones.contains(goalStone))
//									return Double.POSITIVE_INFINITY; 
							}
							
						}
//						liberties++;
//						r = y;
//						c = x;
					}
				}
			}
			
//			libertyList.add(liberties);
			
//			if(liberties == 1)
//			{
//				System.out.println("only 1 liberty left");
//				currentBoard.setPlayerToMove(playerToMove.getOpposingColour());
//				if(currentBoard.isLegalMove(r, c, playerToMove.getOpposingColour(), false).isLegal())
//				{
//					currentBoard.playMove(r, c, playerToMove.getOpposingColour());
//					if(currentBoard.isGoalReached())
//					{
//						System.out.println(playerToMove + " moves somewhere else on the board.");
//						return Double.POSITIVE_INFINITY;
//					}
//				}
//			}
			
			//no legal moves are left => goal is not met
			if(coords.size() == 0)
				return Double.NEGATIVE_INFINITY;
		}
			
		//make a copy of the current board
		//to perform moves at
		GoBoard tempBoard;
		
		if(playerToMove == maximizingPlayer)
		{
			double bestSoFar = Double.NEGATIVE_INFINITY;
			for(int i=0; i < coords.size(); i++)
			{
				if(coords.get(i).y == 0 && coords.get(i).x == 12 && row == -1 && col == -1)
				{
					System.out.println();
				}
				tempBoard = new GoBoard(currentBoard);
				tempBoard.playMove(coords.get(i).y, coords.get(i).x, playerToMove);
				tempBoard.setPlayerToMove(playerToMove.getOpposingColour());
				
				// if we can record the state for the player, proceed, otherwise
				// state has been examined before

				/* Convert board to a string, to hash */
				StringBuilder boardBuilder = new StringBuilder();
				for(int y=0; y < height; y++) {
					for(int x=0; x < width; x++) {
						char ch = ' ';
						switch(tempBoard.stonePositions[y][x].getOwner())
						{
						case BLACK:
							ch = 'x';
							break;
						case EMPTY:
							ch = '-';
							break;
						case NUSED:
							ch = 'E';
							break;
						case WHITE:
							ch = 'o';
							break;
						}
						boardBuilder.append(ch);
					}
				}
				
				//if(dupCutter.addState(tempBoard.stonePositions, playerToMove)) {
				if(dupCutter.addStringState(boardBuilder.toString(), playerToMove)) {
					//dupCutter.addState(tempBoard.stonePositions);
				
					double value = minimaxAiMove(0, 0, playerToMove.getOpposingColour(),
							maximizingPlayer, tempBoard, depth + 1);
					
					//once value is known update record for the map
					dupCutter.getEvaluatedBoards()
						.get(boardBuilder.toString()).put(playerToMove, value);
					
					if(row == -1)
						System.out.println(coords.get(i).y + ", " + coords.get(i).x + " " + value + " " + times);
					
					bestSoFar = Double.max(value, bestSoFar);
					if(value == Double.POSITIVE_INFINITY && row == -1)
					{
						win_x = coords.get(i).x;
						win_y = coords.get(i).y;
					}
				} 
				else {
					//get the value stored for this board 
					double value = dupCutter.getEvaluatedBoards()
							.get(boardBuilder.toString()).get(playerToMove);
					bestSoFar = Double.max(value, bestSoFar);
				}
					
			}
			
			return bestSoFar;
			
		} else {
			
			double bestSoFar = Double.POSITIVE_INFINITY;
			for(int i=0; i < coords.size(); i++)
			{
				tempBoard = new GoBoard(currentBoard);
				tempBoard.playMove(coords.get(i).y, coords.get(i).x, playerToMove);
				tempBoard.setPlayerToMove(playerToMove.getOpposingColour());
				
				StringBuilder boardBuilder = new StringBuilder();
				for(int y=0; y < height; y++) {
					for(int x=0; x < width; x++) {
						char ch = ' ';
						switch(tempBoard.stonePositions[y][x].getOwner())
						{
						case BLACK:
							ch = 'x';
							break;
						case EMPTY:
							ch = '-';
							break;
						case NUSED:
							ch = 'E';
							break;
						case WHITE:
							ch = 'o';
							break;
						}
						boardBuilder.append(ch);
					}
				}
				
				if(dupCutter.addStringState(boardBuilder.toString(), playerToMove)) {
				//if(dupCutter.addState(tempBoard.stonePositions, playerToMove))
				//{
//					dupCutter.addState(tempBoard.stonePositions);
					
					double value = minimaxAiMove(0, 0, playerToMove.getOpposingColour(),
							maximizingPlayer, tempBoard, depth + 1);
					
					//once value is known update record for the map
					dupCutter.getEvaluatedBoards()
						.get(boardBuilder.toString()).put(playerToMove, value);
					
					bestSoFar = Double.min(value, bestSoFar);
//					if(bestSoFar == Double.NEGATIVE_INFINITY)
//					{
//						win_x = coords.get(i).x;
//						win_y = coords.get(i).y;
//					}
				}
				else {
					//get the value stored for this board 
					double value = dupCutter.getEvaluatedBoards()
							.get(boardBuilder.toString()).get(playerToMove);
					bestSoFar = Double.min(value, bestSoFar);
				}
			}
			
			return bestSoFar;
		}
		

	}
	
	
	/**
	 * Function, that uses alpha beta pruning, built on top of minimax function
	 * @param row
	 * @param col
	 * @param playerToMove - the to be evaluated for
	 * @param maximizingPlayer - the player, trying to capture(KILL problems currently supported)
	 * @param currentBoard - the board on which evaluations are performed
	 * @param depth - the current depth in the search tree
	 * @return Positive number if group from goal can be captured
	 */
	public double alphaBeta(int row, int col, StoneOwner playerToMove, StoneOwner maximizingPlayer, GoBoard currentBoard, int depth, double alpha, double beta)
	{

		//TODO: check for depth
		//depth must be even
		if(depth >= 100)
		{
//			//TODO: implement static board evaluator f-n
//			if(playerToMove == maximizingPlayer)
//				return Double.NEGATIVE_INFINITY;
//			else
//				return Double.POSITIVE_INFINITY;
			if(playerToMove == maximizingPlayer)
				return Double.NEGATIVE_INFINITY;
			return -1;
		}
		
		times++;
		if(times == 40)
		{
			System.out.println();
		}
//		System.out.println("times: " + times + " d " + depth);

		//initial call, initialize a new dupCutter
		if(row == -1 && col == -1)
		{
			dupCutter = new DuplicateStateTracker();
		}
		
		//assign a infinite value to a winning move
		if(currentBoard.isGoalReached()){
			goalReached++;
			return Double.POSITIVE_INFINITY;
		}
		
		List<Coordinates> coords = new ArrayList<>();
		List<SimpleEntry<Coordinates, LegalMoveObj>> moves = new ArrayList<>();
		
		for(int y=0; y < height; y++) {
			for(int x=0; x < width; x++) {
				if(!fillingOwnEye(y, x, currentBoard)) {
					LegalMoveObj option = currentBoard.isLegalMove(y, x, playerToMove, false);
					
					//Consider move as legal if it does not fill own eye and is legal 
					if(option.isLegal()) {
						coords.add(new Coordinates(x, y));
						moves.add(new SimpleEntry<Coordinates, LegalMoveObj>(new Coordinates(x, y), option));
					}
				}
			}
		}
		
		if(moves.size() != coords.size())
			System.out.println();
		
		moves.sort(new Comparator<SimpleEntry<Coordinates, LegalMoveObj>>() {
			@Override
			public int compare(SimpleEntry<Coordinates, LegalMoveObj> o1, SimpleEntry<Coordinates, LegalMoveObj> o2) {
				//prioritize removing pieces
				double o2Dist = Math.pow((goalStone.getCol() - o2.getKey().x), 2) + Math.pow(goalStone.getRow() - o2.getKey().y, 2);
				double o1Dist = Math.pow((goalStone.getCol() - o1.getKey().x), 2) + Math.pow(goalStone.getRow() - o1.getKey().y, 2);
				double cmp = ((o1Dist)
						- (o2Dist));
				
				double pieces = (o2.getValue().getCapturedPieces().size()
						- o1.getValue().getCapturedPieces().size()) * 100;
				
				cmp *= 10;
				cmp += pieces;
				
				return (int) Math.round(cmp);
			}
		});
		
//		int cutfactor = (int) (moves.size() * 0.5);
//		List<SimpleEntry<Coordinates, LegalMoveObj>> toRemove = new ArrayList<>();
//		
//		for(int i= cutfactor; i < moves.size(); i++)
//			toRemove.add(moves.get(i));
//		moves.removeAll(toRemove);
		
//		for(SimpleEntry<Coordinates, LegalMoveObj> entry: moves)
//		{
//			System.out.println(goalStone.getCol() + ", " + goalStone.getRow() + 
//					" -> " + entry.getKey().x + ", " + entry.getKey().y + 
//					" distance to goal: " + (Math.pow((goalStone.getCol() - entry.getKey().x), 2) + Math.pow(goalStone.getRow() - entry.getKey().y, 2)));
//		}
		
		
		
		//TODO: check if white has two eyes
		if(moves.size() == 0) {
			//there were no legal moves for one of the players,
			//check if any legal move for the opponent (not filling own eyes)
			
			//change players
			currentBoard.setPlayerToMove(playerToMove.getOpposingColour());
			playerToMove = playerToMove.getOpposingColour();
			
			for(int y=0; y < height; y++) {
				for(int x=0; x < width; x++) {
					if(currentBoard.stonePositions[y][x].getOwner() == StoneOwner.EMPTY)
					{
						if(!fillingOwnEye(y, x, currentBoard))
						{
							LegalMoveObj option = currentBoard.isLegalMove(y, x, playerToMove.getOpposingColour(), false);
							if(option.isLegal())
							{
								coords.add(new Coordinates(x, y));
								moves.add(new SimpleEntry<Coordinates, LegalMoveObj>(new Coordinates(x, y), option));
							}
						}
					}
				}
			}
			
			
			//no legal moves are left for either player and goal is not met
			if(moves.size() == 0)
				return Double.NEGATIVE_INFINITY;
		}
			
		//make a copy of the current board
		//to perform moves at
		GoBoard tempBoard;
		
		if(playerToMove == maximizingPlayer)
		{
			double bestSoFar = Double.NEGATIVE_INFINITY;
			for(int i=0; i < moves.size(); i++)
			{
//				if(moves.get(i).getKey().y == 3 && moves.get(i).getKey().x == 7 && row == -1 && col == -1)
//				{
//					System.out.println();
//				}
				tempBoard = new GoBoard(currentBoard);
				tempBoard.playMove(moves.get(i).getKey().y, moves.get(i).getKey().x, playerToMove);
				tempBoard.setPlayerToMove(playerToMove.getOpposingColour());
				
				// if we can record the state for the player, proceed, otherwise
				// state has been examined before

				/* Convert board to a string, to hash */
				StringBuilder boardBuilder = new StringBuilder();
				for(int y=0; y < height; y++) {
					for(int x=0; x < width; x++) {
						char ch = ' ';
						switch(tempBoard.stonePositions[y][x].getOwner())
						{
						case BLACK:
							ch = 'x';
							break;
						case EMPTY:
							ch = '-';
							break;
						case NUSED:
							ch = 'E';
							break;
						case WHITE:
							ch = 'o';
							break;
						}
						boardBuilder.append(ch);
					}
				}
				//ExxxxxxxExxxxxxxExxxx--xExxxx--xExxxx-xoExxxxxo-Exxxx-o-ExxxxxooExxxxxxxExxxxxxxExxxxxxx
//				if(boardBuilder.toString().equals("ExxxxxxxExxxxxxxExxxx--xExxxx--xExxxx-xoExxxxxo-Exxxx-o-ExxxxxooExxxxxxxExxxxxxxExxxxxxx"))
//				if(boardBuilder.toString().equals("ExxxxxxxExxxxxxxExxxx-oxExxxxooxExxxxo-oExxxxxo-Exxxx-o-ExxxxxooExxxxxxxExxxxxxxExxxxxxx"))
//					System.out.println();
				//check if state is not examined before, if not add it and proceed

//				String problem = 
//						"ExxxxxxxExxxxxxxExxxxxoxExxxxoo-Exxxxo-oExxxxxo-Exxxx-ooExxxxxooExxxxxxxExxxxxxxExxxxxxx";
//				if(boardBuilder.toString().equals(problem));
//					System.out.println();
					
				if(dupCutter.addStringState(boardBuilder.toString(), playerToMove)) {
				
					double value = alphaBeta(0, 0, playerToMove.getOpposingColour(),
							maximizingPlayer, tempBoard, depth + 1, alpha, beta);
					
					//once value is known update record for the map
					if(value == -1)
					{
						// if depth was reached, unmark this board as evaluated
						Map<StoneOwner, Double> mapVal = new HashMap<>();
						mapVal.put(playerToMove, null);
						dupCutter.getEvaluatedBoards().remove(boardBuilder.toString(), mapVal);
					}
					else {
						dupCutter.getEvaluatedBoards()
							.get(boardBuilder.toString()).put(playerToMove, value);
					}
					if(row == -1)
						System.out.println(moves.get(i).getKey().y + ", " + moves.get(i).getKey().x + " " + value + " " + times);
					
					bestSoFar = Double.max(value, bestSoFar);
					if(value == Double.POSITIVE_INFINITY)
					{
						win_x = moves.get(i).getKey().x;
						win_y = moves.get(i).getKey().y;
					}
				} 
				else {
					//State has been calculated before, retrieve result
					double value = dupCutter.getEvaluatedBoards()
							.get(boardBuilder.toString()).get(playerToMove);
					bestSoFar = Double.max(value, bestSoFar);
				}
				
				if(bestSoFar >= beta)
				{
					return beta;
				} 
				if(bestSoFar > alpha)
				{
					alpha = bestSoFar;
				}
				
			}
			
			return alpha;
			
		} else {
			
			double bestSoFar = Double.POSITIVE_INFINITY;
			for(int i=0; i < moves.size(); i++)
			{
//				if(moves.get(i).getKey().y == 2 && moves.get(i).getKey().x == 7)
//					System.out.println();
				tempBoard = new GoBoard(currentBoard);
				tempBoard.playMove(moves.get(i).getKey().y, moves.get(i).getKey().x, playerToMove);
				tempBoard.setPlayerToMove(playerToMove.getOpposingColour());
				
				StringBuilder boardBuilder = new StringBuilder();
				for(int y=0; y < height; y++) {
					for(int x=0; x < width; x++) {
						char ch = ' ';
						switch(tempBoard.stonePositions[y][x].getOwner())
						{
						case BLACK:
							ch = 'x';
							break;
						case EMPTY:
							ch = '-';
							break;
						case NUSED:
							ch = 'E';
							break;
						case WHITE:
							ch = 'o';
							break;
						}
						boardBuilder.append(ch);
					}
				}
				
				if(dupCutter.addStringState(boardBuilder.toString(), playerToMove)) {
					
					double value = alphaBeta(0, 0, playerToMove.getOpposingColour(),
							maximizingPlayer, tempBoard, depth + 1, alpha, beta);
					
					if(value == -1)
					{
						// if depth was reached, unmark this board as evaluated
						Map<StoneOwner, Double> mapVal = new HashMap<>();
						mapVal.put(playerToMove, null);
						dupCutter.getEvaluatedBoards().remove(boardBuilder.toString(), mapVal);
					}
					else {
						//once value is known update record for the map
						dupCutter.getEvaluatedBoards()
							.get(boardBuilder.toString()).put(playerToMove, value);
					
						bestSoFar = Double.min(value, bestSoFar);
					}
//					if(bestSoFar == Double.NEGATIVE_INFINITY)
//					{
//						win_x = coords.get(i).x;
//						win_y = coords.get(i).y;
//					}
				}
				else {
					//get the value stored for this board 
					double value = dupCutter.getEvaluatedBoards()
							.get(boardBuilder.toString()).get(playerToMove);
					bestSoFar = Double.min(value, bestSoFar);
				}
				
				if(bestSoFar <= alpha)
				{
					return bestSoFar;
				}
				if(bestSoFar < beta)
				{
					beta = bestSoFar;
				}
			}
			
			return beta;
		}
		
	}
	
	

	private boolean fillingOwnEye(int y, int x, GoBoard currentBoard) {
		StoneOwner allyColour = currentBoard.playerToMove;

		for(int dy = -1; dy < 2; dy++) {
			for(int dx = -1; dx < 2; dx++)
			{
				if(dx == 0 && dy == 0)
					continue;
				if(currentBoard.isInBounds(y + dy, x + dx))
					if(currentBoard.stonePositions[y+dy][x+dx].getOwner() != allyColour)
						return false;
			}
		}
		
		return true;
	}

	private int _countBlacks(GoBoard currentBoard) {
		int blacks = 0;
		for(int y=0; y < height; y++) {
			for(int x=0; x < width; x++) {
				if(currentBoard.stonePositions[y][x].getOwner() == StoneOwner.BLACK) {
					blacks++;
				}
			}
		}

		return blacks;
	}
	
	
	
}
