package goTests;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import goController.BoardParser;
import goController.BoardParserString;
import goController.BoardStateTracker;
import goController.GoBoard;
import goController.LegalMoveObj;
import goController.Stone;
import goController.StoneOwner;

public class BoardBehaviorTest {

	private String simpleBoardString = 
			   "-x----\n"
	  		 + "x-----\n"
	  		 + "-x----\n"
	  		 + "------\n"
	  		 + "------\n";
	
	private String groupBoard =
			  "-x-\n"
			+ "x--\n"
			+ "oox\n";
	
	private int height = 5;
	private int width = 6;
	
	private GoBoard board;
	private Stone[][] stonePositions;
	
	@Before
	public void setUp(){
		BoardParser parser = new BoardParserString(simpleBoardString);
		stonePositions = parser.parse();
		
		//height(5) width(6)
		board = new GoBoard(height, width);
		
		board.setStonePositions(stonePositions);
	}
	
	@After
	public void tearDown(){
		board = null;
		stonePositions = null;
	}
	
	
	@Test
	public void testGroups()
	{
		BoardParser parser = new BoardParserString(groupBoard);
		stonePositions = parser.parse();
		board = new GoBoard(3, 3);
		board.setStonePositions(stonePositions);
		
		Set<Set<Stone>> groups = board.getGroups(StoneOwner.BLACK, stonePositions);
		groups.removeIf(x -> x.size() == 0);
		assertEquals(3, groups.size());
	}
	
	@Test
	public void testIsLegalKeepsSameState()
	{
		BoardParser parser = new BoardParserString(simpleBoardString);
		stonePositions = parser.parse();
		
		board = new GoBoard(height, width);
		board.setStonePositions(stonePositions);

//		BoardStateTracker states = new BoardStateTracker();
//		states.addState(stonePositions);
		BoardStateTracker states = board.getPreviousStates();
		states.addState(stonePositions);
		
		board.setPlayerToMove(StoneOwner.BLACK);
		
		StoneOwner oldOwner = board.getStonePositions()[1][1].getOwner();
		
		LegalMoveObj legalObj = board.isLegalMove(1, 1, StoneOwner.BLACK);
		assertTrue(legalObj.isLegal());
		
		assertEquals(oldOwner, board.getStonePositions()[1][1].getOwner());
		
//		System.out.println(states.getStates().size());
		
//		System.out.println(legalObj.isLegal());
//		System.out.println("before: " + states.getStates().get(0)[1][1].getOwner());
//		System.out.println("After: " + states.getStates().get(1)[1][1].getOwner());
	}

}
