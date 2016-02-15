import pandas as pd
df = pd.read_csv('../../../resources/fakedata_pandas.csv')
print "age1 maximal: "+str(df["age1"].max()) + ", minimal: "+str(df["age1"].min())
print "age2 maximal: "+str(df["age2"].max()) + ", minimal: "+str(df["age2"].min())
print "height maximal: "+str(df["height"].max()) + ", minimal: "+str(df["height"].min())
print "weight maximal: "+str(df["weight"].max()) + ", minimal: "+str(df["weight"].min())
print "bloodpressureH maximal: "+str(df["bloodpressureH"].max()) + ", minimal: "+str(df["bloodpressureH"].min())
print "bloodpressureL maximal: "+str(df["bloodpressureL"].max()) + ", minimal: "+str(df["bloodpressureL"].min())
print "heartrate maximal: "+str(df["heartrate"].max()) + ", minimal: "+str(df["heartrate"].min())
