import pandas as pd

class AbstractQuery(object):
   
   def __init__(self, data_before, data_after, fields):
      self.data_before = data_before
      self.data_after = data_after
      self.fields = fields
      self.df_before = pd.read_csv(data_before,skiprows=1,header=None,delim_whitespace=True)
      self.df_before.columns = fields
      self.df_after = pd.read_csv(data_after,skiprows=1,header=None)
      self.df_after.columns = fields

   def run_query(self):
      pass
   
