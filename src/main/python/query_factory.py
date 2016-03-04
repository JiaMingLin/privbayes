import abstract_query
from dim5_1M_query import FiveDim1M

class QueryFactory(object):
  
  def __init__(self, dim, size, ep, data_before, data_after):
    self.dim = dim
    self.size = size
    self.ep = ep
    self.data_before = data_before
    self.data_after = data_after

  def create_query(self):
    if self.dim == 5 and self.size == "1M":
       return FiveDim1M(self.data_before, self.data_after)
    else:
       raise Exception("Case not support for dim=%d and size=%s" % (self.dim, self.size))

