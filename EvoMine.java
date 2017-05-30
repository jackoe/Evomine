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

public class EvoMine {
private static final int NUMGAMES = 10;
private static final int BOARDSIZE = 10;
private static final int NUMMINES = 20;
private static final int NUMPATTERNS = 70;
private static final int FITNESSTYPE = 1;
private static final int NUMGENERATIONS = 200;
private static final int POPSIZE = 1;
private static final int FRONTIERNEIGHBORS = 1;



    /*
     * Takes a square, converts it to an int.
     * int is -1 if bomb
     * int is 10 if flag
     */
    private static int squareToInt(Square sq)  {
        if(sq == null)  {
            return -1;
        }  else if(sq.flagged)  {
            return 9;
        }  else if(!sq.shown)  {
            return -1;
        }  else  {
            return sq.value;
        }
    }

    /*
     * takes a game, takes coords, returns the index and match score of the best pattern.
     * match score is the edit distance of one integer string to another
     * where one edit is classiied as +- 1 to one index
     */
    private static int getPatternMatchScoreOffset(MineSweeper game, int x, int y, int[] arrPattern, int startingIndex, int arrPatternIndexOffset)  {
        int deviationSoFar = 0;
        int arrPatternIndex = startingIndex;

        for(int i = -1; i < 2; i++)  {
            for(int j = -1; j < 2; j++)  {
                if(i == 0 && j == 0)  {
                    continue;
                }
                int actualSquareValue = squareToInt(game.get(x + i, y + j));
                int predictedSquareValue = arrPattern[arrPatternIndex + arrPatternIndex];
                deviationSoFar += Math.abs(actualSquareValue - predictedSquareValue);
                arrPatternIndex++;
            }
        }
        return deviationSoFar;

    }
    private static int getPatternMatchScore(MineSweeper game, int x, int y, int arrPattern, int startingIndex)  {
        int bestDeviationSoFar = 10000;
        for(int i = 0; i < 4; i++)  {
        }
    }

    private static double getPatternMatchScore2(MineSweeper game, int x, int y, int[] arrPattern, int startingIndex)  {
        int arrPatternIndex = startingIndex;
        int numSpacesGame=0;
        int numFlaggedGame=0;
        int numSpacesPattern=0;
        int numFlaggedPattern=0;
        double patternFitness=10;
        int centervalue = -2;

        for(int i = -1; i < 2; i++)  {
            for(int j = -1; j < 2; j++)  {
            	int actualSquareValue = squareToInt(game.get(x + i, y + j));
                int predictedSquareValue = arrPattern[arrPatternIndex];
                if(i == 0 && j == 0)  {
               	centervalue = actualSquareValue;
                if (actualSquareValue != predictedSquareValue){
                	return 1;
                }    
               
                if(actualSquareValue==9){
                	numFlaggedGame++;
                }
                if(actualSquareValue==-1){
                	numSpacesGame++;
                }
                if(predictedSquareValue==9){
                	numFlaggedPattern++;
                }
                if(predictedSquareValue==-1){
                	numFlaggedPattern++;
                }
                arrPatternIndex++;
            	}
               }
        }
        if(numFlaggedPattern == numFlaggedGame && numSpacesGame == numSpacesPattern){
        			//System.out.println("match " + Arrays.toString(arrPattern) + " " + arrPatternIndex);
        			int correctedcentervalue = centervalue - numFlaggedGame;
					if(correctedcentervalue == numSpacesGame){
						patternFitness = 0;

						return patternFitness;
					}
					else {
						patternFitness = ((double)correctedcentervalue/numSpacesGame);
						return patternFitness;
					}
                	


                }
                return 1;

    }

    /*
     * Given a game, selects the best pattern to use for a given pair
     * returns both the best pattern index and its index.
     */
    private static int[] selectPattern(MineSweeper game, int x, int y, int[] arrPattern )  {
       
        int minPatternIndex = -1;
        int minPatternScore = 900;
        for(int i = 0; i < NUMPATTERNS; i++)  {
            int patternIndex = i * 9;
            int patternScore = getPatternMatchScore(game, x, y, arrPattern, patternIndex);
            if(patternScore < minPatternScore)  {
                minPatternIndex = patternIndex;
                minPatternScore = patternScore;
            }
        }
        int[] results = {minPatternIndex, minPatternScore};
        return results;
    }


private static double[] selectPattern2(MineSweeper game, int x, int y, int[] arrPattern )  {
       
        int zero = 0;
        double minPatternScore = 900;
        double minPatternIndex = -1;
        
        for(int i = 0; i < NUMPATTERNS; i++)  {
            int patternIndex = i * 9;
            double patternScore = getPatternMatchScore2(game, x, y, arrPattern, patternIndex);
            if (patternScore < 1){
            System.out.println("patternscore " + patternScore);
        }

            if(patternScore < minPatternScore)  {
                minPatternIndex = patternIndex;
                minPatternScore = patternScore;
            }
        }
        double[] results = {minPatternIndex, minPatternScore};
        return results;
    }



    /*
     * Given a chromosome pattern, plays the game.
     * @return the fitness
     */
    private static MineSweeper playGame(int[] chromoPattern)  {
        MineSweeper game = new MineSweeper(BOARDSIZE, NUMMINES);
        game.clickOnAZero();
        boolean hitBomb = false;
        for(int k = 0; k < 70 && !hitBomb; k++)  {
            int bestX = -1;
            int bestY = -1;
            int minPattern = -1;
            double minPatternScore = 10000;
            for(int i = 0; i < BOARDSIZE; i++)  {
                for(int j = 0; j < BOARDSIZE; j++)  {
                   
                    if(game.get(i,j).flagged
                    || (FRONTIERNEIGHBORS != 1
                    && game.get(i,j).shown))  {
                        continue;
                    }
                   
                    double[] patternData = {-1, -1};
                    if (FRONTIERNEIGHBORS==1){
                   	
                    patternData = selectPattern2(game, i, j, chromoPattern);
                    

                	//else
                }

                    //System.out.println(patternData[1] + ", " + patternData[0]);    
                    //System.err.println("" + patternData[1]);
                    if(patternData[1] < minPatternScore)  {
                        //System.out.println("changeover");
                        minPatternScore = patternData[1];
                        minPattern = (int)patternData[0];
                        bestX = i;
                        bestY = j;
                    }
                }
            }

            if(FITNESSTYPE != 1 && chromoPattern[minPattern + 9] >= 5)  {
                game.flag(bestX, bestY);
            } else  {
                //System.out.println("x: " + bestX + "y: " + bestY);
                hitBomb = game.peek(bestX, bestY) == -1;
            }
        /*
        System.out.println("k: " + k);
        game.printBoard(false);
        System.out.println(bestX + "");
        System.out.println(bestY + "");
        System.out.println(minPattern + ", minPatternIndex");
        System.out.println(minPatternScore + ", minPatternScore");
        */
        }
        
            return game;
    }

    /*
     * The eval function, takes a genetic thing,
     * outputs a fitness
     */
    private static Integer evalMaybePrint(Genotype<IntegerGene> gt, int numEvalGames, boolean printGames) {
        int[] chromoPattern = new int[10 * NUMPATTERNS];
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

        return sumFitnesses;
    }

    private static Integer eval(Genotype<IntegerGene> gt)  {
        return evalMaybePrint(gt, NUMGAMES, false);
    }

    public static void main(String[] args) {
        // 1.) Define the genotype (factory) suitable
        //     for the problem.
        Factory<Genotype<IntegerGene>> gtf = null;
        	if (FRONTIERNEIGHBORS == 0){
            gtf = Genotype.of(IntegerChromosome.of(-1,9, 9 * NUMPATTERNS));
        }
        else {
        	gtf = Genotype.of(IntegerChromosome.of(-1,9, 10 * NUMPATTERNS));
        }

        // 3.) Create the execution environment.
        Engine<IntegerGene, Integer> engine = Engine
            .builder(EvoMine::eval, gtf)
            .maximizing()
            .selector(new TournamentSelector(4))
            .populationSize(POPSIZE)
            .build();
        
        final EvolutionStatistics<Integer, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();

        // 4.) Start the execution (evolution) and
        //     collect the result.
        Genotype<IntegerGene> result = engine.stream()
            .limit(NUMGENERATIONS)
            .peek(statistics)
            .collect(EvolutionResult.toBestGenotype());
        
        System.out.println(statistics);
        System.out.println("top result: " + result);

        Scanner in = new Scanner(System.in);
        
        boolean showThreeGames = true;
        while(showThreeGames)  {
            evalMaybePrint(result, 3, true);
            System.out.print("Show three more games?");
            showThreeGames = in.nextLine().toUpperCase().charAt(0) == 'T';
        }
        
    }
}
