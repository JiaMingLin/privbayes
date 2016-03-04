from abstract_query import AbstractQuery

fields = ["HIGHT", "WEIGHT", "BMI", "DBP", "SBP"]

queries=[
lambda df: df[(df['HIGHT']>170) & (df['WEIGHT']>66)],
lambda df: df[(df['DBP']<110) & (df['SBP']>110)],
lambda df: df[(df['HIGHT']>=140) & (df['HIGHT']<160)],
lambda df: df[(df['HIGHT']>=160) & (df['HIGHT']<180)],
lambda df: df[(df['HIGHT']>=180) & (df['HIGHT']<200)],
lambda df: df[(df['WEIGHT']>=40) & (df['WEIGHT']<65)],
lambda df: df[(df['WEIGHT']>=65) & (df['WEIGHT']<95)],
lambda df: df[(df['WEIGHT']>=95) & (df['WEIGHT']<120)],
lambda df: df[(df['BMI']>=13) & (df['BMI']<22)],
lambda df: df[(df['BMI']>=22) & (df['BMI']<33)],
lambda df: df[(df['BMI']>=33) & (df['BMI']<49)]
]

class FiveDim1M(AbstractQuery):
   def __init__(self, data_before, data_after):
      super(FiveDim1M, self).__init__(data_before, data_after,fields)
      pass

   def run_query(self):
      print "Run the queries for dim=5, and 1 million data"
      result_before_df_list = []
      result_after_df_list = []
      for q in queries:
         result_before_df_list.append(q(self.df_before))
         result_after_df_list.append(q(self.df_after))
      return result_before_df_list, result_after_df_list

