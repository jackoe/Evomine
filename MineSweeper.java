import java.util.TreeSet;
import java.util.Random;
import java.util.Arrays;

public class MineSweeper  {
    private Square[][] board;
    
    private void safeIncBoard(int x, int y)  {
        int len = board.length;
        if(x < len
        && y < len
        && x >= 0
        && y >= 0
        && board[x][y].value < 9)  {
           board[x][y].value++;
        }
    }


    MineSweeper(int sideLen)  {
        board = new Square[sideLen][sideLen];
        Random mineLocGen = new Random();
        int numMines = (sideLen * sideLen) / 5;
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
            board[x][y].value = 9;
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


    public static void main(String[] args)  {
        new MineSweeper(10).printBoard(true);
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
            value = 9;
            shown = false;
        }
        
        public String toString()  {
            return value == 9? "*": value + "";
        }
    }
}
