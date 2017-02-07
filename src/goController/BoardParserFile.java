package goController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoardParserFile extends BoardParser {

	private File input;
	private Stone[][] board;
	private int width;
	private int height;
	private List<String> lines;
	private GoGoal goal;
	
	private int rows;
	
	public BoardParserFile(String fname) {
		input = new File(fname);
		
		try(Stream<String> stream = Files.lines(Paths.get(fname)))
		{
			lines = stream.map(x -> x.trim()).collect(Collectors.toList());
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		System.out.println(lines);
		for(String line: lines)
		{
			if(line.equals(""))
				break;
			rows++;
		}
		
		if(rows == 0)
			return;
		int cols = lines.get(0).length();
		
		board = new Stone[rows][cols];
	}
	
	@Override
	public Stone[][] parse() {
		for(int i=0; i < rows; i++)
		{
			for(int j=0; j < lines.get(i).length(); j++)
			{
				switch (lines.get(i).charAt(j))
				{
				case 'x':
					board[i][j] = new Stone(StoneOwner.BLACK, i, j);
					break;
				case 'o':
					board[i][j] = new Stone(StoneOwner.WHITE, i, j);
					break;
				case '-':
					board[i][j] = new Stone(StoneOwner.EMPTY, i, j);
					break;
				case 'E':
					board[i][j] = new Stone(StoneOwner.NUSED, i, j);
					break;
				default:
					System.out.println("Unrecognized character: " + lines.get(i).charAt(j));
				
				}
			}
		}
		
		width = board[0].length;
		height = board.length;
		
		String goalString = lines.get(rows + 1);
		String[] goalPieces = goalString.split(" ");
		goal = new GoGoal(GoGoalEnum.valueOf(goalPieces[0]), Integer.valueOf(goalPieces[2]), Integer.valueOf(goalPieces[1]));
		
		return board;
	}
	
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public GoGoal getGoal() {
		return goal;
	}
}
