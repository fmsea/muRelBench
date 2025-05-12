reset
set term svg
set palette cubehelix
set key right samplen 1.5 offset 0,-1
set grid
set datafile separator ","
set xrange [0.05:0.95]
set xtics border mirror rotate by 45 offset -0.5,-1 format "%.1f"
set ytics right 0,0.1,1 format "%.1f"
set yrange [0.0:1.0]
set xlabel "Density"
set ylabel "Average Time (ms) "

plot '-' using (column("Param: density")):(column("Score")):(column("Score Error (99.9%)")) \
     pt 6 title "Chawdhary" with yerrorbars

set output
