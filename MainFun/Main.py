import time

from Filtri import Filter_I
#from Filtri import Filter_II

start_time = time.time()

Filter_I.Call_Filter_1()
#Filter_II.Call_Filter_II()

end_time = time.time()
duration = end_time - start_time
print(f"Program completed in {duration:.2f} seconds")
