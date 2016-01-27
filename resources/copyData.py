#!/usr/bin/python

# Open a file
fo = open("Train2.dat", "r")
fo2 = open("Train2_1.dat", "w")

for i in xrange(1,2511):
  line = fo.readline()
  print "Read Line: %s" % (line)
  fo2.write(line)


# Close opend file
fo.close()
fo2.close()
