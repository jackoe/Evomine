
for name in ['Tournament12.txt', 'Tournament4.txt', 'Tournament8.txt', 'StochasticUniversal.txt']:
    f = open(name, 'r')
    lines = []

    for line in f:
        splitLine = line.split();
        if len(splitLine) > 1 and splitLine[1] == ('max'):
            lines.append(float(splitLine[3]))

    print name, lines
    f.close()

