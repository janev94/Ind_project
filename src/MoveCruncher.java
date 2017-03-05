import goController.GoBoard;
import goController.StoneOwner;
import javafx.concurrent.Task;

public class MoveCruncher<V> extends Task<V> {

	private GoBoard board;
	private StoneOwner playerToMove;
	private StoneOwner maximizingPlayer;
	
	public MoveCruncher(GoBoard board) {
		this.board = board;
	}

	public MoveCruncher(GoBoard board, StoneOwner playerToMove, StoneOwner maximizingPlayer) {
		this.board = board;
		this.playerToMove = playerToMove;
		this.maximizingPlayer = maximizingPlayer;
	}
	
	@Override
	protected V call() throws Exception {

		System.out.println("Initializing move crunches...");
		long startTime = System.currentTimeMillis();
		
		System.out.println("CRUNCHER: alpha beta:" + 
			board.alphaBeta(-1, -1, playerToMove, maximizingPlayer, board, 0,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
		);

//		System.out.println("CRUNCHER: minimax:" + 
//			board.minimaxAiMove(-1, -1, playerToMove, maximizingPlayer, board, 0)
//		);

		
		System.out.println("CRUNCHER: win_x: " + board.win_x + ", win_y: " + board.win_y);
		
		System.out.println("Time elapsed: " + (System.currentTimeMillis() - startTime));
		System.out.println("Done!");
		
		return null;
	}

}
