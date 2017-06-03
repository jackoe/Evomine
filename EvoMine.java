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

public class EvoMine {
private static final int NUMGAMES = 10;
private static final int BOARDSIZE = 8;
private static final int NUMMINES = 20;
private static final int NUMPATTERNS = 700;
private static final int FITNESSTYPE = 0;
private static final int NUMGENERATIONS = 100;
private static final int POPSIZE = 100;
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
            return -2;
        }  else  {
            return sq.value;
        }
    }

    private static void modArr(int index, int[] chromoPattern){    	
    	if(index%10 == 4){
    		chromoPattern[index] = (chromoPattern[index]%8) + 1;
    	}
    	if (index%10 == 9){
    		chromoPattern[index] = chromoPattern[index]%2;
    	} else  {
    		chromoPattern[index] = chromoPattern[index]%3;
    	}


    }

    /*
     * takes a game, takes coords, returns the index and match score of the best pattern.
     * match score is the edit distance of one integer string to another
     * where one edit is classiied as +- 1 to one index
     */
    private static int getPatternMatchScore3(MineSweeper game, int x, int y, int[] arrPattern, int startingIndex)  {
        int deviationSoFar = 0;
        int arrPatternIndex = startingIndex;

        for(int i = -1; i < 2; i++)  {
            for(int j = -1; j < 2; j++)  {
                if(i == 0 && j == 0)  {
                    continue;
                }
                int actualSquareValue = squareToInt(game.get(x + i, y + j));
                int predictedSquareValue = arrPattern[arrPatternIndex];
                deviationSoFar += Math.abs(actualSquareValue - predictedSquareValue);
                arrPatternIndex++;
            }
        }
        return deviationSoFar;

    }
    /*
    private static int getPatternMatchScore(MineSweeper game, int x, int y, int[] arrPattern, int startingIndex)  {
        int bestDeviationSoFar = 10000;
        for(int i = 0; i < 4; i++)  {

        }
    }
    */

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
                if (actualSquareValue != predictedSquareValue)  {
                        return 1;
                }    
               
                if(actualSquareValue==9)  {
                    numFlaggedGame++;
                }
                if(actualSquareValue==-2)  {
                    numSpacesGame++;
                }
                if(predictedSquareValue==2)  {
                    numFlaggedPattern++;
                }
                if(predictedSquareValue==0)  {
                    numSpacesPattern++;
                }
                arrPatternIndex++;
                }
               }
        }    
        if(numFlaggedPattern == numFlaggedGame && numSpacesGame == numSpacesPattern)  {
           //System.out.println("match " + Arrays.toString(arrPattern) + " " + arrPatternIndex);
           int correctedcentervalue = centervalue - numFlaggedGame;
           if(correctedcentervalue == numSpacesGame)  {
                patternFitness = 0;
                return patternFitness;
            }
                else  {
                patternFitness = ((double)correctedcentervalue/numSpacesGame);
                return patternFitness;
            }
        }
        return 1;
    }

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


private static double[] selectPattern2(MineSweeper game, int x, int y, int[] arrPattern )  {
       
        int zero = 0;
        double minPatternScore = 900;
        double minPatternIndex = -1;
        
        for(int i = 0; i < NUMPATTERNS; i++)  {
            int patternIndex = i * (10);
            double patternScore = getPatternMatchScore2(game, x, y, arrPattern, patternIndex);
            if (patternScore < 1)  {
            //System.out.println("patternscore " + patternScore);
        }

            if(patternScore < minPatternScore)  {
                minPatternIndex = patternIndex;
                minPatternScore = patternScore;
            }
        }
        double[] results = {minPatternIndex, minPatternScore};
        for(int i = 0; i <10; i++){
        	System.out.println("Best Pattern at" + i + " " + arrPattern[(int)minPatternIndex+i]);
        }
        System.out.println("minpatternscore" + minPatternScore);
        return results;
    }

    private static boolean isFrontier(MineSweeper game, int x, int y){
    	for(int i = -1; i < 2; i++)  {
            for(int j = -1; j < 2; j++)  {
            	if (!game.inBounds(x+i, y+j)){
            		continue;
            	}
            	if (squareToInt(game.get(x + i, y + j)) == -2){
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

    	int [] centerIndicies = new int [NUMPATTERNS];
    	int [] actions = new int [NUMPATTERNS];
    	int [] actionIndicies = new int [NUMPATTERNS];


    	
    	int tempSpaces;
		int tempFlagged;
		
    	for(int i=0; i<NUMPATTERNS; i++){
    		patternIndex = 10*i;
			pattern.centers[i] = arrPattern[patternIndex+4];
			centerIndicies[i] = patternIndex +4;
			actions[i] = arrPattern[patternIndex+9];
			actionIndicies[i] = patternIndex +9;
			tempSpaces =0;
			tempFlagged =0;
			
			for(int j=0; j<10; j++){
				if (j==4 || j==9){
					continue;
				}
				else if(arrPattern[i+j]==0){
					tempSpaces++;
				}
				else if(arrPattern[i+j]==2){
					tempFlagged++;
				}
				
			pattern.numSpacesPattern[i]=tempSpaces;
			pattern.numFlaggedPattern[i]=tempFlagged;
			


			}

    	}
    	return pattern;
    }

    private static int[] getNeighborsInfo(MineSweeper game, int i, int j){
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

    
    private static double[] comparePattern(Pattern pattern, int boardCenter, int boardSpaces, int boardFlags){
    	double patternFitness = 1;
    	double[] patternFitnesses = new double[NUMPATTERNS];
    	for(int i=0; i<NUMPATTERNS; i++){
    		if(pattern.centers[i] 		    == boardCenter 
    		&& pattern.numSpacesPattern[i]  == boardSpaces 
    		&& pattern.numFlaggedPattern[i] == boardFlags) {
    			patternFitness = (pattern.centers[i] - pattern.numFlaggedPattern[i]) / pattern.numSpacesPattern[i];
    			System.out.println("B " + boardCenter + " " + boardSpaces + " " + boardFlags + " " + i + " " + patternFitness);
    			System.out.println("P " + pattern.centers[i] + " " + pattern.numSpacesPattern[i] + " " + pattern.numFlaggedPattern[i] + " " + i + " " + patternFitness);
    		}
    		
    		if(patternFitness < 0){
    			patternFitness = 2;
    		}
    		patternFitnesses[i] = patternFitness;
    		
    	}
    	return patternFitnesses;

    }

    private static double[] minPatternFitness(Pattern pattern, int boardCenter, int boardSpaces, int boardFlags){
    	double minPF = 3;
    	double currPF = 0;
    	double currIndex = 0;
    	double minIndex = 0;
    	double [] compared = comparePattern(pattern, boardCenter,boardSpaces,boardFlags);
    	for(int i = 0; i < NUMPATTERNS; i++){
    		currPF = compared[i];
    		if(currPF<minPF){
    			minPF = currPF;
    			minIndex = currIndex;
    			currIndex++;
    		}

    	}
    	
    	
    	compared[0] = minPF;
    	compared[1] = minIndex*10;
    	return compared;
    }	



    private static void actOnBestSquare(MineSweeper game, int[] chromoPatterns, int x, int y, int bestPatternIndex, int evalX, int evalY)  {
		int[] spacesX = new int [8];
		int[] spacesY = new int [8];
		for(int i=0; i<8; i++)  {
			spacesX[i] = -1;
			spacesY[i] = -1;
		}

		int action = chromoPatterns[bestPatternIndex + 9];
	    if(action == 1)  {
			game.flag(evalX, evalY);
		} else  {
			game.peek(evalX, evalY);
		}
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
		int [] nInfo = new int [3];
		
		MineSweeper game = new MineSweeper(BOARDSIZE, NUMMINES);
        game.clickOnAZero();
        game.printBoard(false);

        int bestX = -1;
        int bestY = -1;
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

            	int minPatternFitnessIndex = 0;
            	int spaceIndex = 0;

                nInfo = getNeighborsInfo(game, i,j);
                double[] fitnessInfo = minPatternFitness(pattern, nInfo[0],nInfo[1],nInfo[2]);
                double minPatFitness = fitnessInfo[0];
                int minPatFitnessIndex = (int)fitnessInfo[1];
                System.out.println("minPatFitness " + minPatFitness);
                System.out.println("minPatFitnessIndex " + minPatFitnessIndex);

                int[] spacesX = new int [8];
				int[] spacesY = new int [8];
            	for(int a = -1; a < 2; a++)  {
    				for(int b = -1; b < 2; b++)  {
       					if (!game.inBounds(i+a, b+j)||(i == 0 && j == 0) )  {
    						continue;
            				}
            			//Square sq = game.get(i+a, j+b);
        				int actualSquareValue = squareToInt(game.get(i + a, j + b));
        				if(actualSquareValue == -2){
        					spacesX[spaceIndex] = i+a;
    						spacesY[spaceIndex] = b+j;
    						spaceIndex++;  

        					System.out.println("squareInfo value " + actualSquareValue + " xCoord " + (i+a) + " yCoord " + (b+j));
        					for(int t= 0; t< 8; t++)  {
                				System.out.println("Spaces" + spacesX[t] + " " + spacesY[t]);

                			}
        				}
            		}
            	}

        		// if the pattern is above our running best, 
        		// then we change our running best
				if(minPatFitness < bestPatFitness)  {
					bestPatternIndex = minPatFitnessIndex;
					bestPatFitness = minPatFitness;
					bestX = i;
					bestY = j;
					int spaceSelector = RandomRegistry.getRandom().nextInt(spaceIndex+1);
					evalX = spacesX[spaceSelector];
					evalY = spacesY[spaceSelector];
				}
            // System.out.println("Frontier Coords " + i + " , " + j);
            }
        }
	    actOnBestSquare(game, chromoPatterns, bestX, bestY, bestPatternIndex, evalX, evalY);
		game.printBoard(false);
		return game;
	}


    /*
     * The eval function, takes a genetic thing,
     * outputs a fitness
     */
    private static Integer evalMaybePrint(Genotype<IntegerGene> gt, int numEvalGames, boolean printGames)  {
        int[] chromoPattern = new int[(FRONTIERNEIGHBORS == 1? 10: 9) * NUMPATTERNS];
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
            //if(printGames)  {
               // game.printBoard(false);
           // }
        }

        return sumFitnesses;
    }

    private static Integer eval(Genotype<IntegerGene> gt)  {
        return evalMaybePrint(gt, NUMGAMES, false);
    }

    public static void main(String[] args)  {
        // 1.) Define the genotype (factory) suitable
        //     for the problem.
        Factory<Genotype<IntegerGene>> gtf = null;
        if (FRONTIERNEIGHBORS == 0)  {
            gtf = Genotype.of(IntegerChromosome.of(-1,9, 9 * NUMPATTERNS));
        }  else {
            gtf = Genotype.of(IntegerChromosome.of(0,24, 10 * NUMPATTERNS));
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
