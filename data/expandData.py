import json
import sys

if len(sys.argv) < 4:
	print("usage 1: python expandData.py input output max")
	print("usage 2: python expandData.py input output offset count")
elif len(sys.argv) == 4:
	inName = sys.argv[1]
	outName = sys.argv[2]
	max = int(sys.argv[3])
	counter = 0
	outfile = open(outName,'w+')
	lines = open(inName,'r').readlines();
	while counter < max:
		idx = counter% len(lines)
		obj = json.loads(lines[idx])
		obj['id'] = counter
		modifiedLine = json.dumps(obj)
		outfile.write(modifiedLine+"\n")
		counter = counter+1
elif len(sys.argv) == 5:
	inName = sys.argv[1]
	outName = sys.argv[2]
	start = int(sys.argv[3])
	max = int(sys.argv[4])+start
	counter = start
	outfile = open(outName,'w+')
	lines = open(inName,'r').readlines();
	while counter < max:
		idx = counter% len(lines)
		obj = json.loads(lines[idx])
		obj['id'] = counter
		modifiedLine = json.dumps(obj)
		outfile.write(modifiedLine+"\n")
		counter = counter+1
