#!/usr/bin/env python2.7

import subprocess
import os
import pandas as pd
from summary import Summary

def run_command(command):
    os.system(command)

def mean_std_cal(data_path, delim_whitespace=True):
    print data_path
    if delim_whitespace:
       df = pd.read_csv(data_path,skiprows=1,header=None,delim_whitespace=True)
    else:
       df = pd.read_csv(data_path,skiprows=1,header=None)
    return df.mean().tolist(), df.std().tolist(), df.corr()

def convert_epislon(ep):
    if ep == 0.1:
      return "01"
    elif ep == 0.2:
      return "02"
    elif ep == 0.4:
      return "04"
    elif ep == 1:
      return "1"
    elif ep == 2.0:
      return "2"

resource_path = "../../../resources/"
result_path = "../../../result/"
jar_path = "/root/trunk/newTest/"

statistic_result = os.listdir("%stest/" % (result_path))
for obj in statistic_result:
  os.remove("%stest/%s" % (result_path, obj))

test_round = 5
epsilons = [0.1, 0.2, 0.4, 1.0, 2.0]
#epsilons = [0.1, 0.2]
degree = 2
dimension = 5
data_size = "1M"
jar_name = "PrivBayes.jar"

source_file = "%sdata_%ddim_%s-coarse.csv" % (resource_path, dimension, data_size)
domain_file = "%sdata_%ddim_%s-coarse.domain.csv" % (resource_path, dimension, data_size)

for ep in epsilons:
    test_prefix = "%dD%s%sE%dK" % (dimension, data_size, convert_epislon(ep), degree)
    
    arguments = " %s %s %f %d %s" % (source_file, domain_file, ep, degree, convert_epislon(ep))
    command = "java -Xmx10g -jar %s%s %s" % (jar_path, jar_name, arguments)

    result_file = "%s%s_SyntheticData.csv" % (result_path, test_prefix)
    
    acc_check = Summary(dimension, data_size, ep, source_file, result_file)

    for round in range(test_round):
      # run the java command
      print "Command: %s, Round %d" % (command, round+1)
      run_command(command)

      # after privbayes, do the accuracy check
      acc_check.statistic_accuracy()
      acc_check.query_accuracy()

    acc_check.print_summaries("/privbayes_test/test_round_1")
