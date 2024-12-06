import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Read the CSV files
chimpanzee_df = pd.read_csv('chimpanzee_performance.csv')
chimpanzee_df['Dataset'] = 'Chimpanzee'

dog_df = pd.read_csv('dog_performance.csv')
dog_df['Dataset'] = 'Dog'

human_df = pd.read_csv('human_performance.csv')
human_df['Dataset'] = 'Human'

# Concatenate the DataFrames
performance_df = pd.concat([chimpanzee_df, dog_df, human_df], ignore_index=True)

# Ensure columns are numeric where required
performance_df['BlockingFactor'] = pd.to_numeric(performance_df['BlockingFactor'], errors='coerce')
performance_df['MemoryUsage'] = pd.to_numeric(performance_df['MemoryUsage'], errors='coerce')
performance_df['QueryTime'] = pd.to_numeric(performance_df['QueryTime'], errors='coerce')

# 1. Scatter Plot
plt.figure(figsize=(8, 6))
for dataset in performance_df['Dataset'].unique():
    dataset_df = performance_df[performance_df['Dataset'] == dataset]
    plt.scatter(dataset_df['BlockingFactor'], dataset_df['MemoryUsage'], label=dataset)
plt.xlabel('Blocking Factor')
plt.ylabel('Memory Usage (bytes)')
plt.title('Data Structure Space vs Pattern Matching Query Size Tradeoff')
plt.legend()
plt.grid()
plt.savefig('scatter_plot_tradeoff.png')
plt.show()

# 2. Line Plot
plt.figure(figsize=(8, 6))
for dataset in performance_df['Dataset'].unique():
    dataset_df = performance_df[performance_df['Dataset'] == dataset]
    plt.plot(dataset_df['BlockingFactor'], dataset_df['MemoryUsage'], label=dataset)
plt.xlabel('Blocking Factor')
plt.ylabel('Memory Usage (bytes)')
plt.title('Data Structure Space vs Pattern Matching Query Size Tradeoff')
plt.legend()
plt.grid()
plt.savefig('line_plot_tradeoff.png')
plt.show()

# 3. Dual-Axis Plot
fig, ax1 = plt.subplots(figsize=(8, 6))
for dataset in performance_df['Dataset'].unique():
    dataset_df = performance_df[performance_df['Dataset'] == dataset]
    ax1.plot(dataset_df['BlockingFactor'], dataset_df['MemoryUsage'], label=f"{dataset} Memory Usage")

ax2 = ax1.twinx()
for dataset in performance_df['Dataset'].unique():
    dataset_df = performance_df[performance_df['Dataset'] == dataset]
    ax2.plot(dataset_df['BlockingFactor'], dataset_df['QueryTime'], label=f"{dataset} Query Time", linestyle='dashed')

ax1.set_xlabel('Blocking Factor')
ax1.set_ylabel('Memory Usage (bytes)', color='b')
ax1.tick_params('y', colors='b')
ax2.set_ylabel('Query Time (ns)', color='r')
ax2.tick_params('y', colors='r')

plt.title('Data Structure Space vs Pattern Matching Query Size Tradeoff')
fig.legend(loc='upper left', bbox_to_anchor=(0.1, 0.9))
plt.grid()
plt.savefig('dual_axis_plot_tradeoff.png')
plt.show()

# 4. Heatmap
pivot_data = performance_df.pivot(index='Dataset', columns='BlockingFactor', values='QueryTime')

plt.figure(figsize=(10, 6))
sns.heatmap(pivot_data, cmap='YlOrRd', annot=True, fmt=".1f")
plt.title('Query Time Heatmap')
plt.xlabel('Blocking Factor')
plt.ylabel('Dataset')
plt.colorbar(label='Query Time (ns)')
plt.savefig('heatmap_tradeoff.png')
plt.show()
