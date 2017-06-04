# EvoMine
	Sam Orfield && Jack Wines
Clicking is boring, evolve something to do it for you
### Compile with
```
javac MineSweeper.java -cp ../Evomine
javac EvoMine.java -cp ../Evomine
```
### Run
```
java Minesweeper
```
To play normal minesweeper.
```
java EvoMine
```
To evolve a minesweeper player.
```
java EvoMine baseline
```
To run the randomized baseline test.
### Adjust
If you’ll direct your attention to the top of `EvoMine.java`, you’ll find 7 parameters for the adjusting. You’ll have to recompile afterwards.
`NUMGAMES`: number of games each individual plays each generation
 `BOARDSIZE`: the length of one side of the board
`NUMMINES`: the number of mines on said board
`NUMPATTERNS`: the number of patterns in each individual
`FITNESSTYPE`
0 for # correctly flagged mines - #incorrectly flagged spaces
1 for percentage of board revealed
 `NUMGENERATIONS`: number of generations to run for
`POPSIZE`: the population size