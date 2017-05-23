import java.util.TreeSet;
import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;

public class MineSweeper  {

    // the board
    private Square[][] board;
    public int decrementer = 0;

    
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
        if(!inBounds(x, y))  {
            return;
        }
        // if it isn't already shown or flagged
        if(!board[x][y].shown
        && !board[x][y].flagged)  {
            board[x][y].shown = true;
            if(board[x][y].value == 0)  {
                for(int i = -1; i < 2; i++)  {
                    for(int j = -1; j < 2; j++)  {
                        revealZeros(x + i, y + j);
                    }
                }
            }
        }
    }

    /* 
     * Click on a square
     */
    public int peek(int x, int y)  {
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
     */
    public void flag(int x, int y)  {
        if(board[x][y].shown)  {
            return;
        }
        board[x][y].flagged = !board[x][y].flagged;
        if(board[x][y].flagged){
            decrementer--;
        }
        else{
            decrementer++;
        }
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
        decrementer = numMines;
        
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
    //caluclates fitness
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
            System.out.println(game.decrementer + " Mines Remaining");
            game.printBoard(false);
            System.out.println("Click:1 Flag:2");
            int type = in.nextInt();
            System.out.println("First Coordinate");
            x = in.nextInt();
            System.out.println("Second Coordinate");
            y = in.nextInt();
            
            if(!game.inBounds(x, y))  {
                System.out.println("That's out of bounds");
                continue;
            }
            if(type == 1)  {
                System.err.println("Clicking at (" + x + ", " + y + ")");
                game.peek(x, y);
                lastClick = game.peek(x, y);
            } 
            else  {
                System.err.println("Flagging at (" + x + ", " + y + ")");
                game.flag(x, y);
            }
        }
        System.out.println("you LOST");
        game.printBoard(true);
        System.out.println("Fitness: " + game.fitnessCalc());
    }

    public static void play2() {
    MineSweeper game = new MineSweeper(8, 10);
    int lastClick = 0;
    //Pair coords = new Pair(0,0);
   
        
    while(lastClick != -1)  {
        boolean frontier[]= new boolean[64] ;
      
        
        int a = 0;
              
        int x = 0;
        int y= 0;

        int d=0;
        int e=0;

        System.out.println(game.decrementer + " Mines Remaining");
        game.printBoard(false);
       
        game.peek(x,y);
        lastClick = game.board[x][y].value;
            for (int q=0; q<8; q++){
                for (int r=0; r<8; r++){
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

            for(int c=0; c<64;c++)
                if(frontier[c]){
                    d = c%8;
                    e = (c-d)/8;



                    //Strategy Methods
                    game.check1(d,e);
                }
            }

            }
            


            }
            System.out.println("you LOST");
            game.printBoard(true);
            System.out.println("Fitness: " + game.fitnessCalc());
        }
}


    public boolean check1(int d, int e) {
        int b = 0;
        int h = 0;
        int xadj[]= new int[8] ;
        int yadj[]= new int[8] ;
        for(int i = -1; i < 2 ; i++)  {
            for(int j = -1; j < 2 ; j++)  {
                if(inBounds(d+i, e+j) && board[d+i][e+j].shown)  {
                    b++;
                    xadj[h]= d+i;
                    yadj[h]= e+j;
                    h++;
                }
            }
        }


        if (board[d][e].value == b)  {
            for (int i=0; i<8; i++)  {
                if(xadj[i] != null)  {
                    game.flag(xadj[i], yadj[i]);

                }
            }
        }  
    }
    

    

    public static void main(String[] args)  {
        play2();
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

