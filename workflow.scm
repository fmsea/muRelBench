(define run-benchmarks
  (command #:inputs (jar #:type File) (results-file #:type string)
           #:run "java"
                 "-jar"
                 jar
                 "--time-unit=ms"
                 "--result-format=csv"
                 "--result"
                 results-file
           #:outputs (results #:type File
                              #:binding ((glob . "*.csv")))))

(define splay-files
  (command #:inputs (script #:type File) (results #:type File)
           #:run "python" script results
           #:outputs (splayed-files #:type (array File)
                                    #:binding ((glob . "*.csv")))))

(define generate-plot
  (command #:inputs (plot #:type File) (datafile #:type File)
           #:run "gnuplot" plot
           #:stdin datafile
           #:stdout "$(inputs.datafile.basename).svg"
           #:outputs (plotsvg #:type stdout)))

(workflow ((jar #:type File) (results-file #:type string) (script #:type File) (plot #:type File))
          (pipe (run-benchmarks #:jar jar #:results-file results-file)
                (tee (pipe (splay-files #:script script #:results results)
                           (scatter (generate-plot #:plot plot)
                                    #:datafile splayed-files))
                     (identity))))
