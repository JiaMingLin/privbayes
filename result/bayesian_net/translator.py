def interval_split(maxi, mini, grid):
   sub_intervals = []
   step = float(maxi - mini)/float(grid)
   for i in xrange(0, grid):
      sub = (mini+(float(i)*step), mini+(float(i+1)*step)) 
      sub_intervals.append(sub)
   return sub_intervals
      
attr_file = open("Data2.domain_desc", "r")

attr_dict = dict()

line = attr_file.readline()
pos = 0
while(len(line)>0):
   attr_arr = line.split(' ')
   if attr_arr[1] == 'C':
      grid = int(attr_arr[-1])
      mini = float(attr_arr[2])
      maxi = float(attr_arr[3])
      sub_intervals = interval_split(maxi, mini, grid)
      for sub in sub_intervals:
         pos += 1
         attr_dict[pos] = sub
   else:
      for string in attr_arr[2:]:
         pos += 1
         attr_dict[pos] = string
   line = attr_file.readline()

for key in attr_dict.keys():
   print (key, attr_dict[key])
