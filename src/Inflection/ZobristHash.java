package Inflection;

public class ZobristHash {
	
	private int size;
	private int ZobristTable[][][];
	
	public ZobristHash(int size){
		this.size=size;
		ZobristTable=new int[size+1][size+1][2];
		initTable();
	}

	// Generates a Random number from 0 to a big integer
	private int randomInt()
	{
		 return Math.round((float)Math.random() * 1000000000);
	}

	// This function associates each piece with
	// a number
	private int indexOf(char piece)
	{
	    if (piece=='b')
	        return 0;
	    if (piece=='w')
	        return 1;
	    else return -1;
	}

	// Initializes the table
	private void initTable()
	{
	    for (int i = 1; i<=size; i++)
	      for (int j = 1; j<=size; j++)
	        for (int k = 0; k<2; k++)
	          ZobristTable[i][j][k] = randomInt();
	}

	// Computes the hash value of a given board
	public int computeHash(char board[][]){
		int h = 0;
	    for (int i = 1; i<=size; i++)
	    {
	        for (int j = 1; j<=size; j++)
	        {
	            if (board[i][j]!='n')
	            {
	                int piece = indexOf(board[i][j]);
	                h ^= ZobristTable[i][j][piece];
	            }
	        }
	    }
	    return h;
	}



}
