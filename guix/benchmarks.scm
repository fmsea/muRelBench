(define-module (murelbench benchmarks)
  #:use-module (gnu packages)
  #:use-module (guix packages)
  #:use-module (guix gexp)
  #:use-module (guix profiles)
  #:use-module (guix build-system copy)
  #:use-module (guix build-system gnu)
  #:use-module (guix build-system trivial)
  #:use-module ((guix licenses) #:prefix license:)
  #:use-module (gnu packages base)
  #:use-module (gnu packages bash)
  #:use-module (gnu packages java)
  #:use-module (gnu packages maven)
  #:use-module (gnu packages maths)
  #:export (%manifest))

(define benchmarks-jar
  (package
   (name "benchmarks-jar")
   (version "1.0")
   (source (local-file "../target/benchmarks.jar"))
   (build-system copy-build-system)
   (arguments
    '(#:install-plan '(("benchmarks.jar" "lib/benchmarks.jar"))))
   (home-page "https://github.com/BoiseState/muRelBench")
   (synopsis "The JAR file of benchmarks.")
   (description "The (uber/shaded) JAR file with muRelBench")
   (license license:gpl3+)))

(define entry-script
  (package
    (name "entry-script")
    (version "1.0")
    (source #f)
    (build-system trivial-build-system)
    (arguments
     (list
      #:modules '((guix build utils))
      #:builder
      #~(begin
          (use-modules (guix build utils))
          (let ((bash #$(this-package-native-input "bash-minimal"))
                (jdk #$(this-package-native-input "openjdk"))
                (jar #$(this-package-native-input "benchmarks-jar"))
                (bin (string-append #$output "/bin/")))
            (mkdir-p bin)
            (with-output-to-file (string-append bin "benchmarks.sh")
              (lambda _
                (format #t "#!~a/bin/sh
exec ~a/bin/java -jar ~a/lib/benchmarks.jar $@" bash jdk jar)))
            (chmod (string-append bin "benchmarks.sh") #o755)))))
    (native-inputs
     (list bash-minimal
           openjdk11
           benchmarks-jar))
    (home-page "https://github.com/BoiseState/muRelBench")
    (synopsis "Wrapper script, used for packs of muRelBench.")
    (description "Simple wrapper script for packaged versions of muRelBench")
    (license license:gpl3+)))

(define %manifest
  (packages->manifest
   (list bash
         glibc-locales
         benchmarks-jar
         `(,openjdk11 "jdk")
          entry-script)))

%manifest
