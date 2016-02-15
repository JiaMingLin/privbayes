import pandas as pd
path = "../../../"
census_data_path = path+"resources/fakedata_pandas.csv"
synthetic_data_path = path + "result/FakeSyntheticData.csv"

print "===================Census Data=================="
census_df = pd.read_csv(census_data_path,skiprows=1,header=None)

census_mean_list = census_df.mean().tolist()
census_std_list = census_df.std().tolist()
census_corr = census_df.corr()

print "Census Mean " + str(census_mean_list)
print "Census STD " + str(census_std_list)
print "Census Data Correlations"
print census_corr
print

print "===================Synthetic Data=================="
synthetic_df = pd.read_csv(synthetic_data_path,skiprows=1,header=None)

synthetic_mean_list = synthetic_df.mean().tolist()
synthetic_std_list = synthetic_df.std().tolist()
synthetic_corr = synthetic_df.corr()

print "Synthetic Mean " + str(synthetic_mean_list)
print "Synthetic STD " + str(synthetic_std_list)
print "Synthetic Data Correlations"
print synthetic_corr
print

print "===================Summary======================="
mean_delta_list = [((synthetic_mean_list[i]-census_mean_list[i])/census_mean_list[i])*100.0 for i in range(7)]
std_delta_list = [((synthetic_std_list[i]-census_std_list[i])/census_std_list[i])*100.0 for i in range(7)]
print "Mean Delta: "+str([str(round(m,2))+'%' for m in mean_delta_list])
print "STD Delta: "+str([str(round(s,2))+'%' for s in std_delta_list])
print "Correlations Delta"
print synthetic_corr.subtract(census_corr).div(census_corr)
