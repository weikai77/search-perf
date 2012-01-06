import json
import sys
if len(sys.argv) < 3:
	print("usage: input output max")
else:
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


