import java.util.TreeSet;
import java.util.Random;
import java.util.Arrays;

public class MineSweeper  {
    private Square[][] board;
   
    /*
     * Increments a square at an index if it's in bounds
     */
    private void safeIncBoard(int x, int y)  {
        int len = board.length;
        if(x < len
        && y < len
        && x >= 0
        && y >= 0
        && board[x][y].value >= 0)  {
           board[x][y].value++;
        }
    }
    
    /* 
     * Click on a square
     */
    public int peek(int x, int y)  {
        Square peekingAt = board[x][y];
         peekingAt.shown = true;
         return peekingAt.value;
     }

    /* 
     * Make a minesweeper board
     * @param sideLen The length of a given side.
     * @param numMines The number of mines.
     */
    MineSweeper(int sideLen, int numMines)  {
        board = new Square[sideLen][sideLen];
        Random mineLocGen = new Random();
        TreeSet<Pair> coords = new TreeSet();
        
        while(coords.size() < numMines)  {
            coords.add(new Pair(mineLocGen.nextInt(sideLen),
                                mineLocGen.nextInt(sideLen)));
        }
        
        // fill every board with a blank square
        for(int i = 0; i < sideLen; i++)  {
            for(int j = 0; j < sideLen; j++)  {
                board[i][j] = new Square(0);
            }
        }

        // fills each bomb square with a -1
        // increments all squares around them
        for(Pair p: coords)  {
            int x = p.fst;
            int y = p.snd;
            board[x][y].value = -1;
            for(int i = -1; i < 2; i++)  {
                for(int j = -1; j < 2; j++)  {
                    safeIncBoard(x + i, y + j);
                }
            }
        }
    }

    /* 
     * prints the board
     */
    public void printBoard(boolean show)  {
        for(int i = 0; i < board.length; i++)  {
            for(int j = 0; j < board.length; j++)  {
                Square curr = board[i][j];
                System.out.print(show || curr.shown? board[i][j]: " ");
            }
            System.out.println("");
        }
    }
    
    
    public void play()  {
        Minesweeper game = new Minesweeper(8, 10);
        printBoard(false);
        while(condition)  {
            printBoard(false);
            ge
        }
        
    }

    public static void main(String[] args)  {
        new MineSweeper(8, 10).printBoard(true);
    }

    /*
     * Represents a pair of points
     */
    private class Pair implements Comparable<Pair>  {
        public int fst;
        public int snd;
        
        /*
         * make a pair
         */
        Pair(int fst, int snd)  {
            this.fst = fst;
            this.snd = snd;
        }

        /*
         * Here so we can put pairs in a set
         */
        public int compareTo(Pair othr)  {
            if(this.fst > othr.fst)  {
                return 1;
            }
            if(this.fst > othr.fst)  {
                return -1;
            }
            if(this.snd > othr.snd)  {
                return 1;
            }
            
            if(this.snd < othr.snd)  {
                return -1;
            }
            return 0;

        }

        public String toString()  {
            return "[" + fst + ", " + snd + "]";
        }
    }

    /*
     * Represents a square on a board.
     * A value of -1 represents a mine.
     * Other values represent what the square would show if clicked.
     * Shown is true if square has been clicked.
     */
    private class Square  {
        public boolean shown;
        public int value;

        Square(int sqNumber)  {
            value = sqNumber;
            shown = false;
        }

        Square()  {
            value = -1;
            shown = false;
        }
        
        public String toString()  {
            return value == -1? "*": value + "";
        }
    }
}
