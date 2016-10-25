package goController;


public class BoardParserString extends BoardParser {

	private String source;
	
	public BoardParserString(String source) {
		this.source = source;
	}
	
	@Override
	public Stone[][] parse() {
		String[] lines = source.split("\n");
		
		Stone[][] stonePositions = new Stone[lines.length][lines[0].length()];
		
		for(int i = 0; i < lines.length; i++) { //height
			for(int j = 0; j < lines[i].length(); j++) { // width
				switch(lines[i].charAt(j)) {
				case 'x':
					stonePositions[i][j] = new Stone(StoneOwner.BLACK);
					break;
				case 'o':
					stonePositions[i][j] = new Stone(StoneOwner.WHITE);
					break;
				case '-':
					stonePositions[i][j] = new Stone(StoneOwner.EMPTY);
					break;
				default:
					System.out.println("Unrecognized character: " + lines[i].charAt(j));
				}
			}
		}
		
		return stonePositions;
	}

}
