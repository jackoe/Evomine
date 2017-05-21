import java.util.TreeSet;
import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;

public class MineSweeper  {
    // the board
    private Square[][] board;
    public int numMinesLeft;

    
    /*
     * Returns true if x and y are in bounds.
     * false otherwise
     */
    public boolean inBounds(int x, int y)  {
        return x < board.length
            && y < board.length
            && x >= 0
            && y >= 0;
    }
    /*
     * Increments a square at an index if it's in bounds
     */
    private void safeIncBoard(int x, int y)  {
        if(inBounds(x, y) && board[x][y].value >= 0)  {
            board[x][y].value++;
        }
    }
    
    /*
     * Reveales the blob of zeros
     * Also revealed the nonzeros around said blob.
     */
    private void revealZeros(int x, int y) {
        // catch the out of bounds
        // // if it isn't already shown or flagged
        if(!inBounds(x, y)
        && !board[x][y].shown
        && !board[x][y].flagged)  {
            return;
        }
        
        board[x][y].shown = true;
        // we reveal but don't recurse on nonzeros
        if(board[x][y].value != 0)  {
            return;
        }
        
        for(int i = -1; i < 2; i++)  {
            for(int j = -1; j < 2; j++)  {
                revealZeros(x + i, y + j);
            }
        }
    }

    /* 
     * Click on a square
     */
    public int peek(int x, int y)  {
        // We don't allow clicking on flagged squares
        if(board[x][y].flagged)  {
            return -2;
        }

        Square peekingAt = board[x][y];
        if(!peekingAt.shown)  {
            revealZeros(x, y);
        }
        return peekingAt.value;
     }
    /*
     * flags a mine
     * @param x the x coord
     * @param y the y coord
     */
    public void flag(int x, int y)  {
        // We don't allow flagging revealed squares
        if(board[x][y].shown)  {
            return;
        }
        Square curr = board[x][y];
        curr.flagged = !curr.flagged;
        numMinesLeft += curr.flagged? 1: -1;
    }

    /*
     * Make a minesweeper board
     * @param sideLen The length of a given side.
     * @param numMines The number of mines.
     */
    MineSweeper(int sideLen, int numMines)  {
        board = new Square[sideLen][sideLen];
        Random mineLocGen = new Random();
        TreeSet<Pair> coords = new TreeSet<Pair>();
        numMinesLeft = numMines;
        
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
                System.out.print(curr.toString(show));
            }
            System.out.println("");
        }
    }
    /*
     * Calculates fitness by #true flags - #false flags
     */
    public int fitnessCalc(){
        int fitness = 0;
        for(int i = 0; i < board.length; i++)  {
            for(int j = 0; j < board.length; j++)  {
                Square curr = board[i][j];
                if (board[i][j].flagged) {
                    if (board[i][j].value == -1) {
                        fitness++;
                    }
                    else {
                        fitness--;
                    }    
                }
            }
        }
        return fitness;
    }
   
    /*
    * Plays the game.
    */
    public static void play()  {
        MineSweeper game = new MineSweeper(8, 10);
        int lastClick = 0;
        Scanner in = new Scanner(System.in);
        int x,y;
        
        while(lastClick != -1)  {
            
            // get input from user
            System.out.println(game.numMinesLeft + " Mines Remaining");
            game.printBoard(false);
            System.out.println("Click:1 Flag:2");
            int type = in.nextInt();
            System.out.print("First Coordinate: ");
            x = in.nextInt();
            System.out.print("Second Coordinate: ");
            y = in.nextInt();
            if(!game.inBounds(x, y))  {
                System.out.println("That's out of bounds");
                continue;
            }
            if(type == 1)  {
                System.err.println("Clicking at (" + x + ", " + y + ")");
                lastClick = game.peek(x, y);
            }  else  {
                System.err.println("Flagging at (" + x + ", " + y + ")");
                game.flag(x, y);
            }
        }
        System.out.println("you LOST");
        game.printBoard(true);
        System.out.println("Fitness: " + game.fitnessCalc());
    }

    

    public static void main(String[] args)  {
        play();
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
            return "(" + fst + ", " + snd + ")";
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
        public boolean flagged;
        public int value;

        /*
         * constructor, pass -1 for mine, otherwise pass value
         */
        Square(int sqNumber)  {
            value = sqNumber;
            shown = false;
            flagged = false;
        }

        /*
         * Alternate toString method
         * @param ignoreShown reveal even if hasn't been clicked
         */
        public String toString(boolean ignoreShown)  {
            boolean show = shown || ignoreShown;
            
            if(flagged)
                return "F";
            else if(shown && value == -1)
                return "#";
            else if(show && value == -1)
                return "*";
            else if(show)
                return value + "";
            else 
                return " ";
        }
        /*
         * prints * if mine
         * prints blank space if not revealed
         * prints number if revealed
         */
        public String toString()  {
            return toString(false);
        }
    }
}
