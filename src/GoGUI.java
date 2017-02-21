import java.util.ArrayList;
import java.util.List;

import goController.BoardParser;
import goController.BoardParserFile;
import goController.BoardParserString;
import goController.GoBoard;
import goController.GoPlayer;
import goController.LegalMoveObj;
import goController.Stone;
import goController.StoneOwner;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GoGUI extends Application {

	Stone[][] initialStonePositions;
	GoBoard board;
	
	private int boardWidth;
	private int boardHeight;
	
	private int SQ_SIDE = 40;
	private int RADIUS = 10;
	private int vPadding = 100;
	private int hPadding = 50;

	private boolean turn = false;
	private GoPlayer[] players;
	
	private StoneOwner playerToMove;

	private List<Circle> positions;
	private Label label;
	
	@Override
	public void start(Stage stage) {
		
		players = new GoPlayer[2];
		
		players[0] = GoPlayer.HUMAN;
		players[1] = GoPlayer.HUMAN;
		
		Group root = drawScene();
        Scene scene = new Scene(root, hPadding + SQ_SIDE * (boardWidth), vPadding + SQ_SIDE * (boardHeight));

        stage.setTitle("Go appl");
        stage.setScene(scene);
        stage.show();
        
        board._print_positions();
        board.getPreviousStates().addState(board.getStonePositions());
        //while(!board.isGoalReached())
        //	checkAndMoveAI();
        //GoBoard boardCpy = new GoBoard(board);
        System.out.println("Player to move: " + playerToMove);
        
        StoneOwner maximixingPlayer = StoneOwner.BLACK;
        
        MoveCruncher<?> cruncher = new MoveCruncher<>(board, playerToMove, maximixingPlayer);

        new Thread(cruncher).start();
//        System.out.println("minimax: " + board.minimaxAiMove(-1, -1, playerToMove, maximixingPlayer, board, 0));
        
        System.out.println("win_x: " + board.win_x + ", win_y: " + board.win_y);
       
        System.out.println("Goal reached: " + board.goalReached);
        System.out.println("liberties: ");
	}

	private void checkAndMoveAI() {
		
		if(players[StoneOwner.valueOf(playerToMove.toString()).ordinal()] == GoPlayer.ARTIFICIAL)
        {
			if(countLegals() == 0)
        		return;
			
			board.playAIMove(playerToMove);
        	playerToMove = playerToMove.getOpposingColour();
        	board.setPlayerToMove(playerToMove);
        	label.setText("Player to move: " + playerToMove);
        	renderBoard();
        }
	}
	
	private int countLegals()
	{
		int legals = 0;
		Stone[][] stonePositions = board.getStonePositions();
		for(int y=0; y < boardHeight; y++) {
			for(int x=0; x < boardWidth; x++) {
		
				StoneGUI st = (StoneGUI) positions.get(y*boardWidth + x);
				
				if(board.isLegalMove(st.getStone().getRow(), st.getStone().getCol(), playerToMove, false).isLegal()) {
					legals++;
				}
			}
		}
		
		return legals;
	}

	public Group drawScene() {
		positions = new ArrayList<>();
		List<Line> lines = new ArrayList<>();
		List<Rectangle> rects = new ArrayList<>();
		
		setUpBoard();
		
		label = new Label("Player to move: " + playerToMove.toString());
		label.setStyle("-fx-padding: 50 0 0 0;");

		for(int y=0;y < boardHeight - 1; y++) {
			for( int x=0;x < boardWidth - 1; x++) {
				Rectangle rect = new Rectangle(x * SQ_SIDE + hPadding, y * SQ_SIDE + vPadding, SQ_SIDE, SQ_SIDE);
				rect.setFill(Color.BURLYWOOD);
				rect.setStrokeWidth(1);
				rect.setStroke(Color.BLACK);
				rect.toBack();
				rects.add(rect);
			}
		}
	
		
		for(int y=0; y < boardHeight; y++) {
			for(int x=0; x < boardWidth; x++) {
				Paint paint = Color.TRANSPARENT;
				if(initialStonePositions[y][x].getOwner() == StoneOwner.BLACK)
					paint = Color.BLACK;
				else if(initialStonePositions[y][x].getOwner() == StoneOwner.WHITE)
					paint = Color.WHITESMOKE;
				else if(initialStonePositions[y][x].getOwner() == StoneOwner.NUSED)
					paint = Color.GRAY;
					
//				Circle circ = new Circle((x+1) * SQ_SIDE + RADIUS, (y+1) * SQ_SIDE,
//						RADIUS, paint);
				StoneGUI stone = new StoneGUI(initialStonePositions[y][x], hPadding + (x) * SQ_SIDE,
						vPadding + y * SQ_SIDE, RADIUS, paint);
				
				stone.setStrokeWidth(1);
				stone.setStroke(Color.BLACK);
				
				stone.setOnMouseClicked(e -> { 
					StoneGUI st = (StoneGUI) e.getSource();
					System.out.println("Row:" + st.getStone().getRow() + " Col: " + st.getStone().getCol());
					
					//Play move returns true if current move is possible
					if(board.playMove(st.getStone().getRow(), st.getStone().getCol(), playerToMove))
					{
						
						/* print the formatted stones to the standard out(console)*/
						
//						for(Stone[] row: board.getStonePositions())
//						{
//							for(Stone s: row)
//								System.out.print(s + " ");
//							System.out.println();
//						}

						
						playerToMove = playerToMove.getOpposingColour();
						board.setPlayerToMove(playerToMove);
						label.setText("Player to move: " + playerToMove);
						renderBoard();
						turn = !turn;
					} else
					{
						board.isLegalMove(st.getStone().getRow(), st.getStone().getCol(), playerToMove, true);
						System.out.println("Illegal move");
						System.out.println(playerToMove);
					}
					System.out.println(st.getStone().getRow() + " " + st.getStone().getCol());
//					st.setFill(turn?Color.BLACK: Color.WHITESMOKE);
//					st.toFront();
//					turn = !turn;
				});
				
				positions.add(stone);
			}
		}
		
		//check for legal moves
		//checkLegals();
		
        Circle circ = new Circle(40, 40, 30);

        circ.setFill(Color.TRANSPARENT);
        circ.setStrokeWidth(3);
        circ.setStroke(Color.BLACK);
        
        GridPane grid = new GridPane();
        
        Group root = new Group();
//        root.getChildren().addAll(positions);
        //root.getChildren().addAll(lines);

        root.getChildren().addAll(rects);
        root.getChildren().addAll(positions);
        root.getChildren().add(label);

        MenuBar menuBar = new MenuBar();
        Menu actionMenu = new Menu("Action");
        MenuItem checkLegalsItem = new MenuItem("Check legal moves");
        MenuItem moveAI = new MenuItem("Make AI move");
        
        checkLegalsItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				checkLegals();
			}
		});
        
        moveAI.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				checkAndMoveAI();
			}
		});
        

        actionMenu.getItems().addAll(checkLegalsItem, moveAI);
        menuBar.getMenus().add(actionMenu);
        root.getChildren().add(menuBar);
        
        
		return root;
	}

	
	
	private void renderBoard() {
		Stone[][] stonePositions = board.getStonePositions();
		for(int y=0; y < boardHeight; y++) {
			for(int x=0; x < boardWidth; x++) {
				Paint paint = Color.TRANSPARENT;
				if(stonePositions[y][x].getOwner() == StoneOwner.BLACK)
					paint = Color.BLACK;
				else if(stonePositions[y][x].getOwner() == StoneOwner.WHITE)
					paint = Color.WHITESMOKE;
				else if(stonePositions[y][x].getOwner() == StoneOwner.NUSED)
					paint = Color.GRAY;
			
				positions.get(y*boardWidth + x).setFill(paint);
			}
		}
		
		board.isGoalReached();
		//checkAndMoveAI();
	}
	
	
	private void checkLegals() {
		
		Stone[][] stonePositions = board.getStonePositions();
		for(int y=0; y < boardHeight; y++) {
			for(int x=0; x < boardWidth; x++) {

				StoneGUI st = (StoneGUI) positions.get(y*boardWidth + x);
				
				
				//if owner is not empty move is illegal by default, skip colouring
				if(stonePositions[st.getStone().getRow()][st.getStone().getCol()]
						.getOwner() != StoneOwner.EMPTY)
					continue;
				
				Paint paint;
				if(board.isLegalMove(st.getStone().getRow(), st.getStone().getCol(), playerToMove, false).isLegal()) {
					paint = Color.LIMEGREEN;
				}
				else 
					paint = Color.RED;
				
//				StoneGUI stone = new StoneGUI(stonePositions[y][x], (x+1) * SQ_SIDE + RADIUS,
//						(y+1) * SQ_SIDE, RADIUS, Color.TRANSPARENT);
				
				st.setFill(paint);
				
//				stone.setStrokeWidth(5);
//				stone.setStroke(paint);
			}
		}
	}
	

	private void setUpBoard() {
		String selfCaptureBoardString = 
				    "---xxxx--\n"
				  + "--x-oxxx-\n"
				  + "--xooxxx-\n"
				  + "---xxxx--\n"
				  + "---------\n";
		
		String koRuleBoardString = 
				   "-------\n"
				 + "--xo---\n"
				 + "-x-xo--\n"
				 + "--xo---\n"
				 + "-------\n";
		
		String cornerCaptureBoardString = 
				 "-------\n"
			   + "-------\n"
			   + "------o\n"
			   + "-----ox\n"
		       + "----ox-\n";
		
		String parsedString = selfCaptureBoardString;
		
		BoardParser parser = new BoardParserString(parsedString);
		
		BoardParserFile fileParser = new BoardParserFile("6die_black_border.gpr");
//		BoardParserFile fileParser = new BoardParserFile("7unsettled_sente.gpr");
		fileParser.parse();
		
		String[] rows = parsedString.split("\n");
		System.out.println(rows.length);
		System.out.println(rows[0].length());
		
		//boardWidth = rows[0].length();
		//boardHeight = rows.length;
		
		//TODO: make parse more generic
		initialStonePositions = fileParser.parse();
		boardWidth = fileParser.getWidth();
		boardHeight = fileParser.getHeight();
		
		board = new GoBoard(boardHeight, boardWidth);

		board.setGoal(fileParser.getGoal());
		
		board.setStonePositions(initialStonePositions);
		board.getPreviousStates().addState(initialStonePositions);
		playerToMove = StoneOwner.BLACK;
		board.setPlayerToMove(playerToMove);
	}

	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
