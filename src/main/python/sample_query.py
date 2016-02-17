import pandas as pd

path = "../../../"
total = 40000
census_data_path = path+"resources/fakedata_pandas.csv"
synthetic_data_path = path + "result/FakeSyntheticData.csv"
columns=['age1', 'age2', 'height', 'weight', 'bloodpressureH', 'bloodpressureL', 'heartrate']

census_df = pd.read_csv(census_data_path,skiprows=1,header=None)
synthetic_df = pd.read_csv(synthetic_data_path,skiprows=1,header=None)

census_df.columns = columns
synthetic_df.columns = columns


def query0(df):
    return df[(df['age1'] > 50) & (df['age2'] > 50)]


def query1(df):
    return df[(df['age1'] > 50) & (df['bloodpressureH'] > 120)]


def query2(df):
    return df[(df['weight'] > 59) & (df['bloodpressureH'] > 120)]

def defference(census, synthetic):
    return census[0] - synthetic[0]

#def persent():
#    retrun 

print "Query age1 > 50 and age2 > 50"
q0_census = query0(census_df).shape
q0_synthetic = query0(census_df).shape
print "Census case: " + str(q0_census[0])
print "Synthetic case: " + str(q0_synthetic[0])
print "Delta: "+ str(defference)

print "Query age1 > 50 and bloodpressureH > 120"
print "Census case: " + str(query1(census_df).shape)
print "Synthetic case: " + str(query1(synthetic_df).shape)

print "Query weight > 59 and bloodpressureH > 120"
print "Census case: " + str(query2(census_df).shape)
print "Synthetic case: " + str(query2(synthetic_df).shape)
