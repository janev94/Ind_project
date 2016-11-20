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
		
		assertFalse(board.isLegalMove(0, 1, StoneOwner.BLACK));
	}
	
	@Test
	public void testSelfCapture() {
		BoardParser parser = new BoardParserString(selfCaptureBoardString);
		stonePositions = parser.parse();
		board = new GoBoard(5, 9);
		board.setStonePositions(stonePositions);
		board.setPlayerToMove(StoneOwner.WHITE);
		
		// causes white to have no liberties
		boolean isLegal = board.isLegalMove(1, 3, StoneOwner.WHITE); 
		assertFalse(isLegal); 
	}
	
	@Test
	public void testKoRule() {
		BoardParser parser = new BoardParserString(koRuleBoardString);
		stonePositions = parser.parse();
		board = new GoBoard(5, 7);
		board.setStonePositions(stonePositions);
		board.setPlayerToMove(StoneOwner.WHITE);
		
		assertFalse(board.isLegalMove(2, 2, StoneOwner.WHITE));
	}
	
}
