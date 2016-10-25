package goTests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import goController.BoardParser;
import goController.DummyParser;
import goController.GoBoard;
import goController.Stone;
import goController.StoneOwner;

public class EmptyBoardTest {

	private static BoardParser parser;
	private static Stone[][] stonePositions;
	
	GoBoard board;
	
	@BeforeClass
	public static void setUpBoard() {
		parser = new DummyParser();
		stonePositions = parser.setUpEmptyBoard(5, 5);
		
	}
	
	@Before
	public void setUp() {
		board = new GoBoard(5, 5);
	}
	
	@After
	public void tearDown() {
		board = null;
	}
	
	@Test
	public void testMoveInEmptyBoardForBlack(){
		assertEquals(board.isLegalMove(0, 0, StoneOwner.BLACK),
				board.getPlayerToMove() == StoneOwner.BLACK);
	}
	
	@Test
	public void testMoveInEmptyBoardForWhite() {
		assertEquals(board.isLegalMove(0, 0, StoneOwner.WHITE), 
				board.getPlayerToMove() == StoneOwner.WHITE);
	}
	
	@Test
	public void testMoveOutsideBoard(){
		assertFalse(board.isLegalMove(-1, 0, StoneOwner.BLACK));
		assertFalse(board.isLegalMove(3, -1, StoneOwner.BLACK));
		assertFalse(board.isLegalMove(5, 2, StoneOwner.BLACK));
		assertFalse(board.isLegalMove(2, 5, StoneOwner.BLACK));
		
		assertFalse(board.isLegalMove(10, 3, StoneOwner.BLACK));
	}
}
