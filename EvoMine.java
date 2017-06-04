/* 
 * Jack Wines and Sam Orfield
 * Running this will start evolution
 * compile with:
 * javac MineSweeper.java -cp ../EvoMine
 * javac EvoMine
 * Run with:
 * java Evomine
 */
import org.jenetics.BitChromosome;
import org.jenetics.IntegerChromosome;
import org.jenetics.BitGene;
import org.jenetics.IntegerGene;
import org.jenetics.Genotype;
import org.jenetics.Chromosome;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;
import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.stat.IntMomentStatistics;
import org.jenetics.TournamentSelector;
import java.util.Scanner;
import java.util.Arrays;
import org.jenetics.util.RandomRegistry;
import org.jenetics.StochasticUniversalSelector;

public class EvoMine {
private static final int NUMGAMES = 100;
private static final int BOARDSIZE = 8;
private static final int NUMMINES = 10;
private static final int NUMPATTERNS = 250;
private static final int FITNESSTYPE = 0;
private static final int NUMGENERATIONS = 250;
private static final int POPSIZE = 100;
private static final int FRONTIERNEIGHBORS = 1;



    /*
     * Takes a square, converts it to an int.
     * int is -1 if bomb
     * int is 10 if flag
     */
    private static int squareToInt(Square sq)  {
        if(sq == null)  {
            return -2;
        }  else if(sq.flagged)  {
            return 9;
        }  else if(!sq.shown)  {
            return -2;
        }  else  {
            return sq.value;
        }
    }

    private static void modArr(int index, int[] chromoPattern)  {       
       if(index%11 == 4)  {
          chromoPattern[index] = (chromoPattern[index]%8) + 1;
       }
       if (index%11 == 9)  {
          chromoPattern[index] = chromoPattern[index]%2;
       if (index%11 == 10){
       	chromoPattern[index] = (chromoPattern[index]);
       }   
       } else  {
          chromoPattern[index] = chromoPattern[index]%3;
       }


    }

    /*
     * takes a game, takes coords, returns the index and match score of the best pattern.
     * match score is the edit distance of one integer string to another
     * where one edit is classiied as +- 1 to one index
     */


    /*
     * Given a game, selects the best pattern to use for a given pair
     * returns both the best pattern index and its index.
     *
    private static double[] selectPattern(MineSweeper game, int x, int y, int[] arrPattern)  {
       
        int minPatternIndex = -1;
        double minPatternScore = 900;
        for(int i = 0; i < NUMPATTERNS; i++)  {
            int patternIndex = i * 9;
            int patternScore = getPatternMatchScore(game, x, y, arrPattern, patternIndex);
            if(patternScore < minPatternScore)  {
                minPatternIndex = patternIndex;
                minPatternScore = patternScore;
            }
        }
        double[] results = {minPatternIndex, minPatternScore};
        return results;
    }
    */



    private static boolean isFrontier(MineSweeper game, int x, int y)  {
       for(int i = -1; i < 2; i++)  {
            for(int j = -1; j < 2; j++)  {
               if (!game.inBounds(x+i, y+j))  {
                  continue;
               }
               if (squareToInt(game.get(x + i, y + j)) == -2)  {
                  return true;
               }
            }
        }
        return false;       

    }

    private static Pattern organizePattern(int[] arrPattern)  {
       int patternIndex = 0;

       Pattern pattern = new Pattern();
       pattern.centers = new int [NUMPATTERNS];
       pattern.numSpacesPattern = new int [NUMPATTERNS];
       pattern.numFlaggedPattern = new int [NUMPATTERNS];
       pattern.selectors = new int [NUMPATTERNS];

       int [] centerIndicies = new int [NUMPATTERNS];
       int [] actions = new int [NUMPATTERNS];
       int [] actionIndicies = new int [NUMPATTERNS];
       int [] selectorIndicies = new int [NUMPATTERNS];


       
       int tempSpaces;
      int tempFlagged;
      
       for(int i=0; i<NUMPATTERNS; i++)  {
          patternIndex = 11*i;
         pattern.centers[i] = arrPattern[patternIndex+4];
         centerIndicies[i] = patternIndex +4;
         actions[i] = arrPattern[patternIndex+9];
         actionIndicies[i] = patternIndex +9;
         pattern.selectors[i] = arrPattern[patternIndex + 10];
         actionIndicies[i] = patternIndex +10;


         tempSpaces =0;
         tempFlagged =0;
         
         for(int j=0; j<11; j++)  {
            if (j==4 || j==9 || j==10)  {
               continue;
            }
            else if(arrPattern[i+j]==0)  {
               tempSpaces++;
            }
            else if(arrPattern[i+j]==2)  {
               tempFlagged++;
            }
            
         pattern.numSpacesPattern[i]=tempSpaces;
         pattern.numFlaggedPattern[i]=tempFlagged;
         


         }

       }
       return pattern;
    }


    private static int[] getNeighborsInfo(MineSweeper game, int i, int j)  {
       int tempBoardSpaces =0;
       int tempBoardFlagged =0;
       int tempBoardShown =0;
       for(int a = -1; a < 2; a++)  {
            for(int b = -1; b < 2; b++)  {
                  if (!game.inBounds(i+a, b+j)||(i == 0 && j == 0) )  {
                  continue;
                }
                if (squareToInt(game.get(i+a,b+j)) == -2)  {
                   tempBoardSpaces++;
                }
                if (squareToInt(game.get(i+a,b+j)) == 9)  {
                   tempBoardFlagged++;
                }
                else{
                   tempBoardShown++;
                }
            } 
        }
        int[] results = new int[3];
        results[0] = tempBoardSpaces;
        results[1] = tempBoardShown;
        results[2] = tempBoardFlagged;
        return results;      
    }

    
    private static double[] comparePattern(Pattern pattern, int boardCenter, int boardSpaces, int boardFlags)  {
       double patternFitness = 1;
       double[] patternFitnesses = new double[NUMPATTERNS];
       for(int i=0; i<NUMPATTERNS; i++)  {
          if(pattern.centers[i]           == boardCenter 
          && pattern.numSpacesPattern[i]  == boardSpaces 
          && pattern.numFlaggedPattern[i] == boardFlags) {
             patternFitness = pattern.numSpacesPattern[i] == 0? 2: (pattern.centers[i] - pattern.numFlaggedPattern[i]) / pattern.numSpacesPattern[i];
            //System.out.println("B " + boardCenter + " " + boardSpaces + " " + boardFlags + " " + i + " " + patternFitness);
            //System.out.println("P " + pattern.centers[i] + " " + pattern.numSpacesPattern[i] + " " + pattern.numFlaggedPattern[i] + " " + i + " " + patternFitness);
          }
          
          if(patternFitness < 0)  {
             patternFitness = 2;
          }
          patternFitnesses[i] = patternFitness;
       }
       return patternFitnesses;

    }

    private static double[] minPatternFitness(Pattern pattern, int boardCenter, int boardSpaces, int boardFlags)  {
       double minPF = 3;
       double currPF = 0;
       double currIndex = 0;
       double minIndex = 0;
       double [] compared = comparePattern(pattern, boardCenter,boardSpaces,boardFlags);
       for(int i = 0; i < NUMPATTERNS; i++)  {
          currPF = compared[i];

          //System.out.println("minPatternFitness" + minPF + " " + currPF + " " + currIndex + " " + minIndex + " ");
          if(currPF<minPF)  {
             minPF = currPF;
             minIndex = currIndex;
             
             //System.out.println("minPatternFitness" + minPF + " " + currPF + " " + currIndex + " " + minIndex + " " + i);
          }
          currIndex++;

       }
       
       
       compared[0] = minPF;
       compared[1] = minIndex*11;
       return compared;
    }   



    private static boolean actOnBestSquare(MineSweeper game, int[] chromoPatterns, int bestPatternIndex, int x, int y)  {
        if(x == -1 || y == -1)  {
            return game.clickOnRandomUnmodifiedSq(RandomRegistry.getRandom());
        }
        boolean hitBomb = false;

        int action = chromoPatterns[bestPatternIndex + 9];
        //System.out.println(action + " action");
        if(action == 1 && FITNESSTYPE == 0)  {
            game.flag(x, y);
        } else  {
            hitBomb = game.peek(x, y) == -1;
        }
        return hitBomb;
    }


    private static int getAdjacentSquareData(MineSweeper game, int x, int y, int[] spacesX, int[] spacesY)  {
         int spaceIndex = 0;
         for(int a = -1; a < 2; a++)  {
             for(int b = -1; b < 2; b++)  {
                int currX = x + a;
                int currY = y + b;

                if (!game.inBounds(currX, currY) || (a == 0 && b == 0))
                   continue;

                int actualSquareValue = squareToInt(game.get(currX, currY));
                if(actualSquareValue == -2)  {
                   spacesX[spaceIndex] = currX;
                   spacesY[spaceIndex] = currY;
                   spaceIndex++;

                   /*
                   System.out.println("squareInfo value " + actualSquareValue + " xCoord " + (currX) + " yCoord " + (currY));
                   for(int t= 0; t< 8; t++)  {
                       System.out.println("Spaces" + spacesX[t] + " " + spacesY[t]);
                    }
                    */
             }
         }
      }
      return spaceIndex;
    }

    private static boolean makeMove(MineSweeper game, int[] chromoPatterns, Pattern pattern)  {
        double bestPatFitness = 100;
        int bestPatternIndex = -1;
        int evalX = -1;
        int evalY = -1;
    
        // loop thorugh all of the spaces
        for(int i = 0; i < BOARDSIZE; i++)  {
            for(int j = 0; j < BOARDSIZE; j++)  {
                // skip if it's not a frontier
                if(game.get(i,j).flagged || !game.get(i,j).shown || !isFrontier(game,i,j))  {
                    continue;
                }

                int[] nInfo = getNeighborsInfo(game, i,j);
                double[] fitnessInfo = minPatternFitness(pattern, nInfo[0],nInfo[1],nInfo[2]);
                double minPatFitness = fitnessInfo[0];

                int minPatFitnessIndex = (int)fitnessInfo[1];
                if(minPatFitness < 1){
                	//System.out.println("coords " + i + " " + j + " " + minPatFitnessIndex);
                }

                // System.out.println("minPatFitness " + minPatFitness);
                // System.out.println("minPatFitnessIndex " + minPatFitnessIndex);

                int[] spacesX = new int[8];
                int[] spacesY = new int[8];
                int spaceIndex = getAdjacentSquareData(game, i, j, spacesX, spacesY);

                // if the pattern is above our running best, 
                // then we change our running best
                if(minPatFitness < bestPatFitness)  {
                    bestPatternIndex = minPatFitnessIndex;
                    bestPatFitness = minPatFitness;
                    int spaceSelector = pattern.selectors[minPatFitnessIndex / 11] % spaceIndex;
                    evalX = spacesX[spaceSelector];
                    evalY = spacesY[spaceSelector];
                }
            // System.out.println("Frontier Coords " + i + " , " + j);
            }
        }
        boolean hitBomb = actOnBestSquare(game, chromoPatterns, bestPatternIndex, evalX, evalY);
        //game.printBoard(false);
        return hitBomb;
    }

    /*
     * Given a chromosome pattern, plays the game.
     * @return the fitness
     */
    private static MineSweeper playGame(int[] chromoPatterns)  {
        // modulate the patterns to be <8, <2 .etc
        for (int i = 0; i < chromoPatterns.length; i++) {
                modArr(i, chromoPatterns);
        }

        Pattern pattern = organizePattern(chromoPatterns);
        MineSweeper game = new MineSweeper(BOARDSIZE, NUMMINES);
        boolean hitBomb = false;
        game.clickOnAZero();
       
        // game.printBoard(false);

        for (int i = 0; i < 90 && !hitBomb; i++) {
            hitBomb = makeMove(game, chromoPatterns, pattern);
        }

        // game.printBoard(false);

        return game;
    }


    /*
     * The eval function, takes a genetic thing,
     * outputs a fitness
     */
    private static Double evalMaybePrint(Genotype<IntegerGene> gt, int numEvalGames, boolean printGames)  {
        int[] chromoPattern = new int[11 * NUMPATTERNS];
        int i = 0;
        for(IntegerGene num : gt.getChromosome())  {
            chromoPattern[i] = num.intValue();
            i++;
        }
        int sumFitnesses = 0;

        for(int j = 0; j < numEvalGames; j++)  {
            MineSweeper game = playGame(chromoPattern);
            switch(FITNESSTYPE)  {
                case 0:
                    sumFitnesses += game.fitnessCalc();
                    break;
                case 1:
                    sumFitnesses += game.numTurns;
                    break;
            }
            if(printGames)  {
               game.printBoard(false);
            }
        }

        return (sumFitnesses + 0.0) / numEvalGames;
    }

    private static Double eval(Genotype<IntegerGene> gt)  {
        return evalMaybePrint(gt, NUMGAMES, false);
    }

    public static void main(String[] args)  {
        // 1.) Define the genotype (factory) suitable
        //     for the problem.
        Factory<Genotype<IntegerGene>> gtf = Genotype.of(IntegerChromosome.of(0,24, 11 * NUMPATTERNS));

        // 3.) Create the execution environment.
        Engine<IntegerGene, Double> engine = Engine
            .builder(EvoMine::eval, gtf)
            .maximizing()
            .selector(new TournamentSelector(8))
            .populationSize(POPSIZE)
            .build();
        
        final EvolutionStatistics<Double, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();

        // 4.) Start the execution (evolution) and
        //     collect the result.
        Genotype<IntegerGene> result = engine.stream()
            .limit(NUMGENERATIONS)
            .peek(statistics)
            .collect(EvolutionResult.toBestGenotype());
        
        System.out.println(statistics);
        //System.out.println("top result: " + result);
        
        /*
        Scanner in = new Scanner(System.in);
        
        boolean showThreeGames = true;
        while(showThreeGames)  {
            evalMaybePrint(result, 3, true);
            System.out.print("Show three more games?");
            showThreeGames = in.nextLine().toUpperCase().charAt(0) == 'Y';
        }   
        */
    }
}
