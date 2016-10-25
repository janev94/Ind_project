package goTests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import goController.BoardParser;
import goController.BoardParserString;
import goController.Stone;
import goController.StoneOwner;

public class StringParserTest {

	
	
	private static String board = "-x----\n"
								+ "x-----\n"
								+ "-o----\n"
								+ "------\n"
								+ "------\n";
	
	private static BoardParser parser;
	private static Stone[][] boardPositions;
	
	@BeforeClass
	public static void loadParser() {
		parser = new BoardParserString(board);
		boardPositions = parser.parse();
	}
	
	@Test
	public void testDimensions()
	{
		assertEquals(5, boardPositions.length); // height
		assertEquals(6, boardPositions[1].length); // width
	}
	
	@Test
	public void testStoneOwners() 
	{
		assertEquals(StoneOwner.BLACK, boardPositions[0][1].getOwner());
		assertEquals(StoneOwner.WHITE, boardPositions[2][1].getOwner());
		assertEquals(StoneOwner.EMPTY, boardPositions[3][3].getOwner());
	}
}
