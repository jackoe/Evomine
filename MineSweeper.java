import java.util.TreeSet;
import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.List;

public class MineSweeper  {

    /** the board
     */
    private Square[][] board;
    /** number of mines left
     */
    public int numMinesLeft;
    /** number of turns taken
     */
    public int numTurns;

    /**
     * Returns the square object if it's accessible:
     * null otherwise
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the Square object at that location, or null if out of bounds.
     */
    public Square get(int x, int y)  {
        return inBounds(x,y) ? board[x][y]: null;
    }

    /**
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if x and y are in bounds.
     * false otherwise
     */
    public boolean inBounds(int x, int y)  {
        return x < board.length
            && y < board.length
            && x >= 0
            && y >= 0;
    }
    /**
     * Increments a square at an index if it's in bounds
     * @param x the x coordinate
     * @param y the y coordinate
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
        // if it isn't already shown or flagged
        if(!inBounds(x, y) || board[x][y].shown)  {
            return;
        }

        board[x][y].flagged = false;

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
    /** 
     * @return the number of squares that have been revealed
     */
    public int numRevealed()  {
        int numRevealedCount = 0;
        for(int i = 0; i < board.length; i++)  {
            for(int j = 0; j < board.length; j++)  {
                numRevealedCount += board[i][j].shown ? 1 : 0;
            }
        }
        return numRevealedCount;
    }

    /**
     * Click on a square
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the value of the square (-1 if mine)
     */
    public int peek(int x, int y)  {
        if(!inBounds(x, y))  {
            System.out.println("x: " + x + ", y: " + y + " out of bounds");
            return 0;
        }
        

        numTurns++;
        Square peekingAt = board[x][y];
        if(peekingAt.flagged)  {
            return 0;
        }
        revealZeros(x, y);
        
        return peekingAt.value;
     }

    /**
     * clicks on a random unshown square
     * @return if was successful. Fails if entire board is modified.
     */
    public boolean clickOnRandomUnmodifiedSq(Random rand)  {
        LinkedList<Square> squares = new LinkedList();
        for(int i = 0; i < board.length; i++)  {
            for(int j = 0; j < board.length; j++)  {
                Square sq = get(i,j);
                if(sq.shown && !sq.flagged)  {
                    squares.add(sq);
                }
            }
        }
        if(squares.size() > 0)  {
            Square sq = squares.get(rand.nextInt(squares.size()));
            sq.shown = true;
            numTurns++;
            return true;
        }
        return false;
    }


    /**
     * flags a mine
     * @param x the x coord
     * @param y the y coord
     */
    public void flag(int x, int y)  {
        if(!inBounds(x, y))  {
            System.out.println("x: " + x + ", y: " + y + " out of bounds");
            return;
        }
        // We don't allow flagging revealed squares
        if(board[x][y].shown)  {
            return;
        }
        numTurns++;
        Square curr = board[x][y];
        //curr.flagged = !curr.flagged;
        curr.flagged = true;
        numMinesLeft += curr.flagged? 1: -1;
    }

    /**
     * Make a minesweeper board
     * @param sideLen The length of a given side.
     * @param numMines The number of mines.
     */
    MineSweeper(int sideLen, int numMines)  {
        numTurns = 0;
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

    private void printHyphenLine()  {
        for(int i = 0; i < board.length; i++)  {
            System.out.print("-");
        }
        System.out.println("");
    }

    /**
     * prints the board
     * @param show whether or not user wants all mines to be shown or just the revealed ones.
     */
    public void printBoard(boolean show)  {
        System.out.println("");
        //printHyphenLine();
        for(int i = 0; i < board.length; i++)  {
            //System.out.print("|");
            for(int j = 0; j < board.length; j++)  {
                Square curr = board[i][j];
                System.out.print(curr.toString(show));
            }
            System.out.println("");
        }
        //printHyphenLine();
        System.out.println("number of turns = " + numTurns);
    }
    /**
     * Calculates fitness by #true flags - #false flags
     * @return a fitness score
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
   
    /**
    * Plays the game from the user
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
    
    /**
     * Plays the game with our hand-written AI
     */
    public static void play2() {
        MineSweeper game = new MineSweeper(8, 10);
        int lastClick = 0;
        //Pair coords = new Pair(0,0);
       
            
        while(lastClick != -1)  {
            boolean frontier[]= new boolean[64] ;  
            int a = 0;
                  
            int x = 0;
            int y = 0;

            int d = 0;
            int e = 0;

            System.out.println(game.numMinesLeft + " Mines Remaining");
            game.printBoard(false);
           
            game.peek(x, y);
            lastClick = game.board[x][y].value;
            System.out.println("LastClick "+ lastClick);
            for (int q=0; q<8; q++)  {
                for (int r=0; r<8; r++)  {
                    if(game.board[q][r].shown)  {
                        boolean breakbool = true;
                        for(int i = -1; i < 2 && breakbool; i++)  {
                            for(int j = -1; j < 2 && breakbool; j++)  {
                                if(game.inBounds(x+i, y+j) && !game.board[x+i][y+j].shown)  {
                                    frontier[a] = true;
                                    a++;
                                    breakbool = false;
                                }
                            }
                        } 
                        //coords =  new Pair(x, y);
                        if(a!=0)  {
                            System.out.println(frontier[a-1] + " Last Frontier ");
                        }

                        
                        System.out.println("Hello Hello Hello");
                        for(int c=0; c<64;c++){
                            System.out.println("Hello");
                            if(frontier[c]){
                                System.out.println("Frontier List"+ c);
                                d = c%8;
                                e = (c-d)/8;
                                //Strategy Methods
                                game.check1(d,e);
                            }
                        }
                    }
                }
            } 
        }
        System.out.println("you LOST");
        game.printBoard(true);
        System.out.println("Fitness: " + game.fitnessCalc());
    }

    /**
     * Clicks on a zero.
     * This is intended to be used when just starting out so that we don't always start out with a blank board.
     */
    public void clickOnAZero()  {
        for(int i = 0; i < board.length; i++)  {
            for(int j = 0; j < board.length; j++)  {
                if(!board[i][j].shown
                 && board[i][j].value == 0)  {
                    peek(i, j);
                    return;
                 }
            }
        }
    }

    private void check1(int d, int e) {
        int b = 0;
        for (Square sq : getNeighBors(d,e)) {
            if(sq.shown) {
                b++;

            }
        }
        
        if (board[d][e].value == b)  {
            for (Square sq : getNeighBors(d,e)) {
            if(sq.shown) {
                    sq.flagged = true;
                }
            }
        }  
    }    

    /**
     * @param x the x coord
     * @param y the y coord
     * @return a list of your neighbors
     */
    public List<Square>getNeighBors(int x, int y)  {
        List<Square> neighbors = new LinkedList<Square>();
        for(int i = -1; i < 2; i++)  {
            for(int j = -1; j < 2; j++)  {
                if(inBounds(i,j))  {
                    neighbors.add(board[i][j]);
                    System.out.println(x+" "+y);
                }
            }
        }
        return neighbors;
    }

    
    /**
     * Right now just plays a game with the user
     * @param args the command line arguements, not used
     */
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
        
        /**
         * @return the pair into a string
         */
        public String toString()  {
            return "(" + fst + ", " + snd + ")";
        }
    }
}

