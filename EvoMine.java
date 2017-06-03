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
private static final int NUMPATTERNS = 70;
private static final int FITNESSTYPE = 0;
private static final int NUMGENERATIONS = 10;
private static final int POPSIZE = 3;
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
    	if(index == 5){
    		chromoPattern[index] = (chromoPattern[index]%8) + 1;
    	}
    	if (index == 10){
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
    private static int getPatternMatchScore(MineSweeper game, int x, int y, int[] arrPattern, int startingIndex)  {
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
     */
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



    /*
     * Given a chromosome pattern, plays the game.
     * @return the fitness
     */
    private static MineSweeper playGame2(int[] chromoPattern)  {
    	if(FRONTIERNEIGHBORS == 1)  {
    		for(int i= 0; i<10*NUMPATTERNS; i++)  {
    			modArr(i, chromoPattern);
    		}
    	}
        MineSweeper game = new MineSweeper(BOARDSIZE, NUMMINES);
        game.clickOnAZero();
        boolean hitBomb = false;
        for(int k = 0; k < NUMMINES && !hitBomb; k++)  {
            int bestX = -1;
            int bestY = -1;
            int evalX = -1;
            int evalY = -1;
            int [] spacesX = new int [7];
            int [] spacesY = new int [7];
            for(int i=0; i<7; i++)  {
            	spacesX[i] = -1;
            	spacesY[i] = -1;
            }  
            int minPattern = -1;
            double minPatternScore = 10000;
            for(int i = 0; i < BOARDSIZE; i++)  {
                for(int j = 0; j < BOARDSIZE; j++)  {
                    if(game.get(i,j).flagged || !game.get(i,j).shown || !isFrontier(game,i,j))  {
                        continue;
                    }
                    
                	System.out.println("N flagged, shown, frontier" + game.get(i,j)+ " coordinates " + i + " " + j);
                	game.printBoard(false);

                    double[] patternData = selectPattern2(game, i, j, chromoPattern);
                    System.out.println("Pattern Data" + patternData[0] + patternData[1]);

                    //System.out.println(patternData[1] + ", " + patternData[0]);    
                    //System.err.println("" + patternData[1]);
                    if(patternData[1] < minPatternScore)  {
                        //System.out.println("changeover");
                        minPatternScore = patternData[1];
                        minPattern = (int)patternData[0];
                        bestX = i;
                        bestY = j;
                        int spaceIndex = 0;
                        //for(int e=0; e<7; e++){
            			//		spacesX[e] = -1;
            			//		spacesY[e] = -1;
            			//	}  
                        for(int a = -1; a < 2; a++)  {
            				for(int b = -1; b < 2; b++)  {
               					if (!game.inBounds(i+a, b+j)||(i == 0 && j == 0) )  {
            						continue;
                    				}
                    			Square sq = game.get(i + a, j + b);
                				int actualSquareValue = squareToInt(game.get(i + a, j + b));
            					if (!sq.flagged && !sq.shown && actualSquareValue == -2)  {
            						spacesX[spaceIndex] = i+a;
            						spacesY[spaceIndex] = b+j;
            						spaceIndex++;                    					
           						}
                    		}
	                	}
		                int spaceSelector = RandomRegistry.getRandom().nextInt(spaceIndex+1);
		                evalX = spacesX[spaceSelector];
		                evalY = spacesY[spaceSelector];
		            }

		            int randTest = RandomRegistry.getRandom().nextInt(2);
		            //System.out.println("randTest" + randTest);

		            if(FITNESSTYPE != 1 && randTest == 1) // && chromoPattern[minPattern + 9] == 1)  
		            {
		                game.flag(evalX, evalY);
		                System.out.println("flagged bestX" + bestX + "bestY" + bestY + "evalX" + evalX + "evalY " + evalY);
		                System.out.println("randTest" + randTest);
		                for(int t= 0; t< 7; t++)  {
		                	System.out.println("Spaces" + spacesX[t] + " " + spacesY[t]);

		                }
		                int test40 = squareToInt(game.get(evalX,evalY));
		                System.out.println("SpaceValueFLAG" + test40);

		                
		            } else  {
		                //System.out.println("x: " + bestX + "y: " + bestY);
		                hitBomb = game.peek(evalX, evalY) == -1;
		                System.out.println("Clicked bestX" + bestX + "bestY" + bestY + "evalX" + evalX + "evalY" + evalY);
		                System.out.println("randTest" + randTest);
		                System.out.println("randTest" + randTest);
		                for(int t= 0; t< 7; t++)  {
		                	System.out.println("Spaces" + spacesX[t] + " " + spacesY[t]);
			            }
			            int test41 = squareToInt(game.get(evalX,evalY));
			            System.out.println("SpaceValueCLICK" + test41);
				        /*
				        System.out.println("k: " + k);
				        game.printBoard(false);
				        System.out.println(bestX + "");
				        System.out.println(bestY + "");
				        System.out.println(minPattern + ", minPatternIndex");
				        System.out.println(minPatternScore + ", minPatternScore");
				        */
			        }
		        }
        	}
        }
		return game;
	}
	private static MineSweeper playGame(int[] chromoPattern)  {
		MineSweeper game = new MineSweeper(BOARDSIZE, NUMMINES);
		boolean hitBomb = false;
        game.clickOnAZero();
        game.printBoard(false);
        for(int i = 0; i < BOARDSIZE; i++)  {
                for(int j = 0; j < BOARDSIZE; j++)  {
                    if(game.get(i,j).flagged || !game.get(i,j).shown || !isFrontier(game,i,j))  {
                        continue;

                    }
                    	int spaceIndex = 0;
                    	int [] spacesX = new int [8];
        				int [] spacesY = new int [8];
        				for(int o=0; o<8; o++)  {
            				spacesX[o] = -1;
            				spacesY[o] = -1;
            			}  

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

                				int spaceSelector = RandomRegistry.getRandom().nextInt(spaceIndex+1);
		                		int randTest = RandomRegistry.getRandom().nextInt(2);
		                		int evalX = spacesX[spaceSelector];
		                		int evalY = spacesY[spaceSelector];

		                		if(randTest == 1){
		                			game.flag(evalX, evalY);
		                			game.printBoard(false);
		                		}
		                		game.peek(evalX, evalY);
		                		game.printBoard(false);
                				
            					//if (!sq.flagged && !sq.shown && actualSquareValue == -2)  {
            					//	spacesX[spaceIndex] = i+a;
            					//	spacesY[spaceIndex] = b+j;
            					//	spaceIndex++;       	
                    			}
                    		}		

                   // System.out.println("Frontier Coords " + i + " , " + j);
                }
            }    

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
