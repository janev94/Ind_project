package goTests;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import goController.BoardParser;
import goController.BoardParserString;
import goController.GoBoard;
import goController.Stone;
import goController.StoneOwner;

public class BoardInitializationTest {

	private String simpleBoardString = 
			   "-x----\n"
	  		 + "x-----\n"
	  		 + "-x----\n"
	  		 + "------\n"
	  		 + "------\n";
	
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
	public void testDimensions() {
		// since we access the board by b[h][w], where b is the board
		// h is the row and w is the column we want to access
		assertEquals(height, board.getStonePositions().length);
		assertEquals(width, board.getStonePositions()[0].length);
	}
	
	@Test
	public void testOwnership() {
		//assuming x denotes position owned by black
		assertEquals(StoneOwner.BLACK, board.getStonePositions()[2][1].getOwner());
		assertEquals(StoneOwner.EMPTY, board.getStonePositions()[1][2].getOwner());
	}
	
	@Test
	public void testAccessEdges() {
		assertEquals(StoneOwner.EMPTY, board.getStonePositions()[height - 1][width -1].getOwner());
		assertEquals(StoneOwner.EMPTY, board.getStonePositions()[0][0].getOwner());
		assertEquals(StoneOwner.EMPTY, board.getStonePositions()[height - 1][0].getOwner());
		assertEquals(StoneOwner.EMPTY, board.getStonePositions()[0][width - 1].getOwner());
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testAccessNegativeHeightField() {
		board.getStonePositions()[-1][0].getRow();
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testAccessNegativeWidthField() {
		board.getStonePositions()[0][-1].getRow();
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testAccessOutsideWidthField() {
		board.getStonePositions()[0][width].getRow();
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testAccessOutsideHeightField() {
		board.getStonePositions()[height][0].getRow();
	}
	
	@Test
	public void testGetNeighboursInBoard()
	{
		Set<Stone> neighbors = board.getNeighbors(stonePositions[1][1], stonePositions);
		assertEquals(4, neighbors.size());
		
		int blackNeighbors = 0;
		for(Stone s: neighbors)
			if(s.getOwner() == StoneOwner.BLACK)
				blackNeighbors++;
		//1/1 should have 3 black neighbors
		
		assertEquals(3, blackNeighbors);
	}
	
	@Test
	public void testGetNeighborsInCorner()
	{
		//should have only 2 neighbor cells
		Set<Stone> neigbors = board.getNeighbors(stonePositions[height - 1][width -1], stonePositions);
		assertEquals(2, neigbors.size());
	}
	
	@Test
	public void testGetNeighborsAtEdge()
	{
		//edge stones should have exactly 3 neighbors, unless corner stones
		Set<Stone> neigbors = board.getNeighbors(stonePositions[height - 1][1], stonePositions);
		assertEquals(3, neigbors.size());
	}
}
