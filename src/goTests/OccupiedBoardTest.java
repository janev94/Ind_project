package goTests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import goController.BoardParser;
import goController.BoardParserString;
import goController.DummyParser;
import goController.GoBoard;
import goController.Stone;
import goController.StoneOwner;

public class OccupiedBoardTest {

	private String simpleBoardString = "-x----\n"
							  		 + "x-----\n"
							  		 + "-x----\n"
							  		 + "------\n"
							  		 + "------\n";
	
	private String selfCaptureBoardString = "---xxxx--\n"
										  + "--x-oxxx-\n"
										  + "--xooxxx-\n"
										  + "---xxxx--\n"
										  + "---------\n";
	
	private String koRuleBoardString = "-------\n"
									 + "--xo---\n"
									 + "-x-xo--\n"
									 + "--xo---\n"
									 + "-------\n";
										  
	
	private GoBoard board;
	
	private Stone[][] stonePositions;
	
	
	@Before
	public void setUp() {
		
	}
	
	@After
	public void tearDown() {
		stonePositions = null;
	}
	
	@Test
	public void testMoveOnOccupiedPosition() {
		
		BoardParser parser = new BoardParserString(simpleBoardString);
		stonePositions = parser.parse();
		board = new GoBoard(5, 6);
		board.setStonePositions(stonePositions);
		
		assertFalse(board.isLegalMove(0, 1, StoneOwner.BLACK, false).isLegal());
	}
	
	@Test
	public void testSelfCapture() {
		BoardParser parser = new BoardParserString(selfCaptureBoardString);
		stonePositions = parser.parse();
		board = new GoBoard(5, 9);
		board.setStonePositions(stonePositions);
		board.setPlayerToMove(StoneOwner.WHITE);
		
		// causes white to have no liberties
		boolean isLegal = board.playMove(1, 3, StoneOwner.WHITE);
		System.out.println("Is legal: " + isLegal);
		assertFalse(isLegal); 
	}
	
	@Test
	public void testKoRule() {
		BoardParser parser = new BoardParserString(koRuleBoardString);
		stonePositions = parser.parse();
		board = new GoBoard(5, 7);
		board.setStonePositions(stonePositions);
		board.getPreviousStates().addState(stonePositions);
		board.setPlayerToMove(StoneOwner.WHITE);
		
		boolean isLegal = board.playMove(2, 2, StoneOwner.WHITE);
		assertTrue(isLegal);
		//set next player to move
		board.setPlayerToMove(StoneOwner.BLACK);
		StoneOwner emptyOwner = stonePositions[2][3].getOwner();
		
		assertFalse("Not detecting ko Rule", board.playMove(2, 3, StoneOwner.BLACK));
		assertEquals("Board changed on illegal move", emptyOwner, stonePositions[2][3].getOwner());
	}
	
}
