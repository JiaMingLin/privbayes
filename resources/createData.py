#!/usr/bin/python
import random
import pathlib
import os

file_path = "ExCensus.dat"
data_num = 50000
path = pathlib.Path(file_path)
if path.is_file():
  os.remove(file_path)

# Open a file
output = open(file_path, "w")
output.write(str(data_num))

for i in xrange(1, data_num+1):
  line = "\n%d %d %d" % (random.randint(1, 100), random.randint(200, 300), random.randint(300, 400))
  print "output line: %s" % (line)
  output.write(line)


# Close opend file
output.close()
