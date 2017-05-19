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

        for(int i = 0; i < sideLen; i++)  {
            for(int j = 0; j < sideLen; j++)  {
                board[i][j] = new Square(0);
            }
        }
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

    private class Pair implements Comparable<Pair>  {
        public int fst;
        public int snd;

        Pair(int fst, int snd)  {
            this.fst = fst;
            this.snd = snd;
        }

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
