import sys

with open(sys.argv[1]) as infile, open(sys.argv[2], 'w') as outfile:
	for line in infile:
		line.replace('\n','')
		if line.strip() :
			outfile.write(line.strip() + '\n')
