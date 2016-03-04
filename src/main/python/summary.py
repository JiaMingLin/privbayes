from query_factory import QueryFactory
import pandas as pd

class Summary(object):
   def __init__(self, dim, size, ep, data_before, data_after):
      self.dim = dim
      self.size = size
      self.ep = ep
      self.data_before = data_before
      self.df_before = pd.read_csv(self.data_before,skiprows=1,header=None,delim_whitespace=True)
      self.data_after = data_after
      self.census_mean = None
      self.census_std = None
      self.census_corr = None
      self.cols = self.attr_gen(self.dim)
      self.df_mean = pd.DataFrame(columns = self.cols)
      self.df_std = pd.DataFrame(columns = self.cols)
      self.df_corr_list = []
      self.q_result_df_init = False
      self.df_query_accuracy = None
      self.df_q_before_count = None
      self.df_q_after_count = None

   def query_accuracy(self):
      factory = QueryFactory(self.dim, self.size, self.ep, self.data_before, self.data_after)
      query = factory.create_query()      
      # result of different queries are in list of data frame
      result_bf_ls, result_af_ls = query.run_query()
      diff_rate_list = [self.diff_query_rate(df_tuple[0], df_tuple[1]) for df_tuple in zip(result_bf_ls, result_af_ls)]
      attrs = self.attr_gen(len(diff_rate_list))

      # initialize data frame for query results.
      if self.q_result_df_init == False:
         self.df_query_accuracy = pd.DataFrame(columns=attrs)
         self.df_q_before_count = pd.DataFrame(columns=attrs)
         self.df_q_after_count = pd.DataFrame(columns=attrs)
         self.q_result_df_init = True

      self.df_query_accuracy = self.df_query_accuracy.append({attrs[i]: diff_rate_list[i] for i in range(len(diff_rate_list))}, ignore_index = True)
      self.df_q_before_count = self.df_q_before_count.append({attrs[i]: self.row_count(result_bf_ls[i]) for i in range(len(result_bf_ls))}, ignore_index = True)
      self.df_q_after_count = self.df_q_after_count.append({attrs[i]: self.row_count(result_af_ls[i]) for i in range(len(result_af_ls))}, ignore_index = True)
         
   def statistic_accuracy(self):
      if self.census_mean == None:
         self.census_mean, self.census_std, self.census_corr = self.mean_std_cal(self.data_before)
      synthetic_mean, synthetic_std, synthetic_corr = self.mean_std_cal(self.data_after, False)
      self.df_corr_list.append(synthetic_corr)
      # calculate the difference
      diff_mean = {self.cols[i]:(synthetic_mean[i] - self.census_mean[i])/self.census_mean[i] for i in range(self.dim)}
      diff_std = {self.cols[i]:(synthetic_std[i] - self.census_std[i])/self.census_std[i] for i in range(self.dim)}
      
      self.df_mean = self.df_mean.append(diff_mean, ignore_index = True)
      self.df_std = self.df_std.append(diff_std, ignore_index = True)
   
   def attr_gen(self, num):
      return ["v%d" % i for i in range(num)] 
       
   def diff_query_rate(self, result_before, result_after):
      return float(self.row_count(result_before) - self.row_count(result_after))/float(self.row_count(self.df_before))

   def row_count(self, df):
      return len(df.index)

   def mean_std_cal(self, data_path, delim_whitespace=True):
      print data_path
      if delim_whitespace:
         df = pd.read_csv(data_path,skiprows=1,header=None,delim_whitespace=True)
      else:
         df = pd.read_csv(data_path,skiprows=1,header=None)
      return df.mean().tolist(), df.std().tolist(), df.corr()

   def generate_report(self):
      context = ("================== Result for test case: epislon = %(ep)s  ================ \n"
                 "The average accuracy of mean: %(mean_accuracy)s \n"
                 "The average accuracy of std: %(std_accuracy)s \n"
                 "The average accuracy of corr: \n"
                 "%(corr_accuracy)s \n\n"
                 "The average accuracy of queries: \n"
                 "%(q_accuracy)s \n\n")
      diff_corr = [(df.subtract(self.census_corr)).div(self.census_corr) for df in self.df_corr_list]
      context = context % {
                 'ep': self.ep,
                 'mean_accuracy': str(self.df_mean.mean().tolist()),
                 'std_accuracy': str(self.df_std.mean().tolist()),
                 'corr_accuracy': str(reduce(lambda df1, df2: df1.add(df2), diff_corr)/len(diff_corr)),
                 'q_accuracy': str(self.df_query_accuracy.mean().tolist())}

      inter_statistic = ("===================== Intermediate Statistic Data: epislon = %(ep)s ========================== \n"
                         "Census mean: %(census_mean)s \n"
                         "Census std: %(census_std)s \n"
                         "Census corr: \n"
                         "%(census_corr)s \n\n"
                         "Synthetic means: \n"
                         "%(df_mean)s \n"
                         "Synthetic stds: \n"
                         "%(df_std)s \n"
                         "Synthetic correlations: \n"
                         "%(df_corr)s \n\n"
                         "Customize queries results count: \n"
                         "Census\n"
                         "%(df_q_before_count)s \n"
                         "Synthetic \n"
                         "%(df_q_after_count)s \n\n")
      df_corr_str_ls = [str(df) for df in self.df_corr_list]
      inter_statistic = inter_statistic % {
                 'ep': self.ep,
                 'census_mean': str(self.census_mean),
                 'census_std': str(self.census_std),
                 'census_corr': str(self.census_corr),
                 'df_mean': str(self.df_mean),
                 'df_std': str(self.df_std),
                 'df_corr': "\n\n".join(df_corr_str_ls),
                 'df_q_before_count': str(self.df_q_before_count),
                 'df_q_after_count': str(self.df_q_after_count)}
      return context, inter_statistic
      
   def print_summaries(self, path=None):
      context, inter_statistic = self.generate_report()
      if path != None:
         with open(path, "a+") as output_file:
            output_file.write(context)

         with open(path+"_inter", "a+") as output_file_inter:
            output_file_inter.write(inter_statistic)
      else:
         print context
         print inter_statistic
  
