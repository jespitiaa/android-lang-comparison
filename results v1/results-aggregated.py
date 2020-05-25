import pandas as pd
import time
import os
import matplotlib.pyplot as plt
import csv

def read_avg(proj, alg):
    filename = './agg-'+proj+'.'+alg+'.txt'
    #dd-mm, time, pid, pid, logLvl, tag, timestamp, runtime total,
    #runtime alloc, native alloc, pss
    with open (filename, 'r') as agg:
        two_lin_toggle = False
        last_time=-1
        cur_pid=-1
        elapsed_times=[]
        pid=[]
        runtime_start=[]
        runtime_end=[]
        native_start=[]
        native_end=[]
        pss_start=[]
        pss_end=[]
        
        for line in agg:
            content=line.split()
            if(len(content) > 0):
                if not two_lin_toggle:
                    if '-START' in content[5]:
                        cur_pid = content[2]
                        last_time = content[6]
                        runtime_start.append(int(content[8]))
                        native_start.append(int(content[9]))
                        pss_start.append(int(content[10]))
                else:
                    if '-END' in content[5]:                            
                        pid.append(cur_pid)
                        elapsed_times.append(int(content[6]) - int(last_time))
                        runtime_end.append(int(content[8]))
                        native_end.append(int(content[9]))
                        pss_end.append(int(content[10]))
                    cur_pid = -1
                    last_time = -1
                two_lin_toggle = not two_lin_toggle
            
        data = {
            'pid':  pid,
            'elapsed_times': elapsed_times,
            'runtime_start': runtime_start,
            'runtime_end': runtime_end,
            'native_start': native_start,
            'native_end': native_end,
            'pss_start': pss_start,
            'pss_end': pss_end
        }
        df = pd.DataFrame (data, columns = ['pid','elapsed_times','runtime_start','runtime_end','native_start','native_end','pss_start','pss_end'])
        return df

def read_cpu(proj,alg, start, end):
    phrase=start+' '+end
    filename='cpu-'+proj+'.'+alg+'.txt'
    with open(filename,'r') as f:
        for (i, line) in enumerate(f):
            if phrase in line:
                return line
    return -1


print("starts reading folders")

projects = ['projjava', 'projkotlin', 'flutter']
algorithms = ['binarytrees', 'fannkuch', 'fasta', 'mandelbrot', 'matrixdeterminant', 'nbody', 'spectralnorm']

for lang in projects:
    print('working with ' + lang + ' results')
    with open('aggregate-'+lang+'.csv', 'w') as out:
        writer = csv.writer(out)
        writer.writerow(["operation", "elapsed time", "memory usage avg", "native memory usage avg"])    
        for op in algorithms:
            avg_df= read_avg(lang,op)
            print(avg_df)
            print (lang, op)

            avg_df.to_csv('./'+lang+'-'+op+'.csv')
            #cpu = pd.read_table('./cpu-'+lang+'.'+op+'.txt',delim_whitespace=True, header=None,)

	    #print(cpu)
	    #logs = pd.read_table('./agg-'+lang+'.'+op+'.txt',sep=' ', header=None)
	    
	    #print(logs)

#print ("matplotlib")
#plt.figure()
#plt.plot(x,y, '-o')
#plt.xlabel('# Threads')
#plt.ylabel('Avg Response Time (ms)')
#plt.savefig('testResults.png')
exit()
